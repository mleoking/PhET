/**
 * Class: PotentialProfile
 * Package: edu.colorado.phet.nuclearphysics.model
 * Author: Another Guy
 * Date: Feb 26, 2004
 */
package edu.colorado.phet.nuclearphysics.model;

/**
 * This class represents the potential energy profile of a particular atom.
 * It's attributes include the width of the profile, which corresponds to the
 * spatial distance from the well to the ground state energy outside the well.
 */
public class PotentialProfile {
    private double width;
    private double maxPotential;
    private double wellDepth;

    public PotentialProfile() {
    }

    public PotentialProfile( double width, double maxPotential, double wellDepth ) {
        this.width = width;
        this.maxPotential = maxPotential;
        this.wellDepth = wellDepth;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth( double width ) {
        this.width = width;
    }

    public double getMaxPotential() {
        return maxPotential;
    }

    public void setMaxPotential( double maxPotential ) {
        this.maxPotential = maxPotential;
    }

    public double getWellPotential() {
        return maxPotential - wellDepth;
    }

    public void setWellPotential( double wellPotential ) {
        this.wellDepth = maxPotential - wellPotential;
    }

    public void setWellDepth( double wellDepth ) {
        this.wellDepth = wellDepth;
    }
}
