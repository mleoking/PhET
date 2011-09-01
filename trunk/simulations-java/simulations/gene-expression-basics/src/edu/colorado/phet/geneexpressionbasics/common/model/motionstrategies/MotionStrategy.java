// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.common.model.motionstrategies;

import java.awt.Shape;
import java.awt.geom.Point2D;

/**
 * Base class for motion strategies that can be used to exhibit different sorts
 * of motion.
 *
 * @author John Blanco
 */
public abstract class MotionStrategy {

    protected MotionBounds motionBounds = new MotionBounds();

    /**
     * Get the next location based on the current motion.  If the application of
     * the current motion causes the current location to go outside of the
     * motion bounds, a "bounce" will occur.
     *
     * @param dt
     * @param currentLocation
     * @return
     */
    public abstract Point2D getNextLocation( double dt, Point2D currentLocation );

    /**
     * Get the next location based on the current motion.  If the application of
     * the current motion causes any part of the provided shape to go outside of
     * the motion bounds, a "bounce" will occur.
     *
     * @param dt
     * @param currentLocation
     * @return
     */
    public abstract Point2D getNextLocation( double dt, Point2D currentLocation, Shape shape );

    public void setMotionBounds( MotionBounds motionBounds ) {
        this.motionBounds = motionBounds;
    }
}
