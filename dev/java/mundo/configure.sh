#!/bin/bash
mcroot=`pwd`
mkdir -p config
echo "mcroot=$mcroot" > config/build.properties
if [ `uname` == "Darwin" ]; then
  echo "mcc=\${mcroot}/bin/mcc-mac-x86" >> config/build.properties
else
  echo "mcc=\${mcroot}/bin/mcc-linux" >> config/build.properties
fi
echo "mclib=\${mcroot}/lib/se" >> config/build.properties
echo "mcinterfaces=\${mcroot}/interfaces" >> config/build.properties

echo
cat version.txt
echo
echo "Generated config/build.properties with the following settings:"
echo
cat config/build.properties
echo
