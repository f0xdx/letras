# The Region Model #

In the Region Processing Stage (RPS), the pen events coming in from the driver are dispatched to the regions they are related to. In order to efficiently determine the regions that are concered by a specific sample, the information about the currently most relevant regions is stored in the Region Model. Knowledge on missing regions is fetched as needed using 2 stage region knowledge distribution system. This article describes the requirements that apply to such a Region Model and how they are fulfilled within Letras.

## Regions ##

A region is a two-dimensional shape on the Anoto coordinate plane. Applications can register regions in order to allow interaction on this part of the Anoto coordinate plane. The following assumptions hold true for regions:

  * Regions can be any shape
  * Regions have a minimal bounding rectangle
  * It is possible to test whether a region contains a point
  * Regions can contain child regions (→regions are trees)

### Non-overlap ###

We can ask the question if regions should be allowed to overlap. The answer to this is not straight-forward. Because regions can contain child regions, some overlap is required, namely overlap between regions and the child regions contained within them. This means that the kind of non-overlap assumption that is possible in this model is that child regions of the same parent region cannot overlap each other.

This assumption makes dispatching events to the region tree more efficient because we can stop traversing a Region's children as soon as we have found a region that contains the event sample. This gives us a mean performance advantage of n/2 as opposed to n for the child traversal, where n is the average fan-out of the region tree. This does not reduce complexity of the dispatch (O(n log n) == O (n/2 log n)). However, it might be worthwhile when the fan-out is big. It should however be noted that requiring non-overlap for regions themselves does not imply that their bounding rectangles do not overlap. Because of this, a full intersection test has to be performed before aborting breadth traversal.

If truly overlapping regions are needed by an application, a so called logical region can be used. A logical region is a application side adapter, that contains some regions and registers as data sink for all of these. If events are consumed on any of the managed regions, these events are dispatched to the interested partys.

## Some botanic considerations ##

Because regions can contain child regions, it is already clear that the Region Model is some form of tree. To determine which kind of tree is best suited for the purpose, it is useful to consider what the region tree will probably look like.

### The root ###

There is exactly one root region, which is exactly the Anoto coordinate plane. Below this root, there are pages that serve as interfaces of applications. Because there are potentially large numbers of applications, but the Anoto coordinate plane is still larger, it is probably safe to make the following assumptions:

  1. The fan-out on the first level after the root is enormous.
> 2. The regions on the first level after the root are sparsely distributed across the Anoto coordinate plane.

### Applications ###

Applications will probably register some number of regions as pages. Those are sparsely distributed and are direct children of the root. In those pages, applications register areas for text entry, gesture recognition, widget-based interaction and so on. The numbers of those elements probably corresponds to the number of elements that are usual for other user interfaces. So it is probably safe to assume that fan-out is significantly less than 100 below the second level of the tree.

### Considerations ###

Because the fan-out of the tree differs largely between the first and the second level of the tree, some kind of adaptive tree structure should be useful. Maybe two different algorithms should be used for the first level and for the following levels. This means we need a data structure that is flexible with regard to the ordering of the tree. Bounding Volume Hierarchies (BVH) seem to be best suited to the purpose: The have nodes that physically contain other nodes built right in, the grouping of multiple regions into higher-level nodes is flexible with regard to the algorithm used, and they should handle sparse geometries better than e.g. grid-based approaches.