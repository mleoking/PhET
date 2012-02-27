// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.matchinggame.model;

import fj.F;

/**
 * Motion strategies for moving to various locations (or holding still).
 *
 * @author Sam Reid
 */
public class Motions {
    public static final F<UpdateArgs, MovableFraction> MoveToLeftScale = new F<UpdateArgs, MovableFraction>() {
        @Override public MovableFraction f( UpdateArgs a ) {
            return a.fraction.stepTowards( a.state.leftScale.center(), a.dt );
        }
    };
    public static final F<UpdateArgs, MovableFraction> MoveToRightScale = new F<UpdateArgs, MovableFraction>() {
        @Override public MovableFraction f( UpdateArgs a ) {
            return a.fraction.stepTowards( a.state.rightScale.center(), a.dt );
        }
    };
    public static final F<UpdateArgs, MovableFraction> Stillness = new F<UpdateArgs, MovableFraction>() {
        @Override public MovableFraction f( UpdateArgs a ) {
            return a.fraction;
        }
    };

    public static F<UpdateArgs, MovableFraction> MoveToCell( final Cell cell ) {
        return new F<UpdateArgs, MovableFraction>() {
            @Override public MovableFraction f( UpdateArgs a ) {
                return a.fraction.stepTowards( cell.position(), a.dt );
            }
        };
    }
}