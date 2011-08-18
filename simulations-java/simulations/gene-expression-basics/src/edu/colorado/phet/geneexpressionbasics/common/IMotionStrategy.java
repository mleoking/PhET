// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.common;

import java.awt.geom.Point2D;

/**
 * @author John Blanco
 */
public interface IMotionStrategy {
    Point2D getNextLocation( double dt, Point2D currentLocation );
}
