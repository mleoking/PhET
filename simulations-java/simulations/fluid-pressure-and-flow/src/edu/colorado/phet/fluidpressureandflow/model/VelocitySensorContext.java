// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * @author Sam Reid
 */
public interface VelocitySensorContext {
    public ImmutableVector2D getVelocity( double x, double y );

    public void addVelocityUpdateListener( SimpleObserver observer );
}
