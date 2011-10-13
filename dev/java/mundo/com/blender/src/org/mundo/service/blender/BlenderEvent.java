package org.mundo.service.blender;

import org.mundo.annotation.*;

@mcSerialize
public class BlenderEvent
{
  public int event;
  
  public static final int POWER_ON  = 1;
  public static final int POWER_OFF = 2;
  public static final int MOTOR_ON  = 3;
  public static final int MOTOR_OFF = 4;
}
