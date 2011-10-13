import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Stack;

public class JavaWriter
{
  public JavaWriter(File f) throws IOException
  {
    w = new PrintWriter(f);
  }
  public void close()
  {
    w.close();
  }
  private void writeAttr(String m, NodeConfigItem item)
  {
    if (item.attrs == null)
      return;
    for (String[] a : item.attrs)
    {
      if ("activeClass".equals(a[0]))
        println(m + ".setActiveClassName(\"" + a[1] + "\");");
    }
  }
  private void writeItem(NodeConfigItem item)
  {
    boolean isArray = false;
    if (item.parent!=null && item.parent instanceof NodeConfigArrayItem)
      isArray = true;
    if (isArray)
    {
      String a = "a" + idStack.peek();
      if (item instanceof NodeConfigBoolItem)
        println(a + ".addBoolean(" + ((NodeConfigBoolItem)item).value + ");");
      else if (item instanceof NodeConfigIntItem)
        println(a + ".addInt(" + ((NodeConfigIntItem)item).value + ");");
      else if (item instanceof NodeConfigStringItem)
        println(a + ".addString(\"" + ((NodeConfigStringItem)item).value + "\");");
      else if (item instanceof NodeConfigMapItem)
      {
        idStack.push(++id);
        println("TypedMap m" + id + " = new TypedMap();");
        writeAttr("m" + id, item);
        println(a + ".add(m" + id + ");");
        for (NodeConfigItem c : item.children)
          writeItem(c);
        idStack.pop();
      }
      else if (item instanceof NodeConfigArrayItem)
      {
        idStack.push(++id);
        println("TypedArray a" + id + " = new TypedArray();");
        writeAttr("a" + id, item);
        println(a + ".addArray(a" + id + ");");
        for (NodeConfigItem c : item.children)
          writeItem(c);
        idStack.pop();
      }
      else
        System.out.println("unknown in array: " + item.getClass().getName());
    }
    else
    {
      String m = null;
      if (!idStack.isEmpty())
        m = "m" + idStack.peek();
      if (item instanceof NodeConfigBoolItem)
        println(m + ".putBoolean(\"" + item.key + "\", " + ((NodeConfigBoolItem)item).value + ");");
      else if (item instanceof NodeConfigIntItem)
        println(m + ".putInt(\"" + item.key + "\", " + ((NodeConfigIntItem)item).value + ");");
      else if (item instanceof NodeConfigStringItem)
        println(m + ".putString(\"" + item.key + "\", \"" + ((NodeConfigStringItem)item).value + "\");");
      else if (item instanceof NodeConfigMapItem)
      {
        idStack.push(++id);
        println("TypedMap m" + id + " = new TypedMap();");
        writeAttr("m" + id, item);
        if (m != null)
          println(m + ".put(\"" + item.key + "\", m" + id + ");");
        for (NodeConfigItem c : item.children)
          writeItem(c);
        idStack.pop();
      }
      else if (item instanceof NodeConfigArrayItem)
      {
        idStack.push(++id);
        println("TypedArray a" + id + " = new TypedArray();");
        writeAttr("a" + id, item);
        println(m + ".putArray(\"" + item.key + "\", a" + id + ");");
        for (NodeConfigItem c : item.children)
          writeItem(c);
        idStack.pop();
      }
      else
        System.out.println("unknown in map: " + item.getClass().getName());
    }
  }
  
  public void write(NodeConfigItem item)
  {
    println("package org.mundo.rt;");
    println("import org.mundo.rt.TypedMap;");
    println("import org.mundo.rt.TypedArray;");
    println();
    println("public class DefaultConfig {");
    indent++;
    println("public static TypedMap getMap() {");
    indent++;
//    println("TypedMap m0 = new TypedMap();");
//    idStack.push(0);
    writeItem(item);
    println("return m1;");
    indent--;
    println("}");
    indent--;
    println("}");
  }
  
  private void println(String s)
  {
    for (int i=0; i<indent; i++)
      w.print("  ");
    w.println(s);
  }
  private void println()
  {
    w.println();
  }

  private int indent = 0;
  private int id = 0;
  private Stack<Integer> idStack = new Stack<Integer>();
  private PrintWriter w;
}
