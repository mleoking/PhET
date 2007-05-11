package edu.colorado.phet.rotation.model;

import edu.colorado.phet.rotation.RotationBody;

import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * Author: Sam Reid
 * May 11, 2007, 3:41:07 AM
 */
public class RotationPlatform {
    private ArrayList listeners = new ArrayList();
    private double angle;
    private Point2D.Double center = new Point2D.Double( 200, 200 );
    private double radius = 200.0;

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
        listeners.remove(listener );
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
