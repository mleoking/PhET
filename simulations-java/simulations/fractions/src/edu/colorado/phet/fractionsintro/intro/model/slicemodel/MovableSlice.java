// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.intro.model.slicemodel;

import lombok.Data;

import java.awt.Shape;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;

/**
 * This class is solely to keep track of the container that the slice is in, if any.
 * I'm not sure that this is the best design pattern, maybe we should just keep a map of key->value.
 *
 * @author Sam Reid
 */
@Data public class MovableSlice {
    public final Slice slice;
    public final Slice container;//Null if not in a container

    public MovableSlice angle( double v ) { return new MovableSlice( slice.angle( v ), container ); }

    public MovableSlice translate( ImmutableVector2D minus ) { return new MovableSlice( slice.translate( minus ), container ); }

    public MovableSlice tip( ImmutableVector2D tip ) { return new MovableSlice( slice.tip( tip ), container ); }

    public MovableSlice dragging( boolean b ) { return new MovableSlice( slice.dragging( b ), container ); }

    public boolean dragging() { return slice.dragging;}

    public MovableSlice translate( double width, double height ) { return new MovableSlice( slice.translate( width, height ), container ); }

    public MovableSlice container( Slice closest ) { return new MovableSlice( slice, closest ); }

    public MovableSlice moveTo( Slice target ) { return dragging( false ).angle( target.angle ).tip( target.tip ).container( target ); }

    public Shape shape() { return slice.shape(); }

    public ImmutableVector2D center() {return slice.center();}

    public double angle() { return slice.angle;}

    public MovableSlice animationTarget( AnimationTarget pt ) { return new MovableSlice( slice.animationTarget( pt ), container ); }

    //TODO: container should change when animation target reached
    public MovableSlice stepAnimation() { return new MovableSlice( slice.stepAnimation(), container ); }

    public MovableSlice rotateTowardTarget( double angle ) { return new MovableSlice( slice.rotateTowardTarget( angle ), container ); }

    public ImmutableVector2D tip() {return slice.tip;}

    public boolean movingToward( Slice cell ) { return slice.movingToward( cell ); }

    public AnimationTarget animationTarget() {return slice.animationTarget;}
}