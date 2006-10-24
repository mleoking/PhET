/*
 * Open Source Physics software is free software as described near the bottom of this code file.
 *
 * For additional information and documentation on Open Source Physics please see:
 * <http://www.opensourcephysics.org/>
 */

package org.opensourcephysics.controls;
import java.text.DecimalFormat;
import java.awt.Frame;

/**
 * AbstractAnimation is a template for simple animations.
 *
 * Implement the doStep method to create an animation.  This method is called from the run method and when
 * the stepAnimation button is pressed.
 *
 * @author       Wolfgang Christian
 * @version 1.0
 */
public abstract class AbstractAnimation implements Animation, Runnable {
  protected Control control;     // the model's control
  protected volatile Thread animationThread;
  protected int delayTime = 100; // time between animation steps in milliseconds

  /** Field decimalFormat can be used to display time and other numeric values. */
  protected DecimalFormat decimalFormat = new DecimalFormat("0.00E0"); // default numeric format for messages

  /**
   * Sets the Control for this model and initializes the control's values.
   *
   * @param control
   */
  public void setControl(Control control) {
    this.control = control;
    if(control!=null) {
      control.setLockValues(true);
      resetAnimation(); // sets the control's default values
      control.setLockValues(false);
      if(control instanceof Frame) {
        ((Frame) control).pack();
      }
    }
  }

  /**
   * Gets the Control.
   *
   * @return the control
   */
  public Control getControl() {
    return control;
  }

  /**
   * Initializes the animation by reading parameters from the control.
   */
  public void initializeAnimation() {
    control.clearMessages();
  }

  /**
   * Does an animation step.
   */
  abstract protected void doStep();

  /**
   * Stops the animation.
   *
   * Sets the animationThread to null and waits for a join.
   */
  public synchronized void stopAnimation() {
    if(animationThread==null) {
      return;
    }
    Thread tempThread = animationThread; // temporary reference
    animationThread = null; // signal the animation to stop
    if(Thread.currentThread()==tempThread) {
      return; // cannot join own thread so return
    }
    if((tempThread!=null)&&(tempThread!=Thread.currentThread())) {
      try {                     // guard against an exception in applet mode
        tempThread.interrupt(); // get out of a sleep state
        tempThread.join(2000);  // wait up to 2 seconds
      } catch(InterruptedException e) {
        // System.out.println("excetpion in stop animation"+e);
      }
    }
  }

  /**
   * Determines if the animation is running.
   *
   * @return boolean
   */
  public final boolean isRunning() {
    return animationThread!=null;
  }

  /**
   * Steps the animation.
   */
  public synchronized void stepAnimation() {
    if(animationThread!=null) {
      stopAnimation();
    }
    doStep();
  }

  /**
   * Starts the animation.
   *
   * Use this method to start a timer or a thread.
   */
  public synchronized void startAnimation() {
    if(animationThread!=null) {
      return; // animation is running
    }
    animationThread = new Thread(this);
    animationThread.setPriority(Thread.MIN_PRIORITY);
    animationThread.setDaemon(true);
    animationThread.start(); // start the animation
  }

  /**
   * Resets the animation to a predefined state.
   */
  public void resetAnimation() {
    if(animationThread!=null) {
      stopAnimation(); // make sure animation is stopped
    }
    control.clearMessages();
  }

  /**
   * Implementation of Runnable interface.  DO NOT access this method directly.
   */
  public void run() {
    long sleepTime = delayTime;
    while(animationThread==Thread.currentThread()) {
      long currentTime = System.currentTimeMillis();
      doStep();
      // adjust the sleep time to try and achieve a constant animation rate
      // some VMs will hang if sleep time is less than 10
      sleepTime = Math.max(10, delayTime-(System.currentTimeMillis()-currentTime));
      try {
        Thread.sleep(sleepTime);
      } catch(InterruptedException ie) {}
    }
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
