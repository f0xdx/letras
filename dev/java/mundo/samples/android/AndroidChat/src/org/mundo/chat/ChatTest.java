package org.mundo.chat;

import java.io.InputStreamReader;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import org.mundo.rt.LogEntry;
import org.mundo.rt.Mundo;
import org.mundo.rt.Logger;
import org.mundo.rt.Service;
import org.mundo.rt.Publisher;
import org.mundo.rt.IReceiver;
import org.mundo.rt.Message;
import org.mundo.rt.MessageContext;
import org.mundo.rt.TypedMap;

public class ChatTest extends Activity {
  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    scrollView = (ScrollView)findViewById(R.id.scrollView);
    textPane = (TextView)findViewById(R.id.textPane);
    inputLine = (EditText)findViewById(R.id.inputLine);
    sendButton = (Button)findViewById(R.id.sendButton);
    sendButton.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        String text = inputLine.getText().toString();
        TypedMap map = new TypedMap();
        map.putString("ln", text);
        publisher.send(new Message(map));
        textPane.append("("+text+")\n");
        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
        inputLine.setText("");
      }
    });
    
    Logger.getLogger("global").addHandler(LOG_HANDLER);
    try {
      Mundo.setConfigXML(new InputStreamReader(getClass().getClassLoader().getResourceAsStream("assets/node.conf.xml"), "UTF-8"));
    } catch(Exception x) {
      Logger.getLogger("global").warning("could not read node.conf.xml!");
    }
    Mundo.init();
    Service svc = new Service();
    Mundo.registerService(svc);
    publisher = svc.getSession().publish("lan", "chattest");
    svc.getSession().subscribe("lan", "chattest", CHAT_RECEIVER);
  }

  @Override
  protected void onDestroy() {
    Mundo.shutdown();
    super.onDestroy();
  }
    
  private final IReceiver CHAT_RECEIVER = new IReceiver() {
    public void received(Message msg, MessageContext ctx) {
      runOnUiThread(new PrintAction(msg.getMap().getString("ln")));
    }
  };
  
  class PrintAction implements Runnable {
    PrintAction(String l) {
      ln = l;
    }
    public void run() {
      textPane.append(ln+"\n");
    }
    private String ln;
  }
  
  private final Logger.IHandler LOG_HANDLER = new Logger.IHandler() {
    public void publish(LogEntry e) {
      if (e.getLevel() <= Logger.WARNING)
        runOnUiThread(new PrintAction(e.toString()));
    }
  };

  private Button sendButton;
  private TextView textPane;
  private EditText inputLine;
  private ScrollView scrollView;
  private Publisher publisher;
}
