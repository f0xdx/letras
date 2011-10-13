package org.mundo.service.lbsrule;

import org.mundo.annotation.*;

@mcRemote(className="org.mundo.service.lbsrule.ILBSStatus")
public interface ILBSStatus
{
  @mcMethod
  public void regionEntered(String name);
  @mcMethod
  public void regionExited(String name);
  @mcMethod
  public void commandSpoken(String utterance);
}
