/*
 * Open Source Physics software is free software as described near the bottom of this code file.
 *
 * For additional information and documentation on Open Source Physics please see: 
 * <http://www.opensourcephysics.org/>
 */

package org.opensourcephysics.ejs.control.drawables;
import java.awt.Color;
import org.opensourcephysics.display.Drawable;
import org.opensourcephysics.display2d.BinaryLattice;
import org.opensourcephysics.ejs.control.value.Value;

public class ControlBinaryLattice extends ControlDrawable2D {
  private BinaryLattice lattice;
  private Color[] palette;
  private double minX, maxX, minY, maxY;
  private int nx, ny;

  public ControlBinaryLattice(Object _visual) {
    super(_visual);
  }

  protected Drawable createDrawable(Object _drawable) {
    if(_drawable instanceof BinaryLattice) {
      lattice = (BinaryLattice) _drawable;
    } else {
      lattice = new BinaryLattice(30, 30);
      lattice.setShowGrid(true);
      lattice.setGridLineColor(Color.lightGray);
    }
    nx = lattice.getNx();
    ny = lattice.getNy();
    lattice.setMinMax(minX = -1.0, maxX = 1.0, minY = -1.0, maxY = 1.0);
    lattice.randomize();
    palette = new Color[] {Color.white, Color.black};
    lattice.setColorPalette(palette);
    return lattice;
  }

  // ------------------------------------------------
  // Properties
  // ------------------------------------------------
  static private java.util.ArrayList infoList = null;

  public java.util.ArrayList getPropertyList() {
    if(infoList==null) {
      infoList = new java.util.ArrayList();
      infoList.add("minimumX");
      infoList.add("maximumX");
      infoList.add("minimumY");
      infoList.add("maximumY");
      infoList.add("data");
      infoList.add("color");
      infoList.add("deadcolor");
      infoList.add("showgrid");
      infoList.add("gridcolor");
      infoList.add("visible");
      infoList.addAll(super.getPropertyList());
    }
    return infoList;
  }

  public String getPropertyInfo(String _property) {
    if(_property.equals("minimumX")) {
      return "int|double BASIC";
    }
    if(_property.equals("maximumX")) {
      return "int|double BASIC";
    }
    if(_property.equals("minimumY")) {
      return "int|double BASIC";
    }
    if(_property.equals("maximumY")) {
      return "int|double BASIC";
    }
    if(_property.equals("data")) {
      return "int[][]";
    }
    if(_property.equals("color")) {
      return "Color|Object";
    }
    if(_property.equals("deadcolor")) {
      return "Color|Object";
    }
    if(_property.equals("showgrid")) {
      return "boolean";
    }
    if(_property.equals("gridcolor")) {
      return "Color|Object";
    }
    if(_property.equals("visible")) {
      return "boolean";
    }
    return super.getPropertyInfo(_property);
  }

  // ------------------------------------------------
  // Variable properties
  // ------------------------------------------------
  public void setValue(int _index, Value _value) {
    switch(_index) {
    case 0 :
      if(_value.getDouble()!=minX) {
        lattice.setXMin(minX = _value.getDouble());
      }
      break;
    case 1 :
      if(_value.getDouble()!=maxX) {
        lattice.setXMax(maxX = _value.getDouble());
      }
      break;
    case 2 :
      if(_value.getDouble()!=minY) {
        lattice.setYMin(minY = _value.getDouble());
      }
      break;
    case 3 :
      if(_value.getDouble()!=maxY) {
        lattice.setYMax(maxY = _value.getDouble());
      }
      break;
    case 4 :
      if(_value.getObject() instanceof int[][]) {
        int[][] data = (int[][]) _value.getObject();
        if(data.length!=nx||data[0].length!=ny) {
          lattice.resizeLattice(nx = data.length, ny = data[0].length);
        }
        lattice.setBlock(0, 0, data);
      }
      break;
    case 5 :
      if(_value.getObject() instanceof Color&&palette[0]!=(Color) _value.getObject()) {
        palette[0] = (Color) _value.getObject();
        lattice.setColorPalette(palette);
      }
      break;
    case 6 :
      if(_value.getObject() instanceof Color&&palette[1]!=(Color) _value.getObject()) {
        palette[1] = (Color) _value.getObject();
        lattice.setColorPalette(palette);
      }
      break;
    case 7 :
      lattice.setShowGrid(_value.getBoolean());
      break;
    case 8 :
      if(_value.getObject() instanceof Color) {
        lattice.setGridLineColor((Color) _value.getObject());
      }
      break;
    case 9 :
      lattice.setVisible(_value.getBoolean());
      break;
    default :
      super.setValue(_index-10, _value);
    }
  }

  public void setDefaultValue(int _index) {
    switch(_index) {
    case 0 :
      lattice.setXMin(minX = -1.0);
      break;
    case 1 :
      lattice.setXMax(maxX = 1.0);
      break;
    case 2 :
      lattice.setYMin(minY = -1.0);
      break;
    case 3 :
      lattice.setYMax(maxY = 1.0);
      break;
    case 4 :
      lattice.randomize();
      break;
    case 5 :
      palette[0] = Color.white;
      lattice.setColorPalette(palette);
      break;
    case 6 :
      palette[1] = Color.black;
      lattice.setColorPalette(palette);
      break;
    case 7 :
      lattice.setShowGrid(true);
      break;
    case 8 :
      lattice.setGridLineColor(Color.lightGray);
      break;
    case 9 :
      lattice.setVisible(true);
      break;
    default :
      super.setDefaultValue(_index-10);
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
      return null;
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
