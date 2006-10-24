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
 * AbstractCalculation is a template for simple calculations.
 *
 * Implement the calculate method to create a calculation.
 *
 * @author       Wolfgang Christian
 * @version 1.0
 */
public abstract class AbstractCalculation implements Calculation {
  protected Control control;                                           // the Calculation's control
  protected DecimalFormat decimalFormat = new DecimalFormat("0.00E0"); // display format for messages

  /**
   * Sets object that controls this calculation.
   *
   * The calculation should use this method to register its parameters with the control.
   * This insures that the control displays the program's parameters when it appears onscreen.
   *
   * @param control
   */
  public void setControl(Control control) {
    this.control = control;
    if(control!=null) {
      control.setLockValues(true);
      resetCalculation();
      control.setLockValues(false);
      if(control instanceof Frame) {
        ((Frame) control).pack();
      }
    }
  }

  /**
   * Does the calculation.
   */
  public abstract void calculate();

  /**
   * Resets the calculation to a predefined state.
   */
  public void resetCalculation() {
    control.clearMessages();
    reset();
  }

  /**
   * Resets the program to its default state.
   *
   * Override this method to set the program's parameters.
   */
  public void reset() {}
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
