/**
 * Class: LaserApplicationTest
 * Package: edu.colorado.phet.lasers.controller
 * Author: Another Guy
 * Date: Mar 21, 2003
 */
package edu.colorado.phet.lasers.controller;

public class LaserApplicationTest {

    public static void main( String[] args ) {
        LaserApplication application = new LaserApplication();

        /*
        Atom atom = new Atom();
        atom.setPosition( 400, 400 );
        new AddAtomCmd( atom ).doIt( null );

        Photon photon = null;
        photon = new Photon();
        photon.setPosition( 350, 400 );
        new AddPhotonCmd( photon ).doIt( null );

        CollimatedBeam beam = new CollimatedBeam();
        beam.setPosition( 100, 200 );
        beam.setHeight( 40 );
        application.getPhysicalSystem().addBody( beam );
        */

//        photon = new Photon();
//        photon.setPosition( 300, 400 );
//        new AddPhotonCmd( photon ).doIt( null );
//
//        photon = new Photon();
//        photon.setPosition( 250, 400 );
//        new AddPhotonCmd( photon ).doIt( null );

        application.start();
    }
}
