package edu.colorado.phet.forces1d.model;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Nov 12, 2004
 * Time: 9:58:08 PM
 */
public class Block {
    private double kineticFriction = 1.0;
    private double staticFriction = 2.0;
    private double mass = 1.0;
    private double position = DEFAULT_POSITION;
    private double velocity = 0.0;
    private double acceleration = 0.0;
    private ArrayList listeners = new ArrayList();
    private Force1DModel model;
    public static final double DEFAULT_POSITION = -7.0;

    public Block( Force1DModel model ) {
        this.model = model;
    }

    public double getKineticFriction() {
        return kineticFriction;
    }

    public void setKineticFriction( double kineticFriction ) {
        this.kineticFriction = kineticFriction;
        model.updateBlockAcceleration();
        firePropertyChanged();
    }

    public double getStaticFriction() {
        return staticFriction;
    }

    public void setStaticFriction( double staticFriction ) {
        this.staticFriction = staticFriction;
        model.updateBlockAcceleration();
        firePropertyChanged();
    }

    public double getMass() {
        return mass;
    }

    public void setMass( double mass ) {
        this.mass = mass;
        model.updateBlockAcceleration();
        firePropertyChanged();
    }

    private void firePropertyChanged() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener) listeners.get( i );
            listener.propertyChanged();
        }
    }

    public double getPosition() {
        return position;
    }

    public void setPosition( double position ) {
        this.position = position;
    }

    public double getVelocity() {
        return velocity;
    }

    public void setVelocity( double velocity ) {
        this.velocity = velocity;
    }

    public double getAcceleration() {
        return acceleration;
    }

    public void setAcceleration( double acceleration ) {
        this.acceleration = acceleration;
    }

    public void stepInTime( double dt ) {
        double origPosition = position;
        double origVelocity = velocity;
        velocity += acceleration * dt;

        if ( changedSign( origVelocity, velocity ) ) {
            velocity = 0;
        }
        position += velocity * dt;

        if ( origPosition != position ) {
            for ( int i = 0; i < listeners.size(); i++ ) {
                Listener listener = (Listener) listeners.get( i );
                listener.positionChanged();
            }
        }
    }

    public void setStaticAndKineticFriction( double staticFriction, double kineticFriction ) {
        setStaticFriction( staticFriction );
        setKineticFriction( kineticFriction );
        if ( getStaticFriction() < getKineticFriction() ) {
            setStaticFriction( getKineticFriction() );
        }
    }

    public void userSetKineticFriction( double value ) {
        setKineticFriction( value );
        if ( getStaticFriction() < getKineticFriction() ) {
            setStaticFriction( getKineticFriction() );
        }
    }

    public void userSetStaticFriction( double value ) {
        setStaticFriction( value );
//        if ( getKineticFriction() > getStaticFriction() ) {
//            setKineticFriction( value );
//        }
    }

    static class Sign {
        static final Sign POSITIVE = new Sign( "+" );
        static final Sign NEGATIVE = new Sign( "-" );
        static final Sign ZERO = new Sign( "0" );
        private String s;

        public static Sign toSign( double value ) {
            if ( value > 0 ) {
                return POSITIVE;
            }
            else if ( value < 0 ) {
                return NEGATIVE;
            }
            else {
                return ZERO;
            }
        }

        public Sign( String s ) {
            this.s = s;
        }

        public boolean equals( Object obj ) {
            return obj instanceof Sign && ( (Sign) obj ).s.equals( s );
        }
    }

    private boolean changedSign( double origVelocity, double velocity ) {
        Sign origSign = Sign.toSign( origVelocity );
        Sign newSign = Sign.toSign( velocity );
        boolean leftChange = origSign.equals( Sign.POSITIVE ) && newSign.equals( Sign.NEGATIVE );
        boolean rightChange = origSign.equals( Sign.NEGATIVE ) && newSign.equals( Sign.POSITIVE );
        boolean changed = leftChange || rightChange;
        return changed;
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public double getFrictionCoefficient() {
        if ( isMoving() ) {
            return kineticFriction;
        }
        else {
            return staticFriction;
        }
    }

    public boolean isMoving() {
        return velocity != 0.0;
    }

    public interface Listener {
        void positionChanged();

        void propertyChanged();
    }
}
