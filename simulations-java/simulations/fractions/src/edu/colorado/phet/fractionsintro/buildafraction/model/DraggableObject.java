package edu.colorado.phet.fractionsintro.buildafraction.model;

import lombok.Data;

import edu.colorado.phet.fractions.util.immutable.Vector2D;
import edu.colorado.phet.fractionsintro.buildafraction.view.ObjectID;

/**
 * @author Sam Reid
 */
public @Data class DraggableObject {
    public final ObjectID id;
    public final Vector2D position;
    public final boolean dragging;

    public DraggableObject translate( final Vector2D delta ) { return new DraggableObject( id, position.plus( delta ), dragging ); }

    public DraggableObject withDragging( final boolean dragging ) { return this.dragging == dragging ? this : new DraggableObject( id, position, dragging ); }
}