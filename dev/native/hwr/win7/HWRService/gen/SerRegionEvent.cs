using System;
using MundoCore.Runtime;

namespace org.letras.psi.iregion
{
  partial class RegionEvent : MundoCore.Runtime.IActivate
  {
    public virtual void passivate(MundoCore.Runtime.TypedMap m)
    {
      m.SetActiveClassName("org.letras.psi.iregion.RegionEvent");
      m.PutInt("type", this.type);
      m.PutPassivated("guid", this.guid);
      m.PutString("penID", this.penID);
    }
    public virtual void activate(MundoCore.Runtime.TypedMap m)
    {
      m.GetInt("type", out this.type);
      this.guid=(GUID)m.GetActivated("guid");
      m.GetString("penID", out this.penID);
    }
  }
}