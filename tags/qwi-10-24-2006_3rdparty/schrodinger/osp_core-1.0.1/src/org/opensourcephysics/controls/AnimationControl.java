/*
 * Open Source Physics software is free software as described near the bottom of this code file.
 *
 * For additional information and documentation on Open Source Physics please see:
 * <http://www.opensourcephysics.org/>
 */

package org.opensourcephysics.controls;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.JButton;
import org.opensourcephysics.display.GUIUtils;
import javax.swing.SwingUtilities;

/**
 *  A GUI consisting of an input text area, a message area, and various buttons
 *  to initialize and control an Animation.
 *
 * @author       Wolfgang Christian
 * @author       Joshua Gould
 * @version 1.0
 */
public class AnimationControl extends OSPControl {
  final static String resetToolTipText = ControlsRes.ANIMATION_RESET_TIP;
  final static String initToolTipText = ControlsRes.ANIMATION_INIT_TIP;
  final static String startToolTipText = ControlsRes.ANIMATION_START_TIP;
  final static String stopToolTipText = ControlsRes.ANIMATION_STOP_TIP;
  final static String newToolTipText = ControlsRes.ANIMATION_NEW_TIP;
  final static String stepToolTipText = ControlsRes.ANIMATION_STEP_TIP;
  boolean stepModeEditing = true; // enables input editing while single stepping
  ArrayList customButtons = new ArrayList(); // list of custom buttons, custom buttons are enabled when the animation is stopped
  JButton startBtn = new JButton(ControlsRes.ANIMATION_INIT); // changes to start, stop
  JButton stepBtn = new JButton(ControlsRes.ANIMATION_STEP);
  JButton resetBtn = new JButton(ControlsRes.ANIMATION_RESET); // changes to new

  /**
   *  AnimationControl constructor.
   *
   * @param  animation  the animation for this AnimationControl
   */
  public AnimationControl(Animation animation) {
    super(animation);
    if(model!=null) {
      String name = model.getClass().getName();
      setTitle(name.substring(1+name.lastIndexOf("."))+" Control");
    }
    startBtn.addActionListener(new StartBtnListener());
    startBtn.setToolTipText(initToolTipText);
    stepBtn.addActionListener(new StepBtnListener());
    stepBtn.setToolTipText(stepToolTipText);
    resetBtn.addActionListener(new ResetBtnListener());
    resetBtn.setToolTipText(resetToolTipText);
    stepBtn.setEnabled(false);
    buttonPanel.add(startBtn);
    buttonPanel.add(stepBtn);
    buttonPanel.add(resetBtn);
    validate();
    pack();
  }

  /**
   *  Adds a custom button to the control's frame.
   *
   * @param  methodName   the name of the method in the Animation to be invoked.
   *      The method must have no parameters.
   * @param  text         the button's text label
   * @param  toolTipText  the button's tool tip text
   * @return              the custom button
   */
  public JButton addButton(String methodName, String text, String toolTipText) {
    JButton b = super.addButton(methodName, text, toolTipText);
    customButtons.add(b);
    return b;
  }

  /**
   *  Signals the control that the animation has completed.
   *  The control should reset itself in preparation for a new
   *  animation. The given message is printed in the message area.
   *
   * @param  message
   */
  public void calculationDone(String message) {
    // always update a Swing component from the event thread
    if(model instanceof Animation) {
      ((Animation) model).stopAnimation();
    }
    final String msg = message;
    Runnable doNow = new Runnable() {
      public void run() {
        startBtnActionPerformed(new ActionEvent(this, 0, ControlsRes.ANIMATION_STOP));
        resetBtnActionPerformed(new ActionEvent(this, 0, ControlsRes.ANIMATION_NEW));
        resetBtn.setEnabled(true);
        org.opensourcephysics.display.GUIUtils.enableMenubars(true);
        println(msg);
      }
    };
    try {
      if(SwingUtilities.isEventDispatchThread()) {
        doNow.run();
      } else { // paint within the event thread
        SwingUtilities.invokeAndWait(doNow);
      }
    } catch(java.lang.reflect.InvocationTargetException ex1) {}
    catch(InterruptedException ex1) {}
  }

  /**
   * Method startBtnActionPerformed
   *
   * @param e
   */
  void startBtnActionPerformed(ActionEvent e) {
    // table.getDefaultEditor(Object.class).stopCellEditing();
    if(e.getActionCommand().equals(ControlsRes.ANIMATION_INIT)) {
      stepBtn.setEnabled(true);
      startBtn.setText(ControlsRes.ANIMATION_START);
      startBtn.setToolTipText(startToolTipText);
      resetBtn.setText(ControlsRes.ANIMATION_NEW);
      resetBtn.setToolTipText(newToolTipText);
      resetBtn.setEnabled(true);
      readItem.setEnabled(stepModeEditing);
      table.setEnabled(stepModeEditing);
      messageTextArea.setEditable(false);
      org.opensourcephysics.display.GUIUtils.showDrawingAndTableFrames();
      GUIUtils.clearDrawingFrameData(false);
      if(model==null) {
        println("This AnimationControl's model is null.");
      } else {
        ((Animation) model).initializeAnimation();
      }
    } else if(e.getActionCommand().equals(ControlsRes.ANIMATION_START)) {
      setCustomButtonsEnabled(false);
      startBtn.setText(ControlsRes.ANIMATION_STOP);
      startBtn.setToolTipText(stopToolTipText);
      stepBtn.setEnabled(false);
      resetBtn.setEnabled(false);
      readItem.setEnabled(false);
      table.setEnabled(false);
      org.opensourcephysics.display.GUIUtils.enableMenubars(false);
      ((Animation) model).startAnimation();
    } else { // action command = Stop
      startBtn.setText(ControlsRes.ANIMATION_START);
      setCustomButtonsEnabled(true);
      startBtn.setToolTipText(startToolTipText);
      stepBtn.setEnabled(true);
      resetBtn.setEnabled(true);
      org.opensourcephysics.display.GUIUtils.enableMenubars(true);
      readItem.setEnabled(stepModeEditing);
      table.setEnabled(stepModeEditing);
      ((Animation) model).stopAnimation();
    }
  }

  /**
   * Method resetBtnActionPerformed
   *
   * @param e
   */
  void resetBtnActionPerformed(ActionEvent e) {
    if(e.getActionCommand().equals(ControlsRes.ANIMATION_RESET)) {
      GUIUtils.clearDrawingFrameData(true);
      if(model==null) {
        println("This AnimationControl's model is null.");
        return;
      }
      ((Animation) model).resetAnimation();
      if(xmlDefault!=null) {
        xmlDefault.loadObject(getOSPApp());
      }
    } else { // action command = New
      startBtn.setText(ControlsRes.ANIMATION_INIT);
      startBtn.setToolTipText(initToolTipText);
      resetBtn.setText(ControlsRes.ANIMATION_RESET);
      resetBtn.setToolTipText(resetToolTipText);
      stepBtn.setEnabled(false);
      readItem.setEnabled(true);
      table.setEnabled(true);
      messageTextArea.setEditable(true);
      setCustomButtonsEnabled(true);
    }
  }

  /**
   * Method stepBtnActionPerformed
   *
   * @param e
   */
  void stepBtnActionPerformed(ActionEvent e) {
    ((Animation) model).stepAnimation();
  }

  private void setCustomButtonsEnabled(boolean enabled) {
    if(customButtons!=null) {
      for(Iterator it = customButtons.iterator();it.hasNext();) {
        ((JButton) it.next()).setEnabled(enabled);
      }
    }
  }

  /**
   * Class StartBtnListener
   */
  class StartBtnListener implements ActionListener {

    /**
     * Method actionPerformed
     *
     * @param e
     */
    public void actionPerformed(ActionEvent e) {
      startBtnActionPerformed(e);
    }
  }

  /**
   * Class ResetBtnListener
   */
  class ResetBtnListener implements ActionListener {

    /**
     * Method actionPerformed
     *
     * @param e
     */
    public void actionPerformed(ActionEvent e) {
      resetBtnActionPerformed(e);
    }
  }

  /**
   * Class StepBtnListener
   */
  class StepBtnListener implements ActionListener {

    /**
     * Method actionPerformed
     *
     * @param e
     */
    public void actionPerformed(ActionEvent e) {
      stepBtnActionPerformed(e);
    }
  }

  /**
   * Returns an XML.ObjectLoader to save and load data for this object.
   *
   * @return the object loader
   */
  public static XML.ObjectLoader getLoader() {
    return new AnimationControlLoader();
  }

  /**
   * A class to save and load data for OSPControls.
   */
  static class AnimationControlLoader extends OSPControlLoader {

    /**
     * Saves object data to an ObjectElement.
     *
     * @param element the element to save to
     * @param obj the object to save
     */
    public void saveObject(XMLControl element, Object obj) {
      AnimationControl control = (AnimationControl) obj;
      if(control.startBtn.getText().equals(ControlsRes.ANIMATION_STOP)) {
        control.startBtn.doClick(); // stop the animation if it is running
      }
      element.setValue("initialize_mode", control.startBtn.getText().equals(ControlsRes.ANIMATION_INIT));
      super.saveObject(element, obj);
    }

    /**
     * Creates an object using data from an ObjectElement.
     *
     * @param element the element
     * @return the newly created object
     */
    public Object createObject(XMLControl element) {
      return new AnimationControl(null);
    }

    /**
     * Loads an object with data from an ObjectElement.
     *
     * @param element the element
     * @param obj the object
     * @return the loaded object
     */
    public Object loadObject(XMLControl element, Object obj) {
      AnimationControl animationControl = (AnimationControl) obj;
      if(animationControl.startBtn.getText().equals(ControlsRes.ANIMATION_STOP)) {
        animationControl.startBtn.doClick(); // stop the animation if it is running
      }
      boolean initMode = element.getBoolean("initialize_mode");
      element.setValue("initialize_mode", null); // don't show this internal parameter
      super.loadObject(element, obj); // load the control's parameters and the model
      // put the animation into the initialize state if it was in this state when it was stopped
      if(initMode&&animationControl.startBtn.getText().equals(ControlsRes.ANIMATION_START)) {
        animationControl.resetBtn.doClick();
      }
      if(!initMode&&animationControl.startBtn.getText().equals(ControlsRes.ANIMATION_INIT)) {
        animationControl.startBtn.doClick();
      }
      animationControl.clearMessages();
      return obj;
    }
  }

  /**
   * Creates an animation control and establishes communication between the control and the model.
   *
   * @param model Animation
   * @return AnimationControl
   */
  public static AnimationControl createApp(Animation model) {
    AnimationControl control = new AnimationControl(model);
    model.setControl(control);
    return control;
  }

  /**
   * Creates a animation control and establishes communication between the control and the model.
   * Initial parameters are set using the xml data.
   *
   * @param model Animation
   * @param xml String[]
   * @return AnimationControl
   */
  public static AnimationControl createApp(Animation model, String[] xml) {
    AnimationControl control = createApp(model);
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
