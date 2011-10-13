import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;

public class BuildFileWriter
{
  public void write(String fn, ConfigItem root) throws IOException
  {
    FileWriter w = new FileWriter(fn);
    write(w, root);
  }
  public void write(Writer baseWriter, ConfigItem root)
  {
    scan(root);
    PrintWriter w = new PrintWriter(baseWriter);
    w.println("<project name=\"MundoCore\" default=\"jar\">");
    w.println("  <property file=\"config/build.properties\"/>");
    w.println();
    w.println("  <property name=\"src\" value=\"src\"/>");
    w.println("  <property name=\"prep\" value=\"prep\"/>");
    w.println("  <property name=\"build\" value=\"classes\"/>");
    w.println("  <property name=\"wsdl\" value=\"wsdl\"/>");
    w.println("  <property name=\"apidoc\" value=\"docs/api\"/>");
    w.print("  <property name=\"dist\" value=\"lib/");
    String target = features.get("target");
    if (target.equals("java15"))
      w.print("se");
    else if (target.equals("android"))
      w.print("android");
    else
      w.print("other");
    w.println("\"/>");
    w.println("  <property name=\"javacc\" location=\"lib/javacc-5.0\"/>");
    w.println();
    w.println("  <condition property=\"mkconfig\" value=\"./config/mkconfig.sh\">");
    w.println("    <os family=\"unix\"/>");
    w.println("  </condition>");
    w.println("  <condition property=\"mkconfig\" value=\"./config/mkconfig.bat\">");
    w.println("    <os family=\"windows\"/>");
    w.println("  </condition>");
    w.println();
    w.println("  <target name=\"init\">");
    w.println("    <mkdir dir=\"${prep}\" />");
    w.println("    <mkdir dir=\"${build}\" />");
    w.println("    <mkdir dir=\"${dist}\" />");
    w.println("  </target>");
    w.println();
    w.println("  <target name=\"clean\">");
    w.println("    <delete dir=\"${build}\"/>");
    w.println("    <delete dir=\"${prep}\" />");
    w.println("  </target>");
    w.println();
    
    w.println("  <target name=\"reconfigure\">");
    w.println("    <ant dir=\"tools\" antfile=\"build.xml\" inheritall=\"false\" target=\"jar\"/>");
    w.println("    <java jar=\"tools/configure.jar\" fork=\"true\"/>");
    w.println("  </target>");
    w.println();
    
    w.println("  <target name=\"check-config\">");
    w.println("    <available file=\"config/build.properties\" property=\"properties.present\"/>");
    w.println("  </target>");
    w.println("  <target name=\"create-config\" depends=\"check-config\" unless=\"properties.present\">");
    w.println("    <exec dir=\".\" executable=\"${mkconfig}\"/>");
    w.println("  </target>");
    w.println();
    w.println("  <target name=\"prep\" depends=\"init\">");
    w.println("    <apply executable=\"${mcc}\" parallel=\"true\">");
    w.println("      <!-- arg value=\"-llog.txt\"/ -->");
    w.println("      <arg value=\"-I${src}\"/>");
    w.println("      <arg value=\"-O${prep}\"/>");
    w.println("      <arg value=\"-W${wsdl}\"/>");
    w.println("      <arg value=\"-c\"/>");
    w.println("      <arg value=\"-rmc\"/>");
    w.println("      <arg value=\"-e\"/>");
    HashMap<String,Define> map = defines.get("mcc");
    if (map!=null)
    {
      for (Define d : map.values())
        w.println("      <arg value=\"-D"+d.key+"\"/>");
    }
    w.println("      <fileset dir=\"${src}\">");
    w.println("        <include name=\"**/*.oj\"/>");
    map = defines.get("src-exclude");
    if (map!=null)
    {
      for (Define d : map.values())
        w.println("        <exclude name=\"" + d.key + "\"/>");
    }
    w.println("      </fileset>");
    w.println("    </apply>");
    w.println("    <exec executable=\"${mcc}\">");
    w.println("      <arg value=\"-O${prep}\"/>");
    w.println("      <arg value=\"-M\"/>");
    w.println("      <arg value=\"org.mundo.rt.Metaclasses\"/>");
    w.println("    </exec>");
    w.println("    <copy overwrite=\"true\" file=\"config/DefaultConfig.java\" todir=\"prep/org/mundo/rt\"/>");
    w.println("  </target>");
    w.println();
    w.println("  <target name=\"xquery-parser\">");
    w.println("    <mkdir dir=\"${prep}/org/mundo/xml/xqparser\"/>");
    w.println("    <jjtree target=\"${javacc}/grammar/xquery10.grammar.jjt\" outputdirectory=\"${prep}/org/mundo/xml/xqparser\" javacchome=\"${javacc}\"/>");
    w.println("    <javacc target=\"${prep}/org/mundo/xml/xqparser/xquery10.grammar.jj\" outputdirectory=\"${prep}/org/mundo/xml/xqparser\" javacchome=\"${javacc}\"/>");
//    w.println("      <copy overwrite=\"true\" file=\"${src}/org/mundo/xml/xqparser/SimpleNode.oj\" tofile=\"${prep}/org/mundo/xml/xqparser/SimpleNode.java\"/>");
    w.println("  </target>");
    w.println();
    w.print("  <target name=\"compile\" depends=\"prep");
    map = defines.get("ant-rule");
    if (map!=null)
    {
      for (String key : map.keySet())
        w.print(","+key);
    }
    w.println("\">");
    w.print("    <javac destdir=\"${build}\"");
    map = defines.get("javac");
    if (map!=null)
    {
      for (Define d : map.values())
        w.print(" "+d.key+"=\""+d.value+"\"");
    }
    w.println(" deprecation=\"on\" encoding=\"utf-8\" includeantruntime=\"false\">");
    if (target.equals("android"))
    {
      w.println("      <classpath>");
      w.println("        <pathelement path=\"${android-platform}/android.jar\" />");
      w.println("      </classpath>");
    }
    w.println("      <src path=\"${src}\"/>");
    w.println("      <src path=\"${prep}\"/>");
    w.println("      <include name=\"**/*.java\"/>");
    map = defines.get("src-exclude");
    if (map!=null)
    {
      for (Define d : map.values())
        w.println("      <exclude name=\"" + d.key + "\"/>");
    }
    w.println("    </javac>");
    w.println("  </target>");
    w.println();
    w.println("  <target name=\"jar\" depends=\"compile\">");
    w.println("    <jar jarfile=\"${dist}/mundocore.jar\">");
    w.println("      <fileset dir=\"${build}\">");
    w.println("        <include name=\"**/*.class\"/>");
    w.println("      </fileset>");
    w.println("    </jar>");
    w.println("  </target>");
    w.println();
    w.println("  <target name=\"sources\" depends=\"jar\">");
    w.println("    <delete file=\"${dist}/sources.zip\" />");
    w.println("    <zip destfile=\"${dist}/sources.zip\" basedir=\"src\" includes=\"**/*.java\" update=\"false\" />");
    w.println("    <zip destfile=\"${dist}/sources.zip\" basedir=\"prep\" includes=\"**/*.java\" update=\"true\" />");
    w.println("  </target>");
    w.println();
    w.println("  <target name=\"javadoc\" depends=\"sources\">");
    w.println("    <delete dir=\"${apidoc}\" />");
    w.println("    <javadoc destdir=\"${apidoc}\" windowtitle=\"MundoCore\">");
    w.println("      <packageset dir=\"src\" />");
    w.println("      <packageset dir=\"prep\" />");
    w.println("    </javadoc>");
    w.println("  </target>");
    w.println("</project>");
    w.close();
  }
  
  void scan(ConfigItem ci)
  {
    if (ci instanceof ConfigFeature)
    {
      ConfigFeature cf = (ConfigFeature)ci;
      ConfigOption co = cf.getSelectedOption();
      if (co!=null)
      {
        features.put(cf.key, co.key);
//        System.out.println(cf.key+" -> "+co.key);
      }
    }
    ArrayList<Define> defs = ci.getDefines();
    if (defs!=null)
    {
      for (Define def : defs)
      {
        HashMap<String,Define> map = defines.get(def.target);
        if (map==null)
        {
          map = new HashMap<String,Define>();
          defines.put(def.target, map);
        }
        map.put(def.key, def);
      }
    }
    if (ci.list!=null)
      for (ConfigItem subItem : ci.list)
        scan(subItem);
  }
  
  HashMap<String,HashMap<String,Define>> defines = new HashMap<String,HashMap<String,Define>>();
  HashMap<String,String> features = new HashMap<String,String>();
}
