package org.mundo.service.canusb;

//@mcRemote
public interface ICAN
{
  public boolean send(int msgId, byte[] payload);
}
