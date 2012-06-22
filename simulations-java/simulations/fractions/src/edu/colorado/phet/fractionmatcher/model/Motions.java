// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionmatcher.model;

import fj.F;

import edu.colorado.phet.fractions.util.immutable.Vector2D;

/**
 * Motion strategies for moving to various locations (or holding still).
 *
 * @author Sam Reid
 */
public class Motions {
    public static final F<UpdateArgs, MovableFraction> MOVE_TO_LEFT_SCALE = new F<UpdateArgs, MovableFraction>() {
        @Override public MovableFraction f( UpdateArgs a ) {
            return a.fraction.stepTowards( a.state.leftScale.getAttachmentPoint( a.fraction ), a.dt );
        }
    };
    public static final F<UpdateArgs, MovableFraction> MOVE_TO_RIGHT_SCALE = new F<UpdateArgs, MovableFraction>() {
        @Override public MovableFraction f( UpdateArgs a ) {
            return a.fraction.stepTowards( a.state.rightScale.getAttachmentPoint( a.fraction ), a.dt );
        }
    };
    public static final F<UpdateArgs, MovableFraction> WAIT = new F<UpdateArgs, MovableFraction>() {
        @Override public MovableFraction f( UpdateArgs a ) {
            return a.fraction;
        }
    };

    //Combine two motion strategies
    public static F<UpdateArgs, MovableFraction> composite( final F<UpdateArgs, MovableFraction> a, final F<UpdateArgs, MovableFraction> b ) {
        return new F<UpdateArgs, MovableFraction>() {
            @Override public MovableFraction f( UpdateArgs updateArgs ) {
                updateArgs = updateArgs.withFraction( a.f( updateArgs ) );
                updateArgs = updateArgs.withFraction( b.f( updateArgs ) );
                return updateArgs.fraction;
            }
        };
    }

    //Motion that animates scale to the specified scale value
    public static F<UpdateArgs, MovableFraction> scale( final double scale ) {
        return new F<UpdateArgs, MovableFraction>() {
            @Override public MovableFraction f( UpdateArgs a ) {
                return a.fraction.scaleTowards( scale );
            }
        };
    }

    //Motion that animates position to the specified position
    public static F<UpdateArgs, MovableFraction> moveToPosition( final Vector2D position ) {
        return new F<UpdateArgs, MovableFraction>() {
            @Override public MovableFraction f( UpdateArgs a ) {
                return a.fraction.stepTowards( position, a.dt );
            }
        };
    }

    //Motion that animates position to the specified cell's position
    public static F<UpdateArgs, MovableFraction> moveToCell( final Cell cell ) {
        return new F<UpdateArgs, MovableFraction>() {
            @Override public MovableFraction f( UpdateArgs a ) {
                return a.fraction.stepTowards( cell.getPosition(), a.dt );
            }
        };
    }
}