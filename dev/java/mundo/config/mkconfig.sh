#!/bin/bash
mcroot=$PWD
if [ ! -f build.xml ]; then
  mcroot=`cd ..; pwd`;
fi
if [ ! -f $mcroot/build.xml ]; then
  echo "error: can't locate base directory!"
  exit 1
fi
echo "mcroot=$mcroot" > $mcroot/config/build.properties
if [ `uname` == "Darwin" ]; then
  echo "mcc=\${mcroot}/bin/mcc-mac-x86" >> $mcroot/config/build.properties
  echo "android-platform=/Applications/android-sdk-mac_x86/platforms/android-8" >> $mcroot/config/build.properties
else
  echo "mcc=\${mcroot}/bin/mcc-linux" >> $mcroot/config/build.properties
fi
echo "mclib=\${mcroot}/lib/se" >> $mcroot/config/build.properties
echo "mcinterfaces=\${mcroot}/../interfaces" >> $mcroot/config/build.properties
echo "junit=\${mcroot}/lib/junit-4.4.jar" >> $mcroot/config/build.properties
