/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.model.particle;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.patterns.PubliclyCloneable;

/**
 * This class represents a particle in the model portion of the States of
 * Matter simulation.
 *
 * @author John De Goes, John Blanco
 */
// TODO: JPB TBD - It may make sense to make this into an abstract class once
// all of De Goes old code is removed.
public class StatesOfMatterAtom implements PubliclyCloneable {
    
    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------

    public static final StatesOfMatterAtom TEST = new StatesOfMatterAtom(0.0, 0.0, 1.0, 1.0);
    private static final double DEFAULT_SIGMA = 330;    // Atom diameter, in picometers.
    private static final double DEFAULT_EPSILON = 120;  // epsilon/k-boltzman is in Kelvin.

    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
    
    private Point2D.Double  m_position = new Point2D.Double();
    private Vector2D.Double m_velocity = new Vector2D.Double();
    private Vector2D.Double m_accel    = new Vector2D.Double();
    private volatile double m_radius, m_mass;
    private double m_inverseMass;
    private ArrayList m_listeners = new ArrayList();

    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------
    
    public StatesOfMatterAtom(double x, double y, double radius, double mass) {
        this(x, y, radius, mass, 0.0, 0.0, 0.0, 0.0);

        if (mass   <= 0.0) throw new IllegalArgumentException("Mass is out of range");
        if (radius <= 0.0) throw new IllegalArgumentException("Radius is out of range");
    }

    //----------------------------------------------------------------------------
    // Accessor Methods
    //----------------------------------------------------------------------------

    public double getX() {
        return m_position.x;
    }

    public double getY() {
        return m_position.y;
    }
    
    public void setPosition(double x, double y){
        m_position.x = x;
        m_position.y = y;
        notifyPositionChanged();
    }

    public double getVy() {
        return m_velocity.getY();
    }

    public void setVy(double vy) {
        m_velocity.setY(vy);
    }

    public double getVx() {
        return m_velocity.getX();
    }

    public void setVx(double vx) {
        m_velocity.setX(vx);
    }

    public double getAx() {
        return m_accel.getX();
    }

    public double getAy() {
        return m_accel.getY();
    }

    public void setAx(double ax) {
        m_accel.setX(ax);
    }

    public void setAy(double ay) {
        m_accel.setY(ay);
    }

    public double getMass() {
        return m_mass;
    }

    public double getRadius() {
        return m_radius;
    }
    
    public void setRadius(double radius) {
        m_radius = radius;
        notifyRadiusChanged();
    }
    
    public static double getSigma() {
        return DEFAULT_SIGMA;
    }
    
    public static double getEpsilon() {
        return DEFAULT_EPSILON;
    }
    
    public double getInverseMass() {
        if (m_inverseMass == 0.0) {
            m_inverseMass = m_mass == 0.0 ? Double.MAX_VALUE : 1.0 / m_mass;
        }

        return m_inverseMass;
    }

    public double getKineticEnergy() {
        return 0.5 * m_mass * (getVx() * getVx() + getVy() * getVy());
    }

    public void setKineticEnergy(double energy) {
        // KE = 0.5 * m * v^2 => v = sqrt(2 KE / m)
        double mag = Math.sqrt(2.0 * energy / m_mass);

        double curMag = m_velocity.getMagnitude();

        if (curMag == 0.0) {
            double rad = Math.random() * Math.PI * 2.0;

            m_velocity.setComponents(Math.cos(rad), Math.sin(rad));

            curMag = 1.0;
        }
        
        double scale = mag / curMag;

        m_velocity.scale(scale);
    }

    public Point2D getPositionReference() {
        return m_position;
    }
    
    public Vector2D getVelocity() {
        return m_velocity;
    }

    public Vector2D getAccel() {
        return m_accel;
    }
    
    //----------------------------------------------------------------------------
    // Other Public Methods
    //----------------------------------------------------------------------------

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        StatesOfMatterAtom that = (StatesOfMatterAtom)o;

        if (Double.compare(that.m_inverseMass, m_inverseMass) != 0) {
            return false;
        }
        if (Double.compare(that.m_mass, m_mass) != 0) {
            return false;
        }
        if (Double.compare(that.m_radius, m_radius) != 0) {
            return false;
        }
        if (Double.compare(that.getVx(), getVx()) != 0) {
            return false;
        }
        if (Double.compare(that.getVy(), getVy()) != 0) {
            return false;
        }
        if (Double.compare(that.getX(), getX()) != 0) {
            return false;
        }
        if (Double.compare(that.getY(), getY()) != 0) {
            return false;
        }
        if (Double.compare(that.getAx(), getAx()) != 0) {
            return false;
        }
        if (Double.compare(that.getAy(), getAy()) != 0) {
            return false;
        }

        return true;
    }

    public Object clone() {
        try {
            StatesOfMatterAtom p = (StatesOfMatterAtom)super.clone();

            p.m_position = new Point2D.Double(getX(), getY());
            p.m_velocity = new Vector2D.Double(getVx(), getVy());
            p.m_accel    = new Vector2D.Double(getAx(), getAy());

            return p;
        }
        catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
    }

    public String toString() {
        return getClass().getName() + "[x=" + getX() + ",y=" + getY() + ",radius=" + m_radius + ",mass=" + m_mass + ",vx=" + getVx() + ",vy=" + getVy() + ",ax=" + getAx() + ",ay=" + getAy() + "]";
    }
    
    public void addListener(Listener listener){
        
        if (m_listeners.contains( listener ))
        {
            // Don't bother re-adding.
            return;
        }
        
        m_listeners.add( listener );
    }
    
    public boolean removeListener(Listener listener){
        return m_listeners.remove( listener );
    }
    
    /**
     * Notify the particle that it has been removed from the model so that it
     * can get rid of any memory references (for garbage collection purposes)
     * and can let any listeners know that is has been removed.
     */
    public void removedFromModel(){
        notifyParticleRemoved();
        m_listeners.clear();
    }

    //----------------------------------------------------------------------------
    // Private Methods
    //----------------------------------------------------------------------------
    
    private StatesOfMatterAtom(double x, double y, double radius, double mass, double vx, double vy, double ax, double ay) {
        m_position.setLocation(x, y);
        m_velocity.setComponents(vx, vy);
        m_accel.setComponents(ax, ay);
        
        this.m_mass   = mass;
        this.m_radius = radius;
    }

    private void notifyPositionChanged(){
        for (int i = 0; i < m_listeners.size(); i++){
            ((Listener)m_listeners.get( i )).positionChanged();
        }        
    }

    private void notifyParticleRemoved(){
        for (int i = 0; i < m_listeners.size(); i++){
            ((Listener)m_listeners.get( i )).particleRemoved( this );
        }        
    }

    private void notifyRadiusChanged(){
        for (int i = 0; i < m_listeners.size(); i++){
            ((Listener)m_listeners.get( i )).particleRadiusChanged();
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
         * Inform listeners that this particle has been removed from the
         * model.
         */
        public void particleRemoved(StatesOfMatterAtom particle);
        
        /**
         * Inform listeners that the radius of this particle has been changed.
         */
        public void particleRadiusChanged();
    }
    
    public static class Adapter implements Listener {
        public void positionChanged(){};
        public void particleRemoved(StatesOfMatterAtom particle){};
        public void particleRadiusChanged(){};
    }
}
