import java.io.InputStream;

import org.mundo.rt.Message;
import org.mundo.rt.Publisher;
import org.mundo.rt.TypedMap;

class ExecThread extends Thread
{
  ExecThread(Context c, String n, String l, byte[] s)
  {
    ctx=c;
    name=n;
    cmdline=l;
    stdin=s;
    ctx.execCnt++;
    ctx.totalCnt++;
  }
  ExecThread(Context c, String n, String l)
  {
    ctx=c;
    name=n;
    cmdline=l;
    ctx.execCnt++;
    ctx.totalCnt++;
  }
  public void run()
  {
    try
    {
      System.out.println(name+": starting: "+cmdline);
      if (outPublisher!=null)
      {
        TypedMap map=new TypedMap();
        map.putString("request", "ProcessStarted");
        map.putString("name", name);
        outPublisher.send(new Message(map));
        System.out.println("ProcessStarted");
      }
      proc=Runtime.getRuntime().exec(cmdline); //"java tests.script.TestScriptService "+script);
      InputStream is = proc.getInputStream();
      InputStream eis = proc.getErrorStream();
      if (stdin!=null)
        new StdinThread(proc.getOutputStream(), stdin).start();
      ctx.startedCnt++;
      boolean linestart=false;
      int c;
      for(;;)
      {
        c=0;
        if (is.available()>0)
        {
          c=is.read();
          line.append((char)c);
          if (c=='\n')
            flushStdout();
        }
        else if (eis.available()>0)
        {
          c=eis.read();
          errLine.append((char)c);
          if (c=='\n')
            flushStderr();
        }
        if (c==0)
        {
          if (!isRunning())
            break;
          Thread.sleep(100);
        }
        else if (c==-1)
          break;
      }
      if (line.length()>0)
        flushStdout();
      if (errLine.length()>0)
        flushStderr();
      if (outPublisher!=null)
      {
        TypedMap map=new TypedMap();
        map.putString("request", "ProcessTerminated");
        map.putString("name", name);
        map.putInt("returnCode", proc.exitValue());
        outPublisher.send(new Message(map));
        System.out.println("ProcessTerminated");
        ctx.session.unpublish(outPublisher);
      }
      ctx.execCnt--;
      System.out.println(name+": terminated");
    }
    catch(Exception x)
    {
      x.printStackTrace();
    }
  }
  private boolean isRunning()
  {
    try
    {
      int v=proc.exitValue();
      if (v!=0)
        ctx.returnCode=v;
    }
    catch(IllegalThreadStateException x)
    {
      return true;
    }
    return false;
  }
  public void kill()
  {
    if (isRunning())
    {
      System.out.println(name+": killing process");
      proc.destroy();
    }
  }
  private void flushStdout()
  {
    if (outPublisher!=null)
    {
      TypedMap map=new TypedMap();
      map.putString("request", "ProcessOutput");
      map.putString("name", name);
      map.putString("stream", "out");
      map.putString("text", line.toString());
      outPublisher.send(new Message(map));
    }
    System.out.print(name+".out: "+line);
    line=new StringBuffer();
  }
  private void flushStderr()
  {
    if (outPublisher!=null)
    {
      TypedMap map=new TypedMap();
      map.putString("request", "ProcessOutput");
      map.putString("name", name);
      map.putString("stream", "err");
      map.putString("text", errLine.toString());
      outPublisher.send(new Message(map));
    }
    System.out.print(name+".err: "+errLine);
    errLine=new StringBuffer();
  }
  private Context ctx;
  Publisher outPublisher;
  private String name;
  private String cmdline;
  private byte[] stdin;
  private Process proc;
  private StringBuffer line=new StringBuffer();
  private StringBuffer errLine=new StringBuffer();
}
