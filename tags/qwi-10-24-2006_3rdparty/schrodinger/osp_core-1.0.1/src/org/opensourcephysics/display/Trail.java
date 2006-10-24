/*
 * Open Source Physics software is free software as described near the bottom of this code file.
 *
 * For additional information and documentation on Open Source Physics please see: 
 * <http://www.opensourcephysics.org/>
 */

package org.opensourcephysics.display;
import java.awt.*;
import java.awt.geom.*;
import org.opensourcephysics.controls.*;

/**
 * Title:        Trail
 * Description:  A trail of pixels on the screen.  This object is often used to
 * show the path of a moving object.
 *
 * @author       Wolfgang Christian
 * @version 1.0
 */
public class Trail implements Drawable {
  private GeneralPath generalPath = new GeneralPath();
  private int numpts = 0;           // the number of points in the trail
  private boolean connected = true;
  public Color color = Color.black; // changing the color is harmless so this can be public

  /**
   * Adds a point to the trail.
   * @param x double
   * @param y double
   */
  public synchronized void addPoint(double x, double y) {
    if(!connected||numpts==0) {
      generalPath.moveTo((float) x, (float) y);
    }
    generalPath.lineTo((float) x, (float) y);
    numpts++;
  }

  /**
   * Sets the connectd flag.
   *
   * Successive points are connetected by straight lines.
   * Each point is marked as a colored pixel if the trail is not connected.
   *
   * @param connected boolean
   */
  public void setConnected(boolean connected) {
    this.connected = connected;
  }

  /**
   * Gets the connected flag.
   *
   * @param connected boolean
   */
  public boolean isConnected(boolean connected) {
    return connected;
  }

  /**
   * Starts a new trail segment by moving to a new point without drawing.
   * @param x double
   * @param y double
   */
  public synchronized void moveToPoint(double x, double y) {
    generalPath.moveTo((float) x, (float) y);
    numpts++;
  }

  /**
   * Clears all points from the trail.
   */
  public synchronized void clear() {
    numpts = 0;
    generalPath.reset();
  }

  /**
   * Draw the trail on the panel.
   * @param g
   */
  public void draw(DrawingPanel panel, Graphics g) {
    if(numpts==0) {
      return;
    }
    Graphics2D g2 = (Graphics2D) g;
    g2.setColor(color);
    // transform from world to pixel coordinates
    Shape s = generalPath.createTransformedShape(panel.getPixelTransform());
    g2.draw(s);
  }

  /**
 * Returns the XML.ObjectLoader for this class.
 *
 * @return the object loader
 */
  public static XML.ObjectLoader getLoader() {
    return new Loader();
  }

  /**
   * A class to save and load Dataset data in an XMLControl.
   */
  private static class Loader extends XMLLoader {
    public void saveObject(XMLControl control, Object obj) {
      Trail trail = (Trail) obj;
      control.setValue("connected", trail.connected);
      control.setValue("color", trail.color);
      control.setValue("number of pts", trail.numpts);
      control.setValue("general path", trail.generalPath);
    }

    public Object createObject(XMLControl control) {
      return new Trail();
    }

    public Object loadObject(XMLControl control, Object obj) {
      Trail trail = (Trail) obj;
      trail.connected = control.getBoolean("connected");
      trail.color = (Color) control.getObject("color");
      trail.numpts = control.getInt("number of pts");
      trail.generalPath = (GeneralPath) control.getObject("general path");
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
