For Windows users:

Currently this driver is compatible with Windows running a 32bit Java Virtual Machine 
only. You can find out what type of virtual machine you are running with 'java --version'. 

For Linux users:

The ADP301-Driver depends on the hidraw module that is present in most newer kernels.
However since the hidraw module makes the raw data of most hid devices available, access 
to the hidraw device nodes is by default restricted to root, because otherwise a program 
could easily access the raw stream and implement for example a keylogger.

To enable the use of the ADP301-Driver on Linux you have to create a file in 
/etc/udev/rules.d/ and add the following line to it:

SUBSYSTEM=="hidraw", MODE="666"

This enables access of the driver to the hidraw device, however, it also allows any other
user or program access to hidraw devices. To make sure that your keyboard and mouse is
not visible check if there is any /dev/hidraw* entry prior to connecting the pen. If no
hidraw entry is there, your hid devices are safely claimed by the input subsystem.
