import java.io.File;
import java.io.FileReader;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import org.mundo.rt.IReceiver;
import org.mundo.rt.Blob;
import org.mundo.rt.Message;
import org.mundo.rt.MessageContext;
import org.mundo.rt.Mundo;
import org.mundo.rt.Service;
import org.mundo.rt.TypedMap;
import org.mundo.rt.Logger;

public class TestScriptService extends Service implements IReceiver
{
  TestScriptService()
  {
    cmdHash.put("print", new PrintCommand());
    cmdHash.put("subscribe", new SubscribeCommand());
    cmdHash.put("unsubscribe", new UnsubscribeCommand());
    cmdHash.put("advertise", new AdvertiseCommand());
    cmdHash.put("unadvertise", new UnadvertiseCommand());
    cmdHash.put("send", new SendCommand());
    cmdHash.put("sendMultipart", new SendMultipartCommand());
    cmdHash.put("call", new CallCommand());
    cmdHash.put("execRemote", new ExecRemoteCommand());
    cmdHash.put("expect", new ExpectCommand());
    cmdHash.put("sleep", new SleepCommand());
    cmdHash.put("pause", new PauseCommand());
    cmdHash.put("timeout", new TimeoutCommand());
    cmdHash.put("exec", new ExecCommand());
    cmdHash.put("print-nodeinfo", new PrintNodeInfoCommand());
    cmdHash.put("commitSubscriptions", new CommitSubscriptionsCommand());
  }
  @Override
  public void init()
  {
    super.init();
    ctx = new Context();
    ctx.log = log;
    ctx.session = session;
    ctx.vars.put("java-interpreter", "java -jar mtsi.jar");
  }
  private void cleanup()
  {
    for (ExecThread t : ctx.execThreads)
      t.kill();
  }

  public boolean run(Document doc) throws Exception
  {
    try
    {
      cfg = Mundo.getConfig().getMap("TestScript");
    }
    catch(NoSuchElementException x)
    {
      cfg = new TypedMap();
    }
    try
    {
      ctx.vars.putAll(cfg.getMap("variables"));
    }
    catch(NoSuchElementException x)
    {
    }

    Node node = doc.getFirstChild();
    if (!node.getNodeName().equals("TestScript"))
      throw new Exception("TestScript expected");
    node = node.getFirstChild();
      
    while (node!=null)
    {
      if (node.getNodeType()==Node.ELEMENT_NODE)
      {
        Command cmd=(Command)cmdHash.get(node.getNodeName());
        if (cmd==null)
          throw new Exception("unknown command "+node.getNodeName());
        try
        {
          cmd.run(ctx, node);
        }
        catch(Exception x)
        {
          System.out.println("FAILED: "+x);
          x.printStackTrace();
          ctx.returnCode = 1;
          cleanup();
          return true;
        }
      }
      node=node.getNextSibling();
    }

    while (ctx.expectCnt>0 && ctx.timeout-->0)
    {
//        System.out.print(".");
      Thread.sleep(100);
    }
    if (ctx.expectCnt>0)
    {
      System.out.println("FAILED: timeout waiting for expected messages");
      
      Iterator<SubscriptionEntry> iter = ctx.subscribers.values().iterator();
      while (iter.hasNext())
      {
        SubscriptionEntry se = iter.next();
        se.printMissing(System.out);
      }
      
      ctx.returnCode = 1;
      cleanup();
      return true;
    }
//      else
//        System.out.println("TSS: got all expected messages");
    while (ctx.execCnt>0 && ctx.timeout-->0)
    {
//        System.out.print(".");
      Thread.sleep(100);
    }
    if (ctx.execCnt>0)
    {
      System.out.println("FAILED: timeout waiting for child processes " +
                         "(startedCnt="+ctx.startedCnt+")");
      ctx.returnCode = 1;
      cleanup();
      return true;
    }
    if (ctx.totalCnt > ctx.startedCnt)
    {
      System.out.println("FAILED: more processes reported success than started " +
                         "(totalCnt="+ctx.totalCnt+", startedCnt="+ctx.startedCnt+")");
      ctx.returnCode = 1;
      cleanup();
      return true;
    }
    System.out.println("OK");
    returnCode = ctx.returnCode;
    return true;
  }

  public boolean run(InputSource is)
  {
    try
    {
      DocumentBuilderFactory domFactory=DocumentBuilderFactory.newInstance();
      DocumentBuilder domBuilder=domFactory.newDocumentBuilder();
      Document doc=domBuilder.parse(is);
      return run(doc);
    }
    catch(Exception x)
    {
      x.printStackTrace();
      return false;
    }
  }

  /**
   * @return  false if the test script has errors, true otherwise. returnCode
   *          indicates if the test was successful.
   */
  public boolean run(String filename)
  {
    try
    {
      if (filename==null)
        return run(new InputSource(System.in));
      return run(new InputSource(new FileReader(new File(filename))));
    }
    catch(Exception x)
    {
      x.printStackTrace();
    }
    return false;
  }

  public void runSlave(String channelName)
  {
    try
    {
      cfg = Mundo.getConfig().getMap("TestScript");
    }
    catch(NoSuchElementException x)
    {
      cfg = new TypedMap();
    }
    try
    {
      ctx.vars.putAll(cfg.getMap("variables"));
    }
    catch(NoSuchElementException x)
    {
    }

    session.subscribe("lan", channelName, this);
    System.out.println("hit enter to exit");
    try {
      new BufferedReader(new InputStreamReader(System.in)).readLine();
      return;
    } catch(IOException x) {
      x.printStackTrace();
      log.exception(x);
    }
    // If we get an IOException this usually means that there is no console.
    // Thus, we simply wait forever.
    // FIXME: An exit command could be added to the interpreter.
    try
    {
      for(;;)
      {
        Thread.sleep(10000);
      }
    }
    catch(Exception x)
    {
      log.exception(x);
    }
  }

  public void received(Message msg, MessageContext mctx) // IReceiver
  {
    if ("Run".equals(msg.getMap().getString("request")))
    {
      TypedMap map=msg.getMap();
      String name;
      String cmdline;
      String outChannel;
      try {
        name=ctx.expandVars(map.getString("name"));
      } catch(Exception x) {
        name=null;
      }
      try {
        cmdline=ctx.expandVars(map.getString("cmdline"));
        System.out.println(cmdline);
      } catch(Exception x) {
        System.err.println("<cmdline> expected");
        return;
      }
      try {
        outChannel=map.getString("outChannel");
      } catch(Exception x) {
        outChannel=null;
      }
      try
      {
        System.out.println(cmdline);
        Blob blob=msg.getBlob("stdin", "bin");
        ExecThread t;
        if (blob!=null)
          t = new ExecThread(ctx, name, cmdline, blob.getBuffer());
        else
          t = new ExecThread(ctx, name, cmdline);
        if (outChannel!=null)
          t.outPublisher = session.publish("lan", outChannel);
        t.start();
//        run(new InputSource(new ByteArrayInputStream(msg.getChunk(1).getBuffer())));
      }
      catch(Exception x)
      {
        x.printStackTrace();
      }
    }
    else if ("Put".equals(msg.getMap().getString("request")))
    {
      TypedMap map=msg.getMap();
      String filename;
      try {
        filename=ctx.expandVars(map.getString("filename"));
      } catch(Exception x) {
        System.out.println("missing tag 'filename'");
        return;
      }
      try
      {
        Blob blob=msg.getBlob("content", "bin");
        if (blob==null)
        {
          System.out.println("missing chunk 'content'");
          return;
        }
        FileOutputStream fos=new FileOutputStream(filename);
        fos.write(blob.getBuffer());
        fos.close();
      }
      catch(Exception x)
      {
        x.printStackTrace();
      }
    }
  }

  public static void main(String args[])
  {
    boolean optLoop=false;
    boolean logToFile=false;
    String srvChannel=null;
    String filename=null;

    for (int i=0; i<args.length; i++)
    {    
      if (args[i].equals("-loop"))
        optLoop=true;
      else if (args[i].equals("-server") || args[i].equals("-slave"))
      {
        srvChannel=args[++i];
        Mundo.setConfigFile("server.conf");
      }
      else if (args[i].equals("-cfg"))
      {
        Mundo.setConfigFile(args[++i]);
      }
      else if (args[i].equals("-logfile"))
      {
        Logger.toFile(args[++i], Logger.FINEST);
        logToFile=true;
      }
      else
        filename=args[i];
    }
    
    Mundo.setNodeName("TestScript");
    Mundo.init();
    log.info("this.vpId = "+Mundo.getNodeId());

    TestScriptService svc = new TestScriptService();
    Mundo.registerService(svc);

    try
    {
      TypedMap cfg=Mundo.getConfig().getMap("TestScript");
      try
      {
        TypedMap cfg2=Mundo.getConfig().getMap("TestScript."+getHostName());
        cfg.putAll(cfg2);
      }
      catch(Exception x) {}
      srvChannel=cfg.getString("channel");
      System.out.println("listening on: "+srvChannel);
      if (!logToFile)
      {
        try
        {
          Logger.toFile(cfg.getString("logfile"), Logger.FINER);
        }
        catch(Exception x) {}
      }
    }
    catch(Exception x) {}

    if (srvChannel!=null)
    {
      svc.runSlave(srvChannel);
      Mundo.shutdown();
      return;
    }
      
    if (optLoop)
    {
      int success=0;
      while (svc.run(filename))
      {
        if (returnCode==0)
          success++;
        else
        {
          System.out.println(success+" successful runs.");
          break;
        }
      }
    }
    else
    {
      svc.run(filename);
    }

    Mundo.shutdown();
//    System.out.println(returnCode==0 ? "OK" : "FAILED");
    System.exit(returnCode);
  }
  
  private static String getHostName()
  {
    try
    {
      return InetAddress.getByName("127.0.0.1").getHostName();
    }
    catch(java.net.UnknownHostException x)
    {
      x.printStackTrace();
    }
    return "";
  }

  private Context ctx;
  private TypedMap cfg;
  private HashMap<String,Command> cmdHash = new HashMap<String,Command>();
  private static int returnCode;
  private static Logger log = Logger.getLogger("tss");
}
