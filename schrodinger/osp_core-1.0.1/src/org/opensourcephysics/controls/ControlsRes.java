/*
 * Open Source Physics software is free software as described near the bottom of this code file.
 *
 * For additional information and documentation on Open Source Physics please see: 
 * <http://www.opensourcephysics.org/>
 */

package org.opensourcephysics.controls;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * String constants for OSPControls.
 *
 * @author Wolfgang Christian
 * @version 1.0
 */
public class ControlsRes {
  public static final String ANIMATION_NEW;
  public static final String ANIMATION_INIT;
  public static final String ANIMATION_STEP;
  public static final String ANIMATION_RESET;
  public static final String ANIMATION_START;
  public static final String ANIMATION_STOP;
  public static final String ANIMATION_RESET_TIP;
  public static final String ANIMATION_INIT_TIP;
  public static final String ANIMATION_START_TIP;
  public static final String ANIMATION_STOP_TIP;
  public static final String ANIMATION_NEW_TIP;
  public static final String ANIMATION_STEP_TIP;
  public static final String CALCULATION_CALC;
  public static final String CALCULATION_RESET;
  public static final String CALCULATION_CALC_TIP;
  public static final String CALCULATION_RESET_TIP;

  private ControlsRes() {}

  private static String getString(final ResourceBundle bundle, final String key) {
    try {
      return bundle.getString(key);
    } catch(final MissingResourceException ex) {
      // assert(false) : ex.getMessage();
      return '|'+key+'|';
    }
  }

  static {
    final String BUNDLE_NAME = "org.opensourcephysics.resources.controls.controls_res";
    final ResourceBundle BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME, Locale.getDefault(), ControlsRes.class.getClassLoader());
    ANIMATION_NEW = getString(BUNDLE, "ANIMATION_NEW");
    ANIMATION_INIT = getString(BUNDLE, "ANIMATION_INIT");
    ANIMATION_STEP = getString(BUNDLE, "ANIMATION_STEP");
    ANIMATION_RESET = getString(BUNDLE, "ANIMATION_RESET");
    ANIMATION_START = getString(BUNDLE, "ANIMATION_START");
    ANIMATION_STOP = getString(BUNDLE, "ANIMATION_STOP");
    ANIMATION_RESET_TIP = getString(BUNDLE, "ANIMATION_RESET_TIP");
    ANIMATION_INIT_TIP = getString(BUNDLE, "ANIMATION_INIT_TIP");
    ANIMATION_START_TIP = getString(BUNDLE, "ANIMATION_START_TIP");
    ANIMATION_STOP_TIP = getString(BUNDLE, "ANIMATION_STOP_TIP");
    ANIMATION_NEW_TIP = getString(BUNDLE, "ANIMATION_NEW_TIP");
    ANIMATION_STEP_TIP = getString(BUNDLE, "ANIMATION_STEP_TIP");
    CALCULATION_CALC = getString(BUNDLE, "CALCULATION_CALC");
    CALCULATION_RESET = getString(BUNDLE, "CALCULATION_RESET");
    CALCULATION_CALC_TIP = getString(BUNDLE, "CALCULATION_CALC_TIP");
    CALCULATION_RESET_TIP = getString(BUNDLE, "CALCULATION_RESET_TIP");
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
