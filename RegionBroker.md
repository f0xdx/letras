# Region Broker #
The region broker is a configurable component of the RegionProcessingStage that is responsible for the discovery and management of regions inside the distributed region publishing system.

To the client a region broker has two responsibilities.
  1. respond with a list of regions for a given point in the anoto coordinate space
  1. notify the client on changes that occur in the requested regions (region can be added, removed and changed)

We plan to implement several region brokers, which access different sources and/or participate in different distributed systems.

## Region Broker Implementations ##

|  '''name''' | '''uses''' | '''short description''' |
|:------------|:-----------|:------------------------|
|  SimpleRegionBroker  |  Mundo ServiceDiscovery  |  really simple broker that is inefficient when large numbers of regions are involved   |