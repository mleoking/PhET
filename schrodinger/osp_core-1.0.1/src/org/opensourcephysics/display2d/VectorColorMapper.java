/*
 * Open Source Physics software is free software as described near the bottom of this code file.
 *
 * For additional information and documentation on Open Source Physics please see:
 * <http://www.opensourcephysics.org/>
 */

package org.opensourcephysics.display2d;
import java.awt.Color;
import javax.swing.JFrame;
import org.opensourcephysics.display.InteractivePanel;
import org.opensourcephysics.display.axes.XAxis;
import org.opensourcephysics.display.axes.XYAxis;

public class VectorColorMapper {
  public static final int SPECTRUM = 0;
  public static final int RED = 1;
  public static final int BLUE = 2;
  public static final int GREEN = 3;
  public static final int BLACK = 4;
  private Color background = Color.WHITE;
  private Color[] colors;
  private double ceil, floor;
  private int numColors;
  private int paletteType;
  private JFrame legendFrame;

  public VectorColorMapper(int _numColors, double _ceil) {
    ceil = _ceil;
    numColors = _numColors;
    floor = (numColors<2) ? 0 : ceil/(numColors-1);
    createVectorfieldPalette(); // default colors
  }

  public double getFloor() {
    return floor;
  }

  public double getCeiling() {
    return ceil;
  }

  /**
 * Sets the color palette.
 * @param _paletteType
 */
  protected void setPaletteType(int _paletteType) {
    paletteType = _paletteType;
  }

  protected void checkPallet(Color backgroundColor) {
    if(background==backgroundColor) {
      return;
    }
    background = backgroundColor;
    createVectorfieldPalette();
  }

  /**
   * Sets the scale.
   *
   * @param _ceil
   */
  public void setScale(double _ceil) {
    ceil = _ceil;
  }

  /**
   * Converts a double to a color.
   * @param mag
   * @return the color
   */
  public Color doubleToColor(double mag) {
    if(mag<=floor) { // magnitudes less than floor are clear
      return background;
    }
    double sat = 1-Math.abs(mag/ceil); // saturation
    int r = 255-(int) (sat*background.getRed());
    int g = 255-(int) (sat*background.getGreen());
    int b = 255-(int) (sat*background.getBlue());
    switch(paletteType) {
    case RED :
      if(mag>=ceil) {
        return Color.red;
      } else {
        return new Color(r, 0, 0);
      }
    case BLUE :
      if(mag>=ceil) {
        return Color.blue;
      } else {
        return new Color(0, 0, b);
      }
    case GREEN :
      if(mag>=ceil) {
        return Color.green;
      } else {
        return new Color(0, g, 0);
      }
    case BLACK :
      if(mag>=ceil) {
        return Color.black;
      } else {
        return new Color(0, 0, 0);
      }
    }
    if(mag>=ceil) { // magnitues greater than max tend toward pure black
      return new Color((int) (255.0*ceil/mag), 0, 0);
    }
    int index = (int) ((numColors-1)*mag/ceil);
    return colors[index];
  }

  private void createVectorfieldPalette() {
    colors = new Color[numColors];
    int n1 = numColors/3;
    n1 = Math.max(1, n1);
    int bgr = background.getRed();
    int bgg = background.getGreen();
    int bgb = background.getBlue();
    for(int i = 0;i<n1;i++) { // start with the background and increse toward blue
      int tr = bgr-(int) (bgr*(i)/n1);
      int tg = bgg-(int) (bgg*(i)/n1);
      colors[i] = new Color(tr, tg, bgb);
    }
    for(int i = n1;i<numColors;i++) { // decrease blue and increase green and then red
      double sigma = n1/1.2;
      double arg1 = (i-n1)/sigma;
      double arg2 = (i-2*n1)/sigma;
      double arg3 = (i-numColors)/sigma;
      int b = (int) (255*Math.exp(-arg1*arg1));
      int g = (int) (255*Math.exp(-arg2*arg2));
      int r = (int) (255*Math.exp(-arg3*arg3));
      r = Math.min(255, r);
      b = Math.min(255, b);
      g = Math.min(255, g);
      colors[i] = new Color(r, g, b);
    }
  }

  public JFrame showLegend() {
    double floor = 0;
    double ceil = this.ceil*2;
    InteractivePanel dp = new InteractivePanel();
    dp.setPreferredSize(new java.awt.Dimension(300, 120));
    dp.setGutters(0, 0, 0, 35);
    dp.setClipAtGutter(false);
    dp.setSquareAspect(false);
    if(legendFrame==null) {
      legendFrame = new JFrame("Legend");
    }
    legendFrame.setResizable(true);
    legendFrame.setContentPane(dp);
    int numVecs = 30;
    GridPointData pointdata = new GridPointData(numVecs, 2, 3);
    double[][][] data = pointdata.getData();
    double delta = 1.5*ceil/numVecs;
    double cval = floor-delta/2;
    for(int i = 0, n = data.length;i<n;i++) {
      data[i][1][2] = cval;
      data[i][1][3] = 0;
      data[i][1][4] = 4;
      cval += delta;
    }
    pointdata.setScale(0, 1.5*ceil+delta, 0, 1);
    Plot2D plot = new VectorPlot(pointdata);
    plot.setAutoscaleZ(false, 0.5*ceil, ceil);
    plot.update();
    dp.addDrawable(plot);
    XAxis xaxis = new XAxis("");
    xaxis.setLocationType(XYAxis.DRAW_AT_LOCATION);
    xaxis.setLocation(-0.0);
    xaxis.setEnabled(true);
    dp.addDrawable(xaxis);
    legendFrame.pack();
    legendFrame.setVisible(true);
    return legendFrame;
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
