import java.awt.BorderLayout;
import java.io.IOException;
import java.util.Vector;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

public class BuildConfigPanel extends AbstractConfigPanel
{
  public BuildConfigPanel(ConfigItem ci)
  {
    items = new Vector<ConfigItem>();
    addItem(0, ci);
  }
  
  public void initGui() {
	    listBox = new JList(items);
	    listBox.setCellRenderer(new ConfigCellRenderer());
	    listBox.addListSelectionListener(SELECTION_LISTENER);
	    JScrollPane p0 = new JScrollPane(listBox);

	    rightPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JPanel(), new JScrollPane());

	    setLayout(new BorderLayout());
	    add(new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, p0, rightPane), BorderLayout.CENTER);
  }

  public void loadConfig(String fn) throws Exception
  {
    BuildConfigReader bcr = new BuildConfigReader();
    bcr.read(items.get(0), fn);
    listBox.repaint();
  }
  
  public void writeBuildFile() throws IOException
  {
    BuildFileWriter bfw = new BuildFileWriter();
    bfw.write(Configure.LIB_BUILDFILE, items.get(0));
    
    BuildConfigWriter bcw = new BuildConfigWriter();
    bcw.write(Configure.LIB_CONFIG_XML, items.get(0));
  }
}
