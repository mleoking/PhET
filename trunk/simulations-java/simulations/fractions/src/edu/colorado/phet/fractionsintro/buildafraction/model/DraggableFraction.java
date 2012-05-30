package edu.colorado.phet.fractionsintro.buildafraction.model;

import fj.F;
import fj.data.Option;
import lombok.Data;

import edu.colorado.phet.fractions.util.immutable.Vector2D;

/**
 * @author Sam Reid
 */
public @Data class DraggableFraction {
    public final FractionID id;
    public final DraggableObject draggableObject;
    public final Option<DraggableNumberID> numerator;
    public final Option<DraggableNumberID> denominator;
    public static final F<DraggableFraction, ObjectID> ID = new F<DraggableFraction, ObjectID>() {
        @Override public ObjectID f( final DraggableFraction draggableFraction ) {
            return draggableFraction.getID();
        }
    };

    public FractionID getID() { return id; }

    public DraggableFraction translate( final Vector2D delta ) { return withDraggableObject( draggableObject.translate( delta ) ); }

    public DraggableFraction withDraggableObject( final DraggableObject draggableObject ) { return new DraggableFraction( id, draggableObject, numerator, denominator ); }

    public DraggableFraction withNumerator( final Option<DraggableNumberID> numerator ) { return new DraggableFraction( id, draggableObject, numerator, denominator ); }

    public DraggableFraction withDenominator( final Option<DraggableNumberID> denominator ) { return new DraggableFraction( id, draggableObject, numerator, denominator ); }

    public DraggableFraction withDragging( final boolean b ) { return withDraggableObject( draggableObject.withDragging( b ) ); }

    //Determine whether the draggable fraction has the same ID as the one specified (i.e. that they are referring to the same model instance).
    public boolean equalsID( final FractionID id ) { return this.id.equals( id ); }

    public Vector2D getPosition() { return draggableObject.position; }
}