package edu.colorado.phet.fractionsintro.buildafraction.model;

import lombok.Data;

import edu.colorado.phet.fractions.util.immutable.Vector2D;
import edu.colorado.phet.fractionsintro.buildafraction.view.ObjectID;

/**
 * @author Sam Reid
 */
public @Data class Container {
    public final ObjectID id;
    public final int numSegments;
    public final Vector2D position;
    public final boolean dragging;

    public Container translate( final Vector2D delta ) { return new Container( id, numSegments, position.plus( delta ), dragging ); }

    public Container withDragging( final boolean dragging ) { return this.dragging == dragging ? this : new Container( id, numSegments, position, dragging ); }
}