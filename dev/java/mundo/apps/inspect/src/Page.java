

abstract class Page
{
  Page()
  {
  }
  void setMainWindow(InspectWindow w)
  {
    window=w;
  }
  abstract String getURI();
  abstract void load();

  protected InspectWindow window;
  protected String uri;
  public boolean reloadToRefresh = false;
}
