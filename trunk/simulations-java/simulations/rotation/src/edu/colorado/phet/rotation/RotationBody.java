package edu.colorado.phet.rotation;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.util.persistence.PersistenceUtil;
import edu.colorado.phet.common.mechanics.PhysicalVector;
import edu.colorado.phet.rotation.model.RotationPlatform;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Author: Sam Reid
 * May 11, 2007, 12:12:12 AM
 */
public class RotationBody implements Serializable {
    private UpdateStrategy updateStrategy = new OffPlatform();
    private double x = 10;
    private double y = 10;
    private transient ArrayList listeners = new ArrayList();
    private double orientation = 0.0;

    public void setOffPlatform() {
        setUpdateStrategy( new OffPlatform() );
    }

    private void setUpdateStrategy( UpdateStrategy updateStrategy ) {
        this.updateStrategy.detach();
        this.updateStrategy = updateStrategy;
    }

    public void setOnPlatform( RotationPlatform rotationPlatform ) {
        setUpdateStrategy( new OnPlatform( rotationPlatform ) );
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void translate( double dx, double dy ) {
        setPosition( getPosition().getX() + dx, getPosition().getY() + dy );
    }

    public double getOrientation() {
        return orientation;
    }

    public RotationBody copy() {
        try {
            return (RotationBody)PersistenceUtil.copy( this );
        }
        catch( PersistenceUtil.CopyFailedException e ) {
            e.printStackTrace();
            throw new RuntimeException( e );
        }
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public Vector2D getVelocity() {
        return new Vector2D.Double( );
    }

    public Vector2D getAcceleration() {
        return new Vector2D.Double( );
    }

    private static abstract class UpdateStrategy implements Serializable {
        public abstract void detach();
    }

    private static Point2D rotate( Point2D pt, Point2D center, double angle ) {
        Vector2D.Double v = new Vector2D.Double( center, pt );
        v.rotate( angle );
        return v.getDestination( center );
    }

    private static Line2D rotate( Line2D line, Point2D center, double angle ) {
        return new Line2D.Double( rotate( line.getP1(), center, angle ), rotate( line.getP2(), center, angle ) );
    }

    private class OffPlatform extends UpdateStrategy {

        public void detach() {
        }
    }

    private class OnPlatform extends UpdateStrategy implements RotationPlatform.Listener {
        private RotationPlatform rotationPlatform;

        public OnPlatform( RotationPlatform rotationPlatform ) {
            this.rotationPlatform = rotationPlatform;
            rotationPlatform.addListener( this );
        }

        public void angleChanged( double dtheta ) {
            Line2D segment = new Line2D.Double( getPosition(), Vector2D.Double.parseAngleAndMagnitude( 0.01, getOrientation() ).getDestination( getPosition() ) );
            setPosition( rotate( getPosition(), rotationPlatform.getCenter(), dtheta ) );
            Line2D rot = rotate( segment, rotationPlatform.getCenter(), dtheta );

            setOrientation( new Vector2D.Double( rot.getP1(), rot.getP2() ).getAngle() );
            notifyPositionChanged();
        }

        public void detach() {
            rotationPlatform.removeListener( this );
        }
    }

    private void setPosition( Point2D point2D ) {
        setPosition( point2D.getX(), point2D.getY() );
    }

    private void setOrientation( double orientation ) {
        this.orientation = orientation;
    }

    private void notifyPositionChanged() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.positionChanged();
        }
    }

    public Point2D getPosition() {
        return new Point2D.Double( x, y );
    }

    public static interface Listener {
        void positionChanged();
    }

    public void setPosition( double x, double y ) {
        if( this.x != x || this.y != y ) {
            this.x = x;
            this.y = y;
            System.out.println( "x = " + x );
            notifyPositionChanged();
        }
    }
}
