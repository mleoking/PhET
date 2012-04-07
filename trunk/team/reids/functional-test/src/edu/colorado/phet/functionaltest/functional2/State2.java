// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.functionaltest.functional2;

import fj.F;
import fj.data.List;
import lombok.Data;

/**
 * @author Sam Reid
 */
public @Data class State2 {
    public final List<Circle2> circles;

    public State2 stepInTime( final double simulationTimeChange ) {
        return new State2( circles.map( new F<Circle2, Circle2>() {
            @Override public Circle2 f( final Circle2 circle2 ) {
                return circle2.withPosition( circle2.getPosition().plus( 1, 1 ) );
            }
        } ) );
    }
}