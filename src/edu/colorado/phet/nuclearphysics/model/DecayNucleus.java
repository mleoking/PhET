/**
 * Class: DecayNucleus
 * Class: edu.colorado.phet.nuclearphysics.model
 * User: Ron LeMaster
 * Date: Mar 2, 2004
 * Time: 8:14:04 AM
 */
package edu.colorado.phet.nuclearphysics.model;

import java.awt.geom.Point2D;

public class DecayNucleus extends Nucleus {

    private double dx = 0.1;

    public DecayNucleus( Point2D.Double position, int numProtons, int numNeutrons, PotentialProfile potentialProfile ) {
        super( position, numProtons, numNeutrons, potentialProfile );
    }

    public DecayNucleus( Nucleus nucleus ) {
        super( nucleus.getLocation(), nucleus.getNumProtons(), nucleus.getNumNeutrons(), nucleus.getPotentialProfile() );
//        Thread decayThread = new Thread( this );
//        decayThread.start();
    }

    public void stepInTime( double dt ) {
        this.setPotentialEnergy( Math.max( this.getPotentialEnergy() - dx, 0 ) );
        dx += .5;
//        this.setVelocity( this.getVelocity().multiply( 5 ) );
        super.stepInTime( dt );
    }

    //
    // Interfaces implemented
    //
//    public void run() {
//        this.setPotentialEnergy( getPotentialProfile().getWellPotential() );
//        this.setVelocity( 0.01f, 0f );
//        while( this.getPotentialEnergy() > 0 ) {
//            try {
//                Thread.sleep( sleepTime );
//                this.setPotentialEnergy( this.getPotentialEnergy() - dx );
//                this.setVelocity( this.getVelocity().multiply( 5 ) );
//            }
//            catch( Exception e ) {
//                e.printStackTrace();
//            }
//        }
//        this.setPotentialEnergy( 0.1 );
//    }

    //
    // Static fields and methods
    //
    private static long sleepTime = 50;

    //
    // Inner classes
    //
}
