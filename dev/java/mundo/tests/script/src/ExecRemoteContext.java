import org.mundo.rt.IReceiver;
import org.mundo.rt.Message;
import org.mundo.rt.MessageContext;
import org.mundo.rt.Subscriber;
import org.mundo.rt.TypedMap;

class ExecRemoteContext implements IReceiver
{
  ExecRemoteContext(Context c, String outChannelName)
  {
    ctx = c;
    outSubscriber = ctx.session.subscribe("lan", outChannelName);
    outSubscriber.setReceiver(this);
    outSubscriber.enable();
  }
  public void received(Message msg, MessageContext mctx)
  {
    String req=msg.getMap().getString("request");
    if (req.equals("ProcessOutput"))
    {
      TypedMap map=msg.getMap();
      System.out.print(map.getString("name")+"."+map.getString("stream")+":"+
                       map.getString("text"));
    }
    else if (req.equals("ProcessStarted"))
    {
      TypedMap map=msg.getMap();
      System.out.println(map.getString("name")+": STARTED");
      ctx.startedCnt++;
    }
    else if (req.equals("ProcessTerminated"))
    {
      TypedMap map=msg.getMap();
      int v=map.getInt("returnCode");
      System.out.print(map.getString("name")+": ");
      if (v==0)
        System.out.println("OK");
      else
      {
        System.out.println("FAILED ("+v+")");
        ctx.returnCode = v;
      }
      ctx.session.unsubscribe(outSubscriber);
      ctx.execCnt--;
    }
    else
    {
      System.out.println("ExecRemoteCommand: received unknown reply "+req);
    }
  }
  Context ctx;
  Subscriber outSubscriber;
}
