/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.nuclearphysics.model;

import edu.colorado.phet.common.util.SimpleObservable;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.coreadditions.CubicUtil;
import edu.colorado.phet.nuclearphysics.Config;

import java.awt.*;
import java.awt.geom.*;

/**
 * This class represents the energy profile of a particular atom.
 * <p/>
 * Here is my assumed model for the shape of the profile:
 * <ul>
 * <li>The width of the profile is the distance from tail to tail (i.e., twice the
 * distance from the center of the well to one of the tails).
 * <li>The tails flatten out at energy = 0
 * <li>The sides of the well are vertical, and the bottom is horizontal.
 * <li>The shape of the tails is exponential
 * </ul>
 */
public class EnergyProfile extends SimpleObservable implements SimpleObserver {
    private double width;
    private double maxEnergy;
    private double minEnergy;
    private Shape[] shape = new Shape[5];
    private double alphaDecayX;
    private GeneralPath potentilaProfilePath;
    private Shape totalEnergyPath;
    private CubicUtil cubicUtil;
    private Nucleus nucleus;


    public EnergyProfile( Nucleus nucleus ) {
        this.nucleus = nucleus;
        this.width = Config.defaultProfileWidth * 2;
        this.minEnergy = computeMinEnergy( nucleus );
        this.maxEnergy = computeMaxEnergy( nucleus );
        this.generate();
    }

    private double computeMaxEnergy( Nucleus nucleus ) {
        double maxEnergy = minEnergy + 2.5 * nucleus.getNumProtons();
        return maxEnergy;
    }

    private int computeMinEnergy( Nucleus nucleus ) {
        int n = 130;
        int minEnergy = - ( n + ( nucleus.getNumNeutrons() - n ) * 4 );
        return minEnergy;
    }


    public double getWidth() {
        return width;
    }

    public void setWidth( double width ) {
        this.width = width;
        generate();
    }

    public double getMinEnergy() {
        return minEnergy;
    }

    public void setMinEnergy( double minEnergy ) {
        this.minEnergy = minEnergy;
    }

    public double getMaxEnergy() {
        return maxEnergy;
    }

    public void setMaxEnergy( double maxEnergy ) {
        this.maxEnergy = maxEnergy;
    }

    /**
     * Returns the perpendicular distance from a point to the
     * side of the profile. If the point is inside the hill,
     * the value returned is < 0. If the point is outside the
     * hill, the value returned is > 0.
     *
     * @param pt
     */
    public double getDistFromHill( Point2D.Double pt ) {
        double dx = Math.abs( pt.getX() ) - Math.abs( this.getHillX( pt.getY() ) );
        return dx;
    }

    /**
     * Generates the cubic splines that define the energy profile. Also computes
     * the x coordinate of the hill slope that is at the same y coordinat as the
     * bottom of the well.
     */
    private void generate() {

        // Draw the curve going up the left side of the potential profile
        Point2D endPt1 = new Point2D.Double( -getWidth() / 4, 0 );
//        Point2D endPt1 = new Point2D.Double( -getWidth() / 2, 0 );
        Point2D endPt2 = new Point2D.Double( -nucleus.getRadius(), -maxEnergy );
//        Point2D endPt2 = new Point2D.Double( -getWidth() / 20, -maxEnergy );

        Point2D ctrlPt1 = new Point2D.Double();
        ctrlPt1.setLocation( endPt1.getX() + ( ( endPt2.getX() - endPt1.getX() ) * 2 / 8 ),
                             endPt1.getY() );
        Point2D ctrlPt2A = new Point2D.Double();
        ctrlPt2A.setLocation( endPt2.getX(),
                              endPt2.getY() + Math.abs( endPt2.getY() * 7 / 8 ));
        shape[0] = new CubicCurve2D.Double( endPt1.getX(), endPt1.getY(),
                                            ctrlPt1.getX(), ctrlPt1.getY(),
                                            ctrlPt2A.getX(), ctrlPt2A.getY(),
                                            endPt2.getX(), endPt2.getY() );

        // Instantiate a CubicUtil. We'll use it later
        cubicUtil = new CubicUtil( endPt1, ctrlPt1,
                                   endPt2, ctrlPt2A );

        // Draw the curve down into the left side of the potential well
        Point2D endPt3 = new Point2D.Double( endPt2.getX(), -getMinEnergy() );

        shape[1] = new Line2D.Double( endPt2, endPt3 );

        // draw the curve for the right side of the well
        AffineTransform horizontalReflectionTx = AffineTransform.getScaleInstance( -1, 1 );

        Point2D endPt4 = horizontalReflectionTx.transform( endPt3, null );
        Point2D endPt5 = horizontalReflectionTx.transform( endPt2, null );
        Point2D endPt6 = horizontalReflectionTx.transform( endPt1, null );
        Point2D ctrlPt5A = horizontalReflectionTx.transform( ctrlPt2A, null );
        Point2D ctrlPt6 = horizontalReflectionTx.transform( ctrlPt1, null );

        shape[2] = new Line2D.Double( endPt3, endPt4 );
        shape[3] = new Line2D.Double( endPt4, endPt5 );
        shape[4] = new CubicCurve2D.Double( endPt5.getX(), endPt5.getY(),
                                            ctrlPt5A.getX(), ctrlPt5A.getY(),
                                            ctrlPt6.getX(), ctrlPt6.getY(),
                                            endPt6.getX(), endPt6.getY());

        potentilaProfilePath = new GeneralPath();
        potentilaProfilePath.append( shape[0], true );
        potentilaProfilePath.append( shape[1], true );
        potentilaProfilePath.append( shape[2], true );
        potentilaProfilePath.append( shape[3], true );
        potentilaProfilePath.append( shape[4], true );

        // Compute the distance from the profile's center that corresponds to the alpha decay
        // threshold
        // todo: like spot for problem!!!!
        alphaDecayX = getHillX( minEnergy );
        alphaDecayX = -50;
//        alphaDecayX = getHillX( -getWellPotential() );

        // Generate the line for the total energy
        totalEnergyPath = new Line2D.Double( -2000, getTotalEnergy(), 2000, getTotalEnergy() );
//        totalEnergyPath = new Line2D.Double( -Double.MAX_VALUE, getTotalEnergy(), Double.MAX_VALUE, getTotalEnergy() );

        // Tell everyone we've changed
        notifyObservers();
    }

    public GeneralPath getPotentialEnergyPath() {
        return potentilaProfilePath;
    }

    public Shape getTotalEnergyPath() {
        return totalEnergyPath;
    }

    public double getAlphaDecayX() {
        return this.alphaDecayX;
    }

    /**
     * I made up the function. It is constructed to make the level drop when the min energy drops
     * @return
     */
    public double getTotalEnergy() {
//        return 20;
        double energy = minEnergy + 2 * ( nucleus.getNumProtons() - 1 );
        energy = getHillY( alphaDecayX );
//        System.out.println( "energy = " + energy );
        return energy;
    }

    /**
     *
     */
    public Shape[] getShape() {
        return shape;
    }

    /**
     * Gives the x coordinate of the hill-side of the profile that
     * corresponds to a particular y coordinate.
     * <p/>
     * See: http://www.moshplant.com/direct-or/bezier/math.html
     *
     * @param y
     * @return
     */
    private double getHillX( double y ) {
        double[] roots = cubicUtil.getXforY( y );
        double result = Double.NaN;
        for( int i = 0; i < roots.length; i++ ) {
            double root = roots[i];
            if( !Double.isNaN( root ) ) {
                result = root;
            }
        }
        return result;
    }

    /**
     * Gives the y coordinate of the hill-side of the profile that
     * corresponds to a particular x coordinate.
     * <p/>
     * See: http://www.moshplant.com/direct-or/bezier/math.html
     *
     * @param x
     * @return
     */
    public double getHillY( double x ) {
        double[] roots = cubicUtil.getYforX( x );
        double result = Double.NaN;
        for( int i = 0; i < roots.length; i++ ) {
            double root = roots[i];
            if( !Double.isNaN( root ) ) {
                result = root;
            }
        }
        return result;
    }

    /**
     * Shapes the profile based on the makeup of the nucleus
     */
    public void update() {
        this.width = Config.defaultProfileWidth;
        if( minEnergy != computeMinEnergy( nucleus )
            || maxEnergy != computeMaxEnergy( nucleus ) ) {
            this.minEnergy = computeMinEnergy( nucleus );
            this.maxEnergy = computeMaxEnergy( nucleus );
            generate();
        }
    }

    public double getDyDx( double v ) {
        double dyDx = cubicUtil.dyDx( v );
        return Double.isNaN( dyDx ) ? 0 : dyDx;
    }
}
