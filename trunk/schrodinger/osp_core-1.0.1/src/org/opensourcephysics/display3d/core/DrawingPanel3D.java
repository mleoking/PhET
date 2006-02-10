/*
 * Open Source Physics software is free software as described near the bottom of this code file.
 *
 * For additional information and documentation on Open Source Physics please see: 
 * <http://www.opensourcephysics.org/>
 */

package org.opensourcephysics.display3d.core;
import org.opensourcephysics.controls.XMLControl;
import java.util.*;

/**
 * <p>Title: DrawingPanel3D</p>
 * <p>Description: DrawingPanel3D is the basic 3D drawing panel</p>
 * @author Francisco Esquembre
 * @version March 2005
 */
public interface DrawingPanel3D extends org.opensourcephysics.display3d.core.interaction.InteractionSource {

  /** The panel itself as the only target of the panel */
  static public final int TARGET_PANEL = 0;

  /** Message box location */
  public static final int BOTTOM_LEFT = 0;

  /** Message box location */
  public static final int BOTTOM_RIGHT = 1;

  /** Message box location */
  public static final int TOP_RIGHT = 2;

  /** Message box location */
  public static final int TOP_LEFT = 3;

  /**
   * Getting the pointer to the real JPanel in it
   * @return JFrame
   */
  public java.awt.Component getComponent();

  // ---------------------------------
  // Customization of the panel
  // ---------------------------------

  /**
   * Sets the preferred extrema for the panel. This resets the camera
   * of the panel to its default.
   * @param minX double
   * @param maxX double
   * @param minY double
   * @param maxY double
   * @param minZ double
   * @param maxZ double
   * @see Camera
   */
  public void setPreferredMinMax(double minX, double maxX, double minY, double maxY, double minZ, double maxZ);

  /**
   * Gets the preferred minimum in the X coordinate
   * @return double
   */
  public double getPreferredMinX();

  /**
   * Gets the preferred maximum in the X coordinate
   * @return double
   */
  public double getPreferredMaxX();

  /**
   * Gets the preferred minimum in the Y coordinate
   * @return double
   */
  public double getPreferredMinY();

  /**
   * Gets the preferred maximum in the Y coordinate
   * @return double
   */
  public double getPreferredMaxY();

  /**
   * Gets the preferred minimum in the Z coordinate
   * @return double
   */
  public double getPreferredMinZ();

  /**
   * Gets the preferred maximum in the Z coordinate
   * @return double
   */
  public double getPreferredMaxZ();

  /**
   * Sets the preferred min and max in each dimension so that all
   * elements currently in the panel are visible.
   */
  public void zoomToFit();

  /**
   * Whether the panel should try to keep a square aspect.
   * Default value is true.
   * @param square boolean
   */
  public void setSquareAspect(boolean square);

  /**
   * Whether the panel tries to keep a square aspect.
   * @return boolean
   */
  public boolean isSquareAspect();

  /**
   * Provides the list of visualization hints that the panel uses
   * to display the 3D scene
   * @return VisualizationHints
   * @see VisualizationHints
   */
  public VisualizationHints getVisualizationHints();

  /**
   * Provides the Camera object used to project the scene in 3D modes.
   * @return Camera
   * @see Camera
   */
  public Camera getCamera();

  /**
   * Paints the panel immediately from within the calling thread.
   * @return BufferedImage the generated image
   */
  public java.awt.image.BufferedImage render();

  /**
   * Paints the scene using the graphic context of the provided image
   * @param image Image
   * @return Image the generated image
   */
  public java.awt.Image render(java.awt.Image image);

  /**
   * Repaints the panel using the event queue.
   */
  public void repaint();

  /**
   * Adds an Element to this DrawingPanel3D.
   * @param element Element
   * @see Element
   */
  public void addElement(Element element);

  /**
   * Removes an Element from this DrawingPanel3D
   * @param element Element
   * @see Element
   */
  public void removeElement(Element element);

  /**
   * Removes all Elements from this DrawingPanel3D
   * @see Element
   */
  public void removeAllElements();

  /**
   * Gets the (cloned) list of Elements.
   * (Should be synchronized.)
   * @return cloned list
   */
  public java.util.ArrayList getElements();

  // ----------------------------------------------------
  // XML loader
  // ----------------------------------------------------
  static abstract class Loader implements org.opensourcephysics.controls.XML.ObjectLoader {
    abstract public Object createObject(XMLControl control);

    public void saveObject(XMLControl control, Object obj) {
      DrawingPanel3D panel = (DrawingPanel3D) obj;
      control.setValue("preferred x min", panel.getPreferredMinX());
      control.setValue("preferred x max", panel.getPreferredMaxX());
      control.setValue("preferred y min", panel.getPreferredMinY());
      control.setValue("preferred y max", panel.getPreferredMaxY());
      control.setValue("preferred z min", panel.getPreferredMinZ());
      control.setValue("preferred z max", panel.getPreferredMaxZ());
      control.setValue("visualization hints", panel.getVisualizationHints());
      control.setValue("camera", panel.getCamera());
      control.setValue("elements", panel.getElements());
    }

    public Object loadObject(XMLControl control, Object obj) {
      DrawingPanel3D panel = (DrawingPanel3D) obj;
      double minX = control.getDouble("preferred x min");
      double maxX = control.getDouble("preferred x max");
      double minY = control.getDouble("preferred y min");
      double maxY = control.getDouble("preferred y max");
      double minZ = control.getDouble("preferred z min");
      double maxZ = control.getDouble("preferred z max");
      panel.setPreferredMinMax(minX, maxX, minY, maxY, minZ, maxZ);
      Collection elements = (Collection) control.getObject("elements");
      if(elements!=null) {
        panel.removeAllElements();
        Iterator it = elements.iterator();
        while(it.hasNext()) {
          panel.addElement((Element) it.next());
        }
      }
      // The subclass is responsible to load unmutable objects such as
      // the visualization hints or the camera
      // It is also responsible to update the screen after loading
      return obj;
    }
  } // End of static class DrawingPanel3DLoader
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
