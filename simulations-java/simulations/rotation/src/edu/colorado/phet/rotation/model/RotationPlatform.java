package edu.colorado.phet.rotation.model;

import edu.colorado.phet.common.motion.model.MotionBody;
import edu.colorado.phet.common.phetcommon.math.SerializablePoint2D;

import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * Author: Sam Reid
 * May 11, 2007, 3:41:07 AM
 */
public class RotationPlatform extends MotionBody {
    private SerializablePoint2D center = new SerializablePoint2D( 200, 200 );
    private double radius = 200.0;
    private double innerRadius = 0.0;
    private double momentOfInertia = 2.0;

    public boolean containsPosition( Point2D loc ) {
        return loc.distance( center ) < radius && loc.distance( center ) >= innerRadius;
    }

    public Point2D getCenter() {
        return center;
    }

    public double getRadius() {
        return radius;
    }

    public void setAngle( double angle ) {
        super.getMotionBodyState().setPosition( angle );
    }

    public void setRadius( double radius ) {
        if( this.radius != radius ) {
            this.radius = radius;
            notifyRadiusChanged();
        }
    }

    private ArrayList listeners = new ArrayList();

    public void setInnerRadius( double innerRadius ) {
        if( this.innerRadius != innerRadius ) {
            this.innerRadius = innerRadius;
            notifyInnerRadiusChanged();
        }
    }

    private void notifyInnerRadiusChanged() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.innerRadiusChanged();
        }
    }

    public double getInnerRadius() {
        return innerRadius;
    }

    public double getMomentOfInertia() {
        return momentOfInertia;
    }

    public static interface Listener {
        void radiusChanged();

        void innerRadiusChanged();
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void removeListener( Listener listener ) {
        listeners.remove( listener );
    }

    private void notifyRadiusChanged() {
        for( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener)listeners.get( i ) ).radiusChanged();
        }
    }
}
