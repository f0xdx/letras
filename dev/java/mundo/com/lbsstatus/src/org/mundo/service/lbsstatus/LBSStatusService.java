package org.mundo.service.lbsstatus;

import java.util.List;

import org.mundo.annotation.mcField;
import org.mundo.annotation.mcSerialize;
import org.mundo.rt.Logger;
import org.mundo.rt.Service;
import org.mundo.rt.Signal;
import org.mundo.service.lbsrule.ILBSStatus;

public class LBSStatusService extends Service implements ILBSStatus
{
  @mcSerialize
  public static class Config
  {
    @mcField(name="status-channel",optional=true)
    public String statusChannel;
    @mcField(name="window-width",optional=true)
    public Integer windowWidth;
    @mcField(name="window-y",optional=true)
    public Integer windowY;
  }

  @Override
  public void init()
  {
    super.init();
    if (cfg==null)
      cfg = new Config();
    if (cfg.windowWidth==null)
      cfg.windowWidth = new Integer(400);
    if (cfg.windowY==null)
      cfg.windowY = new Integer(0);
    frame = new StatusFrame(cfg.windowWidth.intValue(), cfg.windowY.intValue());
    frame.setUndecorated(true);
    frame.setAlwaysOnTop(true);
    frame.setVisible(true);
    if (cfg.statusChannel!=null)
      Signal.connect(session.subscribe("lan", cfg.statusChannel), this);
  }
  @Override
  public void shutdown()
  {
    frame.dispose();
    super.shutdown();
  }
  @Override
  public void setServiceConfig(Object obj)
  {
    try
    {
      cfg=(Config)obj;
      log.info("setConfig: "+cfg.toString());
    }
    catch(ClassCastException x)
    {
      x.printStackTrace();
    }
  }
  @Override
  public Object getServiceConfig()
  {
    return cfg;
  }
  /**
   * @param name
   * @see org.mundo.service.lbsrule.ILBSStatus
   */
  public void regionEntered(String name)
  {
    frame.highlight(true);
  }
  /**
   * @param name
   * @see org.mundo.service.lbsrule.ILBSStatus
   */
  public void regionExited(String name)
  {
    frame.highlight(false);
  }
  /**
   * @param utterance
   * @see org.mundo.service.lbsrule.ILBSStatus
   */
  public void commandSpoken(String utterance)
  {
    frame.setText(utterance);
  }

  private StatusFrame frame;
  private Config cfg;
  private Logger log = Logger.getLogger("lbsstatus");
}
