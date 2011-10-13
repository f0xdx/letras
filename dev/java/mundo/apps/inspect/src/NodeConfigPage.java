import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.mundo.rt.TypedMap;
import org.mundo.rt.AsyncCall;
import org.mundo.rt.DoObject;
import org.mundo.service.DebugService;
import org.mundo.service.DoDebugService;
import org.mundo.xml.XMLFormatter;
import org.mundo.xml.XMLSerializer;

class NodeConfigPage extends Page implements AsyncCall.IResultListener
{
  NodeConfigPage(DoDebugService ds)
  {
    doDebugService=ds;
  }
  @Override
  String getURI()
  {
    return "node-config:"+doDebugService._getPublisher().getChannel().getName();
  }
  @Override
  void load()
  {
    AsyncCall ac=doDebugService.getNodeConfig(DoObject.CREATEONLY);
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
      window.showComponent(new JScrollPane(new JTextArea(
            XMLFormatter.format(new XMLSerializer().serializeMap("object", (TypedMap)c.getObj())))));
    }
    catch(Exception x)
    {
      window.showException(x);
    }
  }
  private DoDebugService doDebugService;
}
