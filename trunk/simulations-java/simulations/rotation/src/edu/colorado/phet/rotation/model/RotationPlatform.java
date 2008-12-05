package edu.colorado.phet.rotation.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.common.motion.model.DefaultTemporalVariable;
import edu.colorado.phet.common.motion.model.ITemporalVariable;
import edu.colorado.phet.common.motion.model.MotionBody;
import edu.colorado.phet.common.phetcommon.math.SerializablePoint2D;
import edu.colorado.phet.rotation.torque.TorqueModel;

/**
 * Author: Sam Reid
 * May 11, 2007, 3:41:07 AM
 */
public class RotationPlatform extends MotionBody {
    private SerializablePoint2D center = new SerializablePoint2D( 0, 0 );
    private DefaultTemporalVariable radius = new DefaultTemporalVariable( DEFAULT_OUTER_RADIUS );
    private DefaultTemporalVariable innerRadius = new DefaultTemporalVariable( DEFAULT_INNER_RADIUS );
    private DefaultTemporalVariable mass = new DefaultTemporalVariable( getDefaultMass() );//by default torque equals angular acceleration

    public RotationPlatform() {
        super( RotationModel.getTimeSeriesFactory() );
    }

    private static double getDefaultMassValue( double innerRadius, double radius ) {
        return 1.0 / ( ( innerRadius * innerRadius + radius * radius ) / 2.0 );
    }

    public void setTime( double time ) {
        super.setTime( time );
        radius.setPlaybackTime( time );
        innerRadius.setPlaybackTime( time );
        mass.setPlaybackTime( time );

        notifyRadiusChanged();
        notifyInnerRadiusChanged();
        notifyMassChanged();
    }

    private double getDefaultMass() {
        return 1.0 / ( ( innerRadius.getValue() * innerRadius.getValue() + radius.getValue() * radius.getValue() ) / 2.0 );
    }

    private transient ArrayList listeners = new ArrayList();
    private boolean displayGraph = DEFAULT_DISPLAY_GRAPH;

    public static final double MAX_RADIUS = 4.0;
    public static final double DEFAULT_OUTER_RADIUS = MAX_RADIUS;
    public static final double DEFAULT_INNER_RADIUS = 0.0;
    public static final boolean DEFAULT_DISPLAY_GRAPH = true;

    public static final double MIN_MASS = getDefaultMassValue( DEFAULT_INNER_RADIUS, DEFAULT_OUTER_RADIUS ) / 10.0;
    public static final double MAX_MASS = getDefaultMassValue( DEFAULT_INNER_RADIUS, DEFAULT_OUTER_RADIUS ) * 2;

    public boolean containsPosition( Point2D loc ) {
        return loc.distance( center ) <= radius.getValue() && loc.distance( center ) >= innerRadius.getValue();
    }

    public Point2D getCenter() {
        return center;
    }

    public boolean getDisplayGraph() {
        return displayGraph;
    }

    public void reset() {
        super.reset();
        setRadius( DEFAULT_OUTER_RADIUS );
        setInnerRadius( DEFAULT_INNER_RADIUS );
        setDisplayGraph( DEFAULT_DISPLAY_GRAPH );
        setMass( getDefaultMass() );
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
        return radius.getValue();
    }

    public void setAngle( double angle ) {
        super.setPosition( angle );
    }

    public void setRadius( final double radius ) {
        if ( this.radius.getValue() != radius ) {
            changeValueConserveMomentum( new Setter() {
                public void setValue() {
                    RotationPlatform.this.radius.setValue( radius );
                }
            } );
            notifyRadiusChanged();
        }
    }

    public static interface Setter {
        void setValue();
    }

    public void setInnerRadius( final double innerRadius ) {
        if ( this.innerRadius.getValue() != innerRadius ) {
            changeValueConserveMomentum( new Setter() {
                public void setValue() {
                    RotationPlatform.this.innerRadius.setValue( innerRadius );
                }
            } );
            notifyInnerRadiusChanged();
        }
    }

    private void changeValueConserveMomentum( Setter setter ) {
        double momentum = getAngularMomentum();
        setter.setValue();
        setVelocityForAngMom( momentum );
    }

    private void setVelocityForAngMom( double momentum ) {
        setVelocity( momentum / getMomentOfInertia() );
    }

    private double getAngularMomentum() {
        return getMomentOfInertia() * getVelocity();
    }

    public void stepInTime( double time, double dt ) {
        super.stepInTime( time, dt );
        radius.addValue( radius.getValue(), time );
        innerRadius.addValue( innerRadius.getValue(), time );
        mass.addValue( mass.getValue(),time );
//        System.out.println( "getAngularMomentum() = " + getAngularMomentum() + ", I=" + getMomentOfInertia() + ", omega=" + getVelocity() );
    }

    public void clear() {
        radius.clear();
        innerRadius.clear();
        mass.clear();
    }

    private void notifyInnerRadiusChanged() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener) listeners.get( i ) ).innerRadiusChanged();
        }
    }

    public double getInnerRadius() {
        return innerRadius.getValue();
    }

    public double getMomentOfInertia() {
        return 0.5 * mass.getValue() * ( innerRadius.getValue() * innerRadius.getValue() + radius.getValue() * radius.getValue() );//http://en.wikipedia.org/wiki/List_of_moments_of_inertia
    }

    public double getMass() {
        return mass.getValue();
    }

    public void setMass( final double mass ) {
        if ( this.mass.getValue() != mass ) {
            changeValueConserveMomentum( new Setter() {
                public void setValue() {
                    RotationPlatform.this.mass.setValue(mass);
                }
            } );
            notifyMassChanged();
        }
    }

    private void notifyMassChanged() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener) listeners.get( i ) ).massChanged();
        }
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

    public boolean isForceDriven() {
        return super.getUpdateStrategy() instanceof TorqueModel.ForceDriven;
    }

    public static interface Listener {
        void radiusChanged();

        void innerRadiusChanged();

        void displayGraphChanged();

        void massChanged();
    }

    public static class Adapter implements Listener {
        public void radiusChanged() {
        }

        public void innerRadiusChanged() {
        }

        public void displayGraphChanged() {
        }

        public void massChanged() {
        }
    }
}