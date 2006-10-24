/*
 * Open Source Physics software is free software as described near the bottom of this code file.
 *
 * For additional information and documentation on Open Source Physics please see:
 * <http://www.opensourcephysics.org/>
 */

package org.opensourcephysics.controls;
import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.awt.Color;

/**
 * An AnimationControl that controls the editing of parameters.
 *
 * @author Wolfgang Christian
 * @version 1.0
 */
public class SimulationControl extends AnimationControl implements SimControl {
  Set fixedParameters = new HashSet();

  /**
   * Constructs a SIPAnimationControl for the given animation.
   * @param animation Animation
   */
  public SimulationControl(Simulation animation) {
    super(animation);
  }

  /**
   * Sets the fixed property of the given parameter.
   * Fixed parameters can only be changed before initialization.
   */
  public void setParameterToFixed(String name, boolean fixed) {
    if(fixed) {
      this.fixedParameters.add(name);
    } else {
      this.fixedParameters.remove(name);
    }
    table.refresh();
  }

  /**
   * Determines if the given parameter is fixed and can only be changed during initialization.
   * @param name String
   * @return boolean
   */
  public boolean isParamterFixed(String name) {
    return fixedParameters.contains(name);
  }

  /**
   * Stores an object in the control
   * that can only be edited when the control is in initialization mode.
   *
   * @param name
   * @param val
   */
  public void setValue(String name, Object val) {
    super.setValue(name, val);
    fixedParameters.add(name);
  }

  /**
 * Stores an object in the control that can be edited after initialization.
 *
 * @param name
 * @param val
 */
  public void setAdjustableValue(String name, Object val) {
    super.setValue(name, val);
    fixedParameters.remove(name);
  }

  /**
   * Stores a name and a double value in the control
   * that can only be edited when the control is in initialization mode.
   *
   * @param name
   * @param val
   */
  public void setValue(String name, double val) {
    super.setValue(name, val);
    fixedParameters.add(name);
  }

  /**
   * Stores a double in the control that can be edited after initialization.
   *
   * @param name
   * @param val
   */
  public void setAdjustableValue(String name, double val) {
    super.setValue(name, val);
    fixedParameters.remove(name);
  }

  /**
   * Stores a name and an integer value in the control
   * that can only be edited when the control is in initialization mode.
   *
   * @param name
   * @param val
   */
  public void setValue(String name, int val) {
    super.setValue(name, val);
    fixedParameters.add(name);
  }

  /**
   * Stores an integer in the control that can be edited after initialization.
   *
   * @param name
   * @param val
   */
  public void setAdjustableValue(String name, int val) {
    super.setValue(name, val);
    fixedParameters.remove(name);
  }

  /**
   * Stores a name and a boolean value in the control
   * that can only be edited when the control is in initialization mode.
   *
   * @param name
   * @param val
   */
  public void setValue(String name, boolean val) {
    super.setValue(name, val);
    fixedParameters.add(name);
  }

  /**
 * Removes a parameter from this control.
 *
 * @param name
 */
  public void removeParameter(String name) {
    super.removeParameter(name);
    fixedParameters.remove(name);
  }

  /**
   * Stores a boolean in the control that can be edited after initialization.
   *
   * @param name
   * @param val
   */
  public void setAdjustableValue(String name, boolean val) {
    super.setValue(name, val);
    fixedParameters.remove(name);
  }

  /**
   * Resets the control by putting the control into its initialization state.
   *
   * Fixed parameters are editiable when the control is in the initialization state.
   *
   * @param e
   */
  void resetBtnActionPerformed(ActionEvent e) {
    Iterator it = fixedParameters.iterator();
    while(it.hasNext()) {
      String par = (String) it.next();
      table.setEditable(par, true);
      table.setBackgroundColor(par, Color.WHITE);
    }
    table.refresh();
    super.resetBtnActionPerformed(e);
  }

  /**
   * Starts, stops, or steps the animation depending on the mode.
   *
   * @param e
   */
  void startBtnActionPerformed(ActionEvent e) {
    if(e.getActionCommand().equals(ControlsRes.ANIMATION_INIT)) {
      // animation is being initialized and fixed parameters are no longer editable
      table.setEditable(true);
      Iterator it = fixedParameters.iterator();
      while(it.hasNext()) {
        String par = (String) it.next();
        table.setEditable(par, false);
        table.setBackgroundColor(par, PANEL_BACKGROUND);
      }
   } else if (e.getActionCommand().equals(ControlsRes.ANIMATION_START)){
      Iterator it = control.getPropertyNames().iterator();
      while (it.hasNext()){
         String par = (String) it.next();
         table.setEditable(par, false);
         table.setBackgroundColor(par, PANEL_BACKGROUND);
      }
   } else if (e.getActionCommand().equals(ControlsRes.ANIMATION_STOP)){
      Iterator it = control.getPropertyNames().iterator();
      while (it.hasNext()){
         String par = (String) it.next();
         if(fixedParameters.contains(par))continue;
         table.setEditable(par, true);
         table.setBackgroundColor(par, Color.WHITE);
      }
   }
   table.refresh();
   super.startBtnActionPerformed(e);
   }
  /**
   * Creates a SIP animation control and establishes communication between the control and the model.
   *
   * @param model SIPAnimation
   * @return AnimationControl
   */
  public static SimulationControl createApp(Simulation model) {
    SimulationControl control = new SimulationControl(model);
    model.setControl(control);
    return control;
  }

  /**
 * Creates a simulation control and establishes communication between the control and the model.
 * Initial parameters are set using the xml data.
 *
 * @param model Simulation
 * @param xml String[]
 * @return SimulationControl
 */
  public static SimulationControl createApp(Simulation model, String[] xml) {
    SimulationControl control = createApp(model);
    control.loadXML(xml);
    return control;
  }
}

/*
 * Open Source Physics software is free software; you can redistribute
 * it and/or modify it under the terms of the GNU General Public License (GPL) as
 * published by the Free Software Foundation; either version 2 of the License,
 * or(at your option) any later version.

 * Code that uses any portion of the code in the org.opensourcephysics package
 * or any subpackage (subdirectory) of this package must must also be be released
 * under the GNU GPL license.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston MA 02111-1307 USA
 * or view the license online at http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2007  The Open Source Physics project
 *                     http://www.opensourcephysics.org
 */
