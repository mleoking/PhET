// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.flow.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;

/**
 * Model for the flux meter which the user can use to measure the flux at different points along the pipe.
 *
 * @author Sam Reid
 */
public class FluxMeter {
    public final Pipe pipe;
    public final Property<Boolean> visible = new Property<Boolean>( false );
    public final Property<Double> x = new Property<Double>( 0.0 );

    public FluxMeter( Pipe pipe ) {
        this.pipe = pipe;
    }

    public ImmutableVector2D getTop() {
        return pipe.getPoint( x.get(), 1 );
    }

    public ImmutableVector2D getBottom() {
        return pipe.getPoint( x.get(), 0 );
    }
}