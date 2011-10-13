import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.mundo.annotation.*;
import org.mundo.rt.Mundo;
import org.mundo.rt.Service;
import org.mundo.rt.Signal;

//@mcImport
import org.mundo.speech.synthesis.ITextToSpeech;
import org.mundo.speech.synthesis.DoITextToSpeech;
//@mcImport
import org.mundo.speech.recognition.IRecognitionListener;

class SpeechDemoService extends Service implements IRecognitionListener
{
  private static final String TTS_CHANNEL = "speech.tts";
  private static final String SR_CHANNEL = "speech.utterance";

  private DoITextToSpeech doTTS;

  public void init()
  {
    try
    {
      // Connect to Text-to-Speech (TTS) service
      doTTS = new DoITextToSpeech();
      Signal.connect(doTTS, session.publish("lan", TTS_CHANNEL));

      // Connect to Speech Recognition (SR) service
      Signal.connect(session.subscribe("lan", SR_CHANNEL), this);
    }
    catch(Exception x)
    {
      x.printStackTrace();
    }
  }
  /**
   * Called when the Speech Recognizer has recognized an utterance.
   */
  public void evalUtterance(String text) //RecognitionListener
  {
    System.out.println(text);
  }
  public void evalUtteranceExtended(String text, int a, int b)
  {
    System.out.println(text);
  }
  /**
   * Read user input from console and speak the entered text.
   */
  public void run()
  {
    try
    {
      BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
      String ln;
      while ( (ln=r.readLine())!=null && !ln.equals(".") )
      {
        doTTS.speak(ln, doTTS.ONEWAY);
      }
    }
    catch (Exception x)
    {
      x.printStackTrace();
    }
  }  
}

public class SpeechDemo
{
  public static void main(String args[])
  {
    Mundo.init();

    SpeechDemoService svc = new SpeechDemoService();
    Mundo.registerService(svc);
    svc.run();

    Mundo.shutdown();
  }
}
