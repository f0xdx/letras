# Pen Sample #

Letras will work with several pen models by using different [PenDrivers](PenDriver.md) to connect to them. Because formatting of the data provided by the pens can differ, the first step is to convert them in a common data structure for further use. We call this data structure a Pen Sample:

| '''field''' | '''description''' |
|:------------|:------------------|
| x           | absolute position on the x-axis in the pattern space |
| y           | absolute position on the y-axis in the pattern space |
| force       | force afflicted to the pen's tip |
| timestamp   | time the sample was created (in unixtime) |

A Pen Sample will be created each time the PenDriver receives a complete and valid sample from the pen. In streaming mode, the timestamp will therefore include the delay of transmitting the data, which varies for each pen. In batched mode, the timestamp will be created directly by the pen and will probably be more accurat.
This problem may be addressed when trying to merge batched and streamed data.