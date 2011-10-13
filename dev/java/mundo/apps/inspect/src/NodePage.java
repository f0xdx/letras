import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.mundo.rt.TypedMap;
import org.mundo.rt.AsyncCall;
import org.mundo.rt.DoObject;
import org.mundo.service.DebugService;
import org.mundo.service.DoDebugService;
import org.mundo.xml.XMLFormatter;
import org.mundo.xml.XMLSerializer;

class NodePage extends Page implements AsyncCall.IResultListener
{
  NodePage(DoDebugService ds)
  {
    doDebugService=ds;
  }
  @Override
  String getURI()
  {
    return "node:"+doDebugService._getPublisher().getChannel().getName();
  }
  @Override
  void load()
  {
    AsyncCall ac=doDebugService.getNodeInfo(DoObject.CREATEONLY);
    ac.setResultListener(this);
    ac.invoke();
  }
  public void resultReceived(AsyncCall c)
  {
    window.pageLoaded(this);
    if (c.getException()!=null)
    {
      window.showException(c.getException());
      return;
    }
    try
    {
      DebugService.NodeInfo ni=(DebugService.NodeInfo)c.getObj();
      window.showComponent(new JScrollPane(new JTextArea(
          XMLFormatter.format(new XMLSerializer().serializeMap("object",
          (TypedMap)TypedMap.passivate(ni))))));
    }
    catch(Exception x)
    {
      window.showException(x);
    }
  }
  private DoDebugService doDebugService;
}
