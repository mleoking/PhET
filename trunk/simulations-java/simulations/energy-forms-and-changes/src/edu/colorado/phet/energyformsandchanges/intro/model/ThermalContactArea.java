// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.model;

import java.awt.geom.Area;
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

        double contactLength = 0;
        if ( this.supportsImmersion || that.supportsImmersion && ( this.bounds.intersects( that.bounds ) ) ) {
            // One of the thermal contact areas is immersed within the other.
            if ( this.bounds.contains( that.bounds ) ) {
                contactLength = that.getPerimeter();
            }
            else if ( that.bounds.contains( this.bounds ) ) {
                contactLength = this.getPerimeter();
            }
            else {
                // Partial immersion.  Calculate contact length of immersed region.
                Area immersionArea = new Area( this.bounds );
                immersionArea.intersect( new Area( that.bounds ) );
                Rectangle2D immersionRect = immersionArea.getBounds2D();
                if ( immersionRect.getWidth() == this.bounds.getWidth() || immersionRect.getWidth() == that.bounds.getWidth() ) {
                    contactLength = immersionRect.getWidth() + immersionRect.getHeight() * 2;
                }
                else if ( immersionRect.getHeight() == this.bounds.getHeight() || immersionRect.getHeight() == that.bounds.getHeight() ) {
                    contactLength = immersionRect.getWidth() * 2 + immersionRect.getHeight();
                }
                else {
                    contactLength = immersionRect.getWidth() + immersionRect.getHeight();
                }
            }
        }
        else {
            // Test for whether any surfaces are touching.
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
}
