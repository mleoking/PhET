/**
 * Class: PotentialProfile
 * Package: edu.colorado.phet.nuclearphysics.model
 * Author: Another Guy
 * Date: Feb 26, 2004
 */
package edu.colorado.phet.nuclearphysics.model;

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
    }

    public double getWidth() {
        return width;
    }

    public void setWidth( double width ) {
        this.width = width;
    }

    public double getMaxPotential() {
        return maxPotential;
    }

    public void setMaxPotential( double maxPotential ) {
        this.maxPotential = maxPotential;
    }

    public double getWellPotential() {
        return maxPotential - wellDepth;
    }

    public void setWellPotential( double wellPotential ) {
        this.wellDepth = maxPotential - wellPotential;
    }

    public void setWellDepth( double wellDepth ) {
        this.wellDepth = wellDepth;
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
}
