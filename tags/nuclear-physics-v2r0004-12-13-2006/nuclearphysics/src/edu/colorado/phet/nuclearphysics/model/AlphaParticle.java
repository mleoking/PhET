/**
 * Class: AlphaParticle
 * Class: edu.colorado.phet.nuclearphysics.model
 * User: Ron LeMaster
 * Date: Mar 4, 2004
 * Time: 8:55:18 AM
 */
package edu.colorado.phet.nuclearphysics.model;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.util.PhetUtilities;

import java.awt.geom.Point2D;
import java.util.Random;

/**
 * Alpha Particle
 * <p/>
 * A type of nucleus that is either part of a larger nucleus, or has escaped. It's movement
 * behavior is dependent on this bit of its state.
 */
public class AlphaParticle extends Nucleus {

    //--------------------------------------------------------------------------------------------------
    // Class fields and methods
    //--------------------------------------------------------------------------------------------------

    private static Random random = new Random();
    public static final double RADIUS = NuclearParticle.RADIUS * 2;
    // Controls how fast the alpha particle accelerates down the profile
    private static double forceScale = 0.015;
//    private static double forceScale = 0.01;
    private int stepCnt;
    private int stepsBetweenRandomPlacements = 4;

    //--------------------------------------------------------------------------------------------------
    // Instance fields and methods
    //--------------------------------------------------------------------------------------------------

    private ProfileableNucleus nucleus;
    public boolean isInNucleus = true;
    private double statisticalPositionSigma;
    private boolean escaped = false;
    private double escapeEnergy;

    /**
     * Constructor
     *
     * @param position
     * @param statisticalPositionSigma
     */
    public AlphaParticle( Point2D position, double statisticalPositionSigma ) {
        super( position, 2, 2 );
        this.statisticalPositionSigma = statisticalPositionSigma;
    }

    public void setNucleus( ProfileableNucleus nucleus ) {
        this.nucleus = nucleus;
    }

    public ProfileableNucleus getNucleus() {
        return nucleus;
    }

    public void setEscaped( boolean escaped, double escapeEnergy ) {
        this.escaped = escaped;
        this.escapeEnergy = escapeEnergy;
    }

    public boolean isEscaped() {
        return escaped;
    }

    public void setLocation( double x, double y ) {
        super.setPosition( x, y );
    }

    /**
     * Puts the alpha partical in a randomly selected position if it hasn't escaped from the nucleus.
     *
     * @param dt
     */
    public void stepInTime( double dt ) {
        super.stepInTime( dt );
        if( nucleus != null ) {
            if( !escaped ) {
                // We only move the alpha particle every few time steps, or if the clock is paused and we
                // got a tick from the user clicking the Step button
                if( ++stepCnt % stepsBetweenRandomPlacements == 0 || PhetUtilities.getActiveClock().isPaused() ) {
                    // Generate a random position for the alpha particle
                    double d = ( random.nextGaussian() * statisticalPositionSigma ) * ( random.nextBoolean() ? 1 : -1 );
                    double theta = random.nextDouble() * Math.PI * 2;
                    double dx = d * Math.cos( theta );
                    double dy = d * Math.sin( theta );
                    setLocation( nucleus.getPosition().getX() + dx, nucleus.getPosition().getY() + dy );
                    this.setPotential( -nucleus.getEnergyProfile().getTotalEnergy() );
                }
            }
            else {
                // Accelerate the alpha particle away from the nucleus, with a force
                // proportional to its height on the profile
                EnergyProfile profile = nucleus.getEnergyProfile();
                double d = this.getPosition().distance( nucleus.getPosition() );

                double force = Math.abs( profile.getHillY( -d ) ) * forceScale;
                force = Double.isNaN( force ) ? 0 : force;
                Vector2D a = null;
                if( this.getVelocity().getX() == 0 && this.getVelocity().getY() == 0 ) {
                    double dx = this.getPosition().getX() - nucleus.getPosition().getX();
                    double dy = this.getPosition().getY() - nucleus.getPosition().getY();
                    a = new Vector2D.Double( dx, dy ).normalize().scale( (float)force );
                }
                else {
                    a = new Vector2D.Double( this.getVelocity() ).normalize().scale( (float)force );
                }
                this.setAcceleration( a );
            }
        }
    }

    public double getParentNucleusTotalEnergy() {
        if( isEscaped() ) {
            return escapeEnergy;
        }
        else {
            return nucleus.getEnergyProfile().getTotalEnergy();
        }
    }
    //--------------------------------------------------------------------------------------------------
    // Implementation of Body
    //--------------------------------------------------------------------------------------------------

    public Point2D getCM() {
        return getPosition();
    }

    public double getMomentOfInertia() {
        return Double.NaN;
    }
}
