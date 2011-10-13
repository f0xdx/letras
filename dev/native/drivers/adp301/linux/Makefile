###########################################
# Makefile for building the native driver
# extension for the ADP-301 driver for
# linux.
#
# Make sure the JNI variable points to the
# directory where the jni.h is located.
###########################################

all: adp301

C=gcc
COBJS=../AnotoPenDriver.o LinuxSpecific.o
JNI = /usr/lib/jvm/default-java/include/
CFLAGS=-I../ -I$(JNI) -Wall -c
LIBS=-ludev

LBITS := $(shell getconf LONG_BIT)
ifeq ($(LBITS),64)
   ARCHFLAGS=-fPIC
   OUT=-o libadp301_64.so
else
   OUT=-o libadp301.so
endif



adp301: $(COBJS)
	gcc -Wall -shared $^ $(LIBS) $(OUT)
	rm $(COBJS)

$(COBJS): %.o: %.c
	$(C) $(ARCHFLAGS) $(CFLAGS) $(LIBS) $< -o $@

clean:
	rm $(COBJS) libadp301.so
