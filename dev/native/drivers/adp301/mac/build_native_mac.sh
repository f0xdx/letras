#!/bin/bash
cd ..
cc -c -Wall -I/System/Library/Frameworks/JavaVM.framework/Headers ./AnotoPenDriver.c
cc -c -Wall -I. -I/System/Library/Frameworks/JavaVM.framework/Headers ./mac/MacSpecific.c
cc -dynamiclib -o ./mac/libadp301.jnilib AnotoPenDriver.o MacSpecific.o -framework JavaVM -framework CoreFoundation -framework IOKit
rm AnotoPenDriver.o MacSpecific.o