/**
 * Class: Nucleus
 * Package: edu.colorado.phet.nuclearphysics.model
 * Author: Another Guy
 * Date: Feb 26, 2004
 */
package edu.colorado.phet.nuclearphysics.model;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.coreadditions.Body;

import java.awt.geom.Point2D;
import java.util.Random;

public class Nucleus extends Body {
    private int numProtons;
    private int numNeutrons;
    private double radius;
    private double potentialEnergy;
    private PotentialProfile potentialProfile;
    private Point2D.Double statisticalLocationOffset = new Point2D.Double();

    public Nucleus( Point2D.Double position, int numProtons, int numNeutrons,
                    PotentialProfile potentialProfile ) {
        super( position, new Vector2D(), new Vector2D(), 0, 0 );
        this.setLocation( position.getX(), position.getY() );
        this.numProtons = numProtons;
        this.numNeutrons = numNeutrons;
        this.potentialProfile = potentialProfile;
        this.potentialEnergy = potentialProfile.getWellPotential();

        int numParticles = getNumNeutrons() + getNumProtons();
        double particleArea = ( Math.PI * NuclearParticle.RADIUS * NuclearParticle.RADIUS ) * numParticles;
        radius = Math.sqrt( particleArea / Math.PI ) / 2;
    }

    public Point2D.Double getCM() {
        return getLocation();
    }

    public double getMomentOfInertia() {
        return 0;
    }

    public double getRadius() {
        return radius;
    }

    public int getNumProtons() {
        return numProtons;
    }

    public int getNumNeutrons() {
        return numNeutrons;
    }

    public PotentialProfile getPotentialProfile() {
        return potentialProfile;
    }

    public double getPotentialEnergy() {
        return this.potentialEnergy;
    }

    public void setPotentialEnergy( double energy ) {
        this.potentialEnergy = energy;
    }

    public void stepInTime( double dt ) {
        super.stepInTime( dt );
        double d = ( random.nextGaussian() * potentialProfile.getAlphaDecayX() / 3 ) * ( Math.random() > 0.5 ? 1 : -1 );
        double theta = Math.random() * Math.PI * 2;
        double dx = d * Math.cos( theta );
        double dy = d * Math.sin( theta );
//        statisticalLocationOffset.setLocation( statisticalLocationOffset.getX() - 2 , 0 );
        statisticalLocationOffset.setLocation( dx, dy );
    }

    public Point2D.Double getStatisticalLocationOffset() {
        return statisticalLocationOffset;
    }


    //
    // Statics
    //
    private static double maxStatisticalLocationOffset = 200;
    private static Random random = new Random();
}
