package edu.colorado.phet.fractionsintro.buildafraction.model;

import fj.F;
import lombok.Data;

import edu.colorado.phet.fractions.util.immutable.Vector2D;
import edu.colorado.phet.fractionsintro.buildafraction.view.ObjectID;

/**
 * @author Sam Reid
 */
public @Data class DraggableFraction {
    public final DraggableObject draggableObject;
    public static final F<DraggableFraction, ObjectID> ID = new F<DraggableFraction, ObjectID>() {
        @Override public ObjectID f( final DraggableFraction draggableFraction ) {
            return draggableFraction.getID();
        }
    };

    public ObjectID getID() { return draggableObject.getId(); }

    public boolean isDragging() { return draggableObject.dragging; }

    public DraggableFraction translate( final Vector2D delta ) { return withDraggableObject( draggableObject.translate( delta ) ); }

    private DraggableFraction withDraggableObject( final DraggableObject draggableObject ) { return new DraggableFraction( draggableObject ); }

    public DraggableFraction withDragging( final boolean b ) { return withDraggableObject( draggableObject.withDragging( b ) ); }
}