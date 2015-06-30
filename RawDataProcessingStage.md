# Raw Data Processing Stage #

['''TODO''' describe the raw data processing stage]


---

## Included Components ##


### Pen Drivers ###
  * PenDriver
  * NokiaPenDriver
  * LogitechPenDriver

## Processing Stage Interfaces ##

'''Dependencies''': The provided PSI is the PSI IPen, no other PSIs are required since this is the first processing stage

The PSI IPen allows for discovery of pens through generic pen interface (c.f. [source:/development/trunk/src/org/letras/psi/ipen/IPen.java IPen] interface) in the form of a PenService. Data is communicated as [PenSamples](PenSample.md).


---

## Included Tools ##
  * PenMonitor