import java.awt.BorderLayout;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.File;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

public class NodeConfigPanel extends AbstractConfigPanel
{
  public NodeConfigPanel()
  {
    ConfigBuilder b = new ConfigBuilder();
    b.container("node.conf");
    b.container("IPTransportService");
    b.feature("primary-port", "Primary Port", 4242);
    b.description("Defines the port number for discovery and the server port for "+
        "the first node run on a computer.");
    b.feature("broadcast-discovery", "Broadcast Discovery", false);
    b.description("Enables node discovery based on IP broadcast.");
    b.feature("multicast-discovery", "Multicast Discovery", false);
    b.description("Enables node discovery based on IP multicast.");

    items = new Vector<ConfigItem>();
    addItem(0, b.getRoot());
  }
  
  public void initGui() {
	    listBox = new JList(items);
	    listBox.setCellRenderer(new ConfigCellRenderer());
	    listBox.addListSelectionListener(SELECTION_LISTENER);
	    JScrollPane p0 = new JScrollPane(listBox);

	    rightPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JPanel(), new JScrollPane());

	    setLayout(new BorderLayout());
	    add(new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, p0, rightPane), BorderLayout.CENTER);
  }

  protected void addItem(int indent, ConfigItem ci)
  {
    if (ci instanceof ConfigFeature)
      featureMap.put(ci.key, (ConfigFeature)ci);
    ci.indent = indent;
    items.add(ci);
    if (ci.list!=null)
    {
      for (ConfigItem child : ci.list)
        addItem(indent+1, child);
    }
  }
  
  private boolean getBoolean(String key)
  {
    ConfigFeature f = featureMap.get(key);
    if (f==null)
      throw new IllegalStateException("undefined feature "+key);
    if (!(f instanceof ConfigBoolFeature))
      throw new IllegalStateException("feature "+key+" is not of type boolean");
    return ((ConfigBoolFeature)f).getEnabled();
  }
  
  private int getInt(String key)
  {
    ConfigFeature f = featureMap.get(key);
    if (f==null)
      throw new IllegalStateException("undefined feature "+key);
    if (!(f instanceof ConfigIntFeature))
      throw new IllegalStateException("feature "+key+" is not of type int");
    return ((ConfigIntFeature)f).value;
  }
  
  private NodeConfigItem buildDefaultConfig()
  {
    NodeConfigBuilder b = new NodeConfigBuilder();
    b.map("NodeConfig");
    b.attr("xmlns:xsi", "http://www.w3.org/1999/XMLSchema-instance");
    b.string("name", "noname");

    b.map("ServiceManager", "ServiceManager$Options");
    b.array("instances");

    b.newInstance("org.mundo.net.ProtocolCoordinator", "ProtocolCoordinator");
    b.map("config", "ProtocolCoordinator$Options");
    b.string("default-stack", "default");
    b.map("stacks");

    b.array("default");
    b.string("handler", "org.mundo.net.ActivationService");
    b.string("handler", "org.mundo.net.broker.P2PTopicBroker");
    b.string("handler", "org.mundo.net.routing.RoutingService");
    b.map("if");
    b.string("condition", "org.mundo.net.transport.ip.IfUDP");
    b.array("then");
    b.string("handler", "org.mundo.net.NAckHandler");
    b.string("handler", "org.mundo.net.BinSerializationHandler");
    b.string("handler", "org.mundo.net.BinFragHandler");
    b.up(); // end then
    b.array("else");
    b.string("handler", "org.mundo.net.BinSerializationHandler");
    b.up(); // end else
    b.up(); // end if
    b.string("handler", "org.mundo.net.transport.ip.IPTransportService");
    b.up(); // end default

    b.up(); // end stacks
    b.up(); // end config
    b.up(); // end new-instance

    b.newInstance("org.mundo.net.BinSerializationHandler", "BinSerializationHandler");
    b.up();
    b.newInstance("org.mundo.net.routing.RoutingService", "Routing");
    b.up();
    b.newInstance("org.mundo.net.ActivationService", "Activation");
    b.up();
    b.newInstance("org.mundo.net.broker.P2PTopicBroker", "P2PTopicBroker");
    b.up();
    b.newInstance("org.mundo.net.broker.ContentBroker", "ContentBroker");
    b.up();

    b.newInstance("org.mundo.net.transport.ip.IPTransportService", "IPTransport");
    b.map("config", "IPTransportService$Options");
    b.integer("primary-port", getInt("primary-port"));
    
    b.map("broadcast", "IPTransportService$OptBroadcast");
    b.bool("send", getBoolean("broadcast-discovery"));
    b.array("nets");
    b.map("net", "IPTransportService$OptNet");
    b.string("netmask", "255.255.255.0");
    b.up(); // end net
    b.up(); // end nets
    b.up(); // end broadcast
    
    b.map("multicast", "ServiceManager$OptMulticast");
    b.bool("send", getBoolean("multicast-discovery"));
    b.up(); // end multicast

    b.up(); // end config
    b.up(); // end new-instance

    b.newInstance("org.mundo.net.RMCService", "RMC");
    b.up();
    b.newInstance("org.mundo.service.DebugService", "Debug");
    b.up();
    
    b.up(); // end instances
    b.up(); // end ServiceManager
    
    b.map("Logger");
    b.bool("console", true);
    b.string("default-log-level", "WARNING");
    b.array("log-levels");
    b.map("a");
    b.string("category", "netext");
    b.string("log-level", "SEVERE");
    b.up(); // end a
    b.up(); // end log-levels
    b.up(); // end Logger

    b.up(); // end NodeConfig
    return b.root;
  }
    
//  private void oldWriteJava(String fn) throws IOException
//  {
//    w.println("TypedMap m0 = new TypedMap();");
//    w.println("TypedMap m1 = new TypedMap();");
    
//    w.println("TypedMap m3 = new TypedMap();");
//    w.println("m0.put(\"ServiceManager\", m3);");
//    w.println("m3.setActiveClassName(\"org.mundo.service.ServiceManager$OptServiceManager\");");
//    w.println("TypedArray a2 = new TypedArray();");
//    w.println("m3.put(\"instances\", a2);");
//    w.println("TypedMap m4 = new TypedMap();");
//    w.println("a2.add(m4);");
//    w.println("m4.putString(\"classname\", \"org.mundo.net.ProtocolCoordinator\");");
//    w.println("m4.putString(\"name\", \"ProtocolCoordinator\");");

//    w.println("TypedMap m5 = new TypedMap();");
//    w.println("m4.put(\"config\", m5);");
//    w.println("m5.setActiveClassName(\"ProtocolCoordinator$Options\");");
//    w.println("m5.putString(\"default-stack\", \"default\");");
//    w.println("TypedMap m19 = new TypedMap();");
//    w.println("m5.put(\"stacks\", m19);");
//    w.println("TypedArray a3 = new TypedArray();");
//    w.println("m19.put(\"default\", a3);");
//    w.println("a3.addString(\"org.mundo.net.ActivationService\");");
//    w.println("a3.addString(\"org.mundo.net.broker.P2PTopicBroker\");");
//    w.println("a3.addString(\"org.mundo.net.routing.RoutingService\");");
//    w.println("TypedMap m6 = new TypedMap();");
//    w.println("a3.add(m6);");
//    w.println("TypedArray a4 = new TypedArray();");
//    w.println("m6.put(\"else\", a4);");
//    w.println("a4.addString(\"org.mundo.net.BinSerializationHandler\");");
//    w.println("m6.putString(\"condition\", \"org.mundo.net.transport.ip.IfUDP\");");
//    w.println("TypedArray a5 = new TypedArray();");
//    w.println("m6.put(\"then\", a5);");
//    w.println("a5.addString(\"org.mundo.net.NAckHandler\");");
//    w.println("a5.addString(\"org.mundo.net.BinSerializationHandler\");");
//    w.println("a5.addString(\"org.mundo.net.BinFragHandler\");");
//    w.println("a3.addString(\"org.mundo.net.transport.ip.IPTransportService\");");
//    w.println("TypedMap m7 = new TypedMap();");
//    w.println("a2.add(m7);");
//    w.println("m7.putString(\"classname\", \"org.mundo.net.BinSerializationHandler\");");
//    w.println("m7.putString(\"name\", \"BinSerializationHandler\");");
//    w.println("TypedMap m8 = new TypedMap();");
//    w.println("a2.add(m8);");
//    w.println("m8.putString(\"classname\", \"org.mundo.net.routing.RoutingService\");");
//    w.println("m8.putString(\"name\", \"Routing\");");
//    w.println("TypedMap m9 = new TypedMap();");
//    w.println("a2.add(m9);");
//    w.println("m9.putString(\"classname\", \"org.mundo.net.ActivationService\");");
//    w.println("m9.putString(\"name\", \"Activation\");");
//    w.println("TypedMap m10 = new TypedMap();");
//    w.println("a2.add(m10);");
//    w.println("m10.putString(\"classname\", \"org.mundo.net.broker.P2PTopicBroker\");");
//    w.println("m10.putString(\"name\", \"P2PTopicBroker\");");
//    w.println("TypedMap m11 = new TypedMap();");
//    w.println("a2.add(m11);");
//    w.println("m11.putString(\"classname\", \"org.mundo.net.broker.ContentBroker\");");
//    w.println("m11.putString(\"name\", \"ContentBroker\");");
//    w.println("TypedMap m12 = new TypedMap();");
//    w.println("a2.add(m12);");
//    w.println("m12.putString(\"classname\", \"org.mundo.net.transport.ip.IPTransportService\");");
//    w.println("m12.putString(\"name\", \"IPTransport\");");
//    w.println("TypedMap m13 = new TypedMap();");
//    w.println("m12.put(\"config\", m13);");
//    w.println("m13.setActiveClassName(\"IPTransportService$Options\");");
//    w.println("m13.putBoolean(\"tcp-server\", true);");
//    w.println("TypedMap m14 = new TypedMap();");
//    w.println("m13.put(\"discovery\", m14);");
//    w.println("m14.setActiveClassName(\"IPTransportService$OptDiscovery\");");
//    w.println("m14.putBoolean(\"connect-primary\", true);");
//    w.println("m14.putBoolean(\"localhost-udp\", false);");
//    w.println("m14.putInt(\"primaryPort\", "+getInt("primary-port")+");");
//    boolean broadcast = getBoolean("broadcast-discovery");
//    boolean multicast = getBoolean("multicast-discovery");
//    w.println("m14.putBoolean(\"answer-broadcasts\", "+Boolean.toString(broadcast)+");");
//    if (broadcast)
//    {
//      w.println("TypedArray a6 = new TypedArray();");
//      w.println("TypedMap m17 = new TypedMap();");
//      w.println("m17.setActiveClassName(\"IPTransportService$OptBroadcast\");");
//      w.println("m17.putString(\"netmask\", \"255.255.255.0\");");
//      w.println("a6.add(m17);");
//      w.println("m14.put(\"broadcasts\", a6);");
//    }
//    if (multicast)
//    {
//      w.println("TypedMap m18 = new TypedMap();");
//      w.println("m18.setActiveClassName(\"IPTransportService$OptMulticast\");");
//      w.println("m18.putString(\"group\", \"224.42.142.242\");");
//      w.println("m18.putInt(\"ttl\", 255);");
//      w.println("m14.put(\"multicast\", m18);");
//    }
//    w.println("m13.putBoolean(\"udp-server\", false);");
//    w.println("TypedMap m15 = new TypedMap();");
//    w.println("a2.add(m15);");
//    w.println("m15.putString(\"classname\", \"org.mundo.net.RMCService\");");
//    w.println("m15.putString(\"name\", \"RMC\");");
//    w.println("TypedMap m16 = new TypedMap();");
//    w.println("a2.add(m16);");
//    w.println("m16.putString(\"classname\", \"org.mundo.service.DebugService\");");
//    w.println("m16.putString(\"name\", \"Debug\");");
//    w.println("return m0;");
//    w.println("}");
//    w.println("}");
//    w.close();
//  }

  public void writeJava(File f) throws IOException
  {
    JavaWriter w = new JavaWriter(f);
    w.write(buildDefaultConfig());
    w.close();
  }

  public void writeXML(File f) throws IOException
  {
    XMLWriter w = new XMLWriter(f);
    w.write(buildConfig());
    w.close();
  }

  private NodeConfigItem buildConfig()
  {
    NodeConfigBuilder b = new NodeConfigBuilder();
    b.map("NodeConfig");
    b.attr("xmlns:xsi", "http://www.w3.org/1999/XMLSchema-instance");
    b.string("config-type", "overlay");
    b.string("name", "noname");
    
    b.map("ServiceManager", "ServiceManager$Options");
    b.array("instances");
    b.map("new-instance");
    b.string("name", "IPTransport");
    b.map("config", "IPTransportService$Options");
    b.integer("primary-port", getInt("primary-port"));
    b.map("broadcast", "ServiceManager$OptBroadcast");
    b.bool("send", getBoolean("broadcast-discovery"));
    b.up(); // end broadcast
    b.map("multicast", "ServiceManager$OptMulticast");
    b.bool("send", getBoolean("multicast-discovery"));
    b.up(); // end multicast
    b.up(); // end config
    b.up(); // end new-instance
    b.up(); // end instances
    b.up(); // end ServiceManager

    b.map("Logger");
    b.bool("console", true);
    b.string("default-log-level", "WARNING");
    b.up(); // end Logger
    b.up(); // end NodeConfig

    return b.root;
  }
  
  private HashMap<String,ConfigFeature> featureMap = new HashMap<String,ConfigFeature>();
}
