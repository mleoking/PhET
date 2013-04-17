// Copyright 2002-2012, University of Colorado

package edu.colorado.phet.statesofmatter.model.particle;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.vector.MutableVector2D;
import edu.colorado.phet.statesofmatter.model.AtomType;

/**
 * This class represents a particle in the model portion of the States of
 * Matter simulation.
 *
 * @author John De Goes, John Blanco
 */
public abstract class StatesOfMatterAtom implements Cloneable {

    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------

    private Point2D.Double m_position = new Point2D.Double();  // In picometers.
    private MutableVector2D m_velocity = new MutableVector2D(); // In meters/sec
    private MutableVector2D m_accel = new MutableVector2D(); // In meters/(sec * sec)
    protected double m_radius;                   // In picometers.
    private final double m_mass;         // In atomic mass units.
    private final ArrayList m_listeners = new ArrayList();

    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------

    public StatesOfMatterAtom( double x, double y, double radius, double mass ) {
        this( x, y, radius, mass, 0.0, 0.0, 0.0, 0.0 );

        if ( mass <= 0.0 ) {
            throw new IllegalArgumentException( "Mass is out of range" );
        }
        if ( radius <= 0.0 ) {
            throw new IllegalArgumentException( "Radius is out of range" );
        }
    }

    //----------------------------------------------------------------------------
    // Accessor Methods
    //----------------------------------------------------------------------------

    abstract public AtomType getType();

    public double getX() {
        return m_position.x;
    }

    public double getY() {
        return m_position.y;
    }

    public void setPosition( double x, double y ) {
        m_position.x = x;
        m_position.y = y;
        notifyPositionChanged();
    }

    public double getVy() {
        return m_velocity.getY();
    }

    public void setVy( double vy ) {
        m_velocity.setY( vy );
        notifyVelocityChanged();
    }

    public double getVx() {
        return m_velocity.getX();
    }

    public void setVx( double vx ) {
        m_velocity.setX( vx );
        notifyVelocityChanged();
    }

    public double getAx() {
        return m_accel.getX();
    }

    public double getAy() {
        return m_accel.getY();
    }

    public void setAx( double ax ) {
        m_accel.setX( ax );
        notifyAccelerationChanged();
    }

    public void setAy( double ay ) {
        m_accel.setY( ay );
        notifyAccelerationChanged();
    }

    public double getMass() {
        return m_mass;
    }

    public double getRadius() {
        return m_radius;
    }

    public Point2D getPositionReference() {
        return m_position;
    }

    public MutableVector2D getVelocity() {
        return m_velocity;
    }

    public MutableVector2D getAccel() {
        return m_accel;
    }

    //----------------------------------------------------------------------------
    // Other Public Methods
    //----------------------------------------------------------------------------

    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }

        StatesOfMatterAtom that = (StatesOfMatterAtom) o;

        if ( Double.compare( that.m_mass, m_mass ) != 0 ) {
            return false;
        }
        if ( Double.compare( that.m_radius, m_radius ) != 0 ) {
            return false;
        }
        if ( Double.compare( that.getVx(), getVx() ) != 0 ) {
            return false;
        }
        if ( Double.compare( that.getVy(), getVy() ) != 0 ) {
            return false;
        }
        if ( Double.compare( that.getX(), getX() ) != 0 ) {
            return false;
        }
        if ( Double.compare( that.getY(), getY() ) != 0 ) {
            return false;
        }
        if ( Double.compare( that.getAx(), getAx() ) != 0 ) {
            return false;
        }
        if ( Double.compare( that.getAy(), getAy() ) != 0 ) {
            return false;
        }

        return true;
    }

    public Object clone() throws CloneNotSupportedException {
        try {
            StatesOfMatterAtom p = (StatesOfMatterAtom) super.clone();

            p.m_position = new Point2D.Double( getX(), getY() );
            p.m_velocity = new MutableVector2D( getVx(), getVy() );
            p.m_accel = new MutableVector2D( getAx(), getAy() );

            return p;
        }
        catch ( CloneNotSupportedException e ) {
            throw new InternalError();
        }
    }

    public String toString() {
        return getClass().getName() + "[x=" + getX() + ",y=" + getY() + ",radius=" + m_radius + ",mass=" + m_mass + ",vx=" + getVx() + ",vy=" + getVy() + ",ax=" + getAx() + ",ay=" + getAy() + "]";
    }

    public void addListener( Listener listener ) {

        if ( m_listeners.contains( listener ) ) {
            // Don't bother re-adding.
            return;
        }

        m_listeners.add( listener );
    }

    public boolean removeListener( Listener listener ) {
        return m_listeners.remove( listener );
    }

    /**
     * Notify the particle that it has been removed from the model so that it
     * can get rid of any memory references (for garbage collection purposes)
     * and can let any listeners know that is has been removed.
     */
    public void removedFromModel() {
        notifyParticleRemoved();
        m_listeners.clear();
    }

    //----------------------------------------------------------------------------
    // Private Methods
    //----------------------------------------------------------------------------

    private StatesOfMatterAtom( double x, double y, double radius, double mass, double vx, double vy, double ax, double ay ) {
        m_position.setLocation( x, y );
        m_velocity.setComponents( vx, vy );
        m_accel.setComponents( ax, ay );

        this.m_mass = mass;
        this.m_radius = radius;
    }

    private void notifyPositionChanged() {
        for ( Object listener : m_listeners ) {
            ( (Listener) listener ).positionChanged();
        }
    }

    private void notifyVelocityChanged() {
        for ( Object listener : m_listeners ) {
            ( (Listener) listener ).velocityChanged();
        }
    }

    private void notifyAccelerationChanged() {
        for ( Object listener : m_listeners ) {
            ( (Listener) listener ).accelerationChanged();
        }
    }

    private void notifyParticleRemoved() {
        for ( Object listener : m_listeners ) {
            ( (Listener) listener ).particleRemoved( this );
        }
    }

    protected void notifyRadiusChanged() {
        for ( Object listener : m_listeners ) {
            ( (Listener) listener ).radiusChanged();
        }
    }

    //------------------------------------------------------------------------
    // Inner Interfaces and Adapters
    //------------------------------------------------------------------------

    public static interface Listener {

        /**
         * Inform listeners that the position of this particle has changed.
         */
        public void positionChanged();

        /**
         * Inform listeners that the velocity of this particle has changed.
         */
        public void velocityChanged();

        /**
         * Inform listeners that the acceleration of this particle has changed.
         */
        public void accelerationChanged();

        /**
         * Inform listeners that this particle has been removed from the
         * model.
         */
        public void particleRemoved( StatesOfMatterAtom particle );

        /**
         * Inform listeners that the radius of this particle has been changed.
         */
        public void radiusChanged();

    }

    public static class Adapter implements Listener {
        public void positionChanged() {
        }

        public void velocityChanged() {
        }

        public void accelerationChanged() {
        }

        public void particleRemoved( StatesOfMatterAtom particle ) {
        }

        public void radiusChanged() {
        }
    }
}
