using System;
using MundoCore.Runtime;

/**
 * Automatically generated distributed object class for <code>IHandwritingRecognitionService</code>.
 * @see org.letras.psi.semantic.IHandwritingRecognitionService
 */
namespace org.letras.psi.semantic
{
  public class DoIHandwritingRecognitionService extends org.mundo.rt.ClientStub implements org.letras.psi.semantic.IHandwritingRecognitionService
  {
    public DoIHandwritingRecognitionService()
    {
    }
    public DoIHandwritingRecognitionService(org.mundo.rt.Session session, Object obj) throws org.mundo.rt.RMCException
    {
      _bind(session, obj);
    }
    public DoIHandwritingRecognitionService(org.mundo.rt.Channel channel) throws org.mundo.rt.RMCException
    {
      _setPublisher(channel.getSession().publish(channel.getZone(), channel.getName()));
    }
    public DoIHandwritingRecognitionService(org.mundo.rt.DoObject o)
    {
      _assign(o);
    }
    public org.mundo.rt.ServerStub _getServerStub()
    {
      return SrvIHandwritingRecognitionService._getObject();
    }
    public static DoIHandwritingRecognitionService _of(org.mundo.rt.Session session, Object obj)
    {
      DoIHandwritingRecognitionService cs=(DoIHandwritingRecognitionService)_getClientStub(session, DoIHandwritingRecognitionService.class, obj);
      if (cs==null)
      {
        cs=new DoIHandwritingRecognitionService(session, obj);
        _putClientStub(session, obj, cs);
      }
      return cs;
    }
    public static DoIHandwritingRecognitionService _of(org.mundo.rt.Service s)
    {
      return _of(s.getSession(), s);
    }
    public String _getInterfaceName()
    {
      return "org.letras.psi.semantic.IHandwritingRecognitionService";
    }
    public static IHandwritingRecognitionService _localObject(IHandwritingRecognitionService obj)
    {
      if (obj instanceof org.mundo.rt.ClientStub)
      {
        return (IHandwritingRecognitionService)((org.mundo.rt.ClientStub)obj)._getLocalObject();
      }
      else
      {
        return obj;
      }
    }
    public void StartRecognitionFor(string p0)
    {
      if (localObj!=null) 
      {
        ((org.letras.psi.semantic.IHandwritingRecognitionService)localObj).StartRecognitionFor(p0);
        return;
      }
      StartRecognitionFor(p0, SYNC);
    }
    public org.mundo.rt.AsyncCall StartRecognitionFor(string p0, Options opt) 
    {
      org.mundo.rt.TypedMap m=new org.mundo.rt.TypedMap();
      m.putString("ptypes", "s");
      m.putString("rtype", "");
      m.PutString("p0", p0);
      org.mundo.rt.AsyncCall call=new org.mundo.rt.AsyncCall(this, "org.letras.psi.semantic.IHandwritingRecognitionService", "StartRecognitionFor", m);
      if (opt==ONEWAY)
      {
        call.invokeOneWay();
      }
      else if (opt==SYNC || opt==ASYNC)
      {
        call.invoke();
      }
      if (opt==SYNC)
      {
        try
        {
          if (!call.waitForReply())
          {
            throw call.getException();
          }
        }
        catch(RuntimeException x)
        {
          throw x;
        }
        catch(Exception x)
        {
          throw new org.mundo.rt.RMCException("unexpected exception", x);
        }
      }
      return call;
    }
    
    public boolean StartRecognitionFor(string p0, int p1)
    {
      if (localObj!=null) 
      {
        return ((org.letras.psi.semantic.IHandwritingRecognitionService)localObj).StartRecognitionFor(p0, p1);
      }
      org.mundo.rt.AsyncCall call=StartRecognitionFor(p0, p1, SYNC);
      return (boolean)call.getObj();
    }
    public org.mundo.rt.AsyncCall StartRecognitionFor(string p0, int p1, Options opt) 
    {
      org.mundo.rt.TypedMap m=new org.mundo.rt.TypedMap();
      m.putString("ptypes", "s,i");
      m.putString("rtype", "boolean");
      m.PutString("p0", p0);
      m.PutInt("p1", p1);
      org.mundo.rt.AsyncCall call=new org.mundo.rt.AsyncCall(this, "org.letras.psi.semantic.IHandwritingRecognitionService", "StartRecognitionFor", m);
      if (opt==ONEWAY)
      {
        call.invokeOneWay();
      }
      else if (opt==SYNC || opt==ASYNC)
      {
        call.invoke();
      }
      if (opt==SYNC)
      {
        try
        {
          if (!call.waitForReply())
          {
            throw call.getException();
          }
        }
        catch(RuntimeException x)
        {
          throw x;
        }
        catch(Exception x)
        {
          throw new org.mundo.rt.RMCException("unexpected exception", x);
        }
      }
      return call;
    }
    
    public void StopRecognitionFor(string p0)
    {
      if (localObj!=null) 
      {
        ((org.letras.psi.semantic.IHandwritingRecognitionService)localObj).StopRecognitionFor(p0);
        return;
      }
      StopRecognitionFor(p0, SYNC);
    }
    public org.mundo.rt.AsyncCall StopRecognitionFor(string p0, Options opt) 
    {
      org.mundo.rt.TypedMap m=new org.mundo.rt.TypedMap();
      m.putString("ptypes", "s");
      m.putString("rtype", "");
      m.PutString("p0", p0);
      org.mundo.rt.AsyncCall call=new org.mundo.rt.AsyncCall(this, "org.letras.psi.semantic.IHandwritingRecognitionService", "StopRecognitionFor", m);
      if (opt==ONEWAY)
      {
        call.invokeOneWay();
      }
      else if (opt==SYNC || opt==ASYNC)
      {
        call.invoke();
      }
      if (opt==SYNC)
      {
        try
        {
          if (!call.waitForReply())
          {
            throw call.getException();
          }
        }
        catch(RuntimeException x)
        {
          throw x;
        }
        catch(Exception x)
        {
          throw new org.mundo.rt.RMCException("unexpected exception", x);
        }
      }
      return call;
    }
    
  }
}