package edu.colorado.phet.rotation;

import edu.colorado.phet.common.phetcommon.math.AbstractVector2D;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.rotation.model.RotationPlatform;

import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * Author: Sam Reid
 * May 11, 2007, 12:12:12 AM
 */
public class RotationBody {
    private RotationBodyState rotationBodyState = new OffPlatform( new Point2D.Double() );
    private ArrayList listeners = new ArrayList();

    public void setOffPlatform() {
        rotationBodyState = new OffPlatform( getPosition() );
        notifyPositionChanged();
    }

    public void setOnPlatform( RotationPlatform rotationPlatform ) {
        rotationBodyState = new OnPlatform( getPosition(), rotationPlatform );
        notifyPositionChanged();
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void translate( double dx, double dy ) {
        setPosition( getPosition().getX() + dx, getPosition().getY() + dy );
    }

    private static abstract class RotationBodyState {
        public abstract void setPosition( double x, double y );

        public abstract Point2D getPosition();
    }

    private class OnPlatform extends RotationBodyState implements RotationPlatform.Listener {
        double radius;
        double angle;
        double orientation;
        private RotationPlatform rotationPlatform;

        public OnPlatform( Point2D position, RotationPlatform rotationPlatform ) {
            this.rotationPlatform = rotationPlatform;
            setPosition( position.getX(), position.getY() );
            rotationPlatform.addListener( this );//todo: memory leak
        }

        public void setPosition( double x, double y ) {
            this.angle = Math.atan2( y, x );
            this.radius = rotationPlatform.getCenter().distance( x, y );
            notifyPositionChanged();
        }

        public Point2D getPosition() {
            AbstractVector2D vector = Vector2D.Double.parseAngleAndMagnitude( radius, angle );
            return vector.getDestination( rotationPlatform.getCenter() );
        }

        public void angleChanged( double dtheta ) {
            angle += dtheta;
            notifyPositionChanged();
        }
    }

    private class OffPlatform extends RotationBodyState {
        double x;
        double y;
        double orientation;

        public OffPlatform( Point2D position ) {
            setPosition( position.getX(), position.getY() );
        }

        public void setPosition( double x, double y ) {
            if( this.x != x || this.y != y ) {
                this.x = x;
                this.y = y;
                notifyPositionChanged();
            }
        }

        public Point2D getPosition() {
            return new Point2D.Double( x, y );
        }
    }

    private void notifyPositionChanged() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.positionChanged();
        }
    }

    public Point2D getPosition() {
        return rotationBodyState.getPosition();
    }

    public static interface Listener {
        void positionChanged();
    }

    public void setPosition( double x, double y ) {
        rotationBodyState.setPosition( x, y );
    }
}
