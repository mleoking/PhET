package edu.colorado.phet.fractionsintro.buildafraction.model;

import fj.Equal;
import fj.data.Option;

import edu.colorado.phet.common.phetcommon.model.property.ChangeObserver;
import edu.colorado.phet.fractionsintro.buildafraction.view.ObjectID;

/**
 * Provides a listener layer interface for listening to a specific container and whether it enters or leaves the model.
 *
 * @author Sam Reid
 */
public abstract class ContainerObserver implements ChangeObserver<BuildAFractionState> {
    public final ObjectID id;

    public ContainerObserver( final ObjectID id ) {this.id = id;}

    @Override public void update( final BuildAFractionState newValue, final BuildAFractionState oldValue ) {
        final Option<Container> old = oldValue.getContainer( id );
        final Option<Container> newOne = newValue.getContainer( id );
        boolean equal = Equal.optionEqual( Equal.<Container>anyEqual() ).eq( newOne, old );

        //after detecting that a change occurred, call the listener method
        if ( !equal ) {
            applyChange( old, newOne );
        }
    }

    //Listener method called after change occurs
    public abstract void applyChange( final Option<Container> old, final Option<Container> newOne );
}