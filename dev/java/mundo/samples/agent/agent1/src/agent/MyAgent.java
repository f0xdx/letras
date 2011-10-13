package agent;

import org.mundo.agent.Agent;
import org.mundo.annotation.mcSerialize;
import org.mundo.service.Node;

import api.IMyAgent;

@mcSerialize
public class MyAgent extends Agent implements IMyAgent
{
  String name;
  
  public void run(String name)
  {
    this.name = name;
    System.out.println("*** "+name+" starting at "+Node.thisNode().getName());
    moveTo("server1", "atServer1");
  }
  public void atServer1()
  {
    System.out.println("*** "+name+" now at "+Node.thisNode().getName());
    moveTo("server2", "atServer2");
  }
  public void atServer2()
  {
    System.out.println("*** "+name+" now at "+Node.thisNode().getName());
    moveTo("master", "atMaster");
  }
  public void atMaster()
  {
    System.out.println("*** "+name+" back at "+Node.thisNode().getName());
  }
}
