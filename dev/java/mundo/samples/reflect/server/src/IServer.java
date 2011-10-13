import rec.Record;

import org.mundo.annotation.mcRemote;

@mcRemote
public interface IServer
{
  Record getRecord();
  void printRecord(Record r);
}
