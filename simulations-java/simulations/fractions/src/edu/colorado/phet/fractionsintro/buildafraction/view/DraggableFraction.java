package edu.colorado.phet.fractionsintro.buildafraction.view;

import lombok.Data;

import edu.colorado.phet.fractions.util.immutable.Vector2D;
import edu.colorado.phet.fractionsintro.buildafraction.model.DraggableObject;

/**
 * @author Sam Reid
 */
public @Data class DraggableFraction {
    public final DraggableObject draggableObject;

    public ObjectID getID() { return draggableObject.getId(); }

    public boolean isDragging() { return draggableObject.dragging; }

    public DraggableFraction translate( final Vector2D delta ) { return withDraggableObject( draggableObject.translate( delta ) ); }

    private DraggableFraction withDraggableObject( final DraggableObject draggableObject ) { return new DraggableFraction( draggableObject ); }
}