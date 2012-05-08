package edu.colorado.phet.fractionsintro.buildafraction.model;

import fj.F;
import lombok.Data;

import edu.colorado.phet.fractions.util.immutable.Vector2D;

/**
 * Pieces that go in containers.
 *
 * @author Sam Reid
 */
public @Data class Piece {
    public final PieceID id;
    public final DraggableObject draggableObject;
    public final int numSegments;

    //Getter for the ID
    public static final F<Piece, ObjectID> ID = new F<Piece, ObjectID>() {
        @Override public ObjectID f( final Piece piece ) {
            return piece.getID();
        }
    };

    public Piece translate( final Vector2D delta ) { return new Piece( id, draggableObject.translate( delta ), numSegments ); }

    public Piece withDragging( final boolean dragging ) { return new Piece( id, draggableObject.withDragging( dragging ), numSegments ); }

    public boolean isDragging() { return draggableObject.dragging; }

    public PieceID getID() { return id; }

    public Vector2D getPosition() { return draggableObject.position; }
}