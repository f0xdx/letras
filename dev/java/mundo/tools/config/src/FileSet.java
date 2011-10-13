import java.util.ArrayList;

public class FileSet
{
  public void add(Entry e)
  {
    entries.add(e);
  }
  public int size()
  {
    return entries.size();
  }
  
  public static class Entry
  {
    String name;
  }
  
  public static class FileEntry extends Entry
  {
    public FileEntry(String fn)
    {
      name = fn;
    }
  }
  
  public static class DirEntry extends Entry
  {
    public DirEntry(String dn)
    {
      name = dn;
    }
  }
  
  ArrayList<Entry> entries = new ArrayList<Entry>();
}
