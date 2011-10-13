import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Map;

import org.mundo.rt.TypedContainer;
import org.mundo.rt.TypedMap;
import org.mundo.rt.TypedArray;
import org.mundo.rt.GUID;
import org.mundo.xml.XMLDeserializer;

public class x2j
{
  private class PutMethod
  {
    PutMethod(String s)
    {
      suffix=s;
      delim="";
    }
    PutMethod(String s, String d)
    {
      suffix=s;
      delim=d;
    }
    String suffix;
    String delim;
  }
  
  private PutMethod getPutMethod(Object obj)
  {
    if (obj instanceof String)
      return new PutMethod("String", "\"");
    else if (obj instanceof Boolean)
      return new PutMethod("Boolean");
    else if (obj instanceof Character)
      return new PutMethod("Character", "'");
    else if (obj instanceof GUID)
      return new PutMethod("GUID");
    else if (obj instanceof Byte)
      return new PutMethod("Byte");
    else if (obj instanceof Short)
      return new PutMethod("Short");
    else if (obj instanceof Integer)
      return new PutMethod("Int");
    else if (obj instanceof Long)
      return new PutMethod("Long");
    else if (obj instanceof TypedContainer.UnsignedByte)
      return new PutMethod("UByte");
    else if (obj instanceof TypedContainer.UnsignedShort)
      return new PutMethod("UShort");
    else if (obj instanceof TypedContainer.UnsignedInteger)
      return new PutMethod("UInt");
    else if (obj instanceof TypedContainer.UnsignedLong)
      return new PutMethod("ULong");
    return null;
  }

  private void newMap()
  {
    mapIndex++;
    out.println("TypedMap m"+mapIndex+" = new TypedMap();");
  }

  private void newArray()
  {
    arrayIndex++;
    out.println("TypedArray a"+arrayIndex+" = new TypedArray();");
  }

  public void generate(TypedMap map)
  {
    int index = mapIndex;
    String acName = map.getActiveClassName();
    if (acName!=null)
      out.println("m"+index+".setActiveClassName(\""+acName+"\");");
    for (Map.Entry<String,Object> e : map.entrySet())
    {
      Object v = e.getValue();
      PutMethod pm = getPutMethod(v);
      if (pm!=null)
      {
        out.println("m"+index+".put"+pm.suffix+"(\""+e.getKey()+"\", "+
                    pm.delim+v+pm.delim+");");
      }
      else if (v instanceof TypedMap)
      {
        newMap();
        out.println("m"+index+".put(\""+e.getKey()+"\", m"+mapIndex+");");
        generate((TypedMap)v);
      }
      else if (v instanceof TypedArray)
      {
        newArray();
        out.println("m"+index+".put(\""+e.getKey()+"\", a"+arrayIndex+");");
        generate((TypedArray)v);
      }
    }
  }

  public void generate(TypedArray a)
  {
    int index = arrayIndex;
    String acName = a.getActiveClassName();
    if (acName!=null)
      out.println("a"+index+".setActiveClassName(\""+acName+"\");");
    for (Object v : a)
    {
      PutMethod pm = getPutMethod(v);
      if (pm!=null)
      {
        out.println("a"+index+".add"+pm.suffix+"("+pm.delim+v+pm.delim+");");
      }
      else if (v instanceof TypedMap)
      {
        newMap();
        out.println("a"+index+".add(m"+mapIndex+");");
        generate((TypedMap)v);
      }
      else if (v instanceof TypedArray)
      {
        newArray();
        out.println("a"+index+".add(a"+arrayIndex+");");
        generate((TypedArray)v);
      }
    }
  }
  
  public void run(String args[])
  {
    try
    {
      XMLDeserializer deser = new XMLDeserializer();
      TypedMap map = (TypedMap)deser.deserializeObject(new FileInputStream(args[0]));
      out = new PrintStream(new FileOutputStream(args[1]));

      String className, pkgName=null;
      int i = args[2].lastIndexOf('.');
      if (i>0)
      {
        pkgName = args[2].substring(0, i);
        className = args[2].substring(i+1);
      }
      else
        className = args[2];

      if (pkgName!=null)
        out.println("package "+pkgName+";");
      out.println("import org.mundo.rt.TypedMap;");
      out.println("import org.mundo.rt.TypedArray;");
      out.println();
      out.println("public class "+className+" {");
      out.println("public static TypedMap getMap() {");
      out.println("TypedMap m0 = new TypedMap();");
      generate(map);
      out.println("return m0;");
      out.println("}");
      out.println("}");
    }
    catch(Exception x)
    {
      x.printStackTrace();
    }
  }

  public static void main(String args[])
  {
	if (args.length<3)
	{
	  System.out.println("syntax: x2j <inputfile> <outputfile> <classname>");
	  return;
	}
    new x2j().run(args);
  }
  
  private PrintStream out;
  private int mapIndex = 0;
  private int arrayIndex = 0;
}
