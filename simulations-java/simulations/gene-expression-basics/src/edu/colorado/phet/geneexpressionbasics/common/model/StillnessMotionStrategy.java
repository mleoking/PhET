// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.common.model;

import java.awt.geom.Point2D;

/**
 * Motion strategy that has no motion, i.e. causes the user to be still.
 *
 * @author John Blanco
 */
public class StillnessMotionStrategy implements IMotionStrategy {

    public Point2D getNextLocation( double dt, Point2D currentLocation ) {
        return new Point2D.Double( currentLocation.getX(), currentLocation.getY() );
    }
}
