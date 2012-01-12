/* $RCSfile$
 * $Author: nicove $
 * $Date: 2010-07-31 02:51:00 -0700 (Sat, 31 Jul 2010) $
 * $Revision: 13783 $
 *
 * Copyright (C) 2002-2006  Miguel, Jmol Development, www.jmol.org
 *
 * Contact: jmol-developers@lists.sf.net
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.jmol.shape;

import java.util.BitSet;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import org.jmol.api.SymmetryInterface;
import org.jmol.util.Escape;
import org.jmol.viewer.JmolConstants;

public class Axes extends FontLineShape {

  Point3f axisXY = new Point3f();
  float scale;
  
  private Point3f fixedOrigin;
  final Point3f originPoint = new Point3f();
  final Point3f[] axisPoints = new Point3f[6];
  final static Point3f pt0 = new Point3f();
  String[] labels;
  
  {
    for (int i = 6; --i >= 0; )
      axisPoints[i] = new Point3f();
  }

  Point3f getOriginPoint(boolean isDataFrame) {
    return (isDataFrame ? pt0 : originPoint);
  }
  
  final Point3f ptTemp = new Point3f();
  Point3f getAxisPoint(int i, boolean isDataFrame) {
    if (!isDataFrame && axisXY.z == 0)
      return axisPoints[i];
    ptTemp.set(axisPoints[i]);
    ptTemp.sub(originPoint);
    ptTemp.scale(0.5f);
    return ptTemp; 
  }
  
  private final static float MIN_AXIS_LEN = 1.5f;

  @Override
  public void setProperty(String propertyName, Object value, BitSet bs) {
    if ("position" == propertyName) {
      axisXY = (Point3f) value;
      // z = 0 for no set xy position (default)
      // z = -Float.MAX_VALUE for percent
      // z = Float.MAX_VALUE for positioned
      return;
    }
    if ("origin" == propertyName) {
      if (value == null) {
        fixedOrigin = null;
      } else {
        if (fixedOrigin == null)
          fixedOrigin = new Point3f();
        fixedOrigin.set((Point3f) value);
      }
      initShape();
      return;
    }
    if ("labels" == propertyName) {
      labels = (String[]) value;
      return;
    }
    if ("labelsOn" == propertyName) {
      labels = null;
      return;
    }
    if ("labelsOff" == propertyName) {
      labels = new String[] {"", "", ""};
      return;
    }
    
    super.setProperty(propertyName, value, bs);
  }

  @Override
  public void initShape() {
    super.initShape();
    myType = "axes";
    font3d = g3d.getFont3D(JmolConstants.AXES_DEFAULT_FONTSIZE);
    int axesMode = viewer.getAxesMode();
    if (fixedOrigin == null)
      originPoint.set(0, 0, 0);
    else
      originPoint.set(fixedOrigin);
    if (axesMode == JmolConstants.AXES_MODE_UNITCELL
        && modelSet.getCellInfos() != null) {
      SymmetryInterface unitcell = viewer.getCurrentUnitCell();
      if (unitcell != null && unitcell.haveUnitCell()) {
        Point3f[] vectors = unitcell.getUnitCellVertices();
        Point3f offset = unitcell.getCartesianOffset();
        if (fixedOrigin == null) {
          originPoint.set(offset);
        } else {
          offset = fixedOrigin;
        }
        scale = viewer.getAxesScale() / 2f;
        // We must divide by 2 because that is the default for ALL axis types.
        // Not great, but it will have to do.
        axisPoints[0].scaleAdd(scale, vectors[4], offset);
        axisPoints[1].scaleAdd(scale, vectors[2], offset);
        axisPoints[2].scaleAdd(scale, vectors[1], offset);
        return;
      }
    } else if (axesMode == JmolConstants.AXES_MODE_BOUNDBOX) {
      if (fixedOrigin == null)
        originPoint.set(viewer.getBoundBoxCenter());
    }
    setScale(viewer.getAxesScale() / 2f);
  }
  
  @Override
  public Object getProperty(String property, int index) {
    if (property == "axisPoints")
      return axisPoints;
    if (property == "origin")
      return fixedOrigin;
    if (property == "axesTypeXY")
      return (axisXY.z == 0 ? Boolean.FALSE : Boolean.TRUE);
    return null;
  }

  Vector3f corner = new Vector3f();
  
  void setScale(float scale) {
    this.scale = scale;
    corner.set(viewer.getBoundBoxCornerVector());
    for (int i = 6; --i >= 0;) {
      Point3f axisPoint = axisPoints[i];
      axisPoint.set(JmolConstants.unitAxisVectors[i]);
      // we have just set the axisPoint to be a unit on a single axis
   
      // therefore only one of these values (x, y, or z) will be nonzero
      // it will have value 1 or -1
      if (corner.x < MIN_AXIS_LEN)
        corner.x = MIN_AXIS_LEN;
      if (corner.y < MIN_AXIS_LEN)
        corner.y = MIN_AXIS_LEN;
      if (corner.z < MIN_AXIS_LEN)
        corner.z = MIN_AXIS_LEN;
      if (axisXY.z == 0) {
        axisPoint.x *= corner.x * scale;
        axisPoint.y *= corner.y * scale;
        axisPoint.z *= corner.z * scale;
      }
      axisPoint.add(originPoint);
    }
  }
  
 @Override
public String getShapeState() {
    StringBuffer sb = new StringBuffer();
    sb.append("  axes scale ").append(viewer.getAxesScale()).append(";\n"); 
    if (fixedOrigin != null)
      sb.append("  axes center ")
          .append(Escape.escape(fixedOrigin)).append(";\n");
    if (axisXY.z != 0)
      sb.append("  axes position [")
          .append((int) axisXY.x).append(" ")
          .append((int) axisXY.y).append(" ")
          .append(axisXY.z < 0 ? " %" : "").append("];\n");
    if (labels != null) {
      sb.append("  axes labels ");
      for (int i = 0; i < labels.length; i++)
        sb.append(Escape.escape(labels[i])).append(" ");
      sb.append(";\n");
    }
    return super.getShapeState() + sb;
  }

}
