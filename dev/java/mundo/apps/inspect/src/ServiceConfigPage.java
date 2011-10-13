import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.mundo.rt.AsyncCall;
import org.mundo.rt.DoObject;
import org.mundo.rt.TypedMap;
import org.mundo.rt.GUID;
import org.mundo.service.DoIServiceManager;
import org.mundo.service.ServiceInstance;
import org.mundo.xml.XMLDeserializer;
import org.mundo.xml.XMLFormatter;
import org.mundo.xml.XMLSerializer;

public class ServiceConfigPage extends Page implements AsyncCall.IResultListener
{
  ServiceConfigPage(DoIServiceManager sm, GUID sid)
  {
    doServiceManager=sm;
    serviceId=sid;
  }
  @Override
  String getURI()
  {
    return "service-config:"+doServiceManager._getPublisher().getChannel().getName()+
           "/"+serviceId;
  }
  @Override
  void load()
  {
    AsyncCall ac=doServiceManager.getServiceConfig(serviceId, DoObject.CREATEONLY);
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
      JPanel panel=new JPanel();
      panel.setLayout(new BorderLayout());
      textArea=new JTextArea();

      XMLSerializer ser=new XMLSerializer();
      try
      { 
        textArea.setText(XMLFormatter.format(ser.serializeMap("config", (TypedMap)c.getObj())));
      }
      catch(Exception x)
      {
        x.printStackTrace();
      }
      
      panel.add(new JScrollPane(textArea), BorderLayout.CENTER);
      JPanel buttonPanel=new JPanel();
      buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
      JButton btn=new JButton("Get");
      buttonPanel.add(btn);
      btn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent ev) {
          getConfig();
        }
      });
      btn=new JButton("Set");
      buttonPanel.add(btn);
      btn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent ev) {
          setConfig();
        }
      });
      panel.add(buttonPanel, BorderLayout.SOUTH);
      window.showComponent(panel);
    }
    catch(Exception x)
    {
      window.showException(x);
    }      
  }
  
  private void getConfig()
  {
    load();
  }
  
  private void setConfig()
  {
    XMLDeserializer deser=new XMLDeserializer();
    try
    {
      TypedMap map=(TypedMap)deser.deserializeObject(textArea.getText());
      doServiceManager.setServiceConfig(serviceId, map);
    }
    catch(Exception x)
    {
      x.printStackTrace();
    }
  }
 
  private DoIServiceManager doServiceManager;
  private GUID serviceId;
  private JTextArea textArea;
}
