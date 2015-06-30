# FAQ #

This represents a collection of frequently asked questions regarding Letras. It can be used as a resource to help trouble shooting. If you have an important question in mind which you feel belongs here, please contact one of the project members.




---


## Hardware Related Questions ##

### With which pen models does Letras work? ###

Letras has a very flexible hot-plug mechanism for pen drivers. If deployed as a standard java application simply put any appropriate driver into the `./drivers` directory and the driver can be used by your pipeline. Some standard drivers are provided alongside with Letras:

  * Nokia SU-1B
  * Logitech IO2 Bluetooth and ADP-201
  * Anoto ADP-301

Due to restrictions in the Android HID-Stack the ADP-301 is currently not supported on Android.

We encourage the community to develop additional drivers and will gladly include any contributions into the code base.

### Why does my Nokia SU-1B not work with Letras? ###

The bad news is that the Nokia SU-1B pen is not capable of streaming digital ink out of the box. The good news is, that there is a firmware patch available that adds this behavior. So you will need to obtain this patch, install it on your pen and you are ready to use it with Letras.


---


## Running Letras ##

### I try to run the Letras examples, but nothing works.. What am I doing wrong? ###

Do you use a 64-Bit version of the Java Virtual Machine under Windows or are you running OS X 10.8? Some of the pen drivers provided with Letras base on the [BlueCove](http://bluecove.org/) Bluetooth library (Nokia SU-1B and Logitech IO2 BT). Its standard version, however, does not work with a 64-Bit JVM on Windows neither under OS X 10.8. You can either switch to a 32-Bit JVM, or try recompiling the BlueCove JNI parts for your platform and provide the jar to Letras.

### Why can't I process some digital ink that is stored on my pen in Letras? ###

Currently Letras only supports the interactive operation mode, that is dynamically streamed digital ink over a bluetooth connection. However, the pipeline is in theory also capable of processing digital ink stored on a digital pen, e.g. when it is plugged into a craddle. This would require appropriate drivers that handle the USB connection to the digital pen. If you are interested in developing such drivers, please contact one of the project members.