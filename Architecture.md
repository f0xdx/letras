#Overview about Letras Architecture

# Architecture #

## General Overview ##

In general, Letras employs a mixture of an Event Driven Architecture (EDA) and a modular or component based Pipeline architectural approach. There are several conceptual layers, or Pipeline Processing Stages (ProcessingStage), formed of components designed to take a certain kind of input, or consume certain types of events. Inside such a pipeline processing stage, appropriate handling components can be chosen. Each of these takes a defined type of input and processes it. Input consists of streaming data in form of messages and events. The specification of streaming data and potential events forms the Processing Stage Interface (ProcessingStageInterface), which is provided by all components of a given stage.

<img src='http://letras.googlecode.com/git/doc/gfx/pipeline_architecture01.png' width='300' />

Each component in a processing stage n can rely on the interfaces of previous processing stages and has to expose its own stage's interface. In most cases, it will take the data and events of the processing stage n-1 process them further and expose them to subsequent process stages via its PSI. Out of convenience, the exposed interface of a processing stage n is PSI\_n+1, whereas the required interface is PSI\_n as shown in the figure to the right.

Components of successive process stages form the processing pipeline. Using the processing stage interfaces, it is easy to choose from available components those best matching the purposes of the a specific application. It is also possible to build custom components and integrate them in the pipeline. For example, an application using explicit context data, might choose a handwriting recognition component using such context data to improve its results, whereas an application without context data, would choose a more basic recognition component.

This separation into processing stages and processing stage interfaces is mirrored by the package structure. Each processing stage corresponds to an accordingly named package. The raw data processing stage, corresponds to the package rawdata in the processing stage package tree (ps), thus its toplevel package is ps.rawdata. For the processing stage interfaces (PSI), the very same concept is employed. Following the separated interfaces top level design paradigm, each of these interfaces can be found in a package under the psi toplevel package.

Throughout the following sections, the processing stages, basic components and processing stage interfaces will be described. After the individual stages have been introduced, their interoperation in a complete pipeline instance will be described.

## Raw Data Processing Stage ##

The first stage in the pen input processing pipeline is the raw data processing stage. Its detailed description can be found in RawDataProcessingStage. Components of this processing stage access the pen hardware, extract its raw data and process it to form meaningful samples. These [PenSample](PenSample.md)s are then published on dedicated channels, one channel for each pen. Different pen models are supported through [PenDriver](PenDriver.md)s. To simplify integration of multiple pen drivers, an internal interface allows access to a common communication component providing appropriate abstractions to allow easy publishing of a pen's samples: the PenManager.

## Regin Processing Stage ##

### Pen Access Regulation ###
When a pen connects to the Region Processing Stage on a node it has to pass the AccessController, before data coming from the pen is used in the Region Processor.

### Region Model ###
The RegionModel is the internal representation that is used to determine where a pen event should to be dispatched. The RegionModel does not hold all published regions but loads required regions lazily. This is done by querying the RegionBroker on demand.

### Region Broker ###
The RegionBroker is responsible for the discovery and management of regions. The broker can be queried so retrieve all regions at a given location in the anoto coordinate space.

### Processing Stage Interfaces ###

'''Dependencies''': The RegionProcessingStage requires the PSI IPen and provides/requires the PSI IRegion. A description of the PSI IPen can be found at the RawDataProcessingStage.

The IRegion PSI consists of
> [Regions](http://code.google.com/p/letras/source/browse/dev/java/src/org/letras/psi/iregion/IRegion.java)::
> > Offered by the application, regions provide the information needed by the RegionProcessingStage to dispatch samples to interested parties (using the publish/subscribe communication paradigm). The application defines the set of pens it is interested in, the shape of the interactive region, a channel samples and events on the region are published on and a flag indicating whether this region is also interested in the samples enclosed by one of its children.

> [RegionSamples](http://code.google.com/p/letras/source/browse/dev/java/src/org/letras/psi/iregion/RegionSample.java)::
> > These are samples streamed by the pen, which are mapped to regions.

> [PenEvents](http://code.google.com/p/letras/source/browse/dev/java/src/org/letras/psi/ipen/PenEvent.java)::
> > Note that the PenEvent of the PSI IPen is also used here, along with some of the defined pen states. The PSI IRegion does not define its own pen events.

> [RegionEvents](http://code.google.com/p/letras/source/browse/dev/java/src/org/letras/psi/iregion/RegionEvent.java)::
> > The transfer of digital ink data to interested parties bases on individual samples, rather than complete (aggregated) structures as traces. However, checking for start or end of traces in must be performed in relation to a region. Therefore the RegionProcessingStage injects samples describing the aggregation of digital ink data structures into the data stream (DigitalInkEvents).

## Semantic Processing Stage ##

### The Semantic Segmenter ###

The Semantic Segmenter is the central component of the Semantic Processing Stage. Its task is to group Traces into higher-order structures that are to be interpreted as a single semantic unit, i.e. characters for handwriting recognition or gestures. Open Questions:

  * What is a segment? Is a segment always > a trace or can a trace belong to multiple segments? What about strokes (i.e., all events that share a GUID)?
  * What is the output?
    * Just add Segment messages whenever (part of) a new segment is recognized?
    * Completely control the stream of events, i.e. send Segment\_Start, relay events, send Segment\_End in correct sequence?
    * Do we already add a confidence score here?
    * What about other metadata?
  * How environment-aware is the segmenter?
    * Does it know all its consumers?
    * Can consumer's communicate with the segmenter? (Some kind of feedback loop? Probably need that one...)
      * If yes, how? "Segment understood, not understood, scores, models, ..."?=

### Interfaces ###

The segmenter uses a number of interfaces to communicate with the components surrounding it.

#### The Region Interface (IRegion / IDigitalInkConsumer) ####

The Region interface is already defined and used '''transparently''' by the segmenter for multiple purposes:
  * When segmenting should occur, the region it should occur on has to be registered with the Segmenter.
  * The segmenter queries the region interface for geometric information needed for segmenting.
  * Incoming ink data is collected by subscribing to the region channel.
  * Segmentation Metadata is posted to the region channel.

Thus, the region interface defines the main input and output facility of the segmenter. '''NOTE:''' Splitting the RegionImpl into a region definition and channel subscriber part is probably required.

#### The segmenter service interface (ISegmenter) ####

The segmenter service interface is exposed to the application. Its responsibilities include:
  * adding regions for segmentation
  * removing segmentation for regions
  * other application-specific configuration of the segmenter ('''TODO''' concretize)
  * segmenter lifecycle operations ('''TODO''' find out if this is actually what we want)

#### The segmentation configuration interface (ISegmentationProvider) ####

The segmenter provides an interface to its clients. This can be used to:
  * Register themselves using an id
  * Provide the segmenter with a model of what data they are interested in
  * Reject a segmentation
  * Accept a segmentation

#### The semenatic service interface (ISemanticService) ####

This interface is exposed by the segmentation services (such as gesture recognition to the client application. Its relevant responsibilities are:
  * Injecting a segmenter into the segmentation service