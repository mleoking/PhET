package org.jmol.api;

import java.awt.Component;

import org.jmol.viewer.Viewer;

public interface JmolModelKitInterface {

  public abstract JmolModelKitInterface getModelKit(Viewer viewer, Component parentFrame);

  public abstract void getMenus(boolean doTranslate);

  public abstract void show(int x, int y, char type);

}
