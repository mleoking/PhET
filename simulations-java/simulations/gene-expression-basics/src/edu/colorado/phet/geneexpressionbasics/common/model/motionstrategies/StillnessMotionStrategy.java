// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.common.model.motionstrategies;

import java.awt.Shape;
import java.awt.geom.Point2D;

/**
 * Motion strategy that has no motion, i.e. causes the user to be still.
 *
 * @author John Blanco
 */
public class StillnessMotionStrategy extends MotionStrategy {

    @Override public Point2D getNextLocation( Point2D currentLocation, Shape shape, double dt ) {
        return new Point2D.Double( currentLocation.getX(), currentLocation.getY() );
    }
}
