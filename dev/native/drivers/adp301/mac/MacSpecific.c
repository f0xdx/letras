/*
 *  MacSpecific.c
 *  Project: ADP301-Driver for Letras
 *
 *	This is the Mac specific implementation of the Anoto ADP-301 Pen Driver
 *	for Letras. It is designed to work on Mac OS X 10.5 or newer.
 *  For a description of the interface/global functions that are implemented
 *	look at SpecificPlatform.h
 *
 *  Created by Niklas Lochschmidt on 29.07.10.
 */


#include <CoreFoundation/CoreFoundation.h>
#include <IOKit/hid/IOHIDManager.h>
#include <IOKit/hid/IOHIDLib.h>

#include "SpecificPlatform.h"
#include "CommonInterface.h"

/*	along with the PenContext defined in CommonInterface.h we need a pointer 
 *	to the report buffer for each pen in order to free the space when the pen
 *	is disconnected
 */
typedef struct {
	PenContext *externalPenContext;
	uint8_t *reportPtr;
} InternalPenContext;

// reference to the HID manager of Mac OS X
static IOHIDManagerRef ioHIDManagerRef = NULL;

// reference to the runloop responsible for handling all HID events
static CFRunLoopRef runLoop;

static void Handle_IOHIDDeviceIOHIDReportCallback(
												  void *          inContext,         
												  IOReturn        inResult,          
												  void *          inSender,          
												  IOHIDReportType inType,            
												  uint32_t        inReportID,        
												  uint8_t *       inReport,         
												  CFIndex         inReportLength) 
{
	
	InternalPenContext *internalPenContext = (InternalPenContext *) inContext;
	//decode the HID report
	switch (inReport[0]) {
		case 0x1:
			receivedPenSample(internalPenContext->externalPenContext, 
								calculateCoordinate(inReport + 1), 
								calculateCoordinate(inReport +5),
							    calculateForce(inReport[12]));
			break;
		case 0xC:
			switch (inReport[1]) {
				case 0x1:
					penDown(internalPenContext->externalPenContext);
					break;
				case 0x0:
					penUp(internalPenContext->externalPenContext);
					break;
				case 0x2:
					penDisconnected(internalPenContext->externalPenContext);
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


// this will be called when a HID device is removed (unplugged)
static void Handle_RemovalCallback(
								   void *         inContext,     
								   IOReturn       inResult,
								   void *         inSender) {
	InternalPenContext *penContext = (InternalPenContext *) inContext;
	//free all memory
	free(penContext->reportPtr);
	freePenContext(penContext->externalPenContext);
	free(penContext);
}

// this will be called when the HID Manager matches a new (hot plugged) HID device
static void Handle_DeviceMatchingCallback(
										  void *          inContext,       
										  IOReturn        inResult,        
										  void *          inSender,        
										  IOHIDDeviceRef  inIOHIDDeviceRef 
										  ) {
	
	IOHIDDeviceOpen(inIOHIDDeviceRef, kIOHIDOptionsTypeNone);
	
	// schedule on runloop
	IOHIDDeviceScheduleWithRunLoop(inIOHIDDeviceRef, CFRunLoopGetCurrent(), kCFRunLoopDefaultMode);
	
	//get maximum report size
	CFNumberRef reportSizeRef = (CFNumberRef) IOHIDDeviceGetProperty(inIOHIDDeviceRef, CFSTR(kIOHIDMaxInputReportSizeKey));
	int reportSize = 0;
	CFNumberGetValue(reportSizeRef, kCFNumberIntType, &reportSize);
	//allocate memory for report
	uint8_t *report = malloc(reportSize);
	
	//get serial number
	CFTypeRef serialNumberRef = IOHIDDeviceGetProperty(inIOHIDDeviceRef, CFSTR(kIOHIDSerialNumberKey));
	char serialNumber[256];
	CFStringGetCString(serialNumberRef, serialNumber, 256, CFStringGetSystemEncoding());
	
	//create the pen context
	InternalPenContext *context = malloc(sizeof(InternalPenContext));
	context->reportPtr = report;
	PenContext *externalContext = createNewPenContext(serialNumber);
	context->externalPenContext = externalContext;
	
	//send pen connected event
	penConnected(context->externalPenContext);
	
	//register for device events and reports
	IOHIDDeviceRegisterRemovalCallback(inIOHIDDeviceRef, Handle_RemovalCallback, context);
	
	IOHIDDeviceRegisterInputReportCallback(inIOHIDDeviceRef,   
										   context->reportPtr, 
										   reportSize,         
										   Handle_IOHIDDeviceIOHIDReportCallback, 
										   context);
}

//create a dictionary that contains information to identify proper ADP-301 pens
static CFDictionaryRef createMatchingDictionary() {
	CFMutableDictionaryRef result = CFDictionaryCreateMutable (kCFAllocatorDefault,
															   0, &kCFTypeDictionaryKeyCallBacks, &kCFTypeDictionaryValueCallBacks);
	if (result) {
		int usagePage = 0xFF00;
		int usage = 0x0001;
		int vendor = 7777;
		int product = 768;
		
		// value for usage page to refine the matching dictionary
		CFNumberRef usagePageCFNumberRef = CFNumberCreate( kCFAllocatorDefault, kCFNumberIntType, &usagePage);
		// value for usage to refine the matching dictionary.
		CFNumberRef usageCFNumberRef = CFNumberCreate( kCFAllocatorDefault, kCFNumberIntType, &usage);
		// value for vendor id to refine the matching dictionary
		CFNumberRef vendorCFNumberRef = CFNumberCreate( kCFAllocatorDefault, kCFNumberIntType, &vendor);
		// value for product id to refine the matching dictionary.
		CFNumberRef productCFNumberRef = CFNumberCreate( kCFAllocatorDefault, kCFNumberIntType, &product);
		
		if (usagePageCFNumberRef && usageCFNumberRef && vendorCFNumberRef && productCFNumberRef) {
			
			CFDictionarySetValue(result, CFSTR(kIOHIDDeviceUsagePageKey), usagePageCFNumberRef);
			CFDictionarySetValue(result, CFSTR(kIOHIDDeviceUsageKey), usageCFNumberRef);
			CFDictionarySetValue(result, CFSTR( kIOHIDProductKey), productCFNumberRef);
			CFDictionarySetValue(result, CFSTR( kIOHIDVendorIDKey), vendorCFNumberRef);
			
			CFRelease(usagePageCFNumberRef);
			CFRelease(usageCFNumberRef);
			CFRelease(vendorCFNumberRef);
			CFRelease(productCFNumberRef);
		} else {
			fprintf( stderr, "%s: CFNumberCreate failed.", __PRETTY_FUNCTION__ );
		}
	}
	return result;
}
static void setUpHIDManager() {
	ioHIDManagerRef = IOHIDManagerCreate( kCFAllocatorDefault, 0L );
	if (ioHIDManagerRef){
		CFDictionaryRef matchingDictionary = createMatchingDictionary();
		if (matchingDictionary) {
			IOHIDManagerSetDeviceMatching(ioHIDManagerRef, matchingDictionary);
			IOHIDManagerScheduleWithRunLoop(ioHIDManagerRef, CFRunLoopGetCurrent(), kCFRunLoopDefaultMode);
			CFRelease(matchingDictionary);			
		}
		
	}
}

static void registerForDiscoveryCallbacks() {
	IOHIDManagerRegisterDeviceMatchingCallback(ioHIDManagerRef, &Handle_DeviceMatchingCallback, NULL);
}


static void unregisterFromDiscoveryCallbacks() {
	IOHIDManagerRegisterDeviceMatchingCallback(ioHIDManagerRef, NULL, NULL);
}

static void tearDownHIDManager() {
	if ( ioHIDManagerRef ) {
		IOHIDManagerUnscheduleFromRunLoop(ioHIDManagerRef, CFRunLoopGetCurrent(), kCFRunLoopDefaultMode);
		IOHIDManagerClose( ioHIDManagerRef, 0 );
		ioHIDManagerRef = NULL;
	}
}

void initPenDiscovery() {
	if (!ioHIDManagerRef) {
		
		//get a reference to the HID Manager and add device filter 
		setUpHIDManager();
		
		registerForDiscoveryCallbacks();
	
		runLoop = CFRunLoopGetCurrent();
		
		//start the run loop. this call will return when CFRunLoopStop(runLoop) gets called
		CFRunLoopRun();
	
		unregisterFromDiscoveryCallbacks();
		tearDownHIDManager();
	}
}

void shutdownPenDiscoveryAndDisconnectAllPens() {
	CFRunLoopStop(runLoop);
}