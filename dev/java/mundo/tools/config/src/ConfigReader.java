import java.io.File;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class ConfigReader
{
  ConfigReader()
  {
    builder.container("", "MundoCore");
  }
  void read(String fn, boolean external)
  {
    read(new File(fn), external);
  }
  void read(File file, boolean external)
  {
    try
    {
      System.out.println(file.getAbsolutePath());
      builder.setExternal(external);
      SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
      parser.parse(file, HANDLER);
    }
    catch(Exception x)
    {
      x.printStackTrace();
    }
  }
  void scanDir(String dn, boolean external)
  {
    scanDir(new File(dn), external);
  }
  void scanDir(File dir, boolean external)
  {
    for (File f : dir.listFiles())
    {
      if (f.isDirectory())
        scanDir(f, external);
      else if (f.getName().equals("mcbuild.xml"))
        read(f, external);
    }
  }
  ConfigContainer getRoot()
  {
    return builder.getRoot();
  }
  
  final DefaultHandler HANDLER = new DefaultHandler()
  {
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes)
    {
      if (text!=null)
      {
        text.append("<"+qName+">");
        return;
      }
      if ("container".equals(qName))
        builder.container(attributes.getValue("key"), attributes.getValue("name"));
      else if ("module".equals(qName))
      {
        builder.module(attributes.getValue("key"), attributes.getValue("name"), attributes.getValue("default"));
      }
      else if ("feature".equals(qName))
      {
        feature = attributes.getValue("name");
        String type = attributes.getValue("type");
        if ("boolean".equals(type))
          builder.feature(attributes.getValue("key"), attributes.getValue("name"), "enabled".equals(attributes.getValue("default")));
        else
          builder.feature(attributes.getValue("key"), attributes.getValue("name"));
      }
      else if ("option".equals(qName))
      {
        builder.option(attributes.getValue("key"), attributes.getValue("name"), "enabled".equals(attributes.getValue("default")));
      }
      else if ("description".equals(qName))
      {
        text = new StringBuffer();
      }
      else if ("define".equals(qName))
      {
        builder.define(attributes.getValue("target"), attributes.getValue("key"), attributes.getValue("value"));
      }
      else if ("src".equals(qName))
      {
        String s = attributes.getValue("file");
        if (s!=null)
          builder.srcFile(s);
      }
    }
    @Override
    public void endElement(String uri, String localName, String qName)
    {
      if ("description".equals(qName))
      {
        builder.description(text.toString());
        text = null;
      }
      if (text!=null)
      {
        text.append("</"+qName+">");
        return;
      }
      if ("container".equals(qName) || "module".equals(qName))
        builder.up();
    }
    @Override
    public void characters(char[] ch, int start, int length)
    {
      if (text!=null)
        text.append(ch, start, length);
    }
  };
  
  ConfigBuilder builder = new ConfigBuilder();
  String feature;
  StringBuffer text = null;
}
