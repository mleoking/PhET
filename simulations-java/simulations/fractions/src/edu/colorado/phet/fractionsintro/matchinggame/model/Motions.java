// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.matchinggame.model;

import fj.F;

import edu.colorado.phet.fractions.util.immutable.Vector2D;

/**
 * Motion strategies for moving to various locations (or holding still).
 *
 * @author Sam Reid
 */
public class Motions {
    public static final F<UpdateArgs, MovableFraction> MoveToLeftScale = new F<UpdateArgs, MovableFraction>() {
        @Override public MovableFraction f( UpdateArgs a ) {
            return a.fraction.stepTowards( a.state.leftScale.getAttachmentPoint( a.fraction ), a.dt );
        }
    };
    public static final F<UpdateArgs, MovableFraction> MoveToRightScale = new F<UpdateArgs, MovableFraction>() {
        @Override public MovableFraction f( UpdateArgs a ) {
            return a.fraction.stepTowards( a.state.rightScale.getAttachmentPoint( a.fraction ), a.dt );
        }
    };
    public static final F<UpdateArgs, MovableFraction> Stillness = new F<UpdateArgs, MovableFraction>() {
        @Override public MovableFraction f( UpdateArgs a ) {
            return a.fraction;
        }
    };

    public static F<UpdateArgs, MovableFraction> composite( final F<UpdateArgs, MovableFraction> a, final F<UpdateArgs, MovableFraction> b ) {
        return new F<UpdateArgs, MovableFraction>() {
            @Override public MovableFraction f( UpdateArgs updateArgs ) {
                updateArgs = updateArgs.withFraction( a.f( updateArgs ) );
                updateArgs = updateArgs.withFraction( b.f( updateArgs ) );
                return updateArgs.fraction;
            }
        };
    }

    public static F<UpdateArgs, MovableFraction> Scale( final double scale ) {
        return new F<UpdateArgs, MovableFraction>() {
            @Override public MovableFraction f( UpdateArgs a ) {
                return a.fraction.scaleTowards( scale );
            }
        };
    }

    public static F<UpdateArgs, MovableFraction> MoveToPosition( final Vector2D position ) {
        return new F<UpdateArgs, MovableFraction>() {
            @Override public MovableFraction f( UpdateArgs a ) {
                return a.fraction.stepTowards( position, a.dt );
            }
        };
    }

    public static F<UpdateArgs, MovableFraction> MoveToCell( final Cell cell ) {
        return new F<UpdateArgs, MovableFraction>() {
            @Override public MovableFraction f( UpdateArgs a ) {
                return a.fraction.stepTowards( cell.getPosition(), a.dt );
            }
        };
    }
}