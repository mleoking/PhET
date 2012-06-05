package edu.colorado.phet.fractionsintro.buildafraction_functional.model;

import fj.Equal;
import fj.data.Option;

import edu.colorado.phet.common.phetcommon.model.property.ChangeObserver;

/**
 * Provides a listener layer interface for listening to a specific container and whether it enters or leaves the model.
 *
 * @author Sam Reid
 */
public abstract class PieceObserver implements ChangeObserver<BuildAFractionState> {
    public final PieceID id;

    public PieceObserver( final PieceID id ) {this.id = id;}

    public void update( final BuildAFractionState newValue, final BuildAFractionState oldValue ) {
        final Option<Piece> old = oldValue.getPiece( id );
        final Option<Piece> newOne = newValue.getPiece( id );
        boolean equal = Equal.optionEqual( Equal.<Piece>anyEqual() ).eq( newOne, old );

        //after detecting that a change occurred, call the listener method
        if ( !equal ) {
            applyChange( old, newOne );
        }
    }

    //Listener method called after change occurs
    public abstract void applyChange( final Option<Piece> old, final Option<Piece> newOne );
}