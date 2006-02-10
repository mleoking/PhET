/*
 * Open Source Physics software is free software as described near the bottom of this code file.
 *
 * For additional information and documentation on Open Source Physics please see: 
 * <http://www.opensourcephysics.org/>
 */

package org.opensourcephysics.display.dialogs;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * String constants for OSPControls.
 *
 * @author Wolfgang Christian
 * @version 1.0
 */
public class DialogsRes {
  public static final String AUTOSCALE_AUTOSCALE;
  public static final String AUTOSCALE_AUTO;
  public static final String AUTOSCALE_OK;
  public static final String AUTOSCALE_ZOOM_WARNING;
  public static final String SCALE_SCALE;
  public static final String SCALE_MIN;
  public static final String SCALE_MAX;
  public static final String SCALE_AUTO;
  public static final String SCALE_CANCEL;
  public static final String SCALE_OK;
  public static final String LOG_SCALE;
  public static final String LOG_X;
  public static final String LOG_Y;
  public static final String LOG_OK;
  public static final String LOG_WARNING;

  private DialogsRes() {}

  private static String getString(final ResourceBundle bundle, final String key) {
    try {
      return bundle.getString(key);
    } catch(final MissingResourceException ex) {
      // assert(false) : ex.getMessage();
      return '|'+key+'|';
    }
  }

  static {
    final String BUNDLE_NAME = "org.opensourcephysics.resources.display.dialogs_res";
    final ResourceBundle BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME, Locale.getDefault(), DialogsRes.class.getClassLoader());
    AUTOSCALE_AUTOSCALE = getString(BUNDLE, "AUTOSCALE_AUTOSCALE");
    AUTOSCALE_AUTO = getString(BUNDLE, "AUTOSCALE_AUTO");
    AUTOSCALE_OK = getString(BUNDLE, "AUTOSCALE_OK");
    AUTOSCALE_ZOOM_WARNING = getString(BUNDLE, "AUTOSCALE_ZOOM_WARNING");
    SCALE_SCALE = getString(BUNDLE, "SCALE_SCALE");
    SCALE_MIN = getString(BUNDLE, "SCALE_MIN");
    SCALE_MAX = getString(BUNDLE, "SCALE_MAX");
    SCALE_AUTO = getString(BUNDLE, "SCALE_AUTO");
    SCALE_CANCEL = getString(BUNDLE, "SCALE_CANCEL");
    SCALE_OK = getString(BUNDLE, "SCALE_OK");
    LOG_SCALE = getString(BUNDLE, "LOG_SCALE");
    LOG_X = getString(BUNDLE, "LOG_X");
    LOG_Y = getString(BUNDLE, "LOG_Y");
    LOG_OK = getString(BUNDLE, "LOG_OK");
    LOG_WARNING = getString(BUNDLE, "LOG_WARNING");
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
