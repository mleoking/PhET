/**
 * Class: Nucleus
 * Package: edu.colorado.phet.nuclearphysics.model
 * Author: Another Guy
 * Date: Feb 26, 2004
 */
package edu.colorado.phet.nuclearphysics.model;

public class Nucleus {
    private int numProtons;
    private int numNeutrons;
    private PotentialProfile potentialProfile;

    public Nucleus( int numProtons, int numNeutrons, PotentialProfile potentialProfile ) {
        this.numProtons = numProtons;
        this.numNeutrons = numNeutrons;
        this.potentialProfile = potentialProfile;
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
}
