#include "SpecificPlatform.h"
#include "CommonInterface.h"


#include <libudev.h>
#include <linux/hidraw.h>

#include <sys/ioctl.h>
#include <sys/types.h>
#include <fcntl.h>
#include <unistd.h>

#include <stdlib.h>
#include <stdio.h>
#include <string.h>

/*
 * Internal struct wrapping the PenContext created in AnotoPenDriver.c and internal data needed to manage
 * the connection to the pen.
 * The struct also implements a linked list with the pointer "next"
 */
struct InternalPenContext_ {
	PenContext *externalContext;
	int fileHandle;
        char devNode[32];
	struct InternalPenContext_ *next;
};

typedef struct InternalPenContext_ InternalPenContext;

//udev specific variables
static struct udev *udev;
static struct udev_monitor *mon;
static int monitorFd;
static fd_set fdSetForMonitoring;
static struct timeval nullTimeval;

//constants to identify the pen
const int VENDOR_ID = 0x1e61;
const int PRODUCT_ID = 0x0002;

// the maximum report size in bytes
const int REPORT_SIZE = 14;

// variable to stop the run loop
static int running = 1;

// buffer to store the hid report
static unsigned char *reportPtr;

// entry to the list of connected pens
static InternalPenContext *root = NULL;

//function to clean the Bluetooth address of the ":"
static void cleanAddress(const char* dirtySerial, char *targetSerial, int numberOfCharacters) {
  int j;
  int i;
  i = j = 0;
  while(i<numberOfCharacters) {
      if (dirtySerial[j] == '\0') {
          targetSerial[i] = '\0';
          break;
      }
      if (dirtySerial[j] != ':') {
          targetSerial[i] = dirtySerial[j];
          i++;
      }
      j++;
  }

};

// free the memory allocated for a specific InternalPenContext
static void freeInternalPenContext(InternalPenContext *context) {
	freePenContext(context->externalContext);
	free(context);
}

// disconnect a pen and cleanup all context information
static void disconnectPen(const char *removedDevNode) {
	InternalPenContext *currentCtx = root;

	//we have to delete the pen context from the list of connected pens while keeping
	//the list intact
	if (strcmp(root->devNode, removedDevNode) == 0) {
		root = root->next;
	} else {
		//our pen is somewhere in the list so let's search the entry that's right before our pen
		while(currentCtx->next) {
			if (strcmp(currentCtx->next->devNode, removedDevNode) == 0) {
				//found the pen
				break;
			}
			currentCtx = currentCtx->next;
		}
		if (currentCtx->next) {
			//our pen is somewhere in the middle
			currentCtx->next = currentCtx->next->next;
		} else {
			//our pen is at the end
			currentCtx->next = NULL;
		}
	}

        if (currentCtx) {
            penDisconnected(currentCtx->externalContext);
            freeInternalPenContext(currentCtx);
        }
}

//check the udev monitor for add and remove events
static void checkForUdevEvents() {
	int res;
	
	//setup to query the monitor queue
	FD_ZERO(&fdSetForMonitoring);
	FD_SET(monitorFd,&fdSetForMonitoring);
	//check if new events are queued
	res = select(monitorFd+1, &fdSetForMonitoring, NULL, NULL, &nullTimeval);

	if (res > 0 && FD_ISSET(monitorFd, &fdSetForMonitoring)) {
		struct udev_device *dev;
               
                int fd;
                const char *action;

		//get the device that generated the event		
		dev = udev_monitor_receive_device(mon);
		//get the action of the event                
		action = udev_device_get_action(dev);
                
                if (strcmp(action,"add") == 0) {
                    
		    //a new hidraw device has been added 
                    //we must check whether it was an ADP-301
		    //this can be done by checking the vendor and product id.
		    struct hidraw_devinfo info;
                    fd = open(udev_device_get_devnode(dev), O_RDWR|O_NONBLOCK);
		    //grep the Hidraw Device Information
                    res = ioctl(fd, HIDIOCGRAWINFO, &info);
		    if (res < 0) {
                        perror("HIDIOCGRAWINFO");
                    } else if (info.vendor ==  VENDOR_ID && info.product == PRODUCT_ID) {
			//the hidraw device is an ADP-301
                        InternalPenContext *internalPenContext;

			//to create a new pen service we need the bluetooth address                        
			char address[13];
			struct udev_device *parent;
                   	struct udev_device *parent_parent;
                        parent = udev_device_get_parent(dev);
                        parent_parent = udev_device_get_parent(parent);			
                        cleanAddress(udev_device_get_sysattr_value(parent_parent, "address"),address,13);

			//create and initialize a new InternalPenContext
                        internalPenContext = (InternalPenContext *) malloc(sizeof(InternalPenContext));
                        internalPenContext->fileHandle = fd;
                        strcpy(internalPenContext->devNode, udev_device_get_devnode(dev));
			//create the PenContext
                        internalPenContext->externalContext = createNewPenContext(address);
			
			//send the PenEvent
                        penConnected(internalPenContext->externalContext);

			//add to the list of connected pens
                        internalPenContext->next = root;
                        root = internalPenContext;
                    }
                }
                if (strcmp(action,"remove") == 0) {
		    //disconnect the pen
                    disconnectPen(udev_device_get_devnode(dev));
                }

                udev_device_unref(dev);
	}
}



//decode the HID report that is stored in reportPtr and use it in the given context
static void decodeHIDReport(InternalPenContext *context) {
        unsigned char *reportPointer = reportPtr;

	//In some hidraw versions the first byte indicates the type of the hidraw report.
	//This byte is always 0xa1. If the byte is detected we must skip it.
        if(reportPointer[0] == 0xa1) {
            reportPointer++;
        }
	
	//decode the HID report
	switch (reportPointer[0]) {
		case 0x1:
			receivedPenSample(context->externalContext, 
								calculateCoordinate(reportPointer + 1),
								calculateCoordinate(reportPointer +5),
							    calculateForce(reportPointer[12]));
			break;
		case 0xC:
			switch (reportPointer[1]) {
				case 0x1:
					penDown(context->externalContext);
					break;
				case 0x0:
					penUp(context->externalContext);
					break;
				case 0x2:
					penDisconnected(context->externalContext);
					break;
				default:
					//should never be reached
					break;
			}
			break;
		default:
			//we do not yet handle the session initiation event and error events
			break;
        }
}

/* 
 * recursively go through the list of connected pens and read the report
 * if no report could be read the pen is skipped and if an error is encoutered while
 * reading the report the pen is disconnected.
 */
static void readReports(InternalPenContext *context) {
	if (context) {
		int res;
		res = read(context->fileHandle,reportPtr,REPORT_SIZE);
		if (res > 0) {
			decodeHIDReport(context);
		} else {
			//no report received
		} 
		readReports(context->next);
	} 
}

//recursively disconnect all connected pens
void disconnectAllPens(InternalPenContext *context) {
	if (context) {
		disconnectPen(context->devNode);
		disconnectAllPens(context->next);
	}
}

/*
 * setup a Udev Monitor for receiving events in the hidraw subsystem
 */
static void setupUdevMonitor() {
	udev = udev_new();
	if (!udev) {
		printf("Can't create udev\n");
	} else {
		mon = udev_monitor_new_from_netlink(udev, "udev");
		//restrict to events in the hidraw subsystem
		udev_monitor_filter_add_match_subsystem_devtype(mon, "hidraw", NULL);
		udev_monitor_enable_receiving(mon);
		monitorFd = udev_monitor_get_fd(mon);
		//the nullTimeval will be used when querying the monitor later. Since
		//we want the querying to be non-blocking the nullTimeval is set to 0 seconds.
		nullTimeval.tv_sec = 0;
		nullTimeval.tv_usec = 0;
	}
}

// see SpecificPlatform.h for documentation on this function
void initPenDiscovery() {
	reportPtr = (unsigned char *) malloc(sizeof(unsigned char)*REPORT_SIZE);

	setupUdevMonitor();

	while (running) {
		checkForUdevEvents();
		readReports(root);
		//sleep to save CPU cycles
		usleep(10*1000);
	}
	//shutdown has occured so clean up everything
	disconnectAllPens(root);
}

// see SpecificPlatform.h for documentation on this function
void shutdownPenDiscoveryAndDisconnectAllPens() {
	running = 0;
}
