/**
 * Class: Nucleus
 * Package: edu.colorado.phet.nuclearphysics.model
 * Author: Another Guy
 * Date: Feb 26, 2004
 */
package edu.colorado.phet.nuclearphysics.model;

import java.awt.geom.Point2D;

public class Nucleus {
    private int numProtons;
    private int numNeutrons;
    private PotentialProfile potentialProfile;
    private Point2D.Double position;

    public Nucleus( Point2D.Double position, int numProtons, int numNeutrons,
                    PotentialProfile potentialProfile ) {
        this.position = position;
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

    public Point2D.Double getPosition() {
        return position;
    }
}
