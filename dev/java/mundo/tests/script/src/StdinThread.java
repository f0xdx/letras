import java.io.OutputStream;

class StdinThread extends Thread
{
  StdinThread(OutputStream s, byte[] b)
  {
    os=s;
    buffer=b;
  }
  public void run()
  {
    try {
      os.write(buffer);
      os.close();
    } catch(Exception x) {
      x.printStackTrace();
    }
  }
  OutputStream os;
  byte[] buffer;
}
