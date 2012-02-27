// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.matchinggame.model;

import fj.F;
import lombok.Data;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.fractionsintro.intro.model.Fraction;
import edu.umd.cs.piccolo.PNode;

/**
 * Immutable class representing a movable fraction in the matching game
 *
 * @author Sam Reid
 */
@Data public class MovableFraction {

    //The location of the fraction, I haven't decided if this is center or top left
    public final ImmutableVector2D position;

    //Numerator and denominator of the unreduced fraction
    public final int numerator;
    public final int denominator;

    //Flag to indicate whether the user is dragging the fraction
    public final boolean dragging;

    //Way of creating nodes for rendering and doing bounds/layouts.  This is in the model because object locations, bounds, animation are also in the model.
    //It is a function that creates nodes instead of a single PNode because I am not sure if the same PNode can be used safely in multiple places in a piccolo scene graph
    public transient final F<Fraction, PNode> node;

    //Strategy for moving the fraction over time (e.g. toward the scale or back to its original cell)
    public final F<UpdateArgs, MovableFraction> motion;

    public MovableFraction dragging( boolean dragging ) { return new MovableFraction( position, numerator, denominator, dragging, node, motion );}

    public MovableFraction translate( double dx, double dy ) { return position( position.plus( dx, dy ) ); }

    public MovableFraction translate( ImmutableVector2D v ) { return translate( v.getX(), v.getY() ); }

    public MovableFraction position( ImmutableVector2D position ) { return new MovableFraction( position, numerator, denominator, dragging, node, motion );}

    public MovableFraction motion( F<UpdateArgs, MovableFraction> motion ) { return new MovableFraction( position, numerator, denominator, dragging, node, motion );}

    public Fraction fraction() { return new Fraction( numerator, denominator );}

    public MovableFraction stepInTime( UpdateArgs args ) { return motion.f( args ); }

    public MovableFraction stepTowards( ImmutableVector2D position, double dt ) {
        double velocity = 600 * dt;
        final MovableFraction result = translate( position.minus( this.position ).getInstanceOfMagnitude( velocity ) );
        return result.position.distance( position ) <= velocity ? result.motion( new F<UpdateArgs, MovableFraction>() {
            @Override public MovableFraction f( UpdateArgs a ) {
                return a.fraction;
            }
        } ) :
               result;
    }
}