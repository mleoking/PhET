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
 * An Electromagnet to which an arbitrary gradient can be applied.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class GradientElectromagnet extends Electromagnet {
    private Gradient gradient;
    private double width;

    /**
     * Constructor
     *
     * @param position
     * @param width
     * @param height
     * @param clock
     * @param gradient
     */
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

        /**
         * Returns a factor that is to be multiplied against the base field of the magnet to
         * achieve the desired gradient.
         *
         * @param x     The place along the magnet's length for which the multiplier is to be determined
         * @param width
         * @return the factor to be multiplied against the base field magnitude
         * of the magnet
         */
        double getValueAt( double x, double width );
    }

    /**
     * Essentially a no-op. The gradient is 0 across the length of the magnet
     */
    public static class Constant implements Gradient {
        public double getValueAt( double x, double width ) {
            return 1;
        }
    }

    /**
     * Applies a linear gradient to the field
     */
    public static class LinearGradient implements Gradient {
        private double m,b;

        public LinearGradient( double m, double b ) {
            this.m = m;
            this.b = b;
        }

        public double getValueAt( double x, double width ) {
            return m * ( (x - width / 2)  / width ) + b;
        }
    }
}
