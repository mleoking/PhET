package edu.colorado.phet.rotation;

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
    }

    public void setOnPlatform() {
        rotationBodyState = new OnPlatform( getPosition() );
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

    private class OnPlatform extends RotationBodyState {
        double radius;
        double angle;
        double orientation;

        public OnPlatform( Point2D position ) {
            setPosition( position.getX(), position.getY() );
        }

        public void setPosition( double x, double y ) {
        }

        public Point2D getPosition() {
            return null;
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
