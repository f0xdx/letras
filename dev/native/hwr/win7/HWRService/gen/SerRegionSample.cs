using System;
using MundoCore.Runtime;

namespace org.letras.psi.iregion
{
  partial class RegionSample : MundoCore.Runtime.IActivate
  {
    public virtual void passivate(MundoCore.Runtime.TypedMap m)
    {
      m.SetActiveClassName("org.letras.psi.iregion.RegionSample");
      m.PutDouble("pscX", this.pscX);
      m.PutInt("force", this.force);
      m.PutDouble("pscY", this.pscY);
      m.PutDouble("x", this.x);
      m.PutDouble("y", this.y);
      m.PutString("penID", this.penID);
      m.PutLong("timestamp", this.timestamp);
    }
    public virtual void activate(MundoCore.Runtime.TypedMap m)
    {
      m.GetDouble("pscX", out this.pscX);
      m.GetInt("force", out this.force);
      m.GetDouble("pscY", out this.pscY);
      m.GetDouble("x", out this.x);
      m.GetDouble("y", out this.y);
      m.GetString("penID", out this.penID);
      m.GetLong("timestamp", out this.timestamp);
    }
  }
}