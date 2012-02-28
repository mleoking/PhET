// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.matchinggame.model;

import fj.F;
import lombok.Data;

import edu.colorado.phet.fractions.util.immutable.Vector2D;
import edu.colorado.phet.fractionsintro.intro.model.Fraction;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.fractionsintro.matchinggame.model.Motions.Stillness;

/**
 * Immutable class representing a movable fraction in the matching game
 *
 * @author Sam Reid
 */
@Data public class MovableFraction {

    //The location of the fraction, I haven't decided if this is center or top left
    public final Vector2D position;

    //Numerator and denominator of the unreduced fraction
    public final int numerator;
    public final int denominator;

    //Flag to indicate whether the user is dragging the fraction
    public final boolean dragging;

    //The cell where the fraction came from, so it can be animated back there if necessary.
    public final Cell home;

    //Scale for the node, size changes as it animates to the scoring cell
    public final double scale;

    //Way of creating nodes for rendering and doing bounds/layouts.  This is in the model because object locations, bounds, animation are also in the model.
    //It is a function that creates nodes instead of a single PNode because I am not sure if the same PNode can be used safely in multiple places in a piccolo scene graph
    public transient final F<Fraction, PNode> node;

    //Strategy for moving the fraction over time (e.g. toward the scale or back to its original cell)
    public final F<UpdateArgs, MovableFraction> motion;

    public MovableFraction translate( double dx, double dy ) { return position( position.plus( dx, dy ) ); }

    public MovableFraction translate( Vector2D v ) { return translate( v.getX(), v.getY() ); }

    public MovableFraction dragging( boolean dragging ) { return new MovableFraction( position, numerator, denominator, dragging, home, scale, node, motion );}

    public MovableFraction position( Vector2D position ) { return new MovableFraction( position, numerator, denominator, dragging, home, scale, node, motion );}

    public MovableFraction scale( double scale ) { return new MovableFraction( position, numerator, denominator, dragging, home, scale, node, motion );}

    public MovableFraction motion( F<UpdateArgs, MovableFraction> motion ) { return new MovableFraction( position, numerator, denominator, dragging, home, scale, node, motion );}

    public Fraction fraction() { return new Fraction( numerator, denominator );}

    public MovableFraction stepInTime( UpdateArgs args ) { return motion.f( args ); }

    public MovableFraction stepTowards( Vector2D target, double dt ) {
        double velocity = 600;
        double stepSize = velocity * dt;
        if ( this.position.distance( target ) < stepSize ) {
            return this.position( target ).motion( Stillness );
        }
        final MovableFraction result = translate( target.minus( this.position ).getInstanceOfMagnitude( stepSize ) );
        return result.position.distance( target ) <= stepSize ? result.motion( new F<UpdateArgs, MovableFraction>() {
            @Override public MovableFraction f( UpdateArgs a ) {
                return a.fraction;
            }
        } ).position( target ) :
               result;
    }

    public double getValue() { return ( (double) numerator ) / denominator;}

    public MovableFraction scaleTowards( double scale ) {
        if ( scale == this.scale ) {
            return this;
        }
        double ds = ( scale - this.scale ) / Math.abs( scale - this.scale ) * 0.01;
        return scale( this.scale + ds );
    }

    public PNode toNode() {
        final PNode node = this.node.f( fraction() );
        node.setScale( scale );
        return node;
    }
}