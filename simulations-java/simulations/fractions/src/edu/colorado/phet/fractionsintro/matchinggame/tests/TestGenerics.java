// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.matchinggame.tests;

import fj.F;
import lombok.Data;

import edu.colorado.phet.fractions.util.immutable.Vector2D;

/**
 * @author Sam Reid
 */
public class TestGenerics {

    public static interface Movable {
        public Vector2D getPosition();

        public Movable withPosition( Vector2D v );
    }

    public static @Data class MovableBlock implements Movable {
        public final Vector2D position;

        public MovableBlock withPosition( Vector2D v ) {
            return new MovableBlock( v );
        }
    }

    public static class Motions {
        public static final F<MovableBlock, MovableBlock> move = new F<MovableBlock, MovableBlock>() {
            @Override public MovableBlock f( MovableBlock movableBlock ) {
                return movableBlock.withPosition( movableBlock.position.plus( 1, 0 ) );
            }
        };
        public static final F<Movable, Movable> move2 = new F<Movable, Movable>() {
            @Override public Movable f( Movable movable ) {
                return movable.withPosition( movable.getPosition().plus( 1, 0 ) );
            }
        };
    }

    public static class Move3<T extends Movable> extends F<T, T> {
        @Override public T f( T t ) {
            return (T) t.withPosition( t.getPosition().plus( 1, 0 ) );
        }
    }

    public static void main( String[] args ) {
        MovableBlock m = new MovableBlock( new Vector2D( 0, 0 ) );
        MovableBlock m2 = new Move3<MovableBlock>().f( m );
        System.out.println( "m2 = " + m2 );
    }
}