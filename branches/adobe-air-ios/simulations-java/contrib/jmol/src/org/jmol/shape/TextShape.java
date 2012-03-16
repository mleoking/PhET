/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2006-09-13 00:06:10 -0500 (Wed, 13 Sep 2006) $
 * $Revision: 5516 $
 *
 * Copyright (C) 2002-2005  The Jmol Development Team
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

import org.jmol.g3d.*;

import java.util.BitSet;
import java.util.Iterator;

public abstract class TextShape extends Object2dShape {

  // echo, hover
  
  @Override
  public void setProperty(String propertyName, Object value, BitSet bsSelected) {

    if ("text" == propertyName) {
      String text = (String) value;
      if (currentObject == null) {
        if (isAll) {
          Iterator<Text> e = objects.values().iterator();
          while (e.hasNext()) {
            e.next().setText(text);
          }
        }
        return;
      }
      ((Text) currentObject).setText(text);
      return;
    }

    if ("font" == propertyName) {
      currentFont = (Font3D) value;
      if (currentObject == null) {
        if (isAll) {
          Iterator<Text> e = objects.values().iterator();
          while (e.hasNext()) {
            e.next().setFont(currentFont);
          }
        }
        return;
      }
      ((Text) currentObject).setFont(currentFont);
      ((Text) currentObject).setFontScale(0);
      return;
    }
    
    super.setProperty(propertyName, value, bsSelected);
  }
}

