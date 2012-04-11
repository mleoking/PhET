// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.functionaltest.functional.model;

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
public class FunctionalModel {
    public final Property<FunctionalState> model = new Property<FunctionalState>( new FunctionalState( List.iterableList( new ArrayList<Circle>() {{
        for ( int i = 0; i < 10; i++ ) {
            add( new Circle( new Vector2D( i * 60, 100 ), 50, false ) );
        }
    }} ) ) );

    public FunctionalModel( final IClock clock ) {
        clock.addClockListener( new ClockAdapter() {
            @Override public void simulationTimeChanged( final ClockEvent clockEvent ) {
                model.set( model.get().stepInTime( clockEvent.getSimulationTimeChange() ) );
            }
        } );
    }
}