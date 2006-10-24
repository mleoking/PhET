/*
 * Open Source Physics software is free software as described near the bottom of this code file.
 *
 * For additional information and documentation on Open Source Physics please see: 
 * <http://www.opensourcephysics.org/>
 */

package org.opensourcephysics.ejs.control.drawables;
import java.awt.Color;
import org.opensourcephysics.display2d.ColorMapper;
import org.opensourcephysics.display2d.GridPointData;
import org.opensourcephysics.display2d.InterpolatedPlot;
import org.opensourcephysics.display2d.TestData;
import org.opensourcephysics.ejs.control.value.Value;

public class ControlZInterpolatedPlot extends ControlDrawable2D implements org.opensourcephysics.ejs.control.swing.NeedsPreUpdate {
  protected InterpolatedPlot plot;
  protected GridPointData pointdata;
  protected boolean auto;
  protected int levels, colormode;
  protected double minX, maxX, minY, maxY;
  protected double minZ, maxZ;
  protected Color floorColor, ceilingColor;
  private boolean mustUpdate = true;
  private double[][] dataArray;

  public ControlZInterpolatedPlot(Object _visual) {
    super(_visual);
  }

  protected org.opensourcephysics.display.Drawable createDrawable(Object _drawable) {
    pointdata = new GridPointData(30, 30, 1);
    dataArray = null;
    if(_drawable instanceof InterpolatedPlot) {
      plot = (InterpolatedPlot) _drawable;
      plot.setGridData(pointdata);
    } else {
      plot = new InterpolatedPlot(pointdata);
    }
    minX = -1.0;
    maxX = 1.0;
    minY = -1.0;
    maxY = 1.0;
    plot.setAutoscaleZ(auto = true, minZ = -1.0, maxZ = 1.0);
    plot.setPaletteType(colormode = ColorMapper.SPECTRUM);
    plot.setColorPalette(ColorMapper.getColorPalette(levels = 16, colormode));
    plot.setFloorCeilColor(floorColor = Color.darkGray, ceilingColor = Color.lightGray);
    plot.setShowGridLines(true);
    plot.setGridLineColor(Color.lightGray);
    preupdate();
    return plot;
  }

  public void preupdate() {
    if(mustUpdate) {
      if(dataArray==null) { // Still demo data
        if(pointdata.getLeft()!=minX||pointdata.getRight()!=maxX||pointdata.getBottom()!=maxY||pointdata.getTop()!=minY) {
          pointdata.setScale(minX, maxX, maxY, minY);
        }
        TestData.gaussianScalarField(pointdata);
      } else { // Real data
        int nx = dataArray.length, ny = dataArray[0].length;
        if(pointdata.getNx()!=nx||pointdata.getNy()!=ny) {
          pointdata = new GridPointData(nx, ny, 1);
          pointdata.setScale(minX, maxX, maxY, minY);
          double[][][] data = pointdata.getData();
          for(int i = 0;i<nx;i++) {
            for(int j = 0;j<ny;j++) {
              data[i][j][2] = dataArray[i][j];
            }
          }
          plot.setGridData(pointdata);
        } else {
          if(pointdata.getLeft()!=minX||pointdata.getRight()!=maxX||pointdata.getBottom()!=maxY||pointdata.getTop()!=minY) {
            pointdata.setScale(minX, maxX, maxY, minY);
          }
          double[][][] data = pointdata.getData();
          for(int i = 0;i<nx;i++) {
            for(int j = 0;j<ny;j++) {
              data[i][j][2] = dataArray[i][j];
            }
          }
        }
      }
      mustUpdate = false;
    }
    plot.update();
  }

  // ------------------------------------------------
  // Visual component
  // ------------------------------------------------
  static private java.util.ArrayList infoList = null;

  public java.util.ArrayList getPropertyList() {
    if(infoList==null) {
      infoList = new java.util.ArrayList();
      infoList.add("minimumX");
      infoList.add("maximumX");
      infoList.add("minimumY");
      infoList.add("maximumY");
      infoList.add("z");
      infoList.add("autoscaleZ");
      infoList.add("minimumZ");
      infoList.add("maximumZ");
      infoList.add("levels");
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
    if(_property.equals("minimumX")) {
      return "int|double ";
    }
    if(_property.equals("maximumX")) {
      return "int|double ";
    }
    if(_property.equals("minimumY")) {
      return "int|double ";
    }
    if(_property.equals("maximumY")) {
      return "int|double ";
    }
    if(_property.equals("z")) {
      return "double[][]|double";
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
    if(_property.equals("levels")) {
      return "int BASIC";
    }
    if(_property.equals("colormode")) {
      return "ColorMode|int BASIC";
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
      if(minX!=_value.getDouble()) {
        minX = _value.getDouble();
        mustUpdate = true;
      }
      break;
    case 1 :
      if(maxX!=_value.getDouble()) {
        maxX = _value.getDouble();
        mustUpdate = true;
      }
      break;
    case 2 :
      if(minY!=_value.getDouble()) {
        minY = _value.getDouble();
        mustUpdate = true;
      }
      break;
    case 3 :
      if(maxY!=_value.getDouble()) {
        maxY = _value.getDouble();
        mustUpdate = true;
      }
      break;
    case 4 :
      if(_value.getObject() instanceof double[][]) {
        dataArray = (double[][]) _value.getObject();
        mustUpdate = true;
      }
      break;
    case 5 :
      if(auto!=_value.getBoolean()) {
        plot.setAutoscaleZ(auto = _value.getBoolean(), minZ, maxZ);
      }
      break;
    case 6 :
      if(minZ!=_value.getDouble()) {
        plot.setAutoscaleZ(auto, minZ = _value.getDouble(), maxZ);
      }
      break;
    case 7 :
      if(maxZ!=_value.getDouble()) {
        plot.setAutoscaleZ(auto, minZ, maxZ = _value.getDouble());
      }
      break;
    case 8 :
      if(levels!=_value.getInteger()) {
        plot.setColorPalette(ColorMapper.getColorPalette(levels = _value.getInteger(), colormode));
        plot.setFloorCeilColor(floorColor, ceilingColor);
      }
      break;
    case 9 :
      if(colormode!=_value.getInteger()) {
        plot.setPaletteType(colormode = _value.getInteger());
        plot.setFloorCeilColor(floorColor, ceilingColor);
      }
      break;
    case 10 :
      if(_value.getObject() instanceof Color&&floorColor!=(Color) _value.getObject()) {
        plot.setFloorCeilColor(floorColor = (Color) _value.getObject(), ceilingColor);
      }
      break;
    case 11 :
      if(_value.getObject() instanceof Color&&ceilingColor!=(Color) _value.getObject()) {
        plot.setFloorCeilColor(floorColor, ceilingColor = (Color) _value.getObject());
      }
      break;
    case 12 :
      plot.setShowGridLines(_value.getBoolean());
      break;
    case 13 :
      if(_value.getObject() instanceof Color) {
        plot.setGridLineColor((Color) _value.getObject());
      }
      break;
    case 14 :
      plot.setVisible(_value.getBoolean());
      break;
    default :
      super.setValue(_index-15, _value);
    }
  }

  public void setDefaultValue(int _index) {
    switch(_index) {
    case 0 :
      minX = -1.0;
      mustUpdate = true;
      break;
    case 1 :
      maxX = 1.0;
      mustUpdate = true;
      break;
    case 2 :
      minY = -1.0;
      mustUpdate = true;
      break;
    case 3 :
      maxY = 1.0;
      mustUpdate = true;
      break;
    case 4 :
      plot.setGridData(pointdata = new GridPointData(30, 30, 1));
      dataArray = null;
      mustUpdate = true;
      break;
    case 5 :
      plot.setAutoscaleZ(auto = true, minZ, maxZ);
      break;
    case 6 :
      plot.setAutoscaleZ(auto, minZ = -1.0, maxZ);
      break;
    case 7 :
      plot.setAutoscaleZ(auto, minZ, maxZ = 1.0);
      break;
    case 8 :
      plot.setColorPalette(ColorMapper.getColorPalette(levels = 16, colormode));
      plot.setFloorCeilColor(floorColor, ceilingColor);
      break;
    case 9 :
      plot.setPaletteType(colormode = ColorMapper.SPECTRUM);
      plot.setFloorCeilColor(floorColor, ceilingColor);
      break;
    case 10 :
      plot.setFloorCeilColor(floorColor = Color.darkGray, ceilingColor);
      break;
    case 11 :
      plot.setFloorCeilColor(floorColor, ceilingColor = Color.lightGray);
      break;
    case 12 :
      plot.setShowGridLines(true);
      break;
    case 13 :
      plot.setGridLineColor(Color.lightGray);
      break;
    case 14 :
      plot.setVisible(true);
      break;
    default :
      super.setDefaultValue(_index-15);
      break;
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
    case 10 :
    case 11 :
    case 12 :
    case 13 :
    case 14 :
      return null; // The element does not modify these
    default :
      return super.getValue(_index-17);
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
