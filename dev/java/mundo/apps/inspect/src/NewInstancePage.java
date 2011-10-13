import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import org.mundo.rt.AsyncCall;
import org.mundo.rt.DoObject;
import org.mundo.rt.GUID;
import org.mundo.rt.TypedMap;
import org.mundo.rt.AsyncCall.IResultListener;
import org.mundo.service.DoDebugService;
import org.mundo.service.DoIServiceManager;
import org.mundo.xml.XMLDeserializer;
import org.mundo.xml.XMLFormatter;
import org.mundo.xml.XMLSerializer;

class NewInstancePage extends Page implements IResultListener
{
  NewInstancePage(DoDebugService ds, DoIServiceManager sm, String cn)
  {
    doDebugService=ds;
    doServiceManager=sm;
    className=cn;
  }
  @Override
  String getURI()
  {
    return "new-instance:"+doServiceManager._getPublisher().getChannel().getName()+
           "/"+className;
  }
  @Override
  void load()
  {
    window.pageLoaded(this);
    JPanel gridPane=new JPanel();

    gridPane.add(new JLabel("className"));
    JTextField fld=new JTextField(className);
    fld.setEditable(false);
    gridPane.add(fld);

    gridPane.add(new JLabel("instanceName"));
    tfInstanceName=new JTextField();
    gridPane.add(tfInstanceName);
    
    gridPane.add(new JLabel("Configuration:"));
    gridPane.add(new JLabel());
    
    gridPane.setLayout(new SpringLayout());
    SpringUtilities.makeCompactGrid(gridPane, 3, 2, 8, 4, 8, 2);

    taConfig=new JTextArea(
        "<config xsi:type=\"map\" activeClass=\""+className+"$Config\">\n"+
        "</config>");

    JPanel buttonPane=new JPanel();
    buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.X_AXIS));
    JButton btn;
    buttonPane.add(btn=new JButton("New"));
    btn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ev) {
        newPressed();
      }
    });
    
    JPanel pane=new JPanel();
    pane.setLayout(new BorderLayout());
    pane.add(gridPane, BorderLayout.NORTH);
    pane.add(taConfig, BorderLayout.CENTER);
    pane.add(buttonPane, BorderLayout.SOUTH);

    window.showComponent(pane);
  }
  
  private void newPressed()
  {
    try
    {
      XMLDeserializer deser=new XMLDeserializer();
      TypedMap config=(TypedMap)deser.deserializeObject(taConfig.getText());
      AsyncCall call=doServiceManager.newInstance(className,
          tfInstanceName.getText(), config, DoObject.CREATEONLY);
      call.setResultListener(this);
      call.invoke();
    }
    catch(Exception x)
    {
      x.printStackTrace();
    }
  }
  
  public void resultReceived(AsyncCall c)
  {
    window.open(new ServiceInstancesPage(doDebugService, doServiceManager));
  }

  private DoDebugService doDebugService;
  private DoIServiceManager doServiceManager;
  private String className;
  private JTextField tfInstanceName;
  private JTextArea taConfig;
}
