package org.jmol.viewer.binding;

import org.jmol.viewer.ActionManager;

public class DragBinding extends JmolBinding {

  public DragBinding() {
    super("drag");
    setSelectBindings();
    
    bind(DOWN+LEFT, ActionManager.ACTION_selectAndDrag);
    bind(SINGLE_CLICK+LEFT, ActionManager.ACTION_dragSelected);
    bind(SINGLE_CLICK+LEFT, ActionManager.ACTION_pickAtom);
  }
    
  private void setSelectBindings() {
    bind(SINGLE_CLICK+LEFT, ActionManager.ACTION_select);
    bind(SINGLE_CLICK+SHIFT+LEFT, ActionManager.ACTION_selectToggle);
    bind(SINGLE_CLICK+ALT+LEFT, ActionManager.ACTION_selectOr);
    bind(SINGLE_CLICK+ALT+SHIFT+LEFT, ActionManager.ACTION_selectAndNot);
  }


}

