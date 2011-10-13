package agent;

import org.mundo.annotation.mcSerialize;
import org.mundo.service.Node;
import org.mundo.agent.Agent;

import api.IMyAgent;

@mcSerialize
public class MyAgent extends Agent implements IMyAgent
{
  public void sayHello()
  {
    System.out.println("*** Hello from " + Node.thisNode().getName());
  }
}
