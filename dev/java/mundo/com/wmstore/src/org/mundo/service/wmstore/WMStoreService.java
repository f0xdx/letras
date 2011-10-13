package org.mundo.service.wmstore;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.List;
import java.util.ArrayList;

import org.mundo.annotation.*;
import org.mundo.rt.Logger;
import org.mundo.rt.Message;
import org.mundo.rt.Publisher;
import org.mundo.rt.Service;
import org.mundo.rt.Signal;
import org.mundo.rt.TypedArray;
import org.mundo.rt.TypedContainer;
import org.mundo.rt.TypedMap;
import org.mundo.xml.XMLDeserializer;
import org.mundo.xml.XMLSerializer;
import org.mundo.xml.XMLFormatter;

@mcRemote(className="org.mundo.service.wmstore.WMStoreService")
public class WMStoreService extends Service
{
  @mcSerialize
  public static class Config
  {
    /**
     * Export service interface to this channel.
     */
    public String channel;
    /**
     * Name of database file.
     */
    public String filename;
  }

  public WMStoreService()
  {
  }
  @Override
  public void init()
  {
    super.init();
    log.info("init");
    readFile();
    Signal.connect(session.subscribe("lan", conf.channel), this);
    publisher=session.publish("lan", conf.channel+".event");
    emitChanged();
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
  public void put(TypedMap m)
  {
    log.info("put");
    db=m;
    commitFile();
    emitChanged();
  }
  @mcMethod
  public TypedMap get()
  {
    log.info("get");
    return db;
  }
  @mcMethod
  public List select(String activeClass)
  {
    ArrayList result=new ArrayList();
    recSelect(result, db, activeClass);
    return result;
  }
  private void recSelect(List result, TypedContainer cont, String activeClass)
  {
//    System.out.println(cont.getClass());
    if (activeClass.equals(cont.getActiveClassName()))
    {
      result.add(cont);
      return;
    }
    if (cont instanceof TypedMap)
    {
      for (Object obj : ((TypedMap)cont).values())
      {
        if (obj instanceof TypedContainer)
          recSelect(result, (TypedContainer)obj, activeClass);
      }
    }
    else if (cont instanceof TypedArray)
    {
      for (Object obj : (TypedArray)cont)
      {
        if (obj instanceof TypedContainer)
          recSelect(result, (TypedContainer)obj, activeClass);
      }
    }
  }
  private void emitChanged()
  {
    TypedMap map=new TypedMap();
    map.putString("request", "WorldModelChanged");
    publisher.send(new Message(map));
  }

  private synchronized void readFile()
  {
    db=null;
    try
    {
      FileReader fr=new FileReader(conf.filename);
      XMLDeserializer deser=new XMLDeserializer();
      db=(TypedMap)deser.deserializeObject(fr);
    }
    catch(FileNotFoundException x)
    {
    }
    catch(Exception x)
    {
      log.exception(x);
    }
    if (db==null)
      db=new TypedMap();
  }

  private synchronized void commitFile()
  {
    try
    {
      FileWriter fw=new FileWriter(conf.filename);
      XMLFormatter w=new XMLFormatter(fw);
      w.write("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n");
      XMLSerializer ser=new XMLSerializer();
      w.write(ser.serializeMap("db-file", db, XSI_NS_DECL));
      w.close();
    }
    catch(Exception x)
    {
      x.printStackTrace();
    }
  }
    
  private Publisher publisher;
  private TypedMap db;
  private Config conf;
  private Logger log=Logger.getLogger("wmstore");
  private static final String XSI_NS_DECL="xmlns:xsi=\"http://www.w3.org/1999/XMLSchema-instance\"";
}
