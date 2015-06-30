<img src='http://letras.googlecode.com/git/doc/gfx/letras-umpc-medium.jpg' align='right' border='16' width='196' height='196'>

<b>NOTE</b>: Letras is currently moving to google code. Its original location was at <a href='https://wiki.tk.informatik.tu-darmstadt.de/bin/view/Letras/WebHome'>https://wiki.tk.informatik.tu-darmstadt.de/bin/view/Letras/WebHome</a> - some of its documentation can still be found there<br>
<br>
<i>Letras</i> provides a framework to support easy and flexible integration of pen input into applications. It allows to connect digital pens to a (distributed) processing pipeline and to dispatch data generated by these pens to registered regions, e.g. paper documents or screen surfaces. <i>Letras</i> comes with drivers for several Anoto digital pens and supports access to these pens on a broad variety of different platforms (Windows, Mac OS X and Linux). It is developed as an active research project at <a href='http://www.tk.informatik.tu-darmstadt.de'>Telecooperation Labs</a>, TU Darmstadt (University of Technology Darmstadt, Germany).<br>
<br>
<h2>Features</h2>
<ul><li>distributed approach: based on the ubiquitous computing middleware <a href='https://wiki.tk.informatik.tu-darmstadt.de/bin/view/Mundo/WebHome'>MundoCore</a>, <i>Letras</i> provides a distributed pen input processing pipeline; the deployment of the pipeline can be easily tailored to specific application requirements<br>
</li><li>support for different platforms: Microsoft Windows (XP, Vista, 7), Mac OS X and Linux<br>
</li><li>mobile devices: additional support for the Android platform<br>
</li><li>multiple interaction resources: <i>Letras</i> provides a general architecture to connect multiple interaction resources with a system, combined with a driver reference implementation for current Anoto based pens (Nokia SU-1B, Logitech IO2 BT and Anoto ADP-301)<br>
</li><li>dynamic interactive region management: <i>Letras</i> supports dynamic management of interactive regions, based on a distributed region model<br>
</li><li>highly flexible graybox framework: <i>Letras</i> mixes blackbox and whitebox framework concepts - available components can be connected and re-arranged based on a generic pipeline model; if available components do not suffice, it is easily possible to extend the framework</li></ul>


<h1>Why Letras?</h1>

Traditional paper remains a prevalent medium in many domains of our daily lives. We use handwritten grocery lists, sign contracts and read printed out articles because of the mobile, robust and highly flexible nature of paper. Instead of being replaced by digital systems, paper artifacts coexist with these systems, inevitably leading to the problem of integration: the ''Digital-Physical Gap''. In order to bridge this gap, a new generation of user interfaces successfully employed digital pen technology, giving shape to a new style of interaction: Pen-and-Paper Interaction (PPI).<br>
<br>
To provide support for PPI in applications and systems, developers need adequate infrastructures and toolkits. These should offer robust processing of pen based input, capture of gestures and strokes and finally allow for their interpretation by present applications in a dedicated pen input processing pipeline. On the one hand, it must be flexible, scalable and support multiple simultaneously used digital pens, as well as using the same digital pen in many applications of different origins. On the other hand, it still needs to be lightweight enough to execute on resource constraint devices, supporting important mobile usage characteristics of paper (e.g. in mobile or nomadic settings).<br>
<br>
Letras provides such tool support. It presents a novel toolkit for pen based interaction, with a focus on ubiquitous use of PPI and sharing of processing resources among applications: interaction in mobile and changing environments, on arbitrary surfaces with a single digital pen becomes easily possible.<br>
<br>
<hr />