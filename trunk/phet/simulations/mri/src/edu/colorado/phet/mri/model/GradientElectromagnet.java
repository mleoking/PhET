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
import java.util.HashSet;
import java.util.Set;

/**
 * GradientElectromagnet
 * <p/>
 * An Electromagnet to which an arbitrary gradient can be applied. The length of the magnet is its extent
 * perpendicular to the field. (This is sloppy nomenclature, I know.)
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class GradientElectromagnet extends Electromagnet {

    //--------------------------------------------------------------------------------------------------
    // Class fields and methods
    //--------------------------------------------------------------------------------------------------

    public static class Orientation {
    }

    public static final Orientation HORIZONTAL = new Orientation();
    public static final Orientation VERTICAL = new Orientation();
    private static Set ORIENTATIONS = new HashSet();

    static {
        ORIENTATIONS.add( HORIZONTAL );
        ORIENTATIONS.add( VERTICAL );
    }

    //--------------------------------------------------------------------------------------------------
    // Instance fields and methods
    //--------------------------------------------------------------------------------------------------

    private Orientation orientation;
    private Gradient gradient;
    private double length;

    /**
     * Constructor
     *
     * @param position
     * @param width    width of the magnet
     * @param height   thickness of the magnet
     * @param clock
     * @param gradient
     */
    public GradientElectromagnet( Point2D position,
                                  double width,
                                  double height,
                                  IClock clock,
                                  Gradient gradient,
                                  Orientation orientation ) {
        super( position, width, height, clock );

        if( !ORIENTATIONS.contains( orientation ) ) {
            throw new IllegalArgumentException( "invalid orientation" );
        }

        this.orientation = orientation;
        this.length = orientation == HORIZONTAL ? width : height;
        this.gradient = gradient;
    }

    /**
     * Gets the strength of the field from this magnet at an absolute point in space
     *
     * @param p
     * @return the magnitude of the field
     */
    public double getFieldStrengthAtAbsolute( Point2D p ) {
        double loc = orientation == HORIZONTAL
                     ? p.getX() - ( getPosition().getX() - length / 2 )
                     : p.getY() - ( getPosition().getY() - length / 2 );
        if( orientation == VERTICAL ) {
        }
        return getFieldStrength() * gradient.getValueAt( loc, length );
    }

    /**
     * Gets the strength of the field from the magnet at a point in space relative to
     * the center of the magnet
     *
     * @param p
     * @return the magnitude of the field
     */
    public double getFieldStrengthAtRelative( Point2D p ) {
        double loc = orientation == HORIZONTAL
                     ? p.getX()
                     : p.getY();
        return getFieldStrength() * gradient.getValueAt( loc, length );
    }

    /**
     * Sets the gradient function that the magnet will use to compute the field along its length
     *
     * @param gradient
     */
    public void setGradient( Gradient gradient ) {
        this.gradient = gradient;
    }

    /**
     * @return the orientation of the magnet
     */
    public Orientation getOrientation() {
        return orientation;
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
         *         of the magnet
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
     * Applies a linear gradient to the field. The gradient is 0 at the center of the magnet. Field goes
     * negative for x < 0, and positive for x > 0.
     */
    public static class LinearGradient implements Gradient {
        private double m, b;

        public LinearGradient( double m, double b ) {
            this.m = m;
            this.b = b;
        }

        public double getValueAt( double x, double width ) {
            return m * ( ( x - width / 2 ) / width ) + b;
        }
    }
}
