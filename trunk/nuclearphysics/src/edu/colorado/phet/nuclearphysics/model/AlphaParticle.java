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
    // Controls how fast the alpha particle accelerates down the profile
    private static double forceScale = 0.05;
    //    private static double forceScale = 0.0008;


    //
    // Instance fields and methods
    //
    private Nucleus nucleus;
    public boolean isInNucleus = true;
    private double statisticalPositionSigma;
    private boolean escaped = false;

    public AlphaParticle( Point2D.Double position, double statisticalPositionSigma ) {
        super( position, 2, 2 );
        this.statisticalPositionSigma = statisticalPositionSigma;
    }

    public void setNucleus( Nucleus nucleus ) {
        this.nucleus = nucleus;
    }

    public void setEscaped( boolean escaped ) {
        this.escaped = escaped;
    }

    public void setLocation( double x, double y ) {
        super.setPosition( x, y );
    }

    public void stepInTime( double dt ) {
        super.stepInTime( dt );
        if( nucleus != null ) {
            if( !escaped ) {
                // Generate a random position for the alpha particle
                double d = ( random.nextGaussian() * statisticalPositionSigma ) * ( Math.random() > 0.5 ? 1 : -1 );
                double theta = Math.random() * Math.PI * 2;
                double dx = d * Math.cos( theta );
                double dy = d * Math.sin( theta );
                setLocation( dx, dy );
                this.setPotential( nucleus.getPotentialProfile().getWellPotential() );
            }
            else {
                // Accelerate the alpha particle away from the nucleus, with a force
                // proportional to its height on the profile
                PotentialProfile profile = nucleus.getPotentialProfile();
                double d = this.getPosition().distance( nucleus.getPosition() );

                double force = Math.abs( profile.getHillY( -d ) ) * forceScale;
                force = Double.isNaN( force ) ? 0 : force;
                force = -profile.getDyDx( -d ) * forceScale;
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
                double potential = Double.isNaN( -profile.getHillY( -d ) ) ? 0 : -profile.getHillY( -d );
                this.setPotential( potential );
            }
        }
    }

    //
    // Interfaces implemented
    //
    public Point2D getCM() {
        return getPosition();
    }

    public double getMomentOfInertia() {
        return Double.NaN;
    }
}
