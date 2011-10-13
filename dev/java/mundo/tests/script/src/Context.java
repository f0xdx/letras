import java.util.HashMap;
import java.util.ArrayList;

import org.mundo.rt.Logger;
import org.mundo.rt.Session;
import org.mundo.rt.Publisher;
import org.mundo.rt.TypedMap;

class Context
{
  Session session;
  HashMap<String,SubscriptionEntry> subscribers = new HashMap<String,SubscriptionEntry>();
  HashMap<String,Publisher> publishers = new HashMap<String,Publisher>();
  int execCnt=0;
  int timeout=60*10;
  HashMap<String,Boolean> dependHash = new HashMap<String,Boolean>();
  int totalCnt = 0;
  int expectCnt = 0;
  int returnCode = 0;
  int startedCnt = 0;
  Logger log;
  TypedMap vars = new TypedMap();
  ArrayList<ExecThread> execThreads = new ArrayList<ExecThread>();

  static boolean isNameChar(char c)
  {
    return Character.isLetter(c) || Character.isDigit(c) || c=='-' || c=='_';
  }
  String expandVars(String s)
  {
    try
    {
      int start, i;
      for(;;)
      {
        start=s.indexOf('$');
        if (start<0)
          return s;
        i=start+1;
        while (i<s.length() && isNameChar(s.charAt(i)))
          i++;
        s=s.substring(0, start)+   
        vars.getString(s.substring(start+1, i))+
        s.substring(i);
      }
    }
    catch(Exception x)
    {
    }
    return s;
  }
}
