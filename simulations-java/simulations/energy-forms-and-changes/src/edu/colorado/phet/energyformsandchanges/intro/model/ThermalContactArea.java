// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.model;

import java.awt.geom.Rectangle2D;

/**
 * Class that represents a 2D space that can come into contact with other
 * thermal areas.  This is basically just a shape and a flag that indicates
 * whether immersion can occur (such as when
 *
 * @author John Blanco
 */
public class ThermalContactArea {

    // Threshold of distance for determining whether two areas are in contact.
    private static double TOUCH_DISTANCE_THRESHOLD = 0.001; // In meters.

    private final Rectangle2D bounds = new Rectangle2D.Double();
    private final boolean supportsImmersion;

    public ThermalContactArea( Rectangle2D bounds, boolean supportsImmersion ) {
        this.bounds.setFrame( bounds );
        this.supportsImmersion = supportsImmersion;
    }

    /**
     * Get the amount of thermal contact that exists between this and another
     * thermal area.  Since thermal contact areas are 2D, the amount of
     * contact is a 1D quantity.  For example, when a rectangle is sitting
     * on top of another that is the same width, the contact length is the
     * width of the shared edge.
     *
     * @param that
     * @return
     */
    public double getThermalContactLength( ThermalContactArea that ) {

        double xOverlap = getHorizontalOverlap( this.bounds, that.bounds );
        double yOverlap = getVerticalOverlap( this.bounds, that.bounds );

        double contactLength = 0;
        if ( xOverlap > 0 && yOverlap > 0 ) {
            // One of the areas is overlapping another.  This should be an
            // 'immersion' situation, i.e. one is all or partially immersed in
            // the other.
            if ( this.supportsImmersion || that.supportsImmersion ) {
                Rectangle2D immersionRect = this.bounds.createIntersection( that.bounds );
                contactLength = immersionRect.getWidth() * 2 + immersionRect.getHeight() * 2;
                if ( immersionRect.getWidth() != this.bounds.getWidth() && immersionRect.getWidth() != that.bounds.getWidth() ) {
                    // Not fully overlapping in X direction, so adjust contact length accordingly.
                    contactLength -= immersionRect.getHeight();
                }
                if ( immersionRect.getHeight() != this.bounds.getHeight() && immersionRect.getHeight() != that.bounds.getHeight() ) {
                    // Not fully overlapping in Y direction, so adjust contact length accordingly.
                    contactLength -= immersionRect.getWidth();
                }
            }
            else {
                // TODO: Not sure if this will happen, so have a printout in
                // place to see how often it does.  Remove if noisy.
                System.out.println( getClass().getName() + "Double overlap detected in case where neither energy container supports immersion.  Ignoring." );
            }
        }
        else {
            // There is overlap in one dimension but not the other, so test to
            // see if the two containers are touching.
            if ( xOverlap > 0 &&
                 Math.abs( this.bounds.getMaxY() - that.bounds.getMinY() ) < TOUCH_DISTANCE_THRESHOLD ||
                 Math.abs( this.bounds.getMinY() - that.bounds.getMaxY() ) < TOUCH_DISTANCE_THRESHOLD ) {
                contactLength = xOverlap;
            }
            else if ( yOverlap > 0 &&
                      Math.abs( this.bounds.getMaxX() - that.bounds.getMinX() ) < TOUCH_DISTANCE_THRESHOLD ||
                      Math.abs( this.bounds.getMinX() - that.bounds.getMaxX() ) < TOUCH_DISTANCE_THRESHOLD ) {
                contactLength = xOverlap;
            }
        }

        return contactLength;
    }

    /**
     * Convenience function for getting outer perimeter of the bounds.
     *
     * @return
     */
    public double getPerimeter() {
        return bounds.getWidth() * 2 + bounds.getHeight() * 2;
    }

    // Convenience method for determining overlap of rectangles in X dimension.
    private double getHorizontalOverlap( Rectangle2D r1, Rectangle2D r2 ) {
        double lowestMax = Math.min( r1.getMaxX(), r2.getMaxX() );
        double highestMin = Math.max( r1.getMinX(), r2.getMinX() );
        return Math.max( lowestMax - highestMin, 0 );
    }

    // Convenience method for determining overlap of rectangles in X dimension.
    private double getVerticalOverlap( Rectangle2D r1, Rectangle2D r2 ) {
        double lowestMax = Math.min( r1.getMaxY(), r2.getMaxY() );
        double highestMin = Math.max( r1.getMinY(), r2.getMinY() );
        return Math.max( lowestMax - highestMin, 0 );
    }
}
