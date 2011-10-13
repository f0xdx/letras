package agent;

import org.mundo.agent.Agent;
import org.mundo.annotation.mcSerialize;
import org.mundo.service.Node;
import org.mundo.rt.Mundo;

import api.IMyAgent;
import api.IMyServer;

@mcSerialize
public class MyAgent extends Agent implements IMyAgent
{
  public void run()
  {
    System.out.println("*** starting at "+Node.thisNode().getName());

    Node[] nodes = Node.getNeighbors();
    if (nodes.length < 1)
      throw new IllegalStateException("no peers present");
    moveTo(nodes[0].getName(), "atServer1");
  }
  public void atServer1()
  {
    System.out.println("*** now at "+Node.thisNode().getName());

    IMyServer srv = (IMyServer)Mundo.getServiceByType(IMyServer.class);
    if (srv == null)
      throw new IllegalStateException("server service not found!");
    srv.step1();
    srv.step2();
    srv.step3();
    srv.step4();
    srv.step5();

    System.out.println("*** leaving "+Node.thisNode().getName());
    moveTo("master", "atMaster");
  }
  public void atMaster()
  {
    System.out.println("*** back at "+Node.thisNode().getName());
  }
}
