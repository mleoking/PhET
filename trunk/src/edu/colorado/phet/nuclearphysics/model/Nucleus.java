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

public class Nucleus extends Body {

    private int numProtons;
    private int numNeutrons;
    private double radius;
    private double potentialEnergy;
    private PotentialProfile potentialProfile;

    public Nucleus( Point2D.Double position, int numProtons, int numNeutrons,
                    PotentialProfile potentialProfile ) {
        super( position, new Vector2D(), new Vector2D(), 0, 0 );
        this.setLocation( position.getX(), position.getY() );
        this.numProtons = numProtons;
        this.numNeutrons = numNeutrons;
        this.potentialProfile = new PotentialProfile( this );
//        this.potentialProfile = potentialProfile;
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

    public void setNumProtons( int numProtons ) {
        this.numProtons = numProtons;
    }

    public void setNumNeutrons( int numNeutrons ) {
        this.numNeutrons = numNeutrons;
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
}
