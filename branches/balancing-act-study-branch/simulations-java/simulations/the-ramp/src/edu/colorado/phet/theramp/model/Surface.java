// Copyright 2002-2012, University of Colorado

/*  */
package edu.colorado.phet.theramp.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.vector.MutableVector2D;
import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.util.SimpleObservable;

/**
 * User: Sam Reid
 * Date: Feb 11, 2005
 * Time: 10:12:53 AM
 */

public abstract class Surface extends SimpleObservable {
    private double angle;
    private double x0;
    private double y0;
    private double length;
    private double distanceOffset;
    ArrayList collisionListeners = new ArrayList();

    public Surface( double angle, double length ) {
        this( angle, length, 0, 0, 0.0 );
    }

    public Surface( double angle, double length, double x0, double y0, double distanceOffset ) {
        this.angle = angle;
        this.x0 = x0;
        this.y0 = y0;
        this.length = length;
        this.distanceOffset = distanceOffset;
    }

    public Point2D getOrigin() {
        return new Point2D.Double( x0, y0 );
    }

    public double getLength() {
        return length;
    }

    public Point2D getLocation( double distAlongRamp ) {
        Vector2D vector = MutableVector2D.createPolar( distAlongRamp, angle );
        return vector.getDestination( getOrigin() );
    }

    public Point2D getEndPoint() {
        return getLocation( length );
    }

    public void setAngle( double angle ) {
        if ( this.angle != angle ) {
            this.angle = angle;
            notifyObservers();
        }
    }

    public void setLength( double length ) {
        this.length = length;
        notifyObservers();
    }

    public double getAngle() {
        return angle;
    }

    public double getHeight() {
        return length * Math.sin( angle );
    }

    public abstract Surface copyState();

    public void setState( Surface state ) {
        setAngle( state.angle );
        this.x0 = state.x0;
        this.y0 = state.y0;
        this.length = state.length;
        this.distanceOffset = state.distanceOffset;
        //todo notify observers
    }

    public abstract boolean applyBoundaryConditions( RampPhysicalModel rampPhysicalModel, Block block );

    public abstract double getWallForce( double sumOtherForces, Block block );

    public double getDistanceOffset() {
        return distanceOffset;
    }

    public void setDistanceOffset( double distanceOffset ) {
        this.distanceOffset = distanceOffset;
    }

    public void notifyCollision() {
        for ( int i = 0; i < collisionListeners.size(); i++ ) {
            CollisionListener collisionListener = (CollisionListener) collisionListeners.get( i );
            collisionListener.collided( this );
        }
    }


    public void addCollisionListener( CollisionListener collisionListener ) {
        collisionListeners.add( collisionListener );
    }

    public abstract String getName();

    public static interface CollisionListener {
        void collided( Surface surface );
    }
}
