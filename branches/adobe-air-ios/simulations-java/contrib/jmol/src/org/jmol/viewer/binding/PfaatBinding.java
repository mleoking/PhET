package org.jmol.viewer.binding;

import org.jmol.viewer.ActionManager;

public class PfaatBinding extends JmolBinding {

  public PfaatBinding() {
    super("extendedSelect");
    setSelectBindings();
  }

  private void setSelectBindings() {
    bind(SINGLE_CLICK+LEFT, ActionManager.ACTION_select);    
    bind(SINGLE_CLICK+LEFT, ActionManager.ACTION_selectNone);    
    bind(SINGLE_CLICK+SHIFT+LEFT, ActionManager.ACTION_selectToggle);
    bind(SINGLE_CLICK+ALT+SHIFT+LEFT, ActionManager.ACTION_selectAndNot);
    bind(SINGLE_CLICK+ALT+LEFT, ActionManager.ACTION_selectOr);
  }


}

