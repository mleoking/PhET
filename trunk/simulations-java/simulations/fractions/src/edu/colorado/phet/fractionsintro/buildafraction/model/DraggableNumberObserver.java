package edu.colorado.phet.fractionsintro.buildafraction.model;

import fj.Equal;
import fj.data.Option;

import edu.colorado.phet.common.phetcommon.model.property.ChangeObserver;

/**
 * Provides a listener layer interface for listening to a specific container and whether it enters or leaves the model.
 *
 * @author Sam Reid
 */
public abstract class DraggableNumberObserver implements ChangeObserver<BuildAFractionState> {
    public final DraggableNumberID id;

    public DraggableNumberObserver( final DraggableNumberID id ) {this.id = id;}

    public void update( final BuildAFractionState newValue, final BuildAFractionState oldValue ) {
        final Option<DraggableNumber> old = oldValue.getDraggableNumber( id );
        final Option<DraggableNumber> newOne = newValue.getDraggableNumber( id );
        boolean equal = Equal.optionEqual( Equal.<DraggableNumber>anyEqual() ).eq( newOne, old );

        //after detecting that a change occurred, call the listener method
        if ( !equal ) {
            applyChange( old, newOne );
        }
    }

    //Listener method called after change occurs
    public abstract void applyChange( final Option<DraggableNumber> old, final Option<DraggableNumber> newOne );
}