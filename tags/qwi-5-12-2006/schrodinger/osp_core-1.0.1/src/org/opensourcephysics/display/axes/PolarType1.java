/*
 * Open Source Physics software is free software as described near the bottom of this code file.
 *
 * For additional information and documentation on Open Source Physics please see:
 * <http://www.opensourcephysics.org/>
 */

package org.opensourcephysics.display.axes;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.text.DecimalFormat;
import org.opensourcephysics.display.Dimensioned;
import org.opensourcephysics.display.DrawingPanel;
import org.opensourcephysics.display.PlottingPanel;
import java.awt.Font;
import org.opensourcephysics.display.DrawableTextLine;

/**
 * PolarType1 defines a polar coordinate axes that always shows the entire polar grid.
 *
 * @author W. Christian
 */
public class PolarType1 implements PolarAxes, Dimensioned {
  DecimalFormat labelFormat = new DecimalFormat("0.0");
  double dr = 1;
  double dtheta = Math.PI/8;
  Color gridcolor = Color.lightGray;
  Color interiorColor = Color.white;
  DrawableTextLine titleLine = new DrawableTextLine("", 0, 0);
  Font titleFont = new Font("Dialog", Font.BOLD, 14);
  boolean visible = true;

  public PolarType1(PlottingPanel panel) {
    if(panel==null) {
      return;
    }
    panel.setGutters(25, 25, 25, 25);
    titleLine.setJustification(DrawableTextLine.CENTER);
    titleLine.setFont(titleFont);
    panel.setAxes(this);
    panel.setCoordinateStringBuilder(CoordinateStringBuilder.createPolar());
  }

  /**
   * Gets the spacing of the radial gridlines.
   */
  public double getDeltaR() {
    return dr;
  }

  /**
   * Sets the spacing of the radial gridlines.
   * @param dr
   */
  public void setDeltaR(double dr) {
    this.dr = dr;
  }

  /**
   * Gets the spacing of the radial gridlines.
   */
  public double getDeltaTheta() {
    return dtheta;
  }

  /**
   * Sets the spacing of the radial gridlines.
   * @param dtheta in degree
   */
  public void setDeltaTheta(double dtheta) {
    this.dtheta = Math.abs(dtheta);
  }

  /**
   * Gets the interior dimension of the drawing panel in pixel units.
   *
   * Because this method is called before the pixel scale is set, it
   * may change the gutters and the preferred scale.
   *
   * @param panel the drawing panel that requested the interior
   * @return the interior dimension
   */
  public Dimension getInterior(DrawingPanel panel) {
    double max = Math.abs(panel.getPreferredXMax());
    max = Math.max(max, Math.abs(panel.getPreferredXMin()));
    max = Math.max(max, Math.abs(panel.getPreferredYMax()));
    max = Math.max(max, Math.abs(panel.getPreferredYMin()));
    panel.setPreferredMinMax(-max, max, -max, max);
    return null; // gutters have already been set
  }

  /**
   * Method setLabelFormat
   *
   * @param formatString
   */
  public void setLabelFormat(String formatString) {
    labelFormat = new DecimalFormat(formatString);
  }

  /**
   * Sets the x label of the axes.
   * The font names understood are those understood by java.awt.Font.decode().
   * If the font name is null, the font remains unchanged.
   *
   * @param  s the label
   * @param font_name an optional font name
   */
  public void setXLabel(String s, String font_name) {}

  /**
   * Sets the y label of the axes.
   * The font names understood are those understood by java.awt.Font.decode().
   * If the font name is null, the font remains unchanged.
   *
   * @param  s the label
   * @param font_name an optional font name
   */
  public void setYLabel(String s, String font_name) {}

  /**
   * Gets the x axis label.
   *
   * @return String
   */
  public String getXLabel() {
    return "";
  }

  /**
   * Gets the y axis label.
   *
   * @return String
   */
  public String getYLabel() {
    return "";
  }

  /**
   * Gets the title.
   *
   * @return String
   */
  public String getTitle() {
    return titleLine.getText();
  }

  /**
   * Set a title that will be drawn within the drawing panel.
   * The font names understood are those understood by java.awt.Font.decode().
   * If the font name is null, the font remains unchanged.
   *
   * @param  s the label
   * @param font_name an optional font name
   */
  public void setTitle(String s, String font_name) {
    titleLine.setText(s);
    if((font_name==null)||font_name.equals("")) {
      return;
    }
    titleLine.setFont(Font.decode(font_name));
  }

  /**
   * Sets the x axis to linear or logarithmic.
   *
   * @param isLog true for log scale; false otherwise
   */
  public void setXLog(boolean isLog) {}

  /**
   * Sets the y axis to linear or logarithmic.
   *
   * @param isLog true for log scale; false otherwise
   */
  public void setYLog(boolean isLog) {}

  /**
   * Sets the visibility of the axes.
   *
   * @param isVisible true if the axes are visible
   */
  public void setVisible(boolean isVisible) {
    visible = isVisible;
  }

  /**
   * Sets the interior background color.
   */
  public void setInteriorBackground(Color color) {
    interiorColor = color;
  }

  /**
   * Shows a grid line for every x axis major tickmark.
   */
  public void setShowMajorXGrid(boolean showGrid) {}

  /**
   * Shows a grid line for every x axis minor tickmark.
   */
  public void setShowMinorXGrid(boolean showGrid) {}

  /**
   * Shows a grid line for every y axis major tickmark.
   */
  public void setShowMajorYGrid(boolean showGrid) {}

  /**
   * Shows a grid line for every y axis minor tickmark.
   */
  public void setShowMinorYGrid(boolean showGrid) {}

  /**
 * Draws a representation of an object in a drawing panel.
 * @param panel
 * @param g
 */
  public void draw(DrawingPanel panel, Graphics g) {
    double rmax = Math.abs(panel.getPreferredXMax());
    drawRings(rmax, panel, g);
    drawSpokes(rmax, panel, g);
    titleLine.setX((panel.getXMax()+panel.getXMin())/2);
    if(panel.getTopGutter()>20) {
      titleLine.setY(panel.getYMax()+5/panel.getYPixPerUnit());
    } else {
      titleLine.setY(panel.getYMax()-25/panel.getYPixPerUnit());
    }
    titleLine.draw(panel, g);
  }

  /**
   * Draws the rings for the polar plot.
   * @param panel
   * @param g
   */
  public void drawRings(double rmax, DrawingPanel panel, Graphics g) {
    int xcenter = panel.xToPix(0);
    int ycenter = panel.yToPix(0);
    int xrad = (int) (panel.getXPixPerUnit()*rmax);
    int yrad = (int) (panel.getYPixPerUnit()*rmax);
    g.setColor(interiorColor);
    g.fillOval(xcenter-xrad, ycenter-yrad, 2*xrad, 2*yrad);
    g.setColor(gridcolor);
    for(double r = 0, dr = this.dr;r<=rmax;r += dr) {
      xrad = panel.xToPix(r)-xcenter;
      yrad = ycenter-panel.yToPix(r);
      g.drawOval(xcenter-xrad, ycenter-yrad, 2*xrad, 2*yrad);
    }
  }

  /**
   * Draws the spokes for the polar plot.
   * @param panel
   * @param g
   */
  public void drawSpokes(double rmax, DrawingPanel panel, Graphics g) {
    g.setColor(gridcolor);
    for(double theta = 0;theta<Math.PI;theta += dtheta) {
      int x1 = panel.xToPix(rmax*Math.cos(theta));
      int y1 = panel.yToPix(rmax*Math.sin(theta));
      int x2 = panel.xToPix(-rmax*Math.cos(theta));
      int y2 = panel.yToPix(-rmax*Math.sin(theta));
      g.drawLine(x1, y1, x2, y2);
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
