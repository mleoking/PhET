// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.chemicalreactions.model;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;

import edu.colorado.phet.chemicalreactions.ChemicalReactionsConstants;
import edu.colorado.phet.chemicalreactions.box2d.BodyModel;
import edu.colorado.phet.chemicalreactions.box2d.DebugHandler;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;

import static edu.colorado.phet.chemicalreactions.ChemicalReactionsConstants.ENABLE_BOX2D_DEBUG_DRAW;
import static edu.colorado.phet.chemicalreactions.model.MoleculeShape.*;

public class ChemicalReactionsModel {
    private final World world;

    private final List<BodyModel> bodyModels = new ArrayList<BodyModel>();

    private final DebugHandler debugHandler;

    public final KitCollection kitCollection;

    public ChemicalReactionsModel( IClock clock, final LayoutBounds layoutBounds ) {
        world = new World( new Vec2( 0, -9.8f ), true );

        kitCollection = new KitCollection() {{
            addKit( new Kit( layoutBounds,
                             // reactants
                             new ArrayList<MoleculeBucket>() {{
                                 add( new MoleculeBucket( O2, new Dimension( 600, 200 ) ) );
                                 add( new MoleculeBucket( H2, new Dimension( 600, 200 ) ) );
                             }},

                             // products
                             new ArrayList<MoleculeBucket>() {{
                                 add( new MoleculeBucket( H2O, new Dimension( 600, 200 ) ) );
                             }}
            ) );
            addKit( new Kit( layoutBounds,
                             // reactants
                             new ArrayList<MoleculeBucket>() {{
                                 add( new MoleculeBucket( Cl2, new Dimension( 600, 200 ) ) );
                                 add( new MoleculeBucket( H2, new Dimension( 600, 200 ) ) );
                             }},

                             // products
                             new ArrayList<MoleculeBucket>() {{
                                 add( new MoleculeBucket( HCl, new Dimension( 600, 200 ) ) );
                             }}
            ) );
        }};

        debugHandler = ENABLE_BOX2D_DEBUG_DRAW ? new DebugHandler( world ) : null;

        clock.addClockListener( new ClockAdapter() {
            @Override public void clockTicked( ClockEvent clockEvent ) {
                super.clockTicked( clockEvent );

                world.step( (float) clockEvent.getSimulationTimeChange(), ChemicalReactionsConstants.MODEL_ITERATIONS_PER_FRAME, ChemicalReactionsConstants.MODEL_ITERATIONS_PER_FRAME );

                if ( debugHandler != null ) {
                    debugHandler.step();
                }

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

    public KitCollection getKitCollection() {
        return kitCollection;
    }
}
