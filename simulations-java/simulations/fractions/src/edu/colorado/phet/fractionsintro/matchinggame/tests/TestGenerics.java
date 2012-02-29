// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.matchinggame.tests;

import fj.F;
import lombok.Data;

import edu.colorado.phet.fractions.util.immutable.Vector2D;

/**
 * Test for generics issues.
 *
 * @author Sam Reid
 */
public class TestGenerics {

    //Interface we want our library to work with.  Immutable object with a position.
    public static interface Movable {
        public Vector2D getPosition();

        public Movable withPosition( Vector2D v );
    }

    //Client side implementation of Movable
    public static @Data class MovableBlock implements Movable {
        public final Vector2D position;

        public MovableBlock withPosition( Vector2D v ) {
            return new MovableBlock( v );
        }
    }

    //Static methods for moving the block, but doesn't work on the Movable abstraction, just the concrete MovableBlock
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

    //Strategy (as a class) that moves the Movable to the right one step.  How to get rid of the unchecked cast?
    public static class Move3<T extends Movable> extends F<T, T> {
        @Override public T f( T t ) {
            return (T) t.withPosition( t.getPosition().plus( 1, 0 ) );
        }
    }

    //Strategy (as a method) for moving the Movable
    @SuppressWarnings("unchecked") public static <T extends Movable> F<T, T> Move4() {
        return new F<T, T>() {
            @Override public T f( T t ) {
                return (T) t.withPosition( t.getPosition().plus( 1, 0 ) );
            }
        };
    }

    @SuppressWarnings("unchecked") public static <T extends Movable> T Move5( T t ) {
        return (T) t.withPosition( t.getPosition().plus( 1, 0 ) );
    }

    //Strategy (as a field) for moving the Movable
    //TODO: how to do this as a field?
//    public static <T extends Movable> F<? extends Movable, ? extends Movable> Move5 = new F<T, T>() {
//        @Override public T f( T t ) {
//            return (T) t.withPosition( t.getPosition().plus( 1, 0 ) );
//        }
//    };

    public static class Wrapper<T extends Movable> {
        //Type check could still fail at runtime, but only if withPosition() returns a type other than T (even if Movable)
        @SuppressWarnings("unchecked") private F<T, T> Move6 = new F<T, T>() {
            @Override public T f( T a ) {
                return (T) a.withPosition( a.getPosition().plus( 10, 10 ) );
            }
        };
    }

    public static void main( String[] args ) {
        MovableBlock m = new MovableBlock( new Vector2D( 0, 0 ) );
        MovableBlock m2 = (MovableBlock) Move4().f( m );
        MovableBlock m3 = Move5( m );
        final F<MovableBlock, MovableBlock> function = new Wrapper<MovableBlock>().Move6;
        MovableBlock m4 = function.f( m );
        System.out.println( "m2 = " + m2 );//prints m2 = TestGenerics.MovableBlock(position=Vector2D(x=1.0, y=0.0))
    }
}