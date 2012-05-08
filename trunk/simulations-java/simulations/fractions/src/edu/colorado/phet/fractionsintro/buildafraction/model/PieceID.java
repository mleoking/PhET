package edu.colorado.phet.fractionsintro.buildafraction.model;

/**
 * @author Sam Reid
 */
public class PieceID extends ObjectID {
    public PieceID( final int id ) { super( id ); }

    public static int count = 0;

    public static PieceID nextID() { return new PieceID( count++ ); }
}