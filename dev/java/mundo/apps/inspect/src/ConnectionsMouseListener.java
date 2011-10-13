import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.util.List;
import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;
import javax.swing.JTable;

import org.mundo.rt.RMCException;
import org.mundo.service.DebugService;
import org.mundo.service.DoDebugService;

class ConnectionsMouseListener extends MouseAdapter
{
  ConnectionsMouseListener(ConnectionsPage p)
  {
    page=p;
  }
  public void mouseClicked(MouseEvent e)
  {
    if (e.getButton()==MouseEvent.BUTTON1 && e.getClickCount()==2)
    {
      page.openNode();
    }
    if (e.getButton()==MouseEvent.BUTTON3)
    {
      JPopupMenu menu=new JPopupMenu();
      JMenuItem item=new JMenuItem("Inspect node");
      menu.add(item);
      item.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent action) {
          page.openNode();
        }
      });
      if (page.window.advanced)
      {
        item=new JMenuItem("Cripple: Block sends");
        menu.add(item);
        item.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent action) {
            page.blockSends();
          }
        });
      }
      menu.show(page.table, e.getX(), e.getY());
    }
  }

  ConnectionsPage page;
}
