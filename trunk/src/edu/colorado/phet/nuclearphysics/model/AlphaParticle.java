/**
 * Class: AlphaParticle
 * Class: edu.colorado.phet.nuclearphysics.model
 * User: Ron LeMaster
 * Date: Mar 4, 2004
 * Time: 8:55:18 AM
 */
package edu.colorado.phet.nuclearphysics.model;

import java.awt.geom.Point2D;
import java.util.Random;

public class AlphaParticle extends Nucleus {

    //
    // Statics
    //
    private static Random random = new Random();

    public static final double RADIUS = NuclearParticle.RADIUS * 2;
    private double statisticalPositionSigma;


    //
    // Instance fields and methods
    //
    public boolean isInNucleus = true;

    public AlphaParticle( Point2D.Double position, double statisticalPositionSigma ) {
        super( position, 2, 2, new PotentialProfile( 1, 1, 1 ) );
        this.statisticalPositionSigma = statisticalPositionSigma;
    }

    public void stepInTime( double dt ) {
        super.stepInTime( dt );
        if( isInNucleus ) {
            double d = ( random.nextGaussian() * statisticalPositionSigma ) * ( Math.random() > 0.5 ? 1 : -1 );
            double theta = Math.random() * Math.PI * 2;
            double dx = d * Math.cos( theta );
            double dy = d * Math.sin( theta );
            setLocation( dx, dy );
        }
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
