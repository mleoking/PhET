package org.jmol.api;

public interface JmolAppConsoleInterface {

  public void setVisible(boolean b);

  public void sendConsoleEcho(String strEcho);

  public void sendConsoleMessage(String strInfo);

  public JmolScriptEditorInterface getScriptEditor();

  public JmolAppConsoleInterface getAppConsole(JmolViewer viewer);

  public void zap();

  public void dispose();

}
