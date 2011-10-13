package org.mundo.service.scale;

import org.mundo.annotation.*;

@mcSerialize
public class ScaleEvent
{
  public double weight;
  public double tolerance;
  public boolean stable;
  public String unit;
}
