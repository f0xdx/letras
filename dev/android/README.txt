# README

This Readme assumes that you use Eclipse with the Google ADT and that you
already know how to use the Letras library for desktop development. There are
two basic ways to do this: i. use the Letras Android application and interface
using the regular Android IPC means, ii. use Letras as an Android library. Which
type is best depends on the requirements of your particular application.

## How to use the Letras Android Application

*NOTE* the Letras Android application has not yet been released (as of version
0.2.2). If you need access to the development version please contact me: 
felix (DOT) heinrichs (AT) cs (DOT) tu-darmstadt (DOT) de

## How to use the Letras Android Library

The easiest way when starting a new project is to:

 * Checkout both the whole letras-android folder
 * Import the library and the example project into Eclipse
 * Start implementing your application by modifying the example
	
If Letras for Android has to be added to an existing project it is also 
possible:

 * Checkout and import the library project into Eclipse
 * In the existing project's properties go to the Android section and 
   add Letras as a library
 * Copy the xml files in the assets folder of the library project to 
   the assets folder of your own project
 * See the example project on howto start and use the Letras pipeline
	
When a new version of Letras is released it is possible to simply replace/update
the library project folder.  In some cases a diff on the xml-Files in the assets
folders should be run to see if there were any changes in the configuration.
