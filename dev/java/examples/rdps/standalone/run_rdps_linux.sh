#!/bin/bash
if [! -e "../../../lib/bluecove-gpl-2.1.0.jar"]
then
   java -cp ../../../lib/letras.jar:../../../lib/mundocore.jar:../../../lib/bluecove-2.1.0.jar:../../../lib/bluecove-gpl-2.1.0.jar org.letras.ps.rawdata.RawDataProcessor
else
   cat ../../../lib/linux_readme.txt 
fi