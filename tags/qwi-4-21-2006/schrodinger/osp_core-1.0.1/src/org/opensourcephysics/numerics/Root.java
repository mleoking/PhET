/*
 * Open Source Physics software is free software as described near the bottom of this code file.
 *
 * For additional information and documentation on Open Source Physics please see:
 * <http://www.opensourcephysics.org/>
 */

package org.opensourcephysics.numerics;

/**
 * Class Root defines various root finding algorithms.
 *
 * This class cannot be subclassed or instantiated because all methods are static.
 *
 * @author Wolfgang Christian
 */
public class Root {
  static final int MAX_ITERATIONS = 15;

  private Root() {} // prohibit instantiation because all methods are static

  /**
   * Solves for the roots of the quadratic equation
   * ax<sup>2</sup>+bx+c=0.
   *
   * @param a double quadratic term coefficient
   * @param b double linear term coefficient
   * @param c double constant term
   * @return double[] an array containing the two roots.
   */
  public static double[] solveQuadratic(final double a, final double b, final double c) {
    final double roots[] = new double[2];
    final double q = -0.5*(b+(b<0.0 ? -1.0 : 1.0)*Math.sqrt(b*b-4.0*a*c));
    roots[0] = q/a;
    roots[1] = c/q;
    return roots;
  }

  /**
   * Implements Newton's method for finding the root of a function.
   * The derivative is calculated numerically using Romberg's method.
   *
   * @param f Function the function
   * @param x double guess the root
   * @param tol double computation tolerance
   * @return double the root or NaN if root not found.
   */
  public static double newton(final Function f, double x, final double tol) {
    int count = 0;
    while(count<MAX_ITERATIONS) {
      double xold = x; // save the old value to test for convergence
      double df = 0;
      try {
        df = Derivative.romberg(f, x, Math.max(0.001, 0.001*Math.abs(x)), tol/10);
      } catch(NumericMethodException ex) {
        return Double.NaN; // did not converve
      }
      x -= f.evaluate(x)/df;
      if(Util.relativePrecision(Math.abs(x-xold), x)<tol) {
        return x;
      }
      count++;
    }
    return Double.NaN; // did not converve in max iterations
  }

  /**
   * Implements Newton's method for finding the root of a function.
   *
   * @param f Function the function
   * @param df Function the derivative of the function
   * @param x double guess the root
   * @param tol double computation tolerance
   * @return double the root or NaN if root not found.
   */
  public static double newton(final Function f, final Function df, double x, final double tol) {
    int count = 0;
    while(count<MAX_ITERATIONS) {
      double xold = x; // save the old value to test for convergence
      // approximate the derivative using centered difference
      x -= f.evaluate(x)/df.evaluate(x);
      if(Util.relativePrecision(Math.abs(x-xold), x)<tol) {
        return x;
      }
      count++;
    }
    return Double.NaN; // did not converve in max iterations
  }

  /**
   * Implements the bisection method for finding the root of a function.
   * @param f Function the function
   * @param x1 double lower
   * @param x2 double upper
   * @param tol double computation tolerance
   * @return double the root or NaN if root not found
   */
  public static double bisection(final Function f, double x1, double x2, final double tol) {
    int count = 0;
    int maxCount = (int) (Math.log(Math.abs(x2-x1)/tol)/Math.log(2));
    maxCount = Math.max(MAX_ITERATIONS, maxCount)+2;
    double y1 = f.evaluate(x1), y2 = f.evaluate(x2);
    if(y1*y2>0) {        // y1 and y2 must have opposite sign
      return Double.NaN; // interval does not contain a root
    }
    while(count<maxCount) {
      double x = (x1+x2)/2;
      double y = f.evaluate(x);
      if(Util.relativePrecision(Math.abs(x1-x2), x)<tol) {
        return x;
      }
      if(y*y1>0) { // replace end-point that has the same sign
        x1 = x;
        y1 = y;
      } else {
        x2 = x;
        y2 = y;
      }
      count++;
    }
    return Double.NaN; // did not converge in max iterations
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
