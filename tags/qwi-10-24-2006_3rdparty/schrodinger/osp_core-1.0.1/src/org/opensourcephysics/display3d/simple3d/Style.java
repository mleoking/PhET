/*
 * Open Source Physics software is free software as described near the bottom of this code file.
 *
 * For additional information and documentation on Open Source Physics please see: 
 * <http://www.opensourcephysics.org/>
 */

package org.opensourcephysics.display3d.simple3d;
import java.awt.*;
import org.opensourcephysics.controls.*;

public class Style implements org.opensourcephysics.display3d.core.Style {
  static final int STYLE_LINE_COLOR = 0;
  static final int STYLE_LINE_WIDTH = 1;
  static final int STYLE_FILL_COLOR = 2;
  static final int STYLE_RESOLUTION = 3;
  static final int STYLE_DRAWING_FILL = 4;
  static final int STYLE_DRAWING_LINES = 5;

  // Configuration variables
  private boolean drawsFill = true, drawsLines = true;
  private Color lineColor = Color.black;
  private float lineWidth = 1.0f;
  private Color fillColor = Color.blue;
  private org.opensourcephysics.display3d.core.Resolution resolution = null;

  // Implementation variables
  private boolean drawFillsSet = false, drawLinesSet = false;

  /**
   * The owner element. This is needed to report to the element any change.
   */
  private Element element = null;
  private Stroke lineStroke = new BasicStroke(lineWidth);

  /**
   * Package-private constructor
   * User must obtain Style objects from elements, by using the getStyle() method
   * @param _element Element
   */
  Style(Element _element) {
    this.element = _element;
  }

  /**
   * Sets the element. For the use of ElementLoader only.
   * @param _element Element
   */
  void setElement(Element _element) {
    this.element = _element;
  }

  public void setLineColor(Color _color) {
    this.lineColor = _color;
    if(element!=null) {
      element.styleChanged(STYLE_LINE_COLOR);
    }
  }

  final public Color getLineColor() {
    return this.lineColor;
  }

  public void setLineWidth(float _width) {
    if(this.lineWidth==_width) {
      return;
    }
    this.lineStroke = new BasicStroke(this.lineWidth = _width);
    if(element!=null) {
      element.styleChanged(STYLE_LINE_WIDTH);
    }
  }

  final public float getLineWidth() {
    return this.lineWidth;
  }

  /**
   * Gets the Stroke derived from the line width
   * @return Stroke
   * @see java.awt.Stroke
   */
  final Stroke getLineStroke() {
    return this.lineStroke;
  }

  public void setFillColor(Color _color) {
    this.fillColor = _color;
    if(element!=null) {
      element.styleChanged(STYLE_FILL_COLOR);
    }
  }

  final public Color getFillColor() {
    return this.fillColor;
  }

  public void setResolution(org.opensourcephysics.display3d.core.Resolution _res) {
    this.resolution = _res; // No need to clone. Resolution is unmutable
    if(element!=null) {
      element.styleChanged(STYLE_RESOLUTION);
    }
  }
  // No danger. Resolution is unmutable

  final public org.opensourcephysics.display3d.core.Resolution getResolution() {
    return this.resolution;
  }

  public boolean isDrawingFill() {
    return drawsFill;
  }

  public void setDrawingFill(boolean _drawsFill) {
    this.drawsFill = _drawsFill;
    drawFillsSet = true;
    if(element!=null) {
      element.styleChanged(STYLE_DRAWING_FILL);
    }
  }

  public boolean isDrawingLines() {
    return drawsLines;
  }

  public void setDrawingLines(boolean _drawsLines) {
    this.drawsLines = _drawsLines;
    drawLinesSet = true;
    if(element!=null) {
      element.styleChanged(STYLE_DRAWING_LINES);
    }
  }

  // ----------------------------------------------------
  // XML loader
  // ----------------------------------------------------
  public static XML.ObjectLoader getLoader() {
    return new StyleLoader();
  }

  protected static class StyleLoader extends org.opensourcephysics.display3d.core.Style.Loader {
    public Object createObject(XMLControl control) {
      return new Style((Element) null);
    }

    public void saveObject(XMLControl control, Object obj) {
      Style style = (Style) obj;
      control.setValue("line color", style.getLineColor());
      control.setValue("line width", style.getLineWidth());
      control.setValue("fill color", style.getFillColor());
      control.setValue("resolution", style.getResolution());
      if(style.drawFillsSet) {
        control.setValue("drawing fill", style.isDrawingFill());
      }
      if(style.drawLinesSet) {
        control.setValue("drawing lines", style.isDrawingLines());
      }
    }

    public Object loadObject(XMLControl control, Object obj) {
      Style style = (Style) obj;
      style.setLineColor((Color) control.getObject("line color"));
      style.setLineWidth((float) control.getDouble("line width"));
      style.setFillColor((Color) control.getObject("fill color"));
      style.setResolution((org.opensourcephysics.display3d.core.Resolution) control.getObject("resolution"));
      if(control.getPropertyType("drawing fill")!=null) {
        System.out.println("Reading drawFills");
        style.setDrawingFill(control.getBoolean("drawing fill"));
      } else {
        System.out.println("Not reading drawFills");
      }
      if(control.getPropertyType("drawing lines")!=null) {
        System.out.println("Reading drawLines");
        style.setDrawingLines(control.getBoolean("drawing lines"));
      } else {
        System.out.println("Not reading drawLines");
      }
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
