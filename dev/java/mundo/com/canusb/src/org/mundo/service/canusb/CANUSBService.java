package org.mundo.service.canusb;

import java.util.HashMap;

import org.mundo.annotation.*;
import org.mundo.rt.Service;
import org.mundo.rt.Signal;
import org.mundo.rt.Logger;
import org.mundo.rt.DoObject;

//@mcImport
import org.mundo.service.serial.ISerialPorts;
import org.mundo.service.serial.DoISerialPorts;
//@mcImport
import org.mundo.service.serial.ISerialPort;
import org.mundo.service.serial.DoISerialPort;
//@mcImport
import org.mundo.service.serial.ISerialReceiver;

public class CANUSBService extends Service implements ICAN, ISerialReceiver
{
  @mcSerialize(className="org.mundo.service.canusb.CANUSBService$Options")
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
    public String dbChannel;
    /**
     * Port description string of the CANUSB device.
     */
    @mcField(name="port-description",optional=true)
    public String portDescription;
    /**
     * Port name string for CANUSB device.
     */
    @mcField(name="port-name",optional=true)
    public String portName;
  }

  public CANUSBService()
  {
  }
  @Override
  public void init()
  {
    super.init();
    log.info("channel: "+conf.channel);
    Signal.connect(session.subscribe("lan", conf.channel), this);
//    publisher=session.publish("lan", conf.channel+".event");

    Signal.connect(session.subscribe("lan", conf.channel), this);
    doGlobalEv = new DoICANEvent();
    Signal.connect(doGlobalEv, session.publish("lan", conf.channel+".event"));

    doDB = new DoISerialPorts();
    Signal.connect(doDB, session.publish("lan", conf.dbChannel));

    if (conf.portName!=null)
      doSP = doDB.getSerialPort(conf.portName,
                                doDB.PORT_NAME | doDB.PERSISTENT);
    else if (conf.portDescription!=null)
      doSP = doDB.getSerialPort(conf.portDescription,
                                doDB.PORT_DESCRIPTION | doDB.PERSISTENT);

    Signal.connect(session.subscribe("lan", doSP.getInChannel()), this);
    doSP.setParams(921600, 8, 'N', 1);
    doSP.setEOLChars("\7\r");
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
    
    // close the channel before changing bus speed
    doSP.send("C\r");
    ln = doSP.readLine(1000);
    log.fine("close channel: " + statusString(ln));
    if (ln==null)
      return;

    // set bus speed
    doSP.send("S6\r");
    ln = doSP.readLine(1000);
    log.fine("set speed: " + statusString(ln));
    if (!statusOK(ln))
      return;

    // open channel
    doSP.send("O\r");
    ln = doSP.readLine(1000);
    log.fine("open channel: " + statusString(ln));
    if (!statusOK(ln))
      return;
    
    doSP.setReadMode(doSP.READ_EVENTS);
    log.info("init OK");

    doGlobalEv.busConnected(DoObject.ONEWAY);
  }
  private String statusString(String ln)
  {
    if (ln==null)
      return "timeout";
    int l=ln.length();
    if (l>0 && ln.charAt(l-1)=='\r')
      return "OK";
    return "failed";
  }
  private boolean statusOK(String ln)
  {
    if (ln==null)
      return false;
    int l=ln.length();
    return (l>0 && ln.charAt(l-1)=='\r');
  }
  public void portDisconnected() // ISerialReceiver
  {
    log.info("portDisconnected");
    doGlobalEv.busDisconnected(DoObject.ONEWAY);
  }
  public void lineReceived(String ln) // ISerialReceiver
  {
    int l = ln.length();
    log.fine("received: "+ln.substring(0, l-1) +
             ((ln.charAt(l-1)=='\r') ? " (OK)" : " (ERROR)"));
    if (ln.charAt(0)=='t')
    {
      String msgIdStr = ln.substring(1, 4).toLowerCase();
      int msgId = Integer.parseInt(msgIdStr, 16);
      int msgLen = Integer.parseInt(ln.substring(4, 5)) / 2;
      byte[] b = new byte[msgLen];
      for (int i=0; i<msgLen; i++)
        b[i] = (byte)Integer.parseInt(ln.substring(i*2+5, i*2+7), 16);
      
      DoICANEvent doEv = emitters.get(msgIdStr);
      if (doEv==null)
      {
        doEv = new DoICANEvent();
        Signal.connect(doEv, session.publish("lan", conf.channel+"."+msgIdStr));
        emitters.put(msgIdStr, doEv);
      }
      doEv.messageReceived(msgId, b, DoObject.ONEWAY);
    }
  }
  public boolean send(int msgId, byte[] b)
  {
    StringBuffer sb = new StringBuffer();
    sb.append("t");
    sb.append(String.format("%03x", msgId));
    sb.append(b.length);
    for (int i=0; i<b.length; i++)
      sb.append(String.format("%02x", b[i]));
    String s = sb.toString();
    log.fine("send: "+s);
    return doSP.send(s+"\r");
  }

//  private Publisher publisher;
  private HashMap<String,DoICANEvent> emitters = new HashMap<String,DoICANEvent>();
  private DoISerialPorts doDB;
  private DoISerialPort doSP;
  private DoICANEvent doGlobalEv;
  private Config conf;
  private Logger log = Logger.getLogger("canusb");
}
