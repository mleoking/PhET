package edu.colorado.phet.rotation.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.common.motion.model.ITemporalVariable;
import edu.colorado.phet.common.motion.model.MotionBody;
import edu.colorado.phet.common.phetcommon.math.SerializablePoint2D;

/**
 * Author: Sam Reid
 * May 11, 2007, 3:41:07 AM
 */
public class RotationPlatform extends MotionBody {
    private SerializablePoint2D center = new SerializablePoint2D( 0, 0 );
    private double radius = MAX_RADIUS;
    private double innerRadius = 0.0;
    private double mass = 1.0 / ( ( innerRadius * innerRadius + radius * radius ) / 2.0 );//by default torque equals angular acceleration
    private transient ArrayList listeners = new ArrayList();
    private boolean displayGraph = true;

    public static final double MAX_RADIUS = 4.0;

    public boolean containsPosition( Point2D loc ) {
        return loc.distance( center ) <= radius && loc.distance( center ) >= innerRadius;
    }

    public Point2D getCenter() {
        return center;
    }

    public boolean getDisplayGraph() {
        return displayGraph;
    }

    public void setDisplayGraph( boolean displayGraph ) {
        if ( this.displayGraph != displayGraph ) {
            this.displayGraph = displayGraph;
            for ( int i = 0; i < listeners.size(); i++ ) {
                ( (Listener) listeners.get( i ) ).displayGraphChanged();
            }
        }
    }

    public double getRadius() {
        return radius;
    }

    public void setAngle( double angle ) {
        super.setPosition( angle );
    }

    public void setRadius( double radius ) {
        if ( this.radius != radius ) {
            this.radius = radius;
            notifyRadiusChanged();
        }
    }

    public void setInnerRadius( double innerRadius ) {
        if ( this.innerRadius != innerRadius ) {
            this.innerRadius = innerRadius;
            notifyInnerRadiusChanged();
        }
    }

    private void notifyInnerRadiusChanged() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ((Listener) listeners.get( i )).innerRadiusChanged();
        }
    }

    public double getInnerRadius() {
        return innerRadius;
    }

    public double getMomentOfInertia() {
        return 0.5 * mass * ( innerRadius * innerRadius + radius * radius );//http://en.wikipedia.org/wiki/List_of_moments_of_inertia
    }

    public double getMass() {
        return mass;
    }

    public void setMass( double mass ) {
        this.mass = mass;
    }

    public ITemporalVariable getAngularAcceleration() {
        return super.getAccelerationVariable();
    }

    public ITemporalVariable getAngularVelocity() {
        return super.getVelocityVariable();
    }

    public ITemporalVariable getAngle() {
        return super.getPositionVariable();
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void removeListener( Listener listener ) {
        listeners.remove( listener );
    }

    private void notifyRadiusChanged() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener) listeners.get( i ) ).radiusChanged();
        }
    }

    public static interface Listener {
        void radiusChanged();

        void innerRadiusChanged();

        void displayGraphChanged();
    }

    public static class Adapter implements Listener {
        public void radiusChanged() {
        }

        public void innerRadiusChanged() {
        }

        public void displayGraphChanged() {
        }
    }
}
