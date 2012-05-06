package edu.colorado.phet.fractionsintro.buildafraction.model;

import fj.data.List;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.fractions.util.immutable.Vector2D;
import edu.colorado.phet.fractionsintro.buildafraction.controller.ModelUpdate;
import edu.colorado.phet.fractionsintro.buildafraction.view.ObjectID;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Model for the "Build a Fraction" tab which contains a switchable immutable state.
 *
 * @author Sam Reid
 */
public class BuildAFractionModel {
    public final Property<BuildAFractionState> state = new Property<BuildAFractionState>( new BuildAFractionState( List.<Container>nil() ) );
    public final ConstantDtClock clock = new ConstantDtClock();

    public BuildAFractionModel() {
    }

    public void update( ModelUpdate update ) { state.set( update.update( state.get() ) ); }

    public void drag( final PDimension delta ) {
        state.set( state.get().drag( new Vector2D( delta ) ) );
    }

    public void addContainerListener( ContainerObserver observer ) { state.addObserver( observer ); }

    public void removeContainerListener( ContainerObserver observer ) { state.removeObserver( observer ); }

    public void startDragging( final ObjectID container ) {
        update( new ModelUpdate() {
            @Override public BuildAFractionState update( final BuildAFractionState state ) {
                return state.startDragging( container );
            }
        } );
    }

}