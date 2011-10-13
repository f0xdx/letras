#include "SpecificPlatform.h"
#include "CommonInterface.h"
#include "hidapi.h"
#include <windows.h>
#include <strsafe.h>

#ifdef __cplusplus
extern "C" {
#endif

/*
 * Internal struct wrapping the PenContext created in AnotoPenDriver.c and internal data needed to manage
 * the connection to the pen.
 * The struct also implements a linked list with the pointer "next"
 */
struct InternalPenContext_ {
	PenContext *externalContext;
	hid_device *handle;
	wchar_t serialNr[13];
	struct InternalPenContext_ *next;
};

typedef struct InternalPenContext_ InternalPenContext;

// number of hid report readings that occur between checking for new pens 
const int POLLING_SKIPS = 50;

// the maximum report size in bytes
const int REPORT_SIZE = 14;

// variable to stop the run loop
static int running = 1;

// number of hid report readings after last pen discovery cycle
static int pollSkipCount = 0;

// buffer to store the hid report
static unsigned char *reportPtr;

// entry to the list of connected pens
static InternalPenContext *root = NULL;

//check if one or more new pens have been connected
static void checkForNewPens() {
	if (pollSkipCount < POLLING_SKIPS) {
		//do not check this time increase counter
		pollSkipCount++;
		return;
	} else {
		struct hid_device_info *devs, *cur_dev;
		InternalPenContext *currentCtx;
		pollSkipCount = 0;
		//get a linked list of all devices with the Anoto Vendor ID 0x1e61 and the ADP-301 Product ID 0x0002
		devs = hid_enumerate(0x1e61, 0x2);
		cur_dev = devs;	
		//iterate over all pens and check if there are any new pens 
		while (cur_dev) {
			currentCtx = root;
			while(currentCtx) {
				//check if the pen is already in the list of connected pens
				if (wcscmp(currentCtx->serialNr, cur_dev->serial_number) == 0) {
					//we know this pen already
					break;
				}
				currentCtx = currentCtx->next;
			}
			if (!currentCtx) {
				//we have iterated over all connected pens and did not find the cur_dev
				//so let's connect the pen

				//get the serial number as char string
				char cserialNumber[13];
				StringCbPrintfA(cserialNumber, sizeof(cserialNumber), "%ls", cur_dev->serial_number);
				
				//create a new InternalPenContext
				currentCtx = (InternalPenContext *) malloc(sizeof(InternalPenContext));
				
				StringCbPrintfW(currentCtx->serialNr, sizeof(currentCtx->serialNr) , L"%s", cur_dev->serial_number);
				
				//open the hid_device
				currentCtx->handle = hid_open_path(cur_dev->path);
				//set to nonblocking because we want to handle multiple pens on the same thread
				hid_set_nonblocking(currentCtx->handle, 1);	
				
				//get the PenContext
				currentCtx->externalContext = createNewPenContext(cserialNumber);
				
				//add the new pen to the beginning of the list of connected pens
				currentCtx->next = root;
				root = currentCtx;
				
				//notify java that a new pen has been connected
				penConnected(currentCtx->externalContext);
			}
			cur_dev = cur_dev->next;
		}
		//free the list of all pens
		hid_free_enumeration(devs);
	}
}

// free the memory allocated for a specific InternalPenContext
static void freeInternalPenContext(InternalPenContext *context) {
	freePenContext(context->externalContext);
	free(context);
}

// disconnect a pen and cleanup all context information
static void disconnectPen(InternalPenContext *context) {
	InternalPenContext *currentCtx = root;
	
	//hid_close also frees context->handle
	hid_close(context->handle);

	//we have to delete the pen context from the list of connected pens while keeping
	//the list intact
	if (wcscmp(root->serialNr, context->serialNr) == 0) {
		//our pen is the head of the list
		root = root->next;
	} else {
		//our pen is somewhere in the list so let's search the entry that's right before our pen
		while(currentCtx->next) {
			if (wcscmp(currentCtx->next->serialNr, context->serialNr) == 0) {
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
	freeInternalPenContext(context);
}

//decode the HID report that is stored in reportPtr and use it in the given context
static void decodeHIDReport(InternalPenContext *context) {
	
	//decode the HID report
	switch (reportPtr[0]) {
		case 0x1:
			receivedPenSample(context->externalContext, 
								calculateCoordinate(reportPtr + 1), 
								calculateCoordinate(reportPtr +5),
							    calculateForce(reportPtr[12]));
			break;
		case 0xC:
			switch (reportPtr[1]) {
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
		memset(reportPtr, 0, REPORT_SIZE);
		res = hid_read(context->handle, reportPtr, REPORT_SIZE);
		if (res > 0) {
			decodeHIDReport(context);
		} else if (res == 0) {
			//no report received
		} else {
			//error received the pen has been disconnected
			disconnectPen(context);
		}
		readReports(context->next);
	} 
}

//recursively disconnect all connected pens
void disconnectAllPens(InternalPenContext *context) {
	if (context) {
		disconnectPen(context);
		disconnectAllPens(context->next);
	}
}

// see SpecificPlatform.h for documentation on this function
void initPenDiscovery() {
	reportPtr = (unsigned char *) malloc(sizeof(unsigned char)*REPORT_SIZE);
	while (running) {
		checkForNewPens();
		readReports(root);
		//sleep to save CPU cycles
		Sleep(10);
	}
	//shutdown has occured so clean up everything
	disconnectAllPens(root);
}

// see SpecificPlatform.h for documentation on this function
void shutdownPenDiscoveryAndDisconnectAllPens() {
	running = 0;
}

#ifdef __cplusplus
}
#endif