package org.mundo.rfid;

import java.util.HashMap;

import org.mundo.annotation.*;
import org.mundo.rt.Service;
import org.mundo.rt.Signal;
import org.mundo.rt.Logger;
import org.mundo.rt.DoObject;
import org.mundo.rt.Publisher;
import org.mundo.rt.Message;

//@mcImport
import org.mundo.rfid.RFIDEvent;


//@mcImport
import org.mundo.service.serial.ISerialPorts;
import org.mundo.service.serial.DoISerialPorts;
//@mcImport
import org.mundo.service.serial.ISerialPort;
import org.mundo.service.serial.DoISerialPort;
//@mcImport
import org.mundo.service.serial.ISerialReceiver;


public class ZKRFIDReaderService extends Service implements ISerialReceiver
{
  @mcSerialize(className="org.mundo.rfid.ZKRFIDReaderService$Options")
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
    @mcField(name="devicebridge-channel")
    public String dbChannel;
    /**
     * Port description string of the CANUSB device.
     */
    @mcField(name="port-description")
    public String portDescription;
    /**
     * Reader ID to use in the RFID events.
     */
    @mcField(name="reader-id")
    public String readerId;
  }

  public ZKRFIDReaderService()
  {
  }
  @Override
  public void init()
  {
    super.init();
    log.info("channel: "+conf.channel);
    publisher=session.publish("lan", conf.channel);
	
	doSPs = new DoISerialPorts();
    Signal.connect(doSPs, session.publish("lan", conf.dbChannel));

	doSP = doSPs.getSerialPort(conf.portDescription,
                              doSPs.PORT_DESCRIPTION | doSPs.PERSISTENT);
							  
							  
	Signal.connect(session.subscribe("lan", doSP.getInChannel()), this);
    doSP.setParams(9600, 7, 'E', 1);
    doSP.setEOLChars("\r");
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
/*
    doSP.setReadMode(doSP.READ_LINE);
    doSP.send("\r\r\rV\r");
    String ln;
    for(;;)
    {
      ln = doSP.readLine(1000);
      if (ln==null)
      {
        log.warning("read device version failed");
        return;
      }
      if (ln.length()==6 && ln.charAt(0)=='V' && ln.charAt(5)=='\r')
        break;
    }
    log.fine("version: "+ln.substring(0, 5));
    
    doSP.send("C\r");
    ln = doSP.readLine(1000);
    log.fine("close channel: " + statusString(ln));
    if (ln==null)
      return;

    doSP.send("S6\r");
    ln = doSP.readLine(1000);
    log.fine("set speed: " + statusString(ln));
    if (!statusOK(ln))
      return;

    doSP.send("O\r");
    ln = doSP.readLine(1000);
    log.fine("open channel: " + statusString(ln));
    if (!statusOK(ln))
      return;
    
    doSP.setReadMode(doSP.READ_EVENTS);
    log.info("init OK");

    doGlobalEv.busConnected(DoObject.ONEWAY);
*/
  }
  public void portDisconnected() // ISerialReceiver
  {
    log.info("portDisconnected");
  }
  public void lineReceived(String ln) // ISerialReceiver
  {
    log.fine("received: "+ln);
    int i = ln.indexOf(':');
    if (i<0)
      return;
    RFIDEvent ev = new RFIDEvent();
    // FIXME: CR+LF should be removed properly
    ev.id = ln.substring(i+1, ln.length()-1);
    ev.readerId = conf.readerId;
    ev.tstmp = System.currentTimeMillis();
    publisher.send(Message.fromObject(ev));
  }

  private Publisher publisher;
  private DoISerialPorts doSPs;
  private DoISerialPort doSP;
  private Config conf;
  private Logger log = Logger.getLogger("rfid");
}
