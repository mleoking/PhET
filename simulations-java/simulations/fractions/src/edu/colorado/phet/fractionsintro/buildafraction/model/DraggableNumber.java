package edu.colorado.phet.fractionsintro.buildafraction.model;

import fj.F;
import lombok.Data;

import edu.colorado.phet.fractions.util.immutable.Vector2D;
import edu.colorado.phet.fractionsintro.buildafraction.view.ObjectID;

/**
 * @author Sam Reid
 */
public @Data class DraggableNumber {
    final DraggableObject draggableObject;
    final int number;
    public static final F<DraggableNumber, ObjectID> ID = new F<DraggableNumber, ObjectID>() {
        @Override public ObjectID f( final DraggableNumber o ) {
            return o.getID();
        }
    };

    public ObjectID getID() { return draggableObject.id; }

    public DraggableNumber withDragging( final boolean b ) { return withDraggableObject( draggableObject.withDragging( b ) ); }

    private DraggableNumber withDraggableObject( final DraggableObject draggableObject ) { return new DraggableNumber( draggableObject, number ); }

    public boolean isDragging() { return draggableObject.dragging;}

    public DraggableNumber translate( final Vector2D delta ) { return withDraggableObject( draggableObject.translate( delta ) ); }
}