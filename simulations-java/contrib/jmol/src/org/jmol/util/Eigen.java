/* $RCSfile$
 * $Author: egonw $
 * $Date: 2005-11-10 09:52:44f -0600 (Thu, 10 Nov 2005) $
 * $Revision: 4255 $
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

package org.jmol.util;

//import org.jmol.util.Escape;

/** Eigenvalues and eigenvectors of a real (n x n) symmetric matrix. 

 adapted by Bob Hanson from http://math.nist.gov/javanumerics/jama/ (public domain)

 <P>
 If A is symmetric, then A = V*D*V' where the eigenvalue matrix D is
 diagonal and the eigenvector matrix V is orthogonal.
 I.e. A = V.times(D.times(V.transpose())) and 
 V.times(V.transpose()) equals the identity matrix.
 
 output is as a set of double[n] columns, but for Jmol we use getEigenvectorsFloatTransformed
 to return them as a set of rows for easier use as A[0], A[1], etc. 
  
 **/

public class Eigen {

  /* ------------------------
  Class variables
* ------------------------ */

  /** Row and column dimension (square matrix).
  @serial matrix dimension.
  */
  private int n;

  /** Arrays for internal storage of eigenvalues.
  @serial internal storage of eigenvalues.
  */
  private double[] d, e;

  /** Array for internal storage of eigenvectors.
  @serial internal storage of eigenvectors.
  */
  private double[][] V;

/* ------------------------
  Private Methods
 * ------------------------ */

  // Symmetric Householder reduction to tridiagonal form.

  private void tred2 () {

  //  This is derived from the Algol procedures tred2 by
  //  Bowdler, Martin, Reinsch, and Wilkinson, Handbook for
  //  Auto. Comp., Vol.ii-Linear Algebra, and the corresponding
  //  Fortran subroutine in EISPACK.

     for (int j = 0; j < n; j++) {
        d[j] = V[n-1][j];
     }

     // Householder reduction to tridiagonal form.
  
     for (int i = n-1; i > 0; i--) {
  
        // Scale to avoid under/overflow.
  
        double scale = 0.0;
        double h = 0.0;
        for (int k = 0; k < i; k++) {
           scale = scale + Math.abs(d[k]);
        }
        if (scale == 0.0) {
           e[i] = d[i-1];
           for (int j = 0; j < i; j++) {
              d[j] = V[i-1][j];
              V[i][j] = 0.0;
              V[j][i] = 0.0;
           }
        } else {
  
           // Generate Householder vector.
  
           for (int k = 0; k < i; k++) {
              d[k] /= scale;
              h += d[k] * d[k];
           }
           double f = d[i-1];
           double g = Math.sqrt(h);
           if (f > 0) {
              g = -g;
           }
           e[i] = scale * g;
           h = h - f * g;
           d[i-1] = f - g;
           for (int j = 0; j < i; j++) {
              e[j] = 0.0;
           }
  
           // Apply similarity transformation to remaining columns.
  
           for (int j = 0; j < i; j++) {
              f = d[j];
              V[j][i] = f;
              g = e[j] + V[j][j] * f;
              for (int k = j+1; k <= i-1; k++) {
                 g += V[k][j] * d[k];
                 e[k] += V[k][j] * f;
              }
              e[j] = g;
           }
           f = 0.0;
           for (int j = 0; j < i; j++) {
              e[j] /= h;
              f += e[j] * d[j];
           }
           double hh = f / (h + h);
           for (int j = 0; j < i; j++) {
              e[j] -= hh * d[j];
           }
           for (int j = 0; j < i; j++) {
              f = d[j];
              g = e[j];
              for (int k = j; k <= i-1; k++) {
                 V[k][j] -= (f * e[k] + g * d[k]);
              }
              d[j] = V[i-1][j];
              V[i][j] = 0.0;
           }
        }
        d[i] = h;
     }
  
     // Accumulate transformations.
  
     for (int i = 0; i < n-1; i++) {
        V[n-1][i] = V[i][i];
        V[i][i] = 1.0;
        double h = d[i+1];
        if (h != 0.0) {
           for (int k = 0; k <= i; k++) {
              d[k] = V[k][i+1] / h;
           }
           for (int j = 0; j <= i; j++) {
              double g = 0.0;
              for (int k = 0; k <= i; k++) {
                 g += V[k][i+1] * V[k][j];
              }
              for (int k = 0; k <= i; k++) {
                 V[k][j] -= g * d[k];
              }
           }
        }
        for (int k = 0; k <= i; k++) {
           V[k][i+1] = 0.0;
        }
     }
     for (int j = 0; j < n; j++) {
        d[j] = V[n-1][j];
        V[n-1][j] = 0.0;
     }
     V[n-1][n-1] = 1.0;
     e[0] = 0.0;
  } 

  // Symmetric tridiagonal QL algorithm.
  
  private void tql2 () {

  //  This is derived from the Algol procedures tql2, by
  //  Bowdler, Martin, Reinsch, and Wilkinson, Handbook for
  //  Auto. Comp., Vol.ii-Linear Algebra, and the corresponding
  //  Fortran subroutine in EISPACK.
  
     for (int i = 1; i < n; i++) {
        e[i-1] = e[i];
     }
     e[n-1] = 0.0;
  
     double f = 0.0;
     double tst1 = 0.0;
     double eps = Math.pow(2.0,-52.0);
     for (int eL = 0; eL < n; eL++) {

        // Find small subdiagonal element
  
        tst1 = Math.max(tst1,Math.abs(d[eL]) + Math.abs(e[eL]));
        int m = eL;
        while (m < n) {
           if (Math.abs(e[m]) <= eps*tst1) {
              break;
           }
           m++;
        }
  
        // If m == eL, d[eL] is an eigenvalue,
        // otherwise, iterate.
  
        if (m > eL) {
           int iter = 0;
           do {
              iter = iter + 1;  // (Could check iteration count here.)
  
              // Compute implicit shift
  
              double g = d[eL];
              double p = (d[eL+1] - g) / (2.0 * e[eL]);
              double r = hypot(p,1.0);
              if (p < 0) {
                 r = -r;
              }
              d[eL] = e[eL] / (p + r);
              d[eL+1] = e[eL] * (p + r);
              double dl1 = d[eL+1];
              double h = g - d[eL];
              for (int i = eL+2; i < n; i++) {
                 d[i] -= h;
              }
              f = f + h;
  
              // Implicit QL transformation.
  
              p = d[m];
              double c = 1.0;
              double c2 = c;
              double c3 = c;
              double el1 = e[eL+1];
              double s = 0.0;
              double s2 = 0.0;
              for (int i = m-1; i >= eL; i--) {
                 c3 = c2;
                 c2 = c;
                 s2 = s;
                 g = c * e[i];
                 h = c * p;
                 r = hypot(p,e[i]);
                 e[i+1] = s * r;
                 s = e[i] / r;
                 c = p / r;
                 p = c * d[i] - s * g;
                 d[i+1] = h + s * (c * g + s * d[i]);
  
                 // Accumulate transformation.
  
                 for (int k = 0; k < n; k++) {
                    h = V[k][i+1];
                    V[k][i+1] = s * V[k][i] + c * h;
                    V[k][i] = c * V[k][i] - s * h;
                 }
              }
              p = -s * s2 * c3 * el1 * e[eL] / dl1;
              e[eL] = s * p;
              d[eL] = c * p;
  
              // Check for convergence.
  
           } while (Math.abs(e[eL]) > eps*tst1);
        }
        d[eL] = d[eL] + f;
        e[eL] = 0.0;
     }
    
     // Sort eigenvalues and corresponding vectors.
  
     for (int i = 0; i < n-1; i++) {
        int k = i;
        double p = d[i];
        for (int j = i+1; j < n; j++) {
           if (d[j] < p) {
              k = j;
              p = d[j];
           }
        }
        if (k != i) {
           d[k] = d[i];
           d[i] = p;
           for (int j = 0; j < n; j++) {
              p = V[j][i];
              V[j][i] = V[j][k];
              V[j][k] = p;
           }
        }
     }
  }

  public Eigen(double[][] A) {

    // symmetric option is all we need here
    
    n = A.length;
    
    V = new double[n][n];
    d = new double[n];
    e = new double[n];

       for (int i = 0; i < n; i++) {
          for (int j = 0; j < n; j++) {
             V[i][j] = A[i][j];
          }
       }
 
       // Tridiagonalize.

       tred2();
 
       // Diagonalize and sort.

       tql2();
  }

  public double[][] getEigenvectors() {
    return V;
  }

  public double[] getEigenvalues() {
    return d;
  }

  /**
   * transpose V and turn into floats
   * @return ROWS of eigenvectors f[0], f[1], f[2], etc.
   */
  public float[][] getEigenvectorsFloatTransposed() {
    float[][] f = new float[n][n];
    for (int i = n; --i >= 0; )
      for (int j = n; --j >= 0; )
        f[j][i] = (float) V[i][j];
    return f;
  }
  
  private static double hypot(double a, double b) {

    // sqrt(a^2 + b^2) without under/overflow. 

    double r;
    if (Math.abs(a) > Math.abs(b)) {
      r = b / a;
      r = Math.abs(a) * Math.sqrt(1 + r * r);
    } else if (b != 0) {
      r = a / b;
      r = Math.abs(b) * Math.sqrt(1 + r * r);
    } else {
      r = 0.0;
    }
    return r;
  }

}
