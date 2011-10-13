package org.mundo.service.autohotkey;

import java.io.FileOutputStream;
import java.io.IOException;

import org.mundo.annotation.*;
import org.mundo.rt.Logger;
import org.mundo.rt.Service;
import org.mundo.rt.Signal;

@mcRemote(className="org.mundo.service.autohotkey.AHKService")
public class AHKService extends Service
{
  @mcSerialize
  public static class Config
  {
    /**
     * Export service interface to this channel.
     */
    public String channel;
    /**
     * Path to AutoHotkey.exe.
     */
    public String ahk;
    /**
     * Filename of temporary file.
     */
    @mcField(name="temp-file")
    public String tempFile;
  }

  public AHKService()
  {
  }
  @Override
  public void init()
  {
    super.init();
    log.info("init");
    Signal.connect(session.subscribe("lan", conf.channel), this);
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
  public void exec(String script) throws IOException
  {
    log.info("exec: "+script);

    FileOutputStream fos=new FileOutputStream(conf.tempFile);
    fos.write(script.getBytes());
    fos.close();

    Runtime.getRuntime().exec(conf.ahk+" "+conf.tempFile);
  }

  private Config conf;
  private Logger log=Logger.getLogger("ahk");
}
