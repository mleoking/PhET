/**
 * Class: CubicUtil
 * Package: edu.colorado.phet.coreadditions
 * Author: Another Guy
 * Date: Mar 5, 2004
 */
package edu.colorado.phet.coreadditions;

import java.awt.geom.CubicCurve2D;
import java.awt.geom.Point2D;

/**
 * See: http://www.moshplant.com/direct-or/bezier/math.html
 */
public class CubicUtil {
    private Point2D endPt1;
    private Point2D endPt2;
    private Point2D ctrlPt1;
    private Point2D ctrlPt2;

    double cx, cy, bx, by, ax, ay;
    double x0;
    double y0;
    double x3;
    double y3;
    double x1;
    double y1;
    double x2;
    double y2;


    public CubicUtil( Point2D endPt1, Point2D ctrlPt1,
                      Point2D ctrlPt2, Point2D endPt2 ) {
        this.endPt1 = endPt1;
        this.endPt2 = endPt2;
        this.ctrlPt1 = ctrlPt1;
        this.ctrlPt2 = ctrlPt2;

        x0 = endPt1.getX();
        y0 = endPt1.getY();
        x3 = endPt2.getX();
        y3 = endPt2.getY();
        x1 = ctrlPt1.getX();
        y1 = ctrlPt1.getY();
        x2 = ctrlPt2.getX();
        y2 = ctrlPt2.getY();

        cx = 3 * ( x1 - x0 );
        bx = 3 * ( x2 - x1 ) - cx;
        ax = x3 - x0 - cx - bx;

        cy = 3 * ( y1 - y0 );
        by = 3 * ( y2 - y1 ) - cy;
        ay = y3 - y0 - cy - by;
    }

    public double[] getXforY( double y ) {

        double[] coefs = new double[]{y0 - y, cy, by, ay};
        double[] roots = new double[4];
        int numRoots = CubicCurve2D.solveCubic( coefs, roots );
        double[] xt = new double[numRoots];
        int j = 0;
        for( int i = 0; i < numRoots; i++ ) {
            double t = roots[i];
            // The proper root is in the interval [0...1]
            if( t >= 0 && t <= 1.0 ) {
                xt[j++] = ax * t * t * t + bx * t * t + cx * t + x0;
            }
        }
        double[] result = new double[j];
        for( int i = 0; i < result.length; i++ ) {
            result[i] = xt[i];
        }
        return result;
    }

    public double[] getYforX( double x ) {

        double[] coefs = new double[]{x0 - x, cx, bx, ax};
        double[] roots = new double[4];
        int numRoots = CubicCurve2D.solveCubic( coefs, roots );
        double[] yt = new double[numRoots];
        int j = 0;
        for( int i = 0; i < numRoots; i++ ) {
            double t = roots[i];
            // The proper root is in the interval [0...1]
            if( t >= 0 && t <= 1.0 ) {
                yt[j++] = ay * t * t * t + by * t * t + cy * t + y0;
            }
        }
        double[] result = new double[j];
        for( int i = 0; i < result.length; i++ ) {
            result[i] = yt[i];
        }
        return result;
    }

    /**
     * Computes the derivative of y with respect to x at a specified
     * x value.
     *
     * @param x
     * @return
     */
    public double dyDx( double x ) {

        // Compute t value corresponding to x. If there is not exactly one root,
        // return NaN, as this indicates there is no point on the cubic with the
        // specified x coordinate, or there is more than one.

        double[] coefs = new double[]{x0 - x, cx, bx, ax};
        double[] roots = new double[4];
        int numRoots = CubicCurve2D.solveCubic( coefs, roots );
        double[] yt = new double[numRoots];
        double tp = 0;
        int j = 0;
        for( int i = 0; i < numRoots; i++ ) {
            double t = roots[i];
            // The proper root is in the interval [0...1]
            if( t >= 0 && t <= 1.0 ) {
                yt[j++] = ay * t * t * t + by * t * t + cy * t + y0;
                tp = t;
            }
        }
        if( j < 1 || j > 1 ) {
            return Double.NaN;
        }

        // Get the y value corresponding to the specified x value.
        double y = yt[0];
        double numerator = 3 * ay * y * tp * tp + 2 * by * y * tp + cy * y;
        double denominator = 3 * ax * x * tp * tp + 2 * bx * x * tp + cx * x;
        double result = numerator / denominator;
        return result;
    }
}
