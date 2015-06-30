# Pen Driver #

The Pen Driver is responsible for converting low-level hardware events from a digital pen into [wiki:PenSample PenSamples]. There are several pen models available at the moment with different data formatting each requiring an appropriate Pen Driver implementation (see below). Pen Drivers are deployed as jars for the [Mundo Generic Plug-in Interface](https://leda.tk.informatik.tu-darmstadt.de/cgi-bin/twiki/view/Mundo/JPlugIns) which offers a convenient way for adding new Pen Drivers to the RawDataProcessingStage at runtime without the need to recompile.

Each pen driver contains a main class implementing the [source:development/trunk/src/org/letras/ps/rawdata/IPenDriver.java IPenDriver] interface. Only one pen driver with a specific main class is allowed. If a second pen driver using the same main class should be loaded, the framework will allow loading of this driver only as soon as the other driver has been unloaded. The main class, which needs to offer a nullary constructor, will be instantiated by the pen driver manager in the raw data processing stage when installation of the driver is detected. It might obtain configuration information packaged with the driver in order to configure itself. This is done by implementing mundo's ''IConfigure'' interface and specifying the properties in the drivers config file. This config file named ''plugin.xml'' needs to be present under ''META-INF/plugin.xml'' in the relevant jar file or directory tree. A sample ''plugin.xml'' for a pen driver looks like


```
<plug-in xmlns="http://mundo.org/2004/plugin/">
   <pendriver xmlns="http://letras.org/2009/driver/pendriver" xmlns:xsi="http://www.w3.org/1999/XMLSchema-instance">
      <main-class>org.letras.ps.rawdata.driver.nokia.NokiaPenDriver</main-class>
      <config xsi:type="map">
         <key1>value string</key1>
         <key2 xsi:type="xsd:int">1</key2>
      </config>
   </pendriver>
</plug-in>
```


Note that the pen driver node and its name space must be present in order to allow the framework recognize the plugin as a pen driver. After this node, the framework will expect a ''main-class'' node. The value of this node is the java fully qualified name of the driver's main class. This main class needs, as described above, to fullfill two criteria in order to qualify as availd pen driver:
  * it must provide a nullary constructor
  * it must implement the ''IPenDriver'' interface
After the ''main-class'' node, a regular pen driver ''plugin.xml'' might be already finished. However, it is possible to pass a ''TypedMap'' of configuration options to the pen driver. In order to do so, a ''config'' node must be specified as shown in the example above. Note that the pen driver will only be able to use the specified configuration options if it implements Mundo's ''IConfigure'' interface.


---

Current pen support:
| '''Model''' | '''Pen Support''' | '''Driver''' | '''Batched''' | '''Streaming''' |
|:------------|:------------------|:-------------|:--------------|:----------------|
|Nokia SU-1B  | yes               |  NokiaPenDriver | no            | yes             |
|Nokia SU-27W | no                | -            |-              | -               |
|Logitech IO2 Commercial | yes               | LogitechPenDriver | no            | yes             |
|Anoto ADP-301 | see #60           | AnotoPenDriver | no            | yes             |