/*
 * Open Source Physics software is free software as described near the bottom of this code file.
 *
 * For additional information and documentation on Open Source Physics please see: 
 * <http://www.opensourcephysics.org/>
 */

package org.opensourcephysics.ejs.control.drawables;
import java.awt.Color;
import org.opensourcephysics.display.DrawingPanel;
import org.opensourcephysics.display2d.ColorMapper;
import org.opensourcephysics.display2d.GridPointData;
import org.opensourcephysics.display2d.SurfacePlot;
import org.opensourcephysics.display2d.SurfacePlotMouseController;
import org.opensourcephysics.display2d.TestData;
import org.opensourcephysics.ejs.control.swing.ControlDrawablesParent;
import org.opensourcephysics.ejs.control.value.Value;

public class ControlSurfacePlot extends ControlDrawable2D {
  protected SurfacePlot plot;
  protected GridPointData pointdata;
  protected boolean auto;
  protected int colormode;
  protected double minZ, maxZ;
  protected Color floorColor, ceilingColor;
  private SurfacePlotMouseController controller;

  public ControlSurfacePlot(Object _visual) {
    super(_visual);
  }

  protected org.opensourcephysics.display.Drawable createDrawable(Object _drawable) {
    if(_drawable instanceof SurfacePlot) {
      plot = (SurfacePlot) _drawable;
    } else {
      plot = new SurfacePlot(pointdata = new GridPointData(30, 30, 1));
    }
    plot.setAutoscaleZ(auto = true, minZ = 0.0, maxZ = 1.0);
    plot.setPaletteType(colormode = ColorMapper.SPECTRUM);
    plot.setFloorCeilColor(floorColor = Color.darkGray, ceilingColor = Color.lightGray);
    plot.setShowGridLines(true);
    plot.setGridLineColor(Color.lightGray);
    pointdata.setScale(-1.0, 1.0, -1.0, 1.0);
    TestData.gaussianScalarField(pointdata);
    plot.update();
    return plot;
  }

  public void setParent(ControlDrawablesParent _dp) {
    if(myParent!=null&&controller!=null) {
      ((DrawingPanel) myParent.getVisual()).removeMouseListener(controller);
      ((DrawingPanel) myParent.getVisual()).removeMouseMotionListener(controller);
      controller = null;
    }
    if(_dp!=null) {
      controller = new SurfacePlotMouseController((DrawingPanel) _dp.getVisual(), (SurfacePlot) plot);
      ((DrawingPanel) _dp.getVisual()).addMouseListener(controller);
      ((DrawingPanel) _dp.getVisual()).addMouseMotionListener(controller);
    }
    super.setParent(_dp);
  }

  // ------------------------------------------------
  // Visual component
  // ------------------------------------------------
  static private java.util.ArrayList infoList = null;

  public java.util.ArrayList getPropertyList() {
    if(infoList==null) {
      infoList = new java.util.ArrayList();
      infoList.add("data");
      infoList.add("autoscaleZ");
      infoList.add("minimumZ");
      infoList.add("maximumZ");
      infoList.add("colormode");
      infoList.add("floorcolor");
      infoList.add("ceilingcolor");
      infoList.add("showgrid");
      infoList.add("gridcolor");
      infoList.add("visible");
      infoList.addAll(super.getPropertyList());
    }
    return infoList;
  }

  public String getPropertyInfo(String _property) {
    if(_property.equals("data")) {
      return "double[][][]";
    }
    if(_property.equals("autoscaleZ")) {
      return "boolean";
    }
    if(_property.equals("minimumZ")) {
      return "int|double";
    }
    if(_property.equals("maximumZ")) {
      return "int|double";
    }
    if(_property.equals("colormode")) {
      return "PlotMode|int BASIC";
    }
    if(_property.equals("floorcolor")) {
      return "Color|Object BASIC";
    }
    if(_property.equals("ceilingcolor")) {
      return "Color|Object BASIC";
    }
    if(_property.equals("showgrid")) {
      return "boolean BASIC";
    }
    if(_property.equals("gridcolor")) {
      return "Color|Object BASIC";
    }
    if(_property.equals("visible")) {
      return "boolean";
    }
    return super.getPropertyInfo(_property);
  }

  // ------------------------------------------------
  // Set and Get the values of the properties
  // ------------------------------------------------
  public void setValue(int _index, Value _value) {
    switch(_index) {
    case 0 :
      if(_value.getObject() instanceof double[][][]) {
        double[][][] array = (double[][][]) _value.getObject();
        int nx = array.length, ny = array[0].length;
        if(nx!=pointdata.getNx()||ny!=pointdata.getNy()) {
          pointdata = new GridPointData(nx, ny, 1);
        }
        synchronized(pointdata) {
          pointdata.setData(array);
          plot.setGridData(pointdata);
        }
        plot.update();
      }
      break;
    case 1 :
      if(auto!=_value.getBoolean()) {
        plot.setAutoscaleZ(auto = _value.getBoolean(), minZ, maxZ);
      }
      break;
    case 2 :
      if(_value.getDouble()!=minZ) {
        plot.setAutoscaleZ(auto, minZ = _value.getDouble(), maxZ);
      }
      break;
    case 3 :
      if(_value.getDouble()!=maxZ) {
        plot.setAutoscaleZ(auto, minZ, maxZ = _value.getDouble());
      }
      break;
    case 4 :
      if(colormode!=_value.getInteger()) {
        plot.setPaletteType(colormode = _value.getInteger());
        plot.setFloorCeilColor(floorColor, ceilingColor);
      }
      break;
    case 5 :
      if(_value.getObject() instanceof Color&&floorColor!=(Color) _value.getObject()) {
        plot.setFloorCeilColor(floorColor = (Color) _value.getObject(), ceilingColor);
      }
      break;
    case 6 :
      if(_value.getObject() instanceof Color&&ceilingColor!=(Color) _value.getObject()) {
        plot.setFloorCeilColor(floorColor, ceilingColor = (Color) _value.getObject());
      }
      break;
    case 7 :
      plot.setShowGridLines(_value.getBoolean());
      break;
    case 8 :
      if(_value.getObject() instanceof Color) {
        plot.setGridLineColor((Color) _value.getObject());
      }
      break;
    case 9 :
      plot.setVisible(_value.getBoolean());
      break;
    default :
      super.setValue(_index-10, _value);
    }
    if(isUnderEjs) {
      plot.update();
    }
  }

  public void setDefaultValue(int _index) {
    switch(_index) {
    case 0 :
      TestData.gaussianScalarField(pointdata);
      break;
    case 1 :
      plot.setAutoscaleZ(auto = true, minZ, maxZ);
      break;
    case 2 :
      plot.setAutoscaleZ(auto, minZ = -1.0, maxZ);
      break;
    case 3 :
      plot.setAutoscaleZ(auto, minZ, maxZ = 1.0);
      break;
    case 4 :
      plot.setPaletteType(colormode = ColorMapper.SPECTRUM);
      plot.setFloorCeilColor(floorColor, ceilingColor);
      break;
    case 5 :
      plot.setFloorCeilColor(floorColor = Color.darkGray, ceilingColor);
      break;
    case 6 :
      plot.setFloorCeilColor(floorColor, ceilingColor = Color.lightGray);
      break;
    case 7 :
      plot.setShowGridLines(true);
      break;
    case 8 :
      plot.setGridLineColor(Color.lightGray);
      break;
    case 9 :
      plot.setVisible(true);
      break;
    default :
      super.setDefaultValue(_index-10);
      break;
    }
    if(isUnderEjs) {
      plot.update();
    }
  }

  public Value getValue(int _index) {
    switch(_index) {
    case 0 :
    case 1 :
    case 2 :
    case 3 :
    case 4 :
    case 5 :
    case 6 :
    case 7 :
    case 8 :
    case 9 :
      return null; // The element does not modify these
    default :
      return super.getValue(_index-10);
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
