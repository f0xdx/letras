package org.mundo.service.lbsrule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.mundo.annotation.*;
import org.mundo.context.Vector3d;
import org.mundo.context.Matrix4d;
import org.mundo.context.sensor.IRISSpaceCoord;
import org.mundo.context.sensor.IRISSpaceCoordTable;
import org.mundo.rt.IReceiver;
import org.mundo.rt.Logger;
import org.mundo.rt.Message;
import org.mundo.rt.MessageContext;
import org.mundo.rt.Service;
import org.mundo.rt.Signal;
import org.mundo.rt.Subscriber;
import org.mundo.rt.TypedMap;
import org.mundo.rt.DoObject;

//@mcImport
import org.mundo.service.autohotkey.AHKService;
import org.mundo.service.autohotkey.DoAHKService;
//@mcImport
import org.mundo.service.wmstore.WMStoreService;
import org.mundo.service.wmstore.DoWMStoreService;

@mcRemote(className="org.mundo.service.lbsrule.LBSRuleService")
public class LBSRuleService extends Service implements IReceiver
{
  @mcSerialize
  public static class Config
  {
    /**
     * Channel for world model store.
     */
    public String wmstore;
    /**
     * Channel for speech recognizer.
     */
    @mcField(name="sr-channel",optional=true)
    public String srChannel;
    /**
     * Location-actions.
     */
    @mcField(name="locations",optional=true)
    public List<Location> locations;
    /**
     * Voice actions.
     */
    @mcField(name="commands",optional=true)
    public List<Command> commands;
  }
  
  @mcSerialize
  public static class Location
  {
    @mcField(name="name")
    public String name;
    @mcField(name="ahk-channel")
    public String ahkChannel;
    @mcField(name="status-channel",optional=true)
    public String statusChannel;
    @mcField(name="enter-script",optional=true)
    public String enterScript;
    @mcField(name="exit-script",optional=true)
    public String exitScript;

    @mcField(name="commands",optional=true)
    public List<LocCommand> commands;

    transient DoILBSStatus doStatus=null;
    transient DoAHKService doAHK=null;
    transient Region region;
    transient HashMap<String,LocCommand> commandMap = new HashMap<String,LocCommand>();

    public String toString()
    {
	  return "name="+name+", ahk-channel="+ahkChannel+", region="+region;
	}
  }

  @mcSerialize
  public static class Command
  {
    @mcField(name="utterance")
    public String utterance;
    @mcField(name="ahk-channel")
    public String ahkChannel;
    @mcField(name="script")
    public String script;

    transient DoAHKService doAHK=null;

    public String toString()
    {
	  return "utterance="+utterance+", ahk-channel="+ahkChannel;
    }
  }

  @mcSerialize
  public static class LocCommand
  {
    @mcField(name="utterance")
    public String utterance;
    @mcField(name="script")
    public String script;

    transient Location location = null;

    public String toString()
    {
	  return "utterance="+utterance;
    }
  }
  
  public LBSRuleService()
  {
  }
  @Override
  public void init()
  {
    super.init();
    log.info("init");
    if (conf==null)
    {
	  log.severe("no configuration found - aborting");
	  return;
    }
    if (conf.locations!=null)
    {
      for (Location l : conf.locations)
      {
        log.info("config location: "+l);
        locationMap.put(l.name, l);
        if (l.statusChannel!=null)
        {
          l.doStatus = new DoILBSStatus();
          Signal.connect(l.doStatus, session.publish("lan", l.statusChannel));
        }
        if (l.ahkChannel!=null)
        {
          l.doAHK = new DoAHKService();
          Signal.connect(l.doAHK, session.publish("lan", l.ahkChannel));
        }
        if (l.commands!=null)
        {
          for (LocCommand c : l.commands)
  	        l.commandMap.put(c.utterance, c);
        }
     }
    }
    if (conf.commands!=null)
    {
      for (Command c : conf.commands)
      {
  	    log.info("config global command: "+c);
	    commandMap.put(c.utterance, c);
        if (c.ahkChannel!=null)
        {
          c.doAHK = new DoAHKService();
          Signal.connect(c.doAHK, session.publish("lan", c.ahkChannel));
        }
      }
    }
    doWMStore=new DoWMStoreService();
    Signal.connect(doWMStore, session.publish("lan", conf.wmstore));
    session.subscribe("lan", conf.wmstore+".event", new IReceiver() {
      public void received(Message msg, MessageContext ctx) {
        worldModelChanged();
      }
    });
    if (conf.srChannel!=null)
    {
      session.subscribe("lan", conf.srChannel, new IReceiver() {
        public void received(Message msg, MessageContext ctx) {
          utteranceReceived(msg.getMap().getString("p0"));
        }
      });
    }
//    Signal.connect(session.subscribe("lan", conf.channel), this);
    new WMLoadThread().start();
  }
  @Override
  public void shutdown()
  {
    log.info("shutdown");
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
  @mcMethod
  public void start()
  {
    log.info("start");
  }
  @mcMethod
  public void stop()
  {
    log.info("stop");
  }

  public void received(Message msg, MessageContext ctx)
  {
    try
    {
      Object obj=msg.getObject();
      if (obj instanceof IRISSpaceCoordTable)
      {
        IRISSpaceCoordTable table=(IRISSpaceCoordTable)obj;
        if (table.spaceCoords.size()>0)
        {
          Vector3d p=((IRISSpaceCoord)table.spaceCoords.get(0)).p;
          position(irisMatrix.transform(p));
        }
      }
    }
    catch(Exception x)
    {
      log.exception(x);
    }
  }
  private void position(Vector3d p)
  {
    log.finest("position: "+p);
    Location loc = findLocation(p);
    if (currentLocation == loc)
      return;
    long t = System.currentTimeMillis();
    if (currentLocation!=null && timeEntered+minDuration>t)
      return;
    if (currentLocation!=null)
      exited(currentLocation);
    currentLocation = loc;
    timeEntered = t;
    if (currentLocation!=null)
      entered(currentLocation);
  }
  private Location findLocation(Vector3d p)
  {
	if (conf.locations==null)
	  return null;
    for (Iterator<Location> iter=conf.locations.iterator(); iter.hasNext();)
    {
      Location loc = iter.next();
      if (loc.region.contains(p))
        return loc;
    }
    return null;
  }
  private void entered(Location loc)
  {
    log.fine("entered: "+loc);
    if (loc!=null)
    {
      if (loc.doStatus!=null)
        loc.doStatus.regionEntered(loc.name, DoObject.ONEWAY);
      if (loc.enterScript!=null && loc.doAHK!=null)
        loc.doAHK.exec(loc.enterScript, DoObject.ONEWAY);
    }
  }
  private void exited(Location loc)
  {
    log.fine("exited: "+loc);
    if (loc!=null)
    {
      if (loc.doStatus!=null)
        loc.doStatus.regionExited(loc.name, DoObject.ONEWAY);
      if (loc.exitScript!=null && loc.doAHK!=null)
        loc.doAHK.exec(loc.exitScript, DoObject.ONEWAY);
    }
  }
  private void utteranceReceived(String u)
  {
    log.fine("utterance: "+u);

    if (currentLocation!=null)
    {
      if (currentLocation.doStatus!=null)
        currentLocation.doStatus.commandSpoken(u, DoObject.ONEWAY);
	  LocCommand cmd = currentLocation.commandMap.get(u);
	  if (cmd!=null)
	  {
	    currentLocation.doAHK.exec(cmd.script, DoObject.ONEWAY);
        return;
      }
    }

    Command cmd = commandMap.get(u);
    if (cmd!=null)
      cmd.doAHK.exec(cmd.script, DoObject.ONEWAY);
  }
    
  private void worldModelChanged()
  {
    log.info("world model changed");
    List<TypedMap> list=doWMStore.select("MapIRISCamera");
    if (list!=null && list.size()>0)
    {
      TypedMap map=list.get(0);
      irisChannel=map.getString("channelName");
      if (irisSubscriber!=null)
        session.unsubscribe(irisSubscriber);
      irisSubscriber=session.subscribe("lan", irisChannel, this);
      irisMatrix=new Matrix4d();
      try
      {
        irisMatrix._activate(map.getMap("matrix"), new TypedMap());
      }
      catch(Exception x)
      {
        log.exception(x);
      }
      log.fine("iris: "+irisChannel+", "+irisMatrix);
    }

    List<TypedMap> rects=doWMStore.select("MapRect");
    for (TypedMap rect : rects)
    {
	  String name = rect.getString("name");
	  Location loc = locationMap.get(name);
	  if (loc!=null)
	  {
		loc.region = new Rect(rect.getString("name"),
	                          rect.getDouble("x"), rect.getDouble("y"),
	                          rect.getDouble("width"), rect.getDouble("height"));
        log.fine("found location: "+loc);
      }
    }
  }
  
  private abstract class Region
  {
    Region(String name)
    {
      this.name=name;
    }
    public String toString()
    {
      return name;
    }
    abstract boolean contains(Vector3d p);
    String name;
    String filename;
  }

  private class Rect extends Region
  {
    Rect(String name, double x0, double y0, double w, double h)
    {
      super(name);
      this.x0=x0;
      this.y0=y0;
      this.x1=x0+w;
      this.y1=y0+h;
    }
    boolean contains(Vector3d p)
    {
      return x0<=p.x && p.x<x1 && y0<=p.y && p.y<y1;
    }
    double x0, y0, x1, y1;
  }

  private class WMLoadThread extends Thread
  {
	@Override
	public void run()
	{
	  try
	  {
        // If we do not have a world model one second after startup,
	    // then request it
	    Thread.sleep(1000);
	    if (irisChannel==null)
	      worldModelChanged();
      }
      catch(Exception x)
      {
	  }
	}
  }

  private Location currentLocation = null;
  private long timeEntered=0;
  private int minDuration=3000;

  private HashMap<String,Location> locationMap=new HashMap<String,Location>();
  private HashMap<String,Command> commandMap=new HashMap<String,Command>();
  private Subscriber irisSubscriber=null;
  private String irisChannel;
  private Matrix4d irisMatrix;
  private Config conf;
  private DoWMStoreService doWMStore;
  private Logger log=Logger.getLogger("lbsrule");
}
