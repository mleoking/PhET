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
    private double forceFromNucleus;


    //
    // Instance fields and methods
    //

    public AlphaParticle( Point2D.Double position, double statisticalPositionSigma ) {
        super( position, 2, 2 );
        this.statisticalPositionSigma = statisticalPositionSigma;
    }

    public void bindToNucleus( Nucleus nucleus ) {
        this.nucleus = nucleus;
    }

    public Nucleus getNucleus() {
        return nucleus;
    }

    public void stepInTime( double dt ) {
        super.stepInTime( dt );
        double dd = this.getLocation().distance( nucleus.getLocation() );
        double dn = nucleus.getPotentialProfile().getAlphaDecayX();
        double dSq = this.getLocation().distanceSq( nucleus.getLocation() );
        if( nucleus != null && dSq < dn * dn ) {
            double d = ( random.nextGaussian() * statisticalPositionSigma ) * ( Math.random() > 0.5 ? 1 : -1 );
            double theta = Math.random() * Math.PI * 2;
            double dx = d * Math.cos( theta );
            double dy = d * Math.sin( theta );
            setLocation( dx, dy );
        }
        else if( nucleus != null ) {
            PotentialProfile profile = nucleus.getPotentialProfile();
            double force = profile.getHillY( Math.sqrt( dSq ) ) * 1;
            this.forceFromNucleus = force;
            this.setAcceleration( new Vector2D( this.getVelocity() ).normalize().multiply( (float)force ) );
        }
    }

    public void setForceFromNucleus( double forceFromNucleus ) {
        this.forceFromNucleus = forceFromNucleus;
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
