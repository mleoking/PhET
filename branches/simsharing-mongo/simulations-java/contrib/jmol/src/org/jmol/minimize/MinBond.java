/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2007-11-23 12:49:25 -0600 (Fri, 23 Nov 2007) $
 * $Revision: 8655 $
 *
 * Copyright (C) 2003-2005  The Jmol Development Team
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

package org.jmol.minimize;

public class MinBond {
  //Bond bond;
  public int[] atomIndexes = new int[3]; //third index is bondOrder
  public boolean isAromatic;
  public boolean isAmide;
  
  MinBond(int[] atomIndexes, boolean isAromatic, boolean isAmide) {
    this.atomIndexes = atomIndexes; // includes bond order
    this.isAromatic = isAromatic;
    this.isAmide = isAmide;
  }
  
  public int getOtherAtom(int index) {
    return (atomIndexes[0] == index ? atomIndexes[1] : atomIndexes[0]);    
  }
}
