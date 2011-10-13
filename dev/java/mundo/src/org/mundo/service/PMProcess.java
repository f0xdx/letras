/*
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is MundoCore Java.
 *
 * The Initial Developer of the Original Code is Telecooperation Group,
 * Department of Computer Science, Darmstadt University of Technology.
 * Portions created by the Initial Developer are
 * Copyright (C) 2001-2008 the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 * Erwin Aitenbichler
 */

package org.mundo.service;

import java.io.InputStream;
import java.io.OutputStream;
import org.mundo.rt.GUID;
import org.mundo.rt.Publisher;
import org.mundo.rt.Channel;
import org.mundo.rt.Signal;
import org.mundo.service.DoDebugService;

class PMProcess implements IProcess
{
  PMProcess(ProcessMonitor mgr, String name, String cmdline)
  {
    processMonitor=mgr;
    this.name=name;
    this.cmdline=cmdline;
    Channel ch=processMonitor.getSession().getChannel("lan", new GUID().toString());
    emit=new DoIProcessSignal(ch);
  }
  public String getSignalChannel() // IProcess
  {
    return emit._getPublisher().getChannel().getName();
  }
  public String getName() // IProcess
  {
    return name;
  }
  public GUID getNodeId() // IProcess
  {
    return nodeId;
  }
  public boolean shutdownNode() // IProcess
  {
    if (nodeId==null)
      return false;
    DoDebugService ds = new DoDebugService();
    Signal.connect(ds, processMonitor.getSession().publish("lan", nodeId.toString()+".DebugService"));
    ds.shutdownNode(ds.ONEWAY);
    Signal.disconnect(ds);
    return true;
  }
  void start()
  {
    thread=new ExecThread();
    thread.start();
  }
  void threadTerminated()
  {
    thread=null;
    emit.processTerminated(emit.ONEWAY);
    processMonitor.getSession().unpublish(emit._getPublisher());
    processMonitor.processTerminated(this);
  }
  void nodeFound()
  {
    emit.processIdentified(nodeId, emit.ONEWAY);
  }

  /**
   * StdinThread writes the specified byte array to a stream and then closes
   * the stream. Since the consuming application might block, StdinThread is
   * useful to feed the consumer in parallel.
   */
  private class StdinThread extends Thread
  {
    StdinThread(OutputStream s, byte[] b)
    {
      os=s;
      buffer=b;
    }
    public void run()
    {
      try
      {
        os.write(buffer);
        os.close();
      }
      catch(Exception x)
      {
        x.printStackTrace();
      }
    }
    OutputStream os;
    byte[] buffer;
  }

  /**
   * ExecThread starts a new process and communicates with it via stdin, stdout
   * and stderr. The thread lives as long as the process to monitor.
   */
  private class ExecThread extends Thread
  {
    /**
     * Initializes a new ExecThread.
     */
    ExecThread()
    {
    }
    public void run()
    {
      try
      {
        firstLine=true;
        proc=Runtime.getRuntime().exec(cmdline);
        InputStream is=proc.getInputStream();
        InputStream eis=proc.getErrorStream();
        if (stdin!=null)
          new StdinThread(proc.getOutputStream(), stdin).start();
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
                  nodeFound();
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
/*
        if (outPublisher!=null)
        {
          TypedMap map=new TypedMap();
          map.putString("request", "ProcessTerminated");
          map.putString("name", name);
          map.putInt("returnCode", proc.exitValue());
          outPublisher.send(new Message(map));
          System.out.println("ProcessTerminated>");
          session.unpublish(outPublisher);
        }
*/
      }
      catch(Exception x)
      {
        x.printStackTrace();
      }
      threadTerminated();
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
      emit.processOutput(line.toString(), 1, emit.ONEWAY);
      System.out.print("out: "+line);
      line=new StringBuffer();
    }
    private void flushStderr()
    {
      emit.processOutput(line.toString(), 2, emit.ONEWAY);
      System.out.print("err: "+errLine);
      errLine=new StringBuffer();
    }
    boolean firstLine;
    Process proc;
    StringBuffer line=new StringBuffer();
    StringBuffer errLine=new StringBuffer();
  }

  private ProcessMonitor processMonitor;
  private ExecThread thread;
  private String cmdline;
  private byte[] stdin;
  private int exitValue;
  private GUID nodeId;
  private DoIProcessSignal emit;
  private String name;
}
