// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fluidpressureandflow.common.model;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * Context for observing the velocity at a certain point.
 *
 * @author Sam Reid
 */
public interface VelocitySensorContext {

    //Get the velocity at the specified point
    public Option<Vector2D> getVelocity( double x, double y );

    //Add a listener for when the anything in the environment changes that could change the velocity (such as the shape of a pipe)
    public void addVelocityUpdateListener( SimpleObserver observer );
}
