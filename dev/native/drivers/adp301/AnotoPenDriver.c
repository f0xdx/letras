/*
 *  AnotoPenDriver.c
 *  Project: ADP301-Driver for Letras
 *	
 *	This implementation file implements the functions specified in AnotoPenDriver.h as well
 *	as CommonInterface.h. The part of the driver implemented in this file is responsible 
 *	for communicating between the platform specific parts of an Anoto ADP-301 driver and the
 *	corresponding objects inside the JavaVM. In addition helper methods are implemented that
 *	contain the code to decode information like coordinates and force from the bytes contained
 *	in the HID report.
 *
 *	For descriptions of the functions look at CommonInterface.h 
 *
 *	This implementation is designed to be reusable in each of the platform specific drivers.
 *
 *  Created by Niklas Lochschmidt on 29.07.10.
 */

#include "AnotoPenDriver.h"
#include "CommonInterface.h"
#include "SpecificPlatform.h"
#include <jni.h>
#include <stdlib.h>

//handles for communicating with the JavaVM
static jobject _penDriver;
static JNIEnv *env;

//cached methodIDs
static jmethodID penConnectedMethod = NULL;
static jmethodID penDownMethod = NULL;
static jmethodID sendSampleMethod = NULL;
static jmethodID penUpMethod = NULL;
static jmethodID penDisconnectedMethod = NULL;

//cached pendriver class
static jclass _penDriverClass;

//implementation of functions specified in CommonInterface.h

void penConnected(PenContext *context) {
	(*env)->CallVoidMethod(env,context->penAdapter,penConnectedMethod);
}

void penDown(PenContext *context) {
	(*env)->CallVoidMethod(env,context->penAdapter,penDownMethod);
}

void receivedPenSample(PenContext *context, double x, double y, int force) {
	(*env)->CallVoidMethod(env,context->penAdapter,sendSampleMethod,x,y,force);	
}

void penUp(PenContext *context) {
	(*env)->CallVoidMethod(env,context->penAdapter,penUpMethod);
}

void penDisconnected(PenContext *context) {
	(*env)->CallVoidMethod(env,context->penAdapter,penDisconnectedMethod);
}

int calculateForce(unsigned char force) {
	//we simply invert the pressure byte for now
	return 255-force;
}

double calculateCoordinate(unsigned char *bytes) {
	//cached fraction values for 3 bits fraction
	static float fractions[8] = {0.0, 0.125, 0.25, 0.375, 0.5, 0.625, 0.75, 0.875};
	//calculate relative integer coordinate value 
	int temp = (bytes[1] << 5) | (bytes[0] >> 3);
	//add the fraction
	double result = temp + fractions[(bytes[0] & 0x7)];
	//calculate offset
	temp = ((bytes[3] << 8) | bytes[2]);
	temp = temp * 0x2000;
	//add offset to relative coordinate and return
	return temp + result;
} 

static void initMethodIds() {
	//initialize the cached methodIds for quicker access later on
	jclass adapterClass = (*env)->FindClass(env, "org/letras/ps/rawdata/driver/anoto/adp301/PenAdapterNativeAdapter");

	penConnectedMethod = (*env)->GetMethodID(env, adapterClass, "penConnected", "()V");
	
	penDownMethod = (*env)->GetMethodID(env, adapterClass, "penDown", "()V");
	
	sendSampleMethod = (*env)->GetMethodID(env, adapterClass, "sendSample", "(DDI)V");
	
	penUpMethod = (*env)->GetMethodID(env, adapterClass, "penUp", "()V");
	
	penDisconnectedMethod = (*env)->GetMethodID(env, adapterClass, "penDisconnected", "()V");
}

PenContext *createNewPenContext(char *token) {
	//call java factory method to create PenAdapterNativeAdapter
	jstring jtoken;
	jobject penAdapterLocal;
	PenContext* newContext;
	jmethodID JAVAcreateNewNativePenAdapter;
	JAVAcreateNewNativePenAdapter = (*env)->GetMethodID(env, _penDriverClass, "createNewNativePenAdapter", "(Ljava/lang/String;)Lorg/letras/ps/rawdata/driver/anoto/adp301/PenAdapterNativeAdapter;");
	jtoken = (*env)->NewStringUTF(env,token);
	penAdapterLocal = (*env)->CallObjectMethod(env,_penDriver,JAVAcreateNewNativePenAdapter,jtoken);
	(*env)->DeleteLocalRef(env,jtoken);
	
	//create a new PenContext
	newContext = (PenContext *) malloc(sizeof(PenContext));
	newContext->penAdapter = (*env)->NewGlobalRef(env,penAdapterLocal);
	(*env)->DeleteLocalRef(env,penAdapterLocal);
	return newContext;
}

void freePenContext(PenContext *context) {
	(*env)->DeleteGlobalRef(env,context->penAdapter);
	free(context);
}

JNIEXPORT void JNICALL Java_org_letras_ps_rawdata_driver_anoto_adp301_AnotoPenDriver_runNativeDriver(JNIEnv *environment, jobject java_this) {
	jclass penDriverClassLocal;
	
	env = environment;
    _penDriver = (*env)->NewGlobalRef(env, java_this);

	penDriverClassLocal = (*env)->FindClass(env, "org/letras/ps/rawdata/driver/anoto/adp301/AnotoPenDriver");
	_penDriverClass = (*env)->NewGlobalRef(env, penDriverClassLocal);
	
	initMethodIds();
	
	//this call is blocking and will only return when penDiscovery is shutdown later
	initPenDiscovery();
}

JNIEXPORT void JNICALL Java_org_letras_ps_rawdata_driver_anoto_adp301_AnotoPenDriver_shutdownNativeDriver(JNIEnv *env, jobject java_this) {
	shutdownPenDiscoveryAndDisconnectAllPens();
}