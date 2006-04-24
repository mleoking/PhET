/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.mri.model;

import edu.colorado.phet.common.model.clock.IClock;

import java.awt.geom.Point2D;

/**
 * GradientElectromagnet
 * <p>
 * Note that the location or the magnet is its midpoint
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class GradientElectromagnet extends Electromagnet {
    private Gradient gradient;
//    private Point2D position;
    private double width;
//    private double fieldOffset;


    public GradientElectromagnet( Point2D position, double width, double height, IClock clock, Gradient gradient ) {
        super( position, width, height, clock );
        this.width = width;
        this.gradient = gradient;
    }

    public double getFieldStrengthAt( double x ){
        return getFieldStrength() * gradient.getValueAt( x, width );
    }

    public void setGradient( Gradient gradient ) {
        this.gradient = gradient;
    }

    //--------------------------------------------------------------------------------------------------
    // Gradient specifications
    //--------------------------------------------------------------------------------------------------
    public static interface Gradient {
        double getValueAt( double x, double width );
    }

    public static class LinearGradient implements Gradient {
        private double m,b;

        public LinearGradient( double m, double b ) {
            this.m = m;
            this.b = b;
        }

        public double getValueAt( double x, double width ) {
            // This looks funny because (0,0) for the magnet is at its center,
            // and b is specified for the left end
            return m * (x / width ) + b;//  + ( m * width / 2 );
        }
    }

    //--------------------------------------------------------------------------------------------------
    // Implementation of ModelElement
    //--------------------------------------------------------------------------------------------------

    public void stepInTime( double dt ) {

    }
}
