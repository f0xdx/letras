# Access Controller #

Before any data coming from the RawDataProcessingStage is used in the Region Processing Stage the pen submitting the data has to pass the access controller. The regulation is needed because the Region Processing Stage is able to discover and receive data from any pen connected to the system.

The Access Controller decides whether to allow or deny access based on the pen's identifier and Mundo-zone. The zone is used in addition to the pen's identifier, because the identifier is unique to a pen and will stay the same even when connected to a different node, however the zone may change.

There is always one general rule that by default allows all pens to access the Region Processor. In addition one specific rule for each zone can be defined.
All rules including the general rule can be modified in the node.conf.xml as well as at runtime through the [source:development/trunk/src/org/letras/ps/region/penconnector/IPenAccessConfiguration.java IPenAccessConfiguration] interface.

The general access query procedure has the following steps:

  * Check if a rule for the zone of the pen exists
    * if there is such a rule, check if it allows the pen to access
    * if there is no rule for the zone, check if the general rule allows the pen to access

A rule can be one of the four types (with string representation)
  1. Allow all pens (ALLOW:**)
  1. Deny all pens (DENY:**)
  1. Whitelisting: Allow only specific pens and deny all other (ALLOW:pen1 pen2)
  1. Blacklisting: Deny only specific pens and allow all other (DENY:pen1 pen2)

The type gets automatically updated when pens get white or blacklisted according to the following diagram.

[[Image(source:documents/graphics/rps\_accessrule\_states.png)]]

The configuration of the AccessController looks like this:

```
<access xsi:type="map">
  <general>[general rule as string]</general>
  <zones xsi:type="map">
      <name_of_zone1>[rule for zone1 as string]</name_of_zone1> <!-- optional -->
      <name_of_zone2>[rule for zone2 as string]</name_of_zone2> <!-- optional -->
  </zones>
</access>
```

## Example configurations ##

'''Example 1:''' A developer could be interested in allowing all pens that are directly connected to a Raw Data Processor inside the same runtime ('rt'-zone) as the Region Processor but not pens that are connected through the network ('lan'-zone) as these could be possible wrongdoers.

Configuration for Example 1
```
<access xsi:type="map">
  <general>DENY:*</general>
  <zones xsi:type="map">
      <rt>ALLOW:*</rt>
  </zones>
</access>
```

'''Example 2:''' A developer wants to allow only one specific pen to connect to the system, e.g. single user application, but doesn't care for the zone.

Configuration for Example 1
```
<access xsi:type="map">
  <general>ALLOW:@p9f23ss</general>
  <zones xsi:type="map">
  </zones>
</access>
```