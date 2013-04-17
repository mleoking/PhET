package org.jmol.viewer.binding;

import org.jmol.viewer.ActionManager;

public class JmolBinding extends Binding {

  public JmolBinding() {
    this("toggle");
    setSelectBindings();
  }
  
  public JmolBinding(String name) {
    super(name);
    setGeneralBindings();
    setPickBindings();
  }
    
  private void setSelectBindings() {
    // these are only utilized for  set picking select
    bind(DOUBLE_CLICK+LEFT, ActionManager.ACTION_select);
    bind(SINGLE_CLICK+LEFT, ActionManager.ACTION_selectToggleExtended);
  }

  protected void setGeneralBindings() {
    
    bind(DOUBLE_CLICK+LEFT, ActionManager.ACTION_center);
    bind(SINGLE_CLICK+CTRL+ALT+LEFT, ActionManager.ACTION_translate);
    bind(SINGLE_CLICK+CTRL+RIGHT, ActionManager.ACTION_translate);
    bind(DOUBLE_CLICK+SHIFT+LEFT, ActionManager.ACTION_translate); 
    bind(DOUBLE_CLICK+MIDDLE, ActionManager.ACTION_translate);

    bind(SINGLE_CLICK+LEFT, ActionManager.ACTION_rotate);
    bind(SINGLE_CLICK+ALT+LEFT, ActionManager.ACTION_rotateZ);
    bind(SINGLE_CLICK+SHIFT+RIGHT, ActionManager.ACTION_rotateZ);
    bind(SINGLE_CLICK+SHIFT+LEFT, ActionManager.ACTION_rotateZorZoom);
    bind(SINGLE_CLICK+MIDDLE, ActionManager.ACTION_rotateZorZoom);
    bind(WHEEL, ActionManager.ACTION_wheelZoom);
    bind(SINGLE_CLICK+LEFT, ActionManager.ACTION_slideZoom);

    bind(SINGLE_CLICK+LEFT, ActionManager.ACTION_navTranslate);
    
    bind(SINGLE_CLICK+CTRL+LEFT, ActionManager.ACTION_popupMenu);
    bind(SINGLE_CLICK+RIGHT, ActionManager.ACTION_popupMenu);
    bind(SINGLE_CLICK+LEFT, ActionManager.ACTION_clickFrank);

    bind(SINGLE_CLICK+CTRL+SHIFT+LEFT, ActionManager.ACTION_slab);
    bind(DOUBLE_CLICK+CTRL+SHIFT+LEFT, ActionManager.ACTION_depth); 
    bind(SINGLE_CLICK+CTRL+ALT+SHIFT+LEFT, ActionManager.ACTION_slabAndDepth);
    
    bind(SINGLE_CLICK+LEFT, ActionManager.ACTION_swipe);
    bind(SINGLE_CLICK+LEFT, ActionManager.ACTION_spinDrawObjectCCW);
    bind(SINGLE_CLICK+SHIFT+LEFT, ActionManager.ACTION_spinDrawObjectCW);

    bind(SINGLE_CLICK+ALT+SHIFT+LEFT, ActionManager.ACTION_dragSelected);
    bind(SINGLE_CLICK+ALT+LEFT, ActionManager.ACTION_rotateSelected);
    bind(SINGLE_CLICK+SHIFT+LEFT, ActionManager.ACTION_rotateBranch);

    bind(SINGLE_CLICK+SHIFT+LEFT, ActionManager.ACTION_dragLabel);    
    bind(SINGLE_CLICK+ALT+LEFT, ActionManager.ACTION_dragDrawPoint);
    bind(SINGLE_CLICK+SHIFT+LEFT, ActionManager.ACTION_dragDrawObject);
    
    bind(DOUBLE_CLICK+SHIFT+LEFT, ActionManager.ACTION_reset);
    bind(DOUBLE_CLICK+MIDDLE, ActionManager.ACTION_reset); 
    
    bind(DOUBLE_CLICK+LEFT, ActionManager.ACTION_stopMotion); 
    

  }
  
  protected void setPickBindings() {
    bind(SINGLE_CLICK+LEFT, ActionManager.ACTION_dragAtom);
    bind(SINGLE_CLICK+LEFT, ActionManager.ACTION_dragMinimize);
    bind(SINGLE_CLICK+LEFT, ActionManager.ACTION_dragMinimizeMolecule);
    bind(SINGLE_CLICK+LEFT, ActionManager.ACTION_pickAtom);
    bind(SINGLE_CLICK+LEFT, ActionManager.ACTION_pickPoint);
    bind(SINGLE_CLICK+LEFT, ActionManager.ACTION_pickLabel);
    bind(SINGLE_CLICK+LEFT, ActionManager.ACTION_pickMeasure);
    bind(DOUBLE_CLICK+LEFT, ActionManager.ACTION_setMeasure);
    bind(SINGLE_CLICK+LEFT, ActionManager.ACTION_pickIsosurface); // requires set drawPicking     
    bind(SINGLE_CLICK+CTRL+SHIFT+LEFT, ActionManager.ACTION_pickNavigate);      
    bind(SINGLE_CLICK+LEFT, ActionManager.ACTION_deleteAtom);
    bind(SINGLE_CLICK+LEFT, ActionManager.ACTION_deleteBond);
    bind(SINGLE_CLICK+LEFT, ActionManager.ACTION_connectAtoms);
    bind(SINGLE_CLICK+LEFT, ActionManager.ACTION_assignNew);
  }
  
}

