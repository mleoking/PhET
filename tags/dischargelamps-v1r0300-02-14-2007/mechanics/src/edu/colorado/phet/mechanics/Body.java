/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.mechanics;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.model.Particle;

import java.awt.geom.Point2D;

/**
 * Body
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public abstract class Body extends Particle {

    private Particle lastColidedBody = null;
    private double theta;
    private double omega;
    private double alpha;
    private double prevAlpha;
    private double mass;
    private Vector2D momentum = new Vector2D.Double();


    //--------------------------------------------------------------------------------------------------
    // Abstract methods
    //--------------------------------------------------------------------------------------------------
    public abstract Point2D getCM();

    public abstract double getMomentOfInertia();


    /**
     *
     */
    protected Body() {
    }

    /**
     *
     * @param location
     * @param velocity
     * @param acceleration
     * @param mass
     * @param charge
     */
    protected Body( Point2D location, Vector2D velocity,
                    Vector2D acceleration, double mass, double charge ) {
        super( location, velocity, acceleration );
        setMass( mass );
    }

    /**
     * Returns the total kinetic energy of the body, translational
     * and rotational
     *
     * @return the kinetic energy
     */
    public double getKineticEnergy() {
        return ( getMass() * getVelocity().getMagnitudeSq() / 2 ) +
               getMomentOfInertia() * omega * omega / 2;
    }

    /**
     * @param dt
     */
    public void stepInTime( double dt ) {
        // New orientation
        theta = theta + dt * omega + dt * dt * alpha / 2;
        // New angular velocity
        omega = omega + dt * ( alpha + prevAlpha ) / 2;
        // Track angular acceleration
        prevAlpha = alpha;

        super.stepInTime( dt );

        momentum.setComponents( getVelocity().getX() * getMass(),
                                getVelocity().getY() * getMass() );
    }

    public double getSpeed() {
        return getVelocity().getMagnitude();
    }

    public double getTheta() {
        return theta;
    }

    public void setTheta( double theta ) {
        this.theta = theta;
    }

    public double getOmega() {
        return omega;
    }

    public void setOmega( double omega ) {
        this.omega = omega;
    }

    public double getAlpha() {
        return alpha;
    }

    public void setAlpha( double alpha ) {
        this.alpha = alpha;
    }

    public double getMass() {
        return mass;
    }

    public void setMass( double mass ) {
        this.mass = mass;
    }

    public Vector2D getMomentum() {
        return new Vector2D.Double( getVelocity().getX() * getMass(),
                                    getVelocity().getY() * getMass() );
    }

    public void setMomentum( Vector2D momentum ) {
        setVelocity( momentum.getX() / getMass(), momentum.getY() / getMass() );
    }

    /**
     * @return
     * @deprecated
     */
    public Particle getLastColidedBody() {
        return lastColidedBody;
    }

    /**
     * @deprecated
     */
    public void setLastColidedBody( Particle lastColidedBody ) {
        this.lastColidedBody = lastColidedBody;
    }
}
