/**
 * Class: AlphaParticle
 * Class: edu.colorado.phet.nuclearphysics.model
 * User: Ron LeMaster
 * Date: Mar 4, 2004
 * Time: 8:55:18 AM
 */
package edu.colorado.phet.nuclearphysics.model;

import edu.colorado.phet.common.math.Vector2D;

import java.awt.geom.Point2D;
import java.util.Random;

public class AlphaParticle extends Nucleus {

    //
    // Statics
    //
    private static Random random = new Random();

    public static final double RADIUS = NuclearParticle.RADIUS * 2;
    private double statisticalPositionSigma;
    private Nucleus nucleus;
    private double potential;


    //
    // Instance fields and methods
    //
    public boolean isInNucleus = true;
    private double forceScale = 0.005;

    public AlphaParticle( Point2D.Double position, double statisticalPositionSigma ) {
        super( position, 2, 2 );
        this.statisticalPositionSigma = statisticalPositionSigma;
    }

    private long runningTime = 0;

    public void setNucleus( Nucleus nucleus ) {
        this.nucleus = nucleus;
    }

    public void stepInTime( double dt ) {
//        super.stepInTime( dt );
        if( nucleus != null ) {
            double dn = nucleus.getPotentialProfile().getAlphaDecayX();
            double dSq = this.getLocation().distanceSq( nucleus.getLocation() );
            if( dSq < dn * dn ) {
                double d = ( random.nextGaussian() * statisticalPositionSigma ) * ( Math.random() > 0.5 ? 1 : -1 );
                double theta = Math.random() * Math.PI * 2;
                double dx = d * Math.cos( theta );
                double dy = d * Math.sin( theta );
                setLocation( dx, dy );
                this.setPotential( nucleus.getPotentialProfile().getWellPotential() );
            }
            else {
                PotentialProfile profile = nucleus.getPotentialProfile();

                // If this -Math.sqrt is right, then we need to fix some sign things
                double d = Math.sqrt( dSq );
                double force = Math.abs( profile.getHillY( -d ) ) * forceScale;
                force = Double.isNaN( force ) ? 0 : force;
                double theta;
                Vector2D a = null;
                if( this.getVelocity().getX() == 0 && this.getVelocity().getY() == 0 ) {
                    theta = random.nextDouble() * Math.PI * 2;
                    double dx = this.getLocation().getX() - nucleus.getLocation().getX();
                    double dy = this.getLocation().getY() - nucleus.getLocation().getY();
                    a = new Vector2D( (float)dx, (float)dy ).normalize().multiply( (float)force );
                }
                else {
                    a = new Vector2D( this.getVelocity() ).normalize().multiply( (float)force );
                }
                this.setAcceleration( a );
                this.setPotential( -profile.getHillY( -d ) );
            }
        }
        super.stepInTime( dt );
    }

    private void setPotential( double potential ) {
        this.potential = potential;
    }

    public double getPotential() {
        return potential;
    }

    //
    // Interfaces implemented
    //
    public Point2D.Double getCM() {
        return getLocation();
    }

    public double getMomentOfInertia() {
        return Double.NaN;
    }
}
