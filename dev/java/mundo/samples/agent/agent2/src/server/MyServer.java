package server;

import org.mundo.util.DefaultApplication;

import api.IMyServer;

public class MyServer extends DefaultApplication implements IMyServer
{
  public MyServer()
  {
  }
  public void step1() //IMyServer
  {
    System.out.println("step1");
  }
  public void step2() //IMyServer
  {
    System.out.println("step2");
  }
  public void step3() //IMyServer
  {
    System.out.println("step3");
  }
  public void step4() //IMyServer
  {
    System.out.println("step4");
  }
  public void step5() //IMyServer
  {
    System.out.println("step5");
  }
  public static void main(String args[])
  {
    start(new MyServer());
  }
}

