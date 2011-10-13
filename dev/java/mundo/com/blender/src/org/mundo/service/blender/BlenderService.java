package org.mundo.service.blender;

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

public class BlenderService extends Service implements ISerialReceiver
{
  @mcSerialize(className="org.mundo.service.blender.BlenderService$Options")
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

  public BlenderService()
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
    int ev = 0;
    if (ln.charAt(0) == 'p')
    {
      if (ln.charAt(1) == '1')
      {
        log.info("received: POWER_ON");
        ev = BlenderEvent.POWER_ON;
      }
      else if (ln.charAt(1) == '0')
      {
        log.info("received: POWER_OFF");
        ev = BlenderEvent.POWER_OFF;
      }
    }
    else if (ln.charAt(0) == 'm')
    {
      if (ln.charAt(1) == '1')
      {
        log.info("received: MOTOR_ON");
        ev = BlenderEvent.MOTOR_ON;
      }
      else if (ln.charAt(1) == '0')
      {
        log.info("received: MOTOR_OFF");
        ev = BlenderEvent.MOTOR_OFF;
      }
    }
    BlenderEvent evObj = new BlenderEvent();
    evObj.event = ev;
    evPub.send(Message.fromObject(evObj));
  }

//  private Publisher publisher;
  private Publisher evPub;
  private DoISerialPorts doSPs;
  private DoISerialPort doSP;
  private Config conf;
  private Logger log = Logger.getLogger("blender");
}
