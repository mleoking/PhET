// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.chemicalreactions.model;

import java.util.ArrayList;
import java.util.List;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;

import edu.colorado.phet.chemicalreactions.ChemicalReactionsConstants;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;

public class ChemicalReactionsModel {
    private final World world;

    private final List<BodyModel> bodyModels = new ArrayList<BodyModel>();

    public ChemicalReactionsModel( IClock clock ) {
        world = new World( new Vec2( 0, 0 ), true );

        clock.addClockListener( new ClockAdapter() {
            @Override public void clockTicked( ClockEvent clockEvent ) {
                super.clockTicked( clockEvent );

                world.step( (float) clockEvent.getSimulationTimeChange(), ChemicalReactionsConstants.MODEL_ITERATIONS_PER_FRAME, ChemicalReactionsConstants.MODEL_ITERATIONS_PER_FRAME );

                for ( BodyModel bodyWrapper : bodyModels ) {
                    bodyWrapper.postStep();
                }
            }
        } );
    }

    public void addBody( BodyModel bodyWrapper ) {
        Body body = world.createBody( bodyWrapper.getBodyDef() );
        bodyModels.add( bodyWrapper );
        bodyWrapper.setBody( body );
    }

    public void removeBody( BodyModel bodyWrapper ) {
        bodyModels.remove( bodyWrapper );
        world.destroyBody( bodyWrapper.getBody() );
        bodyWrapper.setBody( null );
    }

}
