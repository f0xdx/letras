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

public class SimonVossRFIDReaderService extends Service implements ISerialReceiver
{
  @mcSerialize(className="org.mundo.rfid.SimonVossRFIDReaderService$Options")
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
    public String serChannel;
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

  public SimonVossRFIDReaderService()
  {
  }
  @Override
  public void init()
  {
    super.init();
    log.info("channel: "+conf.channel);
    publisher=session.publish("lan", conf.channel);

    doDB = new DoISerialPorts();
    Signal.connect(doDB, session.publish("lan", conf.serChannel));

    doSP = doDB.getSerialPort(conf.portDescription,
                              doDB.PORT_DESCRIPTION | doDB.PERSISTENT);

    Signal.connect(session.subscribe("lan", doSP.getInChannel()), this);
    doSP.setParams(57600, 8, 'N', 1);
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
    int i=ln.length()-1;
    while (ln.charAt(i)<32)
      i--;
    RFIDEvent ev = new RFIDEvent();
    ev.id = ln.substring(0, i+1);
    log.fine("received: '"+ev.id+"'");
    ev.readerId = conf.readerId;
    ev.tstmp = System.currentTimeMillis();
    publisher.send(Message.fromObject(ev));
  }

  private Publisher publisher;
  private DoISerialPorts doDB;
  private DoISerialPort doSP;
  private Config conf;
  private Logger log = Logger.getLogger("rfid");
}
