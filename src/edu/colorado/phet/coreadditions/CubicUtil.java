/**
 * Class: CubicUtil
 * Package: edu.colorado.phet.coreadditions
 * Author: Another Guy
 * Date: Mar 5, 2004
 */
package edu.colorado.phet.coreadditions;

import java.awt.geom.CubicCurve2D;
import java.awt.geom.Point2D;

public class CubicUtil {
    private Point2D.Double endPt1;
    private Point2D.Double endPt2;
    private Point2D.Double ctrlPt1;
    private Point2D.Double ctrlPt2;

    public CubicUtil( Point2D.Double endPt1, Point2D.Double ctrlPt1,
                      Point2D.Double ctrlPt2, Point2D.Double endPt2 ) {
        this.endPt1 = endPt1;
        this.endPt2 = endPt2;
        this.ctrlPt1 = ctrlPt1;
        this.ctrlPt2 = ctrlPt2;
    }

    public double getHillX( double y ) {
        double cx, cy, bx, by, ax, ay;
        double x0 = endPt1.getX();
        double y0 = endPt1.getY();

        double x3 = endPt2.getX();
        double y3 = endPt2.getY();

        double x1 = ctrlPt1.getX();
        double y1 = ctrlPt1.getY();

        double x2 = ctrlPt2.getX();
        double y2 = ctrlPt2.getY();

        cx = 3 * ( x1 - x0 );
        bx = 3 * ( x2 - x1 ) - cx;
        ax = x3 - x0 - cx - bx;

        cy = 3 * ( y1 - y0 );
        by = 3 * ( y2 - y1 ) - cy;
        ay = y3 - y0 - cy - by;

        double[] coefs = new double[]{y0 - y, cy, by, ay};
        double[] roots = new double[4];
        int numRoots = CubicCurve2D.solveCubic( coefs, roots );
        double[] xt = new double[numRoots];
        double result = 0;
        for( int i = 0; i < numRoots; i++ ) {
            double t = roots[i];
            // The proper root is in the interval [0...1]
            if( t >= 0 && t <= 1.0 ) {
                xt[i] = ax * t * t * t + bx * t * t + cx * t + x0;
                result = xt[i];
                break;
            }
        }
        return result;
    }

    public double[] getXforY( double y ) {
        double cx, cy, bx, by, ax, ay;
        double x0 = endPt1.getX();
        double y0 = endPt1.getY();

        double x3 = endPt2.getX();
        double y3 = endPt2.getY();

        double x1 = ctrlPt1.getX();
        double y1 = ctrlPt1.getY();

        double x2 = ctrlPt2.getX();
        double y2 = ctrlPt2.getY();

        cx = 3 * ( x1 - x0 );
        bx = 3 * ( x2 - x1 ) - cx;
        ax = x3 - x0 - cx - bx;

        cy = 3 * ( y1 - y0 );
        by = 3 * ( y2 - y1 ) - cy;
        ay = y3 - y0 - cy - by;

        double[] coefs = new double[]{y0 - y, cy, by, ay};
        double[] roots = new double[4];
        int numRoots = CubicCurve2D.solveCubic( coefs, roots );
        double[] xt = new double[numRoots];
        for( int i = 0; i < numRoots; i++ ) {
            double t = roots[i];
            // The proper root is in the interval [0...1]
            if( t >= 0 && t <= 1.0 ) {
                xt[i] = ax * t * t * t + bx * t * t + cx * t + x0;
            }
            else {
                xt[i] = Double.NaN;
            }
        }
        return xt;
    }

    public double[] getYforX( double x ) {
        double cx, cy, bx, by, ax, ay;
        double x0 = endPt1.getY();
        double y0 = endPt1.getX();

        double x3 = endPt2.getY();
        double y3 = endPt2.getX();

        double x1 = ctrlPt1.getY();
        double y1 = ctrlPt1.getX();

        double x2 = ctrlPt2.getY();
        double y2 = ctrlPt2.getX();

        cx = 3 * ( x1 - x0 );
        bx = 3 * ( x2 - x1 ) - cx;
        ax = x3 - x0 - cx - bx;

        cy = 3 * ( y1 - y0 );
        by = 3 * ( y2 - y1 ) - cy;
        ay = y3 - y0 - cy - by;

        double[] coefs = new double[]{y0 - x, cy, by, ay};
        double[] roots = new double[4];
        int numRoots = CubicCurve2D.solveCubic( coefs, roots );
        double[] xt = new double[numRoots];
        for( int i = 0; i < numRoots; i++ ) {
            double t = roots[i];
            // The proper root is in the interval [0...1]
            if( t >= 0 && t <= 1.0 ) {
                xt[i] = ax * t * t * t + bx * t * t + cx * t + x0;
            }
            else {
                xt[i] = Double.NaN;
            }
        }
        return xt;
    }
}
