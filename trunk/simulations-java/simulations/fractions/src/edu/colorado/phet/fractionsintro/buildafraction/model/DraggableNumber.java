package edu.colorado.phet.fractionsintro.buildafraction.model;

import fj.F;
import fj.data.Option;
import lombok.Data;

import edu.colorado.phet.fractions.util.immutable.Vector2D;
import edu.colorado.phet.fractionsintro.common.util.DefaultP2;

/**
 * A number the user can drag and drop into fractions.
 *
 * @author Sam Reid
 */
public @Data class DraggableNumber {
    public final DraggableNumberID id;
    public final DraggableObject draggableObject;
    public final int number;
    public final Option<DefaultP2<FractionID, Boolean>> attachment;//Fraction id and flag for numerator, if attached to fraction.

    public static final F<DraggableNumber, ObjectID> ID = new F<DraggableNumber, ObjectID>() {
        @Override public ObjectID f( final DraggableNumber o ) {
            return o.getID();
        }
    };

    public DraggableNumberID getID() { return id; }

    public DraggableNumber withDragging( final boolean b ) { return withDraggableObject( draggableObject.withDragging( b ) ); }

    private DraggableNumber withDraggableObject( final DraggableObject draggableObject ) { return new DraggableNumber( id, draggableObject, number, attachment ); }

    private DraggableNumber withAttachment( final Option<DefaultP2<FractionID, Boolean>> attachment ) { return new DraggableNumber( id, draggableObject, number, attachment ); }

    public boolean isDragging() { return draggableObject.dragging;}

    public DraggableNumber translate( final Vector2D delta ) { return withDraggableObject( draggableObject.translate( delta ) ); }

    public DraggableNumber attachToFraction( final FractionID fraction, final boolean numerator ) {
        return withAttachment( Option.some( new DefaultP2<FractionID, Boolean>( fraction, numerator ) ) );
    }

    public boolean isAttachedTo( final FractionID fractionID ) { return attachment.isSome() && attachment.some()._1().equals( fractionID ); }

    public DraggableNumber withPosition( final Vector2D position ) { return withDraggableObject( draggableObject.withPosition( position ) ); }
}