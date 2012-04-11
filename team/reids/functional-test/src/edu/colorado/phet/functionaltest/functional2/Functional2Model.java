// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.functionaltest.functional2;

import fj.data.List;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.fractions.util.immutable.Vector2D;

/**
 * @author Sam Reid
 */
public class Functional2Model {
    public final Property<State2> state = new Property<State2>( new State2( List.iterableList( new ArrayList<Circle2>(
    ) {{
        for ( int i = 0; i < 10; i++ ) {
            add( new Circle2( new Vector2D( i * 60, 100 ), 50, false ) );
        }
    }} ) ) );

    public Functional2Model( final IClock clock ) {
        clock.addClockListener( new ClockAdapter() {
            @Override public void simulationTimeChanged( final ClockEvent clockEvent ) {
                state.set( state.get().stepInTime( clockEvent.getSimulationTimeChange() ) );
            }
        } );
    }
}