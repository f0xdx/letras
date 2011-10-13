/*
 * MundoCore
 * (c)2001-2005 Telecooperation Dept., Darmstadt University of Technology
 * (c)2001-2005 Erwin Aitenbichler
 *
 * All rights reserved. Unauthorized copying in any form is prohibited.
 * See license.txt for details.
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.FileWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.JFileChooser;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.metal.MetalScrollButton;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.mundo.service.DebugService;
import org.mundo.service.DoDebugService;
import org.mundo.net.routing.IRoutingService;
import org.mundo.net.transport.ip.IPTransportService;
import org.mundo.rt.GUID;
import org.mundo.rt.Mundo;
import org.mundo.rt.RMCException;
import org.mundo.rt.Service;
import org.mundo.service.DoIServiceManager;

class InspectWindow extends JFrame implements IRoutingService.IConn
{
  public InspectWindow()
  {
  }
  public InspectWindow(MainWindow mw)
  {
    mainWnd=mw;
    setSize(800, 500);
    setTitle(inspect.caption);
    createUI();
    createMainPane();
  }
  void createUI()
  {
  }
  void createMenu()
  {
    JMenu fileMenu=new JMenu("File");
    JMenuItem item=new JMenuItem("Connect");
    item.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        fileConnect();
      }
    });
    fileMenu.add(item);
    
    item=new JMenuItem("Open channel");
    item.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        fileTap();
      }
    });
    fileMenu.add(item);
    
    item=new JMenuItem("Exit");
    item.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        shutdown();
      }
    });
    fileMenu.add(item);
    
    JMenu viewMenu=new JMenu("View");
    autoRefresh=true;
    final JCheckBoxMenuItem autoRefreshItem=new JCheckBoxMenuItem("Auto Refresh", autoRefresh);
    autoRefreshItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        autoRefresh=autoRefreshItem.isSelected();
      }
    });
    viewMenu.add(autoRefreshItem);
    final JCheckBoxMenuItem advancedItem=new JCheckBoxMenuItem("Advanced functions", advanced);
    advancedItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        advanced=advancedItem.isSelected();
      }
    });
    viewMenu.add(advancedItem);

    JMenuBar menuBar=new JMenuBar();
    menuBar.add(fileMenu);
    menuBar.add(viewMenu);
    setJMenuBar(menuBar);
  }
  void createMainPane()
  {
    addrPane=new JPanel();
    addrPane.setBorder(progressBorder=new ProgressBorder(3, 6, 2, 6));
    JPanel btnPane=new JPanel();
    btnPane.setLayout(new BoxLayout(btnPane, BoxLayout.X_AXIS));
    btnPane.add(backButton=new MetalScrollButton(MetalScrollButton.WEST, 20, true));
//    btnPane.add(backButton=new BasicArrowButton(BasicArrowButton.WEST));
    backButton.setEnabled(false);
    backButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ev) {
        goBack();
      }
    });
    btnPane.add(forwardButton=new MetalScrollButton(MetalScrollButton.EAST, 20, true));
//    btnPane.add(forwardButton=new BasicArrowButton(BasicArrowButton.EAST));
    forwardButton.setEnabled(false);
    forwardButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ev) {
        goForward();
      }      
    });
    addrPane.setLayout(new BorderLayout(8, 0));
    addrPane.add(btnPane, BorderLayout.WEST);
    addrPane.add(uriField=new JTextField(), BorderLayout.CENTER);

    JPanel mainPane=new JPanel();
    mainPane.setLayout(new BorderLayout());
    mainPane.add(addrPane, BorderLayout.NORTH);

    if (tree!=null)
    {
      splitPane=new JSplitPane();
      splitPane.setLeftComponent(new JScrollPane(tree));
      splitPane.setRightComponent(RootPage.getContent());
      splitPane.setDividerLocation(200);
      mainPane.add(splitPane, BorderLayout.CENTER);
    }
    else
      mainPane.add(RootPage.getContent(), BorderLayout.CENTER);
    history.add("root:");

    setContentPane(mainPane);

    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent ev) {
        shutdown();
      }
    });
  }
  void createTree()
  {
    root=new ConRoot();
    try
    {
      root.add(new ConNode("Inspect"));
    }
    catch(RMCException x)
    {
      x.printStackTrace();
    }
    treeModel=new DefaultTreeModel(root);
    tree=new JTree(treeModel);
    tree.addMouseListener(new TreeMouseListener(tree));
    tree.addTreeSelectionListener(new ConTreeSelectionListener());
  }

  private void fileConnect()
  {
    String hostName=JOptionPane.showInputDialog("Host name or IP address");
    IPTransportService ipts=(IPTransportService)Mundo.getServiceByType(IPTransportService.class);
    try
    {
      int port=IPTransportService.getPrimaryPort();
      int i=hostName.indexOf(':');
      if (i>0)
      {
        port=Integer.parseInt(hostName.substring(i+1));
        hostName=hostName.substring(0, i);
      }
      if (!ipts.open(new InetSocketAddress(InetAddress.getByName(hostName), port)))
        JOptionPane.showMessageDialog(this, "connect failed");
    }
    catch(Exception x)
    {
      x.printStackTrace();
    }
  }

  private void fileTap()
  {
    String channelName=JOptionPane.showInputDialog("Channel name");
    if (channelName!=null)
      open(new ChannelPage(channelName));
  }
  
  void shutdown()
  {
    running=false;
    dispose();
  }
  
  public void run()
  {
    setVisible(true);
    try
    {
      while (running)
      {
//	    System.out.println("refresh: "+autoRefresh+", "+(currentPage!=null)+", "+pageLoaded);
        if (autoRefresh && currentPage!=null && pageLoaded && currentPage.reloadToRefresh)
          doOpen(currentPage, 0);
        Thread.sleep(1000);
      }
    }
    catch(Exception x)
    {
      x.printStackTrace();
    }
  }

  public void nodeAdded(GUID id)
  {
//    System.out.println("nodeAdded: "+id);
  }
  
  public void nodeRemoved(GUID id)
  {
    org.mundo.rt.Logger.global.info("nodeRemoved: "+id);
    try
    {
      ConNode node=root.findNode(id);
      if (node!=null)
        treeModel.removeNodeFromParent(node);
    }
    catch(Exception x)
    {
      x.printStackTrace();
    }
  }

  void open(Page page)
  {
    open(page, 0);
  }
  
  void open(Page page, int mode)
  {
    doOpen(page, mode);

    while (historyPos<history.size()-1)
      history.remove(history.size()-1);
    history.add(page.getURI());
    historyPos=history.size()-1;
    updateButtons();
  }

  void doOpen(Page page, int mods)
  {
    currentPage = page;
    if ((mods & ActionEvent.SHIFT_MASK)>0)
    {
      InspectWindow wnd=new InspectWindow(mainWnd);
      wnd.move(this.getX()+40, this.getY()+40);
      wnd.setVisible(true);
      wnd.open(page);
      return;
    }
    if (progressThread!=null)
    {
      progressThread.interrupt();
      progressThread=null;
    }
    progressThread=new ProgressThread();
    progressThread.start();
    page.setMainWindow(this);
    uriField.setText(page.getURI());
	pageLoaded = false;
    page.load();
  }
  
  void doOpen(String uri)
  {
    int i=uri.indexOf(':');
    if (i<0)
      return;
    String view=uri.substring(0, i);
    String location=uri.substring(i+1);

    Page page = null;    
    if (view.equals("root"))
      page=new RootPage();
    else
    {
      ConNode node=root.findNode(GUID.parse(location.substring(0, 32)));
      if (view.equals("node"))
        page=new NodePage(node.debugService);
      else if (view.equals("connections"))
        page=new ConnectionsPage(node.debugService);
      else if (view.equals("service-classes"))
        page=new ServiceClassesPage(node.debugService, node.getServiceManager());
      else if (view.equals("service-instances"))
        page=new ServiceInstancesPage(node.debugService, node.getServiceManager());
      else if (view.equals("ers-imports"))
        page=new ERSImportsPage(node.debugService);
      else if (view.equals("ers-exports"))
        page=new ERSExportsPage(node.debugService);
      else if (view.equals("node-config"))
        page=new NodeConfigPage(node.debugService);
      else if (view.equals("log"))
        page=new LogPage(node.debugService);
      else if (view.equals("service"))
      {
        i=uri.indexOf('/');
        if (i<0)
          return;
        page=new ServicePage(node.debugService, node.getServiceManager(),
                             GUID.parse(uri.substring(i+1)));
      }
      else if (view.equals("service-config"))
      {
        i=uri.indexOf('/');
        if (i<0)
          return;
        page=new ServiceConfigPage(node.getServiceManager(),
                                   GUID.parse(uri.substring(i+1)));
      }
      else if (view.equals("new-instance"))
      {
        i=uri.indexOf('/');
        if (i<0)
          return;
        page=new NewInstancePage(node.debugService, node.getServiceManager(),
                                 uri.substring(i+1));
      }
    }
    if (page!=null)
      doOpen(page, 0);
  }

  boolean goBack()
  {
    if (historyPos<1)
      return false;
    historyPos--;
    doOpen(history.get(historyPos));
    updateButtons();
    return true;
  }
  
  boolean goForward()
  {
    if (historyPos>=history.size()-1)
      return false;
    historyPos++;
    doOpen(history.get(historyPos));
    updateButtons();
    return true;
  }
  
  void updateButtons()
  {
    backButton.setEnabled(historyPos>0);
    forwardButton.setEnabled(historyPos<history.size()-1);
  }
  
  void pageLoaded(Page page)
  {
    if (progressThread!=null)
    {
      progressThread.interrupt();
      progressThread=null;
    }
	pageLoaded = true;
  }
  
  Resolver getResolver()
  {
    return resolver;
  }

  Service getService()
  {
    return mainWnd.getService();
  }

  ConNode addNode(GUID remoteId)
  {
    ConNode childNode = root.findNode(remoteId);
    if (childNode!=null)
      return childNode;
    childNode = new ConNode("remote", remoteId);
    treeModel.insertNodeInto(childNode, root, root.getChildCount());
    return childNode;
  }
      
  // Base class for console objects
  class ConObject implements MutableTreeNode
  {
    ConObject(String s)
    {
      name=s;
    }
    public void add(ConObject o)
    {
      objs.add(o);
      o.parent=this;
    }
    public Enumeration<ConObject> children() // TreeNode
    {
      return objs.elements();
    }
    public boolean getAllowsChildren() // TreeNode
    {
      return true;
    }
    public TreeNode getChildAt(int childIndex) // TreeNode
    {
      return (TreeNode)objs.elementAt(childIndex);
    }
    public int getChildCount() // TreeNode
    {
      if (objs.size()==0)
        expand();
      return objs.size();
    }
    public int getIndex(TreeNode node) // TreeNode
    {
      for (int i=0; i<objs.size(); i++)
        if (objs.elementAt(i)==(Object)node)
          return i;
      return -1;
    }
    public TreeNode getParent() // TreeNode
    {
      return parent;
    }
    public boolean isLeaf() // TreeNode
    {
      return false;
    }
    public void insert(MutableTreeNode child, int index)
    {
      objs.add(index, (ConObject)child);
      ((ConObject)child).parent=this;
    }
    public void remove(int index)
    {
      objs.remove(index);
    }
    public void remove(MutableTreeNode node)
    {
      System.out.println("remove(node)");
      throw new UnsupportedOperationException("MutableTreeNode.remove");
    }
    public void removeFromParent()
    {
      System.out.println("removeFromParent");
      throw new UnsupportedOperationException("MutableTreeNode.removeFromParent");
    }
    public void setParent(MutableTreeNode newParent)
    {
      throw new UnsupportedOperationException("MutableTreeNode.setParent");
    }
    public void setUserObject(Object object)
    {
      throw new UnsupportedOperationException("MutableTreeNode.setUserObject");
    }
    public String toString()
    {
      return name;
    }
    protected void expand()
    {
    }
    public ConNode getNode()
    {
      TreeNode node=this;
      do
      {
        if (node instanceof ConNode)
          return (ConNode)node;
        node=node.getParent();
      }
      while (node!=null);
      return null;
    }
    public void pageOpened()
    {
    }
    public void refresh()
    {
    }
    public void showContextMenu(MouseEvent ev)
    {
    }
    protected String name;
    protected ConObject parent = null;
    protected Vector<ConObject> objs = new Vector<ConObject>();
  }

  // Root node
  class ConRoot extends ConObject
  {
    ConRoot()
    {
      super("root");
    }
    public ConNode findNode(GUID g)
    {
      for (ConObject obj : objs)
      {
        ConNode node=(ConNode)obj;
        if (g.equals(node.vpId))
          return node;
      }
      return null;
    }
  }
  
  // MundoCore node
  class ConNode extends ConObject
  {
    ConNode(String friendlyName) throws RMCException
    {
      super(friendlyName);
      vpId=Mundo.getNodeId();
      debugService=new DoDebugService(getService().getSession().getChannel("lan", vpId.toString()+".DebugService"));
      debugService._setTimeout(RMC_TIMEOUT);
    }
    ConNode(String friendlyName, GUID guid) throws RMCException
    {
      super(friendlyName);
      vpId=guid;
      debugService=new DoDebugService(getService().getSession().getChannel("lan", vpId.toString()+".DebugService"));
      debugService._setTimeout(RMC_TIMEOUT);
    }
    public String toString()
    {
      return name+" ("+vpId.shortString()+")";
    }
    protected void expand() // TreeNode
    {
      add(new ConConnections());
      add(new ConServiceInstances());
      add(new ConServiceClasses());
      add(new ConERSImports());
      add(new ConERSExports());
      add(new ConNodeConfig());
      add(new ConLogMessages());
    }
    public DoIServiceManager getServiceManager()
    {
      if (serviceManager==null)
        serviceManager=new DoIServiceManager(getService().getSession().getChannel("lan", vpId.toString()+".ServiceManager"));
      return serviceManager;
    }
    public boolean hasServiceManager()
    {
      return serviceManager!=null;
    }
    public void showContextMenu(MouseEvent ev)
    {
      if (!advanced)
        return; 
      JPopupMenu menu=new JPopupMenu();
      JMenuItem item=new JMenuItem("Shutdown node");
      menu.add(item);
      item.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent action) {
          debugService.shutdownNode();
        }
      });
      menu.show(tree, ev.getX(), ev.getY());
    }
    public GUID vpId;
    public DoDebugService debugService;
    private DoIServiceManager serviceManager;
  }

  // Connections of a node
  class ConConnections extends ConObject
  {
    ConConnections()
    {
      super("Connections");
    }
    public boolean isLeaf() // TreeNode
    {
      return true;
    }
/*
    public void pageOpened()
    {
      try
      {
        showMessage("calling getConnections...");
        tableModel=null;

        AsyncCall ac=getNode().debugService.getConnections(DoObject.CREATEONLY);
        ac.setResultListener(connectionsPageHandler);
        ac.invoke();
      }
      catch(Exception x)
      {
        splitPane.setRightComponent(new JLabel(x.toString()));
      }
    }
    public void refresh()
    {
      if (table==null)
        return;
      try
      {
        AsyncCall ac=getNode().debugService.getConnections(DoObject.CREATEONLY);
        ac.setResultListener(connectionsPageHandler);
        ac.invoke();
      }
      catch(Exception x)
      {
        x.printStackTrace();
      }
    }

    final AsyncCall.IResultListener connectionsPageHandler=new AsyncCall.IResultListener()
    {
      public void resultReceived(AsyncCall c)
      {
        List<DebugService.ConnEntry> connections=(List<DebugService.ConnEntry>)c.getObj();
        if (tableModel==null)
        {
          tableModel=new ConnEntryTableModel(connections);
          table=new JTable(tableModel);
          tableModel.setTableHeader(table.getTableHeader());
          table.addMouseListener(new ConnectionsMouseListener(table, tableModel, getNode().debugService));
          showComponent(new JScrollPane(table));
        }
        else
        {
          int sel=table.getSelectedRow();
          tableModel.set(connections);
          if (sel>=0 && sel<table.getRowCount())
            table.setRowSelectionInterval(sel, sel);
        }
      }
    };
    private JTable table;
    private ConnEntryTableModel tableModel;
*/
  }

  class ConServiceInstances extends ConObject
  {
    ConServiceInstances()
    {
      super("Service Instances");
    }
    protected void expand() // TreeNode
    {
      Collection<DebugService.ServiceEntry> clt=getNode().debugService.getServices();
      for (Iterator<DebugService.ServiceEntry> i=clt.iterator(); i.hasNext();)
        add(new ConService(i.next()));
    }
  }

  class ConServiceClasses extends ConObject
  {
    ConServiceClasses()
    {
      super("Service Classes");
    }
    public boolean isLeaf() // TreeNode
    {
      return true;
    }
  }

  class ConService extends ConObject
  {
    ConService(DebugService.ServiceEntry e)
    {
      super(e.instanceName!=null ? e.instanceName : e.className+" ("+e.guid.toString()+")");
      se=e;
    }
    public boolean isLeaf() // TreeNode
    {
      return true;
    }
    public GUID getId()
    {
      return se.guid;
    }
    private DebugService.ServiceEntry se;
  }

  class ConERSImports extends ConObject
  {
    ConERSImports()
    {
      super("ERS Imports");
    }
    public boolean isLeaf() // TreeNode
    {
      return true;
    }
  }

  class ConERSExports extends ConObject
  {
    ConERSExports()
    {
      super("ERS Exports");
    }
    public boolean isLeaf() // TreeNode
    {
      return true;
    }
  }

  class ConNodeConfig extends ConObject
  {
    ConNodeConfig()
    {
      super("Config");
    }
    public boolean isLeaf() // TreeNode
    {
      return true;
    }
  }

  class ConLogMessages extends ConObject
  {
    ConLogMessages()
    {
      super("Log");
    }
    public boolean isLeaf() // TreeNode
    {
      return true;
    }
    public void showContextMenu(MouseEvent ev)
    {
      JPopupMenu menu=new JPopupMenu();
      JMenuItem item=new JMenuItem("Save as...");
      menu.add(item);
      item.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent action)
        {
          if (!(tableModel instanceof LogTableModel))
            return;
          LogTableModel ltm=(LogTableModel)tableModel;
          JFileChooser chooser=new JFileChooser();
          if (chooser.showSaveDialog(InspectWindow.this)==JFileChooser.APPROVE_OPTION)
          {
            try
            {
              Writer w=new FileWriter(chooser.getSelectedFile().getPath());
              ltm.writeTo(w);
              w.close();
            }
            catch(Exception x)
            {
              x.printStackTrace();
            }
          }
        }
      });
      menu.show(tree, ev.getX(), ev.getY());
    }
  }

  class ServiceEntryTableModel extends AbstractTableModel
  {
    ServiceEntryTableModel(Collection<DebugService.ServiceEntry> clt)
    {
      int i=0;
      entries=new DebugService.ServiceEntry[clt.size()];
      Iterator<DebugService.ServiceEntry> iter=clt.iterator();
      while (iter.hasNext())
        entries[i++]=iter.next();
    }
    public int getRowCount()
    {
      return entries.length;
    }
    public int getColumnCount()
    {
      return 2;
    }
    public String getColumnName(int column)
    {
      switch (column)
      {
        case 0:
          return "ID";
        case 1:
          return "ClassName";
      }
      return "<null>";
    }
    public Object getValueAt(int row, int column)
    {
      DebugService.ServiceEntry se=entries[row];
      switch (column)
      {
        case 0:
          return se.guid.toString();
        case 1:
          return se.className;
      }
      return null;
    }
    private DebugService.ServiceEntry entries[];
  }

  void showException(Throwable x)
  {
    StringWriter sw=new StringWriter();
    x.printStackTrace(new PrintWriter(sw));
    JScrollPane pane=new JScrollPane(new JTextArea(sw.toString()));
    pane.setBorder(new LineBorder(Color.red, 4));
    showComponent(pane);
  }

  private void showMessage(String s)
  {
    showComponent(new JLabel(s));
  }

  void showComponent(JComponent c)
  {
    if (splitPane!=null)
    {
      int div=splitPane.getDividerLocation();
      splitPane.setRightComponent(c);
      splitPane.setDividerLocation(div);
    }
    else
    {
      getContentPane().remove(getContentPane().getComponentCount()-1);
      getContentPane().add(c, BorderLayout.CENTER);
      validate();
    }
  }

  class ConTreeSelectionListener implements TreeSelectionListener
  {
    public void valueChanged(TreeSelectionEvent e)
    {
      ConObject lpc=(ConObject)e.getPath().getLastPathComponent();
      current=lpc;
      try
      {
        if (lpc instanceof ConNode)
        {
          open(new NodePage(lpc.getNode().debugService));
        }
        else if (lpc instanceof ConConnections)
        {
          open(new ConnectionsPage(lpc.getNode().debugService));
        }
        else if (lpc instanceof ConServiceInstances)
        {
          open(new ServiceInstancesPage(lpc.getNode().debugService, lpc.getNode().getServiceManager()));
        }
        else if (lpc instanceof ConServiceClasses)
        {
          open(new ServiceClassesPage(lpc.getNode().debugService, lpc.getNode().getServiceManager()));
        }
        else if (lpc instanceof ConService)
        {
          open(new ServicePage(lpc.getNode().debugService, lpc.getNode().getServiceManager(), ((ConService)lpc).getId()));
        }
        else if (lpc instanceof ConERSImports)
        {
          open(new ERSImportsPage(lpc.getNode().debugService));
        }
        else if (lpc instanceof ConERSExports)
        {
          open(new ERSExportsPage(lpc.getNode().debugService));
        }
        else if (lpc instanceof ConNodeConfig)
        {
          open(new NodeConfigPage(lpc.getNode().debugService));
        }
        else if (lpc instanceof ConLogMessages)
        {
          open(new LogPage(lpc.getNode().debugService));
        }
        else
        {
          open(new RootPage());
        }
      }
      catch(Exception x)
      {
        showException(x);
      }
    }
  }

  class TreeMouseListener extends MouseAdapter
  {
    TreeMouseListener(JTree t)
    {
      tree=t;
    }
    public void mouseClicked(MouseEvent e)
    {
      if (e.getButton()==MouseEvent.BUTTON3)
      {
        TreePath tp=tree.getSelectionPath();
        if (tp!=null)
        {
          ConObject obj=(ConObject)tp.getLastPathComponent();
          if (obj!=null)
            obj.showContextMenu(e);
        }
      }
    }
    JTable table;
    List<DebugService.PublisherEntry> list;
  }

  class ProgressBorder extends EmptyBorder
  {
    ProgressBorder(int t, int l, int b, int r)
    {
      super(t, l, b, r);
    }
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height)
    {
      if (value>100)
      {
        g.setColor(Color.RED);
        g.fillRect(x, y, width, height);
      }
      else
      {
        int w=value*width/100;
        g.setColor(new Color(0xc0, 0xd0, 0xff));
        g.fillRect(x, y, w, height);
        g.setColor(c.getBackground()); //new Color(0xe0, 0xe0, 0xe0));
        g.fillRect(x+w, y, width-w, height);
      }
    }
    void set(int v)
    {
      value=v;
    }
    private int value=0;
  }

  class ProgressThread extends Thread
  {
    public void run()
    {
      try
      {
        for (int i=0; i<=101; i++)
        {
          progressBorder.set(i);
          addrPane.repaint();
          Thread.sleep(20);
        }
      }
      catch(Exception x)
      {
      }
    }
    @Override
    public void interrupt()
    {
      progressBorder.set(0);
      addrPane.repaint();
      super.interrupt();
    }
  }

  ProgressThread progressThread;
  ProgressBorder progressBorder;
  JPanel addrPane;
  Resolver resolver=new Resolver();
  JSplitPane splitPane;
  JTree tree;
  JTextField uriField;
  JButton backButton;
  JButton forwardButton;
  DefaultTreeModel treeModel;
  ConRoot root;
  ConObject current;
  Page currentPage = null;
  private ArrayList<String> history=new ArrayList<String>();
  private int historyPos=0;
  private MainWindow mainWnd;
  private boolean autoRefresh;
  private boolean running = true;
  boolean advanced = false;
  private TableModel tableModel;
  private boolean pageLoaded = false;
  private static final int RMC_TIMEOUT = 10*1000;
}
