/**
 * Class: DecayNucleus
 * Class: edu.colorado.phet.nuclearphysics.model
 * User: Ron LeMaster
 * Date: Mar 2, 2004
 * Time: 8:14:04 AM
 */
package edu.colorado.phet.nuclearphysics.model;

import java.awt.geom.Point2D;

public class DecayNucleus extends Nucleus implements Runnable {

    public DecayNucleus( Point2D.Double position, int numProtons, int numNeutrons, PotentialProfile potentialProfile ) {
        super( position, numProtons, numNeutrons, potentialProfile );
        Thread decayThread = new Thread( this );
        decayThread.start();
    }

    public void stepInTime( double dt ) {
    }

    //
    // Interfaces implemented
    //
    public void run() {
        this.setPotentialEnergy( getPotentialProfile().getWellPotential() );
        while( this.getPotentialEnergy() > 0 ) {
            try {
                Thread.sleep( sleepTime );
                this.setPotentialEnergy( this.getPotentialEnergy() - dx );
            }
            catch( Exception e ) {
                e.printStackTrace();
            }
        }
        this.setPotentialEnergy( 0.1 );
    }

    //
    // Static fields and methods
    //
    private static long sleepTime = 50;
    private static int dx = 20;

    //
    // Inner classes
    //
}
