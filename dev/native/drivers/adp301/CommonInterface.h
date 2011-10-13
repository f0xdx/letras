/*
 *  CommonInterface.h
 *  Project: ADP301-Driver for Letras
 *
 *  Include this header file as well as the SpecificPlatform.h in the
 *	platform specific implementation for connecting to the HID stack.
 *
 *  This header file contains the definition of a struct on which
 *  the funtions provided by the platform independent part of the
 *  driver operates. The signatures of the functions to send events 
 *  and samples are specified as well.
 *  In addition helper functions for decoding the information from 
 *	data packets received from the pen are declared.
 *
 *
 *  Created by Niklas Lochschmidt on 29.07.10.
 */

#ifndef IMPORT_COMMON_INTERFACE_HEADER
#define IMPORT_COMMON_INTERFACE_HEADER


#ifdef WIN32
	#pragma warning(disable:4068)
#endif


//has to be imported to use the jobject type in the context struct
#include <jni.h>

#pragma mark data type definition 

/*
 *	PenContext is a struct that contains information necessary to communicate
 *  with the correct object in the JavaVM. This is necessary when multiple pens
 *	connect to the driver
 *
*/
typedef struct {
	//see org.letras.ps.rawdata.driver.anoto.adp301.PenAdapterNativeAdapter
	jobject penAdapter;
} PenContext;

#pragma mark factory function

/*
 *	PenContext createNewPenContext(char *token) is a factory function that creates a 
 *	new PenContext for the specified token. The token should be a string that uniquely 
 *	identifies the pen. Preferably this should be the bluetooth address of the pen.
 *	Hint:	the bluetooth address is often saved in the SerialNumber field of the HID
 *			Device descriptor
 *	The PenContext returned by this function must be provided as first argument to each
 *	successive call to one of the event functions below (pen*(...), receivedPenSample(...))
 *  @param token the token that uniquely identifies a pen
 *	@return a new pen context
 */
PenContext *createNewPenContext(char *token);

#pragma mark clean up function

/*
 * free the memory of the PenContext
 * @param the pointer to the PenContext
 */
void freePenContext(PenContext *context);

#pragma mark pen specific functions
//the following functions are used to signal pen specific events

/*
 *	sends the Pen-Connected event for the given pen 
 *  @param context the pen for which to issue the event
 */
void penConnected(PenContext *context);

/*
 *	sends a Pen-Down event for the given pen
 *	@param context the pen for which to issue the event
 */
void penDown(PenContext *context);

/*
 *	sends a Pen-Up event for the given pen
 *	@param context the pen for which to issue the event
 */
void penUp(PenContext *context);

/*
 *	sends a sample for the given pen
 *	@param context the pen from which the sample originates
 *			x		x-coordinate in Anoto-coordinates
 *			y		y-coordinate in Anoto-coordinates
 *			force	pressure at the pens tip
 */
void receivedPenSample(PenContext *context, double x, double y, int force);

/*	the following functions are helper functions for decoding information
 *	from the HID reports
 */

/*
 *	sends a Pen-Disconnected event for the given pen
 *	@param context the pen for which to issue the event
 */
void penDisconnected(PenContext *context);

#pragma mark helper functions

/*
 *	decodes and returns the pen tip pressure from the pressure byte in the HID report
 *  @param force the byte in which the tip pressure is encoded
 */
int calculateForce(unsigned char force);

/*
 *	decodes and returns a coordinate from a byte array. The byte array must contain at
 *	least 4 bytes. The rest of the array is ignored.
 *	@param bytes the bytes in which the coordinate is encoded
 *	@return the coordinate as double
 */
double calculateCoordinate(unsigned char *bytes);

#endif