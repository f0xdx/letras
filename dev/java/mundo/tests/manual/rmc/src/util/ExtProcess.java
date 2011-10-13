package util;

import java.io.File;
import java.io.InputStream;
import org.mundo.rt.GUID;

public class ExtProcess extends Thread
{
  public ExtProcess(String className)
  {
    cmdline="java -cp ../../../lib/mundocore.jar"+File.pathSeparatorChar+"bin "+className;
  }
  public void run()
  {
    try
    {
      firstLine=true;
      proc=Runtime.getRuntime().exec(cmdline);
      InputStream is=proc.getInputStream();
      InputStream eis=proc.getErrorStream();
      boolean linestart=false;
      int c;
      for(;;)
      {
        c=0;
        if (is.available()>0)
        {
          c=is.read();
          line.append((char)c);
          if (c=='\r' && firstLine)
          {
            String s=line.toString();
            if (s.length()==33)
            {
              try
              {
                nodeId=GUID.parse(s.substring(0, 32));
                line=new StringBuffer();
              }
              catch(Exception x)
              {
              }
            }
            firstLine=false;
          }
          if (c=='\n')
          {
            flushStdout();
            firstLine=false;
          }
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
          Thread.sleep(500);
        }
        else if (c==-1)
          break;
      }
      if (line.length()>0)
        flushStdout();
      if (errLine.length()>0)
        flushStderr();
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
      exitValue=proc.exitValue();
    }
    catch(IllegalThreadStateException x)
    {
      return true;
    }
    return false;
  }
  private void flushStdout()
  {
    System.out.print("out: "+line);
    line=new StringBuffer();
  }
  private void flushStderr()
  {
    System.out.print("err: "+errLine);
    errLine=new StringBuffer();
  }
  private boolean firstLine;
  private Process proc;
  private StringBuffer line=new StringBuffer();
  private StringBuffer errLine=new StringBuffer();
  private String cmdline;
  private int exitValue;
  private GUID nodeId;
}
