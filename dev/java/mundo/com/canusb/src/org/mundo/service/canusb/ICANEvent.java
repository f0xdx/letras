package org.mundo.service.canusb;

//@mcRemote
public interface ICANEvent
{
  public void busConnected();
  public void busDisconnected();
  public void messageReceived(int msgId, byte[] payload);
}
