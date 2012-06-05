package edu.colorado.phet.fractionsintro.buildafraction.model;

import fj.Equal;
import fj.data.Option;

import edu.colorado.phet.common.phetcommon.model.property.ChangeObserver;

/**
 * Provides a listener layer interface for listening to a specific container and whether it enters or leaves the model.
 *
 * @author Sam Reid
 */
public abstract class DraggableFractionObserver implements ChangeObserver<BuildAFractionState> {
    public final FractionID id;

    public DraggableFractionObserver( final FractionID id ) {this.id = id;}

    public void update( final BuildAFractionState newValue, final BuildAFractionState oldValue ) {
        final Option<DraggableFraction> old = oldValue.getDraggableFraction( id );
        final Option<DraggableFraction> newOne = newValue.getDraggableFraction( id );
        boolean equal = Equal.optionEqual( Equal.<DraggableFraction>anyEqual() ).eq( newOne, old );

        //after detecting that a change occurred, call the listener method
        if ( !equal ) {
            applyChange( old, newOne );
        }
    }

    //Listener method called after change occurs
    public abstract void applyChange( final Option<DraggableFraction> old, final Option<DraggableFraction> newOne );
}