package org.mundo.service.lbsstatus;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

import javax.swing.JFrame;

public class StatusFrame extends JFrame
{
  public StatusFrame(int width, int y)
  {
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    setBounds((screenSize.width-width)/2, y, width, 20);
    setTitle("StatusFrame");
    text = "";
  }
  public void highlight(boolean b)
  {
    hi = b;
    repaint();
  }
  public void setText(String t)
  {
    text = t;
    repaint();
    
    Timer timer = new Timer(5000, new ActionListener() {
      public void actionPerformed(ActionEvent ev) {
        text = "";
        repaint();
      }
    });
    timer.setRepeats(false);
    timer.start();
  }
  public void paint(Graphics g)
  {
    g.setColor(hi ? hilightColor : Color.WHITE);
    g.fillRect(0, 0, getWidth(), getHeight());
    g.setColor(Color.BLACK);
    g.drawString(text, 4, 16);
  }
  private String text;
  private boolean hi;
  private static final Color hilightColor = new Color(0xff, 0x80, 0x80);
}
