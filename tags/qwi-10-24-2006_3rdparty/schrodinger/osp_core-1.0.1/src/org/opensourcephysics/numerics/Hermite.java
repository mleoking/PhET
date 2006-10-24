/*
 * Open Source Physics software is free software as described near the bottom of this code file.
 *
 * For additional information and documentation on Open Source Physics please see: 
 * <http://www.opensourcephysics.org/>
 */

package org.opensourcephysics.numerics;
import java.util.ArrayList;

/**
 * Calcualtes hermite polynomials.
 *
 * @author W. Christian
 * @version 1.0
 */
public class Hermite {
  static final ArrayList hermiteList;
  static final Polynomial twoX = new Polynomial(new double[] {0, 2.0}); // 2x used in recursion

  private Hermite() {}

  public static synchronized Polynomial getPolynomial(int n) {
    if(n<hermiteList.size()) {
      return(Polynomial) hermiteList.get(n);
    }
    Polynomial p1 = getPolynomial(n-1).multiply(twoX);
    Polynomial p2 = getPolynomial(n-2).multiply(2*(n-1));
    return p1.subtract(p2);
  }

  static {
    hermiteList = new ArrayList();
    Polynomial p = new Polynomial(new double[] {1.0});
    hermiteList.add(p);
    p = new Polynomial(new double[] {0, 2.0});
    hermiteList.add(p);
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
