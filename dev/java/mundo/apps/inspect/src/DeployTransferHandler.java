import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Iterator;
import javax.swing.JComponent;
import javax.swing.TransferHandler;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

import org.mundo.rt.Blob;
import org.mundo.service.DoIServiceManager;

public class DeployTransferHandler extends TransferHandler
{
  DeployTransferHandler(DoIServiceManager sm)
  {
    doSvcMan=sm;
    try
    {
      fileFlavor=new DataFlavor("application/x-java-file-list; class=java.util.List");
    }
    catch(ClassNotFoundException x)
    {
    }
  }
    
  @Override
  public boolean canImport(JComponent c, DataFlavor[] flavors)
  {
    return hasFileFlavor(flavors);
  }

  @Override
  public boolean importData(JComponent c, Transferable t)
  {
    try
    {
      List<File> list=(List<File>)t.getTransferData(fileFlavor);
      for (Iterator<File> iter=list.iterator(); iter.hasNext();)
      {
        upload(iter.next());
      }
      return true;
    }
    catch(Exception x)
    {
      x.printStackTrace();
    }
    return false;
  }

  private void upload(File file)
  {
    try
    {
      FileInputStream fis=new FileInputStream(file);
      Blob blob=new Blob();
      blob.readFrom(fis);
      fis.close();
      doSvcMan.uploadFile(file.getName(), blob);
    }
    catch(IOException x)
    {
      x.printStackTrace();
    }
  }

  private boolean hasFileFlavor(DataFlavor[] flavors)
  {
    if (flavors==null || fileFlavor==null)
      return false;
    for (int i=0; i<flavors.length; i++)
    {
      if (fileFlavor.equals(flavors[i]))
        return true;
    }
    return false;
  }
  
  private DataFlavor fileFlavor;
  private DoIServiceManager doSvcMan;
}
