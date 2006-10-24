/*
 * Open Source Physics software is free software as described near the bottom of this code file.
 *
 * For additional information and documentation on Open Source Physics please see:
 * <http://www.opensourcephysics.org/>
 */

package org.opensourcephysics.display;
import java.util.*;
import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;
import org.opensourcephysics.controls.*;
import org.opensourcephysics.display.axes.*;
import org.opensourcephysics.numerics.*;

/**
 *  A Drawing Panel that has an X axis, a Y axis, and a title.
 */
public class PlottingPanel extends InteractivePanel {
  boolean logScaleX = false;
  boolean logScaleY = false;
  DrawableAxes axes;
  FunctionTransform functionTransform = new FunctionTransform();
  final static double log10 = Math.log(10);
  final static LogBase10Function logBase10Function = new LogBase10Function();

  /**
   *  Constructs a new PlottingPanel that uses the given X axis label, Y axis
   *  label, and plot title.
   *
   * @param  xlabel     The X axis label.
   * @param  ylabel     The Y axis label.
   * @param  plotTitle  The plot title.
   */
  public PlottingPanel(String xlabel, String ylabel, String plotTitle) {
    this(xlabel, ylabel, plotTitle, XYAxis.LINEAR, XYAxis.LINEAR);
  }

  /**
   *  Constructs a new PlottingPanel that uses the given X axis type and Y axis
   *  type.
   *
   * @param  _xAxisType  The X axis type.
   * @param  _yAxisType  The Y axis type.
   */
  public PlottingPanel(int _xAxisType, int _yAxisType) {
    this("x", "y", "Title", _xAxisType, _yAxisType);
  }

  /**
   *  Constructs a new PlottingPanel with cartesian axes that use the given X axis label, Y axis
   *  label, and plot title.
   *
   * @param  xlabel      The X axis label.
   * @param  ylabel      The Y axis label.
   * @param  plotTitle   The plot title.
   * @param  xAxisType   Description of Parameter
   * @param  yAxisType   Description of Parameter
   */
  public PlottingPanel(String xlabel, String ylabel, String plotTitle, int xAxisType, int yAxisType) {
    axes = AxisFactory.createAxesType1(this);
    axes.setXLabel(xlabel, null);
    axes.setYLabel(ylabel, null);
    axes.setTitle(plotTitle, null);
    functionTransform.setXFunction(logBase10Function); // set function transforms but do not apply functions
    functionTransform.setYFunction(logBase10Function);
    if(xAxisType==XYAxis.LOG10) {
      logScaleX = true;
    }
    if(yAxisType==XYAxis.LOG10) {
      logScaleY = true;
    }
    setLogScale(logScaleX, logScaleY);
  }

  /**
   *  Constructs a new PlottingPanel with cartesian type 1 axes using the given X axis label, Y axis
   *  label, and plot title.
   *
   * @param  xlabel      The X axis label.
   * @param  ylabel      The Y axis label.
   * @param  plotTitle   The plot title.
   */
  public static PlottingPanel createType1(String xlabel, String ylabel, String plotTitle) {
    PlottingPanel panel = new PlottingPanel(xlabel, ylabel, plotTitle);
    panel.axes = AxisFactory.createAxesType1(panel);
    panel.axes.setXLabel(xlabel, null);
    panel.axes.setYLabel(ylabel, null);
    panel.axes.setTitle(plotTitle, null);
    return panel;
  }

  /**
   *  Constructs a new PlottingPanel with cartesian type 2 axes using the given X axis label, Y axis
   *  label, and plot title.
   *
   * @param  xlabel      The X axis label.
   * @param  ylabel      The Y axis label.
   * @param  plotTitle   The plot title.
   */
  public static PlottingPanel createType2(String xlabel, String ylabel, String plotTitle) {
    PlottingPanel panel = new PlottingPanel(xlabel, ylabel, plotTitle);
    panel.axes = AxisFactory.createAxesType2(panel);
    panel.axes.setXLabel(xlabel, null);
    panel.axes.setYLabel(ylabel, null);
    panel.axes.setTitle(plotTitle, null);
    return panel;
  }

  /**
   *  Constructs a new PlottingPanel with polar type 1 axes using the given title.
   *
   * @param  plotTitle   the plot title.
   */
  public static PlottingPanel createPolarType1(String plotTitle, double deltaR) {
    PlottingPanel panel = new PlottingPanel(null, null, plotTitle);
    PolarAxes axes = new PolarType1(panel);
    axes.setDeltaR(deltaR);
    axes.setDeltaTheta(Math.PI/8); // spokes are separate by PI/8
    panel.setTitle(plotTitle);
    panel.setSquareAspect(true);
    return panel;
  }

  /**
   *  Constructs a new PlottingPanel with polar type 2 axes using the given title.
   *
   * @param  plotTitle   the plot title.
   */
  public static PlottingPanel createPolarType2(String plotTitle, double deltaR) {
    PlottingPanel panel = new PlottingPanel(null, null, plotTitle);
    PolarAxes axes = new PolarType2(panel);
    axes.setDeltaR(deltaR);        // circles are separated by one unit
    axes.setDeltaTheta(Math.PI/8); // spokes are separate by PI/8
    panel.setTitle(plotTitle);
    panel.setSquareAspect(true);
    return panel;
  }

  /**
   *  Constructs a new PlottingPanel with cartesian type 3 axes using the given X axis label, Y axis
   *  label, and plot title.
   *
   * @param  xlabel      The X axis label.
   * @param  ylabel      The Y axis label.
   * @param  plotTitle   The plot title.
   */
  public static PlottingPanel createType3(String xlabel, String ylabel, String plotTitle) {
    PlottingPanel panel = new PlottingPanel(xlabel, ylabel, plotTitle);
    panel.axes = AxisFactory.createAxesType3(panel);
    panel.axes.setXLabel(xlabel, null);
    panel.axes.setYLabel(ylabel, null);
    panel.axes.setTitle(plotTitle, null);
    return panel;
  }

  /**
   * Gets the interactive drawable that was accessed by the last mouse event.
   *
   * This methods overrides the default implemenation in order to check for draggable axes.
   *
   * @return the interactive object
   */
  public Interactive getInteractive() {
    Interactive iad = null;
    iad = super.getInteractive();
    if(iad==null&&axes instanceof Interactive) {
      // check for draggable axes
      iad = ((Interactive) axes).findInteractive(this, mouseEvent.getX(), mouseEvent.getY());
    }
    return iad;
  }

  /**
   * Gets the axes.
   *
   * @return the axes
   */
  public DrawableAxes getAxes() {
    return axes;
  }

  /**
   * Sets the axes.
   *
   * @param _axes the new axes
   */
  public void setAxes(DrawableAxes _axes) {
    axes = _axes;
    if(axes==null) {
      axes = new CustomAxes(this);
      setGutters(0, 0, 0, 0); // setGutters(int left, int top, int right, int bottom)
      axes.setVisible(false);
    }
  }

  /**
   *  Converts this panel to polar coordinates
   *
   * @param plotTitle String
   * @param deltaR double
   */
  public void setPolar(String plotTitle, double deltaR) {
    if(logScaleX||logScaleY) {
      System.err.println("The axes type cannot be swithed when using logarithmetic scales.");
      return;
    }
    PolarAxes axes = new PolarType2(this);
    axes.setDeltaR(deltaR);        // radial coordinate separation
    axes.setDeltaTheta(Math.PI/8); // spokes are separate by PI/8
    setTitle(plotTitle);
    setSquareAspect(true);
  }

  /**
   *  Converts this panel to cartesian coordinates.
   *
   *
   * @param xLabel String
   * @param yLabel String
   * @param plotTitle String
   */
  public void setCartesian(String xLabel, String yLabel, String plotTitle) {
    axes = AxisFactory.createAxesType1(this);
    axes.setXLabel(xLabel, null);
    axes.setYLabel(yLabel, null);
    axes.setTitle(plotTitle, null);
  }

  /**
   *  Sets the label for the X (horizontal) axis.
   *
   * @param  label  the label
   */
  public void setXLabel(String label) {
    axes.setXLabel(label, null);
  }

  /**
   *  Sets the label for the Y (vertical) axis.
   *
   * @param  label  the label
   */
  public void setYLabel(String label) {
    axes.setYLabel(label, null);
  }

  /**
   *  Sets the title.
   *
   * @param  title  the title
   */
  public void setTitle(String title) {
    axes.setTitle(title, null);
  }

  /**
   *  Sets the label and font for the X (horizontal) axis.
   *  If the font name is null, the font is unchanged.
   *
   *  @param  label  the label
   *  @param font_name the optional new font
   */
  public void setXLabel(String label, String font_name) {
    axes.setXLabel(label, font_name);
  }

  /**
   *  Sets the label and font  for the Y (vertical) axis.
   *  If the font name is null, the font is unchanged.
   *
   *  @param  label  the label
   *  @param font_name the optional new font
   */
  public void setYLabel(String label, String font_name) {
    axes.setYLabel(label, font_name);
  }

  /**
   *  Sets the title and font.
   *  If the font name is null, the font is unchanged.
   *
   *  @param title
   *  @param font_name the optional new font
   */
  public void setTitle(String title, String font_name) {
    axes.setTitle(title, font_name);
  }

  /**
   * Sets the visibility of the axes.
   * Axes that are not visible will not be drawn.
   *
   * @param isVisible
   */
  public void setAxesVisible(boolean isVisible) {
    axes.setVisible(isVisible);
  }

  /**
   * Sets Cartesian axes to log scale.
   * @param  _logScaleX  The new logScale value
   * @param  _logScaleY  The new logScale value
   */
  public void setLogScale(boolean _logScaleX, boolean _logScaleY) {
    if(axes instanceof CartesianAxes) {
      ((CartesianAxes) axes).setXLog(_logScaleX);
      logScaleX = _logScaleX;
    } else {
      logScaleX = false;
    }
    if(axes instanceof CartesianAxes) {
      ((CartesianAxes) axes).setYLog(_logScaleY);
      logScaleY = _logScaleY;
    } else {
      logScaleY = false;
    }
  }

  /**
   * Sets Cartesian x-axes to log scale.
   * @param  _logScaleX  The new logScale value
   */
  public void setLogScaleX(boolean _logScaleX) {
    if(axes instanceof CartesianAxes) {
      ((CartesianAxes) axes).setXLog(_logScaleX);
      logScaleX = _logScaleX;
    } else {
      logScaleX = false;
    }
  }

  /**
   * Gets the logScaleX value.
   *
   * @return boolean
   */
  public boolean isLogScaleX() {
    return logScaleX;
  }

  /**
   * Sets Cartesian axes to log scale.
   * @param  _logScaleY  The new logScale value
   */
  public void setLogScaleY(boolean _logScaleY) {
    if(axes instanceof CartesianAxes) {
      ((CartesianAxes) axes).setYLog(_logScaleY);
      logScaleY = _logScaleY;
    } else {
      logScaleY = false;
    }
  }

  /**
   * Gets the logScaleY value.
   *
   * @return boolean
   */
  public boolean isLogScaleY() {
    return logScaleY;
  }

  /**
   * Paints all objects inside this component.
   *
   * @param g
   */
  protected void paintEverything(Graphics g) {
    // find the cliping rectangle within a scroll pane viewport
    viewRect = null;
    Container c = getParent();
    while(c!=null) {
      if(c instanceof JViewport) {
        viewRect = ((JViewport) c).getViewRect();
        break;
      }
      c = c.getParent();
    }
    Dimension interiorDimension = null;
    // A single drawable object can set the dimension
    if(dimensionSetter!=null) {
      interiorDimension = dimensionSetter.getInterior(this);
    }
    // Give the axes a chance to set the gutter and the dimension.
    if(axes instanceof Dimensioned) {
      Dimension axesInterior = ((Dimensioned) axes).getInterior(this);
      if(axesInterior!=null) {
        interiorDimension = axesInterior;
      }
    }
    if(interiorDimension!=null) {
      squareAspect = false;
      leftGutter = rightGutter = Math.max(0, getWidth()-interiorDimension.width)/2;
      topGutter = bottomGutter = Math.max(0, getHeight()-interiorDimension.height)/2;
    }
    // brMessageBox.checkLocation(this);
    ArrayList tempList;
    synchronized(this) {
      tempList = (ArrayList) drawableList.clone();
    }
    scale(tempList);                           // set the x and y scale based on the autoscale values
    g.setColor(getBackground());
    g.fillRect(0, 0, getWidth(), getHeight()); // fill the compponent with the background color
    g.setColor(Color.black); // restore the default drawing color
    setPixelScale();
    axes.draw(this, g);
    paintDrawableList(g, tempList);
  }

  /**
   *  Converts pixel to x world units.
   *
   * @param  pix
   * @return      x panel units
   */
  public double pixToX(int pix) {
    if(logScaleX) {
      return Math.pow(10, super.pixToX(pix));
    }
    return super.pixToX(pix);
  }

  /**
   *  Converts x from world to pixel units.
   *
   * @param  x
   * @return    the pixel value of the x coordinate
   */
  public int xToPix(double x) {
    if(logScaleX) {
      return super.xToPix(logBase10(x));
    } else {
      return super.xToPix(x);
    }
  }

  /**
   *  Converts pixel to x world units.
   *
   * @param  pix
   * @return      x panel units
   */
  public double pixToY(int pix) {
    if(logScaleY) {
      return Math.pow(10, super.pixToY(pix));
    } else {
      return super.pixToY(pix);
    }
  }

  /**
   *  Converts y from world to pixel units.
   *
   * @param  y
   * @return    the pixel value of the y coordinate
   */
  public int yToPix(double y) {
    if(logScaleY) {
      return super.yToPix(logBase10(y));
    } else {
      return super.yToPix(y);
    }
  }

  /*
    TO DO: Fix setPixelScale-what to do if min or max is 0 and using log scale
  */

  /**
   *  Calculates min and max values and the affine transformation based on the
   *  current size of the panel and the squareAspect boolean.
   */
   public void setPixelScale() {
    xmin = xminPreferred; // start with the preferred values.
    xmax = xmaxPreferred;
    ymin = yminPreferred;
    ymax = ymaxPreferred;
    if(logScaleX) {
      xmin = logBase10(xmin);
      xmax = logBase10(xmax);
      if(xmin==0) { // FIX_ME
        xmin = 0.00000001;
      }
      if(xmax==0) { // FIX_ME
        xmax = Math.max(xmin+0.00000001, 0.00000001);
      }
    }
    if(logScaleY) {
      ymin = logBase10(ymin);
      ymax = logBase10(ymax);
      if(ymin==0) { // FIX_ME
        ymin = 0.00000001;
      }
      if(ymax==0) { // FIX_ME
        ymax = Math.max(ymin+0.00000001, 0.00000001);
      }
    }
    width = getWidth();
    height = getHeight();
    if(fixedPixelPerUnit) { // the user has specified a fixed pixel scale
      xmin = (xmaxPreferred+xminPreferred)/2-Math.max(width-leftGutter-rightGutter-1, 1)/xPixPerUnit/2;
      xmax = (xmaxPreferred+xminPreferred)/2+Math.max(width-leftGutter-rightGutter-1, 1)/xPixPerUnit/2;
      ymin = (ymaxPreferred+yminPreferred)/2-Math.max(height-bottomGutter-topGutter-1, 1)/yPixPerUnit/2;
      ymax = (ymaxPreferred+yminPreferred)/2+Math.max(height-bottomGutter-topGutter-1, 1)/yPixPerUnit/2;
      functionTransform.setTransform(xPixPerUnit, 0, 0, -yPixPerUnit, -xmin*xPixPerUnit+leftGutter, ymax*yPixPerUnit+topGutter);
      functionTransform.setApplyXFunction(false);
      functionTransform.setApplyYFunction(false);
      functionTransform.getMatrix(pixelMatrix); // puts the transformation into the pixel matrix
      return;
    }
    xPixPerUnit = (width-leftGutter-rightGutter-1)/(xmax-xmin);
    yPixPerUnit = (height-bottomGutter-topGutter-1)/(ymax-ymin); // the y scale in pixels
    if(squareAspect) {
      double stretch = Math.abs(xPixPerUnit/yPixPerUnit);
      if(stretch>=1) {                                              // make the x range bigger so that aspect ratio is one
        stretch = Math.min(stretch, width);                         // limit the stretch
        xmin = xminPreferred-(xmaxPreferred-xminPreferred)*(stretch-1)/2.0;
        xmax = xmaxPreferred+(xmaxPreferred-xminPreferred)*(stretch-1)/2.0;
        xPixPerUnit = (width-leftGutter-rightGutter-1)/(xmax-xmin); // the x scale in pixels per unit
      } else {                                   // make the y range bigger so that aspect ratio is one
        stretch = Math.max(stretch, 1.0/height); // limit the stretch
        ymin = yminPreferred-(ymaxPreferred-yminPreferred)*(1.0/stretch-1)/2.0;
        ymax = ymaxPreferred+(ymaxPreferred-yminPreferred)*(1.0/stretch-1)/2.0;
        yPixPerUnit = (height-bottomGutter-topGutter-1)/(ymax-ymin); // the y scale in pixels per unit
      }
    }
    functionTransform.setTransform(xPixPerUnit, 0, 0, -yPixPerUnit, -xmin*xPixPerUnit+leftGutter, ymax*yPixPerUnit+topGutter);
    if(logScaleX) {
      functionTransform.setApplyXFunction(true);
    } else {
      functionTransform.setApplyXFunction(false);
    }
    if(logScaleY) {
      functionTransform.setApplyYFunction(true);
    } else {
      functionTransform.setApplyYFunction(false);
    }
    functionTransform.getMatrix(pixelMatrix);
  }

  /**
   * Gets the affine transformation that converts from world to pixel coordinates.
   * @return the affine transformation
   */
  public AffineTransform getPixelTransform() {
    return(AffineTransform) functionTransform.clone();
  }

  /**
   * Method logBase10
   *
   * @param x
   *
   * @return the log
   */
  static double logBase10(double x) {
    return Math.log(x)/log10;
  }

  /**
   * Returns an XML.ObjectLoader to save and load object data.
   *
   * @return the XML.ObjectLoader
   */
  public static XML.ObjectLoader getLoader() {
    return new PlottingPanelLoader();
  }

  /**
   * A class to save and load PlottingPanel data.
   */
  static class PlottingPanelLoader extends DrawingPanelLoader {

    /**
     * Saves PlottingPanel data in an XMLControl.
     *
     * @param control the control
     * @param obj the DrawingPanel to save
     */
    public void saveObject(XMLControl control, Object obj) {
      PlottingPanel panel = (PlottingPanel) obj;
      control.setValue("title", panel.axes.getTitle());
      control.setValue("x axis label", panel.axes.getXLabel());
      control.setValue("y axis label", panel.axes.getYLabel());
      super.saveObject(control, obj);
    }

    /**
     * Creates a PlottingPanel.
     *
     * @param control the control
     * @return the newly created panel
     */
    public Object createObject(XMLControl control) {
      String title = control.getString("title");
      String xlabel = control.getString("x axis label");
      String ylabel = control.getString("y axis label");
      return new PlottingPanel(xlabel, ylabel, title);
    }

    /**
     * Loads a PlottingPanel with data from an XMLControl.
     *
     * @param control the control
     * @param obj the object
     * @return the loaded object
     */
    public Object loadObject(XMLControl control, Object obj) {
      PlottingPanel panel = (PlottingPanel) obj;
      panel.setTitle(control.getString("title"));
      panel.setXLabel(control.getString("x axis label"));
      panel.setYLabel(control.getString("y axis label"));
      super.loadObject(control, obj);
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
