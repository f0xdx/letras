package org.mundo.service.scale;

import java.util.HashMap;

import org.mundo.annotation.*;
import org.mundo.rt.Service;
import org.mundo.rt.Signal;
import org.mundo.rt.Logger;
import org.mundo.rt.Publisher;
import org.mundo.rt.DoObject;
import org.mundo.rt.Message;

//@mcImport
import org.mundo.service.serial.ISerialPorts;
import org.mundo.service.serial.DoISerialPorts;
//@mcImport
import org.mundo.service.serial.ISerialPort;
import org.mundo.service.serial.DoISerialPort;
//@mcImport
import org.mundo.service.serial.ISerialReceiver;

public class KernPCBService extends Service implements ISerialReceiver
{
  @mcSerialize(className="org.mundo.service.scale.KernPCBService$Options")
  public static class Config
  {
    /**
     * Export service interface to this channel.
     */
    @mcField(name="channel")
    public String channel;
    /**
     * Channel name of the device bridge service.
     */
    @mcField(name="serial-channel")
    public String spChannel;
    /**
	 * Port name string.
	 */
    @mcField(name="port-name", optional=true)
    public String portName;
    /**
     * Port description string.
     */
    @mcField(name="port-description", optional=true)
    public String portDescription;
    /**
	 * Filter for USB devices.
	 */
    @mcField(name="port-usb", optional=true)
    public Boolean portUSB;
  }

  public KernPCBService()
  {
  }
  @Override
  public void init()
  {
    super.init();
    log.info("channel: "+conf.channel);

//    Signal.connect(session.subscribe("lan", conf.channel), this);
    evPub = session.publish("lan", conf.channel+".event");

    doSPs = new DoISerialPorts();
    Signal.connect(doSPs, session.publish("lan", conf.spChannel));

    int opts = doSPs.PERSISTENT;
    String name = null;
    if (conf.portName!=null)
	{
	  name = conf.portName;
	  opts |= doSPs.PORT_NAME;
	  log.fine("PORT_NAME: "+name);
	}
    else if (conf.portDescription!=null)
	{
	  name = conf.portDescription;
	  opts |= doSPs.PORT_DESCRIPTION;
	  log.fine("PORT_DESCRIPTION: "+name);
    }
    if (conf.portUSB!=null)
	{
	  if (conf.portUSB.booleanValue())
	  {
	    opts |= doSPs.USB_DEVICE;
	    log.fine("USB_DEVICE");
	  }
	  else
	  {
	    opts |= doSPs.NO_USB_DEVICE;
	    log.fine("NO_USB_DEVICE");
	  }
	}
    doSP = doSPs.getSerialPort(name, opts);

    Signal.connect(session.subscribe("lan", doSP.getInChannel()), this);
    doSP.setParams(9600, 8, 'N', 1);
    doSP.setEOLChars("\n");
	doSP.setFlowControl(1);
    doSP.open();
  }
  @Override
  public void shutdown()
  {
    log.info("shutdown");
    doSP.close(DoObject.ONEWAY);
    super.shutdown();
  }
  @Override
  public void setServiceConfig(Object obj)
  {
    try
    {
      conf=(Config)obj;
      log.info("setConfig: "+conf.toString());
    }
    catch(ClassCastException x)
    {
      x.printStackTrace();
    }
  }
  @Override
  public Object getServiceConfig()
  {
    return conf;
  }

  public void portConnected() // ISerialReceiver
  {
    log.info("portConnected");
  }
  public void portDisconnected() // ISerialReceiver
  {
    log.info("portDisconnected");
  }
  public void lineReceived(String ln) // ISerialReceiver
  {
    boolean stable = false;
	double sgn = 1.0;
    ln = ln.trim();
	if (ln.endsWith("g"))
	{
	  stable = true;
	  ln = ln.substring(0, ln.length()-1);
    }
    if (ln.startsWith("-"))
	{
	  sgn = -1.0;
	  ln = ln.substring(1).trim();
	}
	try
	{
	  double v = sgn*Double.parseDouble(ln);
	  log.info("received: "+v+" g" + (stable ? " (stable)" : " (unstable)"));
	  ScaleEvent ev = new ScaleEvent();
	  ev.weight = v;
	  ev.tolerance = 1;
	  ev.unit = "g";
	  ev.stable = stable;
	  evPub.send(Message.fromObject(ev));
    }
	catch(Exception x)
	{
	  log.warning(x.toString());
	}
  }

//  private Publisher publisher;
  private Publisher evPub;
  private DoISerialPorts doSPs;
  private DoISerialPort doSP;
  private Config conf;
  private Logger log = Logger.getLogger("scale");
}
