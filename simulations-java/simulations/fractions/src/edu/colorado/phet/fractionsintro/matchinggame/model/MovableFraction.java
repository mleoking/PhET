// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.matchinggame.model;

import fj.F;
import lombok.Data;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.fractions.util.immutable.Vector2D;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.fractionsintro.matchinggame.model.Motions.WAIT;

/**
 * Immutable class representing a movable fraction in the matching game, such as a numeric representation like 7/4 or a pattern of shapes.
 *
 * @author Sam Reid
 */
@Data public class MovableFraction {

    //For keeping track of which one is which.  Can't use object equality across time steps in functional programming without a sophisticated get/set lens
    public final int id;

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
    public transient final PNode node;

    //Strategy for moving the fraction over time (e.g. toward the scale or back to its original cell)
    //Marked as transient so won't be considered for equality
    public transient final F<UpdateArgs, MovableFraction> motion;

    //Flag set to true if the user has scored with this fraction (making it no longer draggable)
    public final boolean scored;

    //User component for sim sharing
    public final IUserComponent userComponent;

    //Static instance count for creating unique references, see "id" above
    private static int count;

    //Color shown for this fraction
    public final Color color;

    //Name of the representation, for error checking
    public final String representationName;

    public MovableFraction translate( double dx, double dy ) { return withPosition( position.plus( dx, dy ) ); }

    public MovableFraction translate( Vector2D v ) { return translate( v.getX(), v.getY() ); }

    public MovableFraction withDragging( boolean dragging ) { return new MovableFraction( id, position, numerator, denominator, dragging, home, scale, node, motion, scored, userComponent, color, representationName );}

    public MovableFraction withPosition( Vector2D position ) { return new MovableFraction( id, position, numerator, denominator, dragging, home, scale, node, motion, scored, userComponent, color, representationName );}

    public MovableFraction withScale( double scale ) { return new MovableFraction( id, position, numerator, denominator, dragging, home, scale, node, motion, scored, userComponent, color, representationName );}

    public MovableFraction withMotion( F<UpdateArgs, MovableFraction> motion ) { return new MovableFraction( id, position, numerator, denominator, dragging, home, scale, node, motion, scored, userComponent, color, representationName );}

    public MovableFraction withScored( boolean scored ) { return new MovableFraction( id, position, numerator, denominator, dragging, home, scale, node, motion, scored, userComponent, color, representationName );}

    public MovableFraction stepInTime( UpdateArgs args ) { return motion.f( args ); }

    //Animates by translating toward the specified target position
    public MovableFraction stepTowards( Vector2D target, double dt ) {
        double velocity = 600;
        double stepSize = velocity * dt;
        if ( this.position.distance( target ) < stepSize ) {
            return this.withPosition( target ).withMotion( WAIT );
        }
        final MovableFraction result = translate( target.minus( this.position ).getInstanceOfMagnitude( stepSize ) );
        return result.position.distance( target ) <= stepSize ? result.withMotion( new F<UpdateArgs, MovableFraction>() {
            @Override public MovableFraction f( UpdateArgs a ) {
                return a.fraction;
            }
        } ).withPosition( target ) :
               result;
    }

    public double getFractionValue() { return ( (double) numerator ) / denominator;}

    //Incrementally change the scale toward the specified scale value
    public MovableFraction scaleTowards( double scale ) {
        if ( scale == this.scale ) {
            return this;
        }
        double ds = ( scale - this.scale ) / Math.abs( scale - this.scale ) * 0.02;
        return withScale( this.scale + ds );
    }

    //Gets the node (but with the corrected scale).
    public PNode getNodeWithCorrectScale() {

        //But do update the scale and make sure parent is non-null (otherwise disappears on click)
        node.setScale( scale );
        node.setParent( null );
        return node;
    }

    //Create a unique instance id, see "id" field above.
    public static int nextID() {
        int id = count;
        count++;
        return id;
    }
}