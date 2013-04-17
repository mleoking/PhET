/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2010-08-06 07:07:53 -0700 (Fri, 06 Aug 2010) $
 * $Revision: 13851 $
 *
 * Copyright (C) 2004-2005  The Jmol Development Team
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

import org.jmol.modelset.TickInfo;
import org.jmol.util.Escape;

public abstract class FontLineShape extends FontShape {

  // Axes, Bbcage, Uccage
  
  TickInfo[] tickInfos = new TickInfo[4];

  @Override
  public void setProperty(String propertyName, Object value, BitSet bs) {

    if ("tickInfo" == propertyName) {
      TickInfo t = (TickInfo) value;
      if (t.ticks == null) {
        // null ticks is an indication to delete the tick info
        if (t.type.equals(" "))
          tickInfos[0] = tickInfos[1] = tickInfos[2] = tickInfos[3] = null;
        else
          tickInfos["xyz".indexOf(t.type) + 1] = null;
        return;
      }
      tickInfos["xyz".indexOf(t.type) + 1] = t;
      return;
    }

    super.setProperty(propertyName, value, bs);
  }

  @Override
  public String getShapeState() {
    String s = super.getShapeState();
    if (tickInfos == null)
      return s;
    StringBuffer sb = new StringBuffer(s);
    if (tickInfos[0] != null)
      appendTickInfo(sb, 0);
    if (tickInfos[1] != null)
      appendTickInfo(sb, 1);
    if (tickInfos[2] != null)
      appendTickInfo(sb, 2);
    if (tickInfos[3] != null)
      appendTickInfo(sb, 3);
    if (s.indexOf(" off") >= 0)
      sb.append("  " + myType + " off;\n");
    return sb.toString();
  }
  
  private void appendTickInfo(StringBuffer sb, int i) {
    sb.append("  ");
    sb.append(myType);
    addTickInfo(sb, tickInfos[i], false);
    sb.append(";\n");
  }

  public static void addTickInfo(StringBuffer sb, TickInfo tickInfo, boolean addFirst) {
    sb.append(" ticks ").append(tickInfo.type).append(" ").append(Escape.escape(tickInfo.ticks));
    boolean isUnitCell = (tickInfo.scale != null && Float.isNaN(tickInfo.scale.x));
    if (isUnitCell)
      sb.append(" UNITCELL");
    if (tickInfo.tickLabelFormats != null)
      sb.append(" format ").append(Escape.escape(tickInfo.tickLabelFormats, false));
    if (!isUnitCell && tickInfo.scale != null)
      sb.append(" scale ").append(Escape.escape(tickInfo.scale));
    if (addFirst && !Float.isNaN(tickInfo.first) && tickInfo.first != 0)
      sb.append(" first ").append(tickInfo.first);
    if (tickInfo.reference != null) // not implemented
      sb.append(" point ").append(Escape.escape(tickInfo.reference)); 
  }
}
