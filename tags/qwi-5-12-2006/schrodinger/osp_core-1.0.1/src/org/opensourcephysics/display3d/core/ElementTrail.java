/*
 * Open Source Physics software is free software as described near the bottom of this code file.
 *
 * For additional information and documentation on Open Source Physics please see: 
 * <http://www.opensourcephysics.org/>
 */

package org.opensourcephysics.display3d.core;
import org.opensourcephysics.controls.XMLControl;

/**
 * <p>Title: ElementTrail</p>
 * Description:<p>A trail of 3D pixels on the screen.</p>
 * This object is often used to show the path of a moving object.
 * @author Francisco Esquembre
 * @version March 2005
 */
public interface ElementTrail extends Element {

  /**
   * Adds a new (x,y,z) point to the trail.
   * @param x double
   * @param y double
   * @param z double
   */
  public void addPoint(double x, double y, double z);

  /**
   * Adds a new double[] point to the trail.
   * @param point double[] The array with the coordinates of the point.
   * If the length of the array is 2, the coordinates are asumed to be X
   * and Y (Z=0). If it is 3, then X, Y, and Z (as usual).
   */
  public void addPoint(double[] point);

  /**
   * Sets the maximum number of points for the trail.
   * Once the maximum is reached, adding a new point will cause
   * remotion of the first one. This is useful to keep trails
   * down to a reasonable size, since very long trails can slow
   * down the rendering (in certain implementations).
   * If the value is 0 (the default) the trail grows forever
   * without discarding old points.
   * @param maximum int
   */
  public void setMaximumPoints(int maximum);

  /**
   * Returns the maximum number of points allowed for the trail
   * @return int
   */
  public int getMaximumPoints();

  /**
   * Sets the connected flag.
   * Successive points are connected by a segment if this flag is true.
   * Each point is marked as a colored pixel if the trail is not connected.
   * Setting it temporarily to false helps create discontinuous trails.
   * @param connected boolean
   */
  public void setConnected(boolean connected);

  /**
   * Gets the connected flag.
   * @see #setConnected(boolean)
   */
  public boolean isConnected();

  /**
   * Starts a new (x,y,z) trail segment by moving to a new point
   * without drawing. (Equivalent to setting the connected flag
   * to false and adding one singlepoint, then setting the flag
   * back to true.)
   * @param x double
   * @param y double
   * @param z double
   */
  public void moveToPoint(double x, double y, double z);

  /**
   * Clears all points from the trail.
   */
  public void clear();

  // ----------------------------------------------------
  // XML loader
  // ----------------------------------------------------
  static abstract class Loader extends Element.Loader {
    public void saveObject(XMLControl control, Object obj) {
      super.saveObject(control, obj);
      ElementTrail element = (ElementTrail) obj;
      control.setValue("maximum", element.getMaximumPoints());
      control.setValue("connected", element.isConnected());
      // Don't save the points since loadObject will clear the trail
    }

    public Object loadObject(XMLControl control, Object obj) {
      super.loadObject(control, obj);
      ElementTrail element = (ElementTrail) obj;
      element.setMaximumPoints(control.getInt("maximum"));
      element.setConnected(control.getBoolean("connected"));
      // This implies element.clear()
      return obj;
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
