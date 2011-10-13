import java.io.File;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class BuildConfigReader
{
  public void read(ConfigItem root, String fn) throws Exception
  {
    read(root, new File(fn));
  }
  public void read(ConfigItem root, File file) throws Exception
  {
    current = root;
    SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
    parser.parse(file, HANDLER);
  }
  final DefaultHandler HANDLER = new DefaultHandler()
  {
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes)
    {
      if ("container".equals(qName))
      {
        String key = attributes.getValue("key");
        if (key.length()>0)
          current = current.get(key);
      }
      else if ("module".equals(qName))
      {
        current = current.get(attributes.getValue("key"));
        if (current == null)
          throw new IllegalArgumentException("module not found: " + attributes.getValue("key"));
        ((ConfigModule)current).setState(attributes.getValue("value"));
      }
      else if ("feature".equals(qName))
      {
        ConfigFeature feature = (ConfigFeature)current.get(attributes.getValue("key"));
        feature.selectKey(attributes.getValue("value"));
      }
    }
    @Override
    public void endElement(String uri, String localName, String qName)
    {
      if ("container".equals(qName))
      {
        current = current.parent;
      }
      else if ("module".equals(qName))
      {
        current = current.parent;
      }
    }
  };
  ConfigItem current;
}
