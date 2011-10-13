import java.io.StringWriter;

import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

class ExecCommand extends Command
{
  public void run(Context ctx, Node node) throws Exception
  {
    NamedNodeMap attrMap = node.getAttributes();
    String name = null;
    String cmdline = null;
    byte[] stdin = null;
    try {
      name = attrMap.getNamedItem("name").getNodeValue();
    } catch(NullPointerException x) {
      throw new Exception("attribute name expected");
    }
    try {
      cmdline = attrMap.getNamedItem("cmdline").getNodeValue();
    } catch(NullPointerException x) {
      throw new Exception("attribute cmdline expected");
    }
    
    node = node.getFirstChild();
    while (node!=null)
    {
      if (node.getNodeType()==Node.ELEMENT_NODE)
      {
        StringWriter sw = new StringWriter();
        TransformerFactory.newInstance().newTransformer().transform(new DOMSource(node), new StreamResult(sw));
        stdin = sw.toString().getBytes();
        break;
      }
      node=node.getNextSibling();
    }
    
    ExecThread t = new ExecThread(ctx, name, ctx.expandVars(cmdline), stdin);
    ctx.execThreads.add(t);
    t.start();
  }
}
