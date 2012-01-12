package org.jmol.viewer.binding;

import org.jmol.viewer.ActionManager;

public class RasmolBinding extends JmolBinding {

  public RasmolBinding() {
    super("selectOrToggle");
    setSelectBindings();
  }
    
  private void setSelectBindings() {
    bind(SINGLE_CLICK+LEFT, ActionManager.ACTION_select);
    bind(SINGLE_CLICK+SHIFT+LEFT, ActionManager.ACTION_selectToggle);
  }

}

