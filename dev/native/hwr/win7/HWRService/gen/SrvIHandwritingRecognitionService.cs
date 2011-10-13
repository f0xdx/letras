using System;
using MundoCore.Runtime;

/**
 * Automatically generated server stub for <code>IHandwritingRecognitionService</code>
 * @see org.letras.psi.semantic.IHandwritingRecognitionService
 */
namespace org.letras.psi.semantic
{
  public class SrvIHandwritingRecognitionService : MundoCore.Runtime.SrvObject
  {
    public SrvIHandwritingRecognitionService()
    {
    }
    private static MundoCore.Runtime.SrvObject _obj;
    public static MundoCore.Runtime.SrvObject _GetObject()
    {
      if (_obj==null)
      {
        _obj=new SrvIHandwritingRecognitionService();
      }
      return _obj;
    }
    public override void Invoke(Object o, MundoCore.Runtime.TypedMap m, MundoCore.Runtime.TypedMap r)
    {
      string n = m.GetString("request");
      string t = m.GetString("ptypes");
      IHandwritingRecognitionService p=(IHandwritingRecognitionService)o;
      try
      {
        if (n=="StartRecognitionFor" && t=="s")
        {
          p.StartRecognitionFor(m.GetString("p0"));
          return;
        }
        if (n=="StartRecognitionFor" && t=="s,i")
        {
          r.PutObject("value", p.StartRecognitionFor(m.GetString("p0"), m.GetInt("p1")));
          return;
        }
        if (n=="StopRecognitionFor" && t=="s")
        {
          p.StopRecognitionFor(m.GetString("p0"));
          return;
        }
        if (n=="_getMethods" && t=="")
        {
          r.PutString("value",
          "v StartRecognitionFor(s)\n"+
          "boolean StartRecognitionFor(s,i)\n"+
          "v StopRecognitionFor(s)\n"+
          "");
          return;
        }
      }
      catch(Exception x)
      {
        ExceptionOccured(x, o, n, m, s);
      }
    }
  }
}