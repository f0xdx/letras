using System;
using MundoCore.Runtime;

namespace org.letras.psi.semantic
{
  partial class HandwritingRecognitionResult : MundoCore.Runtime.IActivate
  {
    public virtual void passivate(MundoCore.Runtime.TypedMap m)
    {
      m.SetActiveClassName("org.letras.psi.semantic.HandwritingRecognitionResult");
      m.PutString("topResult", this.topResult);
      m.PutPassivated("traceGUID", this.traceGUID);
      m.PutString("penId", this.penId);
      m.PutPassivated("alternateResults", this.alternateResults);
    }
    public virtual void activate(MundoCore.Runtime.TypedMap m)
    {
      m.GetString("topResult", out this.topResult);
      this.traceGUID=(org.mundo.rt.TypedArray)m.GetActivated("traceGUID");
      m.GetString("penId", out this.penId);
      this.alternateResults=(org.mundo.rt.TypedArray)m.GetActivated("alternateResults");
    }
  }
}