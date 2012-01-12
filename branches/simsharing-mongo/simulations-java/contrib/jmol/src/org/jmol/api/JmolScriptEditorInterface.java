package org.jmol.api;

import org.jmol.script.ScriptContext;

public interface JmolScriptEditorInterface {

  void setVisible(boolean b);

  void output(String message);

  String getText();

  void dispose();
  
  boolean isVisible();

  void notifyContext(ScriptContext property, Object[] data);

  void notifyScriptTermination();

  void notifyScriptStart();

  void setFilename(String filename);

}
