import javax.swing.JComponent;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;

class RootPage extends Page
{
  RootPage()
  {
  }
  @Override
  String getURI()
  {
    return "root:";
  }
  @Override
  void load()
  {
    window.pageLoaded(this);
    window.showComponent(getContent());
  }
  static JComponent getContent()
  {
    return new JScrollPane(new JTextArea("no items to show in this view"));
  }
}
