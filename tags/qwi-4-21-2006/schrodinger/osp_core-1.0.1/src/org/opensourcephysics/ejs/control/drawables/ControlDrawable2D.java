/*
 * Open Source Physics software is free software as described near the bottom of this code file.
 *
 * For additional information and documentation on Open Source Physics please see: 
 * <http://www.opensourcephysics.org/>
 */

package org.opensourcephysics.ejs.control.drawables;
import org.opensourcephysics.display2d.ColorMapper;
import org.opensourcephysics.ejs.control.swing.ControlDrawable;
import org.opensourcephysics.ejs.control.value.IntegerValue;
import org.opensourcephysics.ejs.control.value.Value;

public abstract class ControlDrawable2D extends ControlDrawable {
  public ControlDrawable2D(Object _visual) {
    super(_visual);
  }

  // ------------------------------------------------
  // Definition of Properties
  // ------------------------------------------------
  public Value parseConstant(String _propertyType, String _value) {
    if(_value==null) {
      return null;
    }
    if(_propertyType.indexOf("ColorMode")>=0||_propertyType.indexOf("PlotMode")>=0) {
      _value = _value.trim().toLowerCase();
      if(_value.equals("spectrum")) {
        return new IntegerValue(ColorMapper.SPECTRUM);
      }
      if(_value.equals("grayscale")) {
        return new IntegerValue(ColorMapper.GRAYSCALE);
      }
      if(_value.equals("dualshade")) {
        return new IntegerValue(ColorMapper.DUALSHADE);
      }
      if(_value.equals("red")) {
        return new IntegerValue(ColorMapper.RED);
      }
      if(_value.equals("green")) {
        return new IntegerValue(ColorMapper.GREEN);
      }
      if(_value.equals("blue")) {
        return new IntegerValue(ColorMapper.BLUE);
      }
      if(_value.equals("black")) {
        return new IntegerValue(ColorMapper.BLACK);
      }
      if(_value.equals("binary")) {
        return new IntegerValue(ColorMapper.BLACK);
      }
      if(_value.equals("wireframe")) {
        return new IntegerValue(ColorMapper.WIREFRAME);
      }
      if(_value.equals("norender")) {
        return new IntegerValue(ColorMapper.NORENDER);
      }
      if(_value.equals("redblueshade")) {
        return new IntegerValue(ColorMapper.REDBLUE_SHADE);
      }
    }
    return super.parseConstant(_propertyType, _value);
  }
  // ------------------------------------------------
  // Variables
  // ------------------------------------------------
} // End of interface

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
