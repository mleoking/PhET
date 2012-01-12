/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2010-04-17 16:24:30 -0700 (Sat, 17 Apr 2010) $
 *
 * Copyright (C) 2003-2005  Miguel, Jmol Development, www.jmol.org
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
package org.jmol.bspt;

import javax.vecmath.Point3f;

/**
 * the internal tree is made up of elements ... either Node or Leaf
 *
 * @author Miguel, miguel@jmol.org
 */
abstract class Element {
  Bspt bspt;
  int count;
  abstract Element addTuple(int level, Point3f tuple);
  
  abstract void dump(int level, StringBuffer sb);
  
}

