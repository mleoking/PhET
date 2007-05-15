package edu.colorado.phet.rotation.model;

import edu.colorado.phet.common.phetcommon.math.SerializablePoint2D;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Author: Sam Reid
 * May 11, 2007, 3:41:07 AM
 */
public class RotationPlatform implements Serializable {
    private transient ArrayList listeners = new ArrayList();
    private SerializablePoint2D center = new SerializablePoint2D( 200, 200 );
    private double radius = 200.0;

    private double angle;
    private double angularVelocity = 0.0;
    private double angularAcceleration = 0.0;

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public double getAngle() {
        return angle;
    }

    public void setAngle( double angle ) {
        double origAngle = this.angle;
        if( this.angle != angle ) {
            this.angle = angle;
            notifyAngleChanged( angle - origAngle );
        }
    }

    public boolean containsPosition( Point2D loc ) {
        return loc.distance( center ) < radius;
    }

    public Point2D getCenter() {
        return center;
    }

    public double getRadius() {
        return radius;
    }

    public void removeListener( Listener listener ) {
        listeners.remove( listener );
    }

    public double getAngularAcceleration() {
        return angularAcceleration;
    }

    public double getAngularVelocity() {
        return angularVelocity;
    }

    public void setAngularVelocity( double angularVelocity ) {
        this.angularVelocity = angularVelocity;
    }

    public void setAngularAcceleration( double angularAcceleration ) {
        this.angularAcceleration = angularAcceleration;
    }

    public static interface Listener {
        void angleChanged( double dtheta );
    }

    public void notifyAngleChanged( double dtheta ) {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.angleChanged( dtheta );
        }
    }
}
