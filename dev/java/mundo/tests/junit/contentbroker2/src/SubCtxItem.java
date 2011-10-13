import java.util.Random;
import org.mundo.annotation.mcSerialize;

@mcSerialize
public class SubCtxItem extends CtxItem
{
  SubCtxItem()
  {
    if (random==null)
      random = new Random();
    magic = random.nextInt();
  }
  @Override
  public boolean equals(Object o)
  {
    if (!(o instanceof SubCtxItem))
      return false;
    return magic == ((SubCtxItem)o).magic;
  }
  @Override
  public String toString()
  {
    return "{magic="+magic+"}";
  }

  int magic;
  private static Random random = null;
}
