/**
 * Class: PotentialProfile
 * Package: edu.colorado.phet.nuclearphysics.model
 * Author: Another Guy
 * Date: Feb 26, 2004
 */
package edu.colorado.phet.nuclearphysics.model;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

/**
 * This class represents the potential energy profile of a particular atom.
 * It's attributes include the width of the profile, which corresponds to the
 * spatial distance from the well to the ground state energy outside the well.
 * <p/>
 * Here is my assumed model for the shape of the profile:
 * - The width of the profile is the distance from tail to tail (i.e., twice the
 * distance from the bottom of the well to one of the tails).
 * - The peak of the hill is 1/5 the distance from the well to the tail
 * - The profile of the hill is a straight line from the peak to the tail. (This
 * model will be improved sometime soon, I hope.)
 * - The bottom of the well has an x coordinate of 0
 */
public class PotentialProfile {
    private double width;
    private double maxPotential;
    private double wellDepth;
    private double hillPeakX;
    private Point2D.Double hillPeakLocation;
    private Point2D.Double tailLocation;
    private Line2D.Double hillBoundary;
    private Point2D.Double testPt = new Point2D.Double();
    private Shape[] shape = new Shape[4];
    private Point2D.Double endPt1 = new Point2D.Double();
    private Point2D.Double ctrlPt1 = new Point2D.Double();
    private Point2D.Double endPt2 = new Point2D.Double();
    private Point2D.Double ctrlPt2A = new Point2D.Double();
    private Point2D.Double ctrlPt2B = new Point2D.Double();
    private Point2D.Double endPt3 = new Point2D.Double();
    private Point2D.Double ctrlPt3 = new Point2D.Double();
    private double alphaDecayX;

    public PotentialProfile() {
    }

    public PotentialProfile( double width, double maxPotential, double wellDepth ) {
        this.width = width;
        this.maxPotential = maxPotential;
        this.wellDepth = wellDepth;
        this.hillPeakX = width / 10;
        this.hillPeakLocation = new Point2D.Double( hillPeakX, this.maxPotential );
        this.tailLocation = new Point2D.Double( width / 2, 0 );
        // Note: the order in which the arguments are provided in this constructor are
        // critical if the method getDistFromHill() is to work properly
        this.hillBoundary = new Line2D.Double( tailLocation, hillPeakLocation );

        // Generate the cubic curves for the profile
        this.generate();
    }

    public double getWidth() {
        return width;
    }

    public void setWidth( double width ) {
        this.width = width;
        generate();
    }

    public double getMaxPotential() {
        return maxPotential;
    }

    public void setMaxPotential( double maxPotential ) {
        this.maxPotential = maxPotential;
        generate();
    }

    public double getWellPotential() {
        return maxPotential - wellDepth;
    }

    public void setWellPotential( double wellPotential ) {
        this.wellDepth = maxPotential - wellPotential;
        generate();
    }

    public void setWellDepth( double wellDepth ) {
        this.wellDepth = wellDepth;
        generate();
    }

    /**
     * Returns the perpendicular distance from a point to the
     * side of the profile. If the point is inside the hill,
     * the value returned is < 0. If the point is outside the
     * hill, the value returned is > 0.
     * 
     * @param pt 
     * @return 
     */
    public double getDistFromHill( Point2D.Double pt ) {
        testPt.setLocation( Math.abs( pt.getX() ), pt.getY() );
        int ccw = hillBoundary.relativeCCW( testPt );
        double result = hillBoundary.ptLineDist( testPt ) * ccw;
        return result;
    }

    /**
     * Generates the cubic splines that define the energy profile. Also computes
     * the x coordinate of the hill slope that is at the same y coordinat as the
     * bottom of the well.
     */
    private void generate() {

        // Draw the curve going up the left side of the potential profile
        endPt1.x = -getWidth() / 2;
        endPt1.y = 0;
        endPt2.x = -getWidth() / 10;
        endPt2.y = -getMaxPotential();

        ctrlPt1.x = endPt1.getX() + ( ( endPt2.getX() - endPt1.getX() ) / 3 );
        ctrlPt1.y = endPt1.getY();
        ctrlPt2A.x = endPt2.getX() - ( ( endPt2.getX() - endPt1.getX() ) / 3 );
        ctrlPt2A.y = endPt2.getY();

        shape[0] = new CubicCurve2D.Double( endPt1.x, endPt1.y,
                                            ctrlPt1.x, ctrlPt1.y,
                                            ctrlPt2A.x, ctrlPt2A.y,
                                            endPt2.x, endPt2.y );

        // Draw the curve down into the left side of the potential well
        endPt3.x = 0;
        endPt3.y = -getWellPotential();

        ctrlPt2B.x = endPt2.getX() + ( ( endPt2.getX() - endPt1.getX() ) / 4 );
        ctrlPt2B.y = endPt2.getY();
        ctrlPt3.x = endPt3.getX() - ( ( endPt3.getX() - endPt2.getX() ) / 2 );
        ctrlPt3.y = endPt3.getY();

        shape[1] = new CubicCurve2D.Double( endPt2.x, endPt2.y,
                                            ctrlPt2B.x, ctrlPt2B.y,
                                            ctrlPt3.x, ctrlPt3.y,
                                            endPt3.x, endPt3.y );

        // draw the curve for the right side of the well
        AffineTransform profileTx = new AffineTransform();
        profileTx.setToIdentity();
        profileTx.scale( -1, 1 );

        shape[2] = profileTx.createTransformedShape( shape[1] );
        shape[3] = profileTx.createTransformedShape( shape[0] );

        alphaDecayX = getHillX( -getWellPotential() );
    }

    public double getAlphaDecayX() {
        return this.alphaDecayX;
    }

    public Shape[] getShape() {
        return shape;
    }

    /**
     * Gives the x coordinate of the hill-side of the profile that is at the same energy
     * level as the bottom of the well.
     * <p/>
     * See: http://www.moshplant.com/direct-or/bezier/math.html
     *
     * @param y
     * @return
     */
    private double getHillX( double y ) {
        double cx, cy, bx, by, ax, ay;
        double x0 = endPt1.getX();
        double y0 = endPt1.getY();

        double x3 = endPt2.getX();
        double y3 = endPt2.getY();

        double x1 = ctrlPt1.getX();
        double y1 = ctrlPt1.getY();

        double x2 = ctrlPt2A.getX();
        double y2 = ctrlPt2A.getY();

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
                System.out.println( i + ": " + roots[i] + "  " + xt[i] );
                result = xt[i];
                break;
            }
        }
        return result;
    }

    // A test to see if the parametric equations are being computed properly
    public Point2D.Double[] genPts( int numPts ) {
        Point2D.Double[] result = new Point2D.Double[numPts];
        double cx, cy, bx, by, ax, ay;
        double x0 = endPt1.getX();
        double y0 = endPt1.getY();

        double x3 = endPt2.getX();
        double y3 = endPt2.getY();

        double x1 = ctrlPt1.getX();
        double y1 = ctrlPt1.getY();

        double x2 = ctrlPt2A.getX();
        double y2 = ctrlPt2A.getY();

        cx = 3 * ( x1 - x0 );
        bx = 3 * ( x2 - x1 ) - cx;
        ax = x3 - x0 - cx - bx;

        cy = 3 * ( y1 - y0 );
        by = 3 * ( y2 - y1 ) - cy;
        ay = y3 - y0 - cy - by;
        double t = 0;
        for( int i = 0; i < numPts; i++ ) {
            t += 1.0 / numPts;
            result[i] = new Point2D.Double();
            result[i].x = ax * t * t * t + bx * t * t + cx * t + x0;
            result[i].y = ay * t * t * t + by * t * t + cy * t + y0;
        }
        return result;
    }
}
