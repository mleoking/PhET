/**
 * Class: PhotonAtomCollision
 * Package: edu.colorado.phet.lasers.physics.collision
 * Author: Another Guy
 * Date: Mar 21, 2003
 */
package edu.colorado.phet.lasers.physics.collision;

import edu.colorado.phet.lasers.physics.atom.Atom;
import edu.colorado.phet.lasers.physics.photon.Photon;
import edu.colorado.phet.physics.collision.Collision;
import edu.colorado.phet.physics.collision.CollisionFactory;
import edu.colorado.phet.physics.body.Particle;

public class PhotonAtomCollision implements Collision {

    private Photon photon;
    private Atom atom;

    /**
     * Provided so class can register a prototype with the CollisionFactory
     */
    private PhotonAtomCollision() {
        //NOP
    }

    public PhotonAtomCollision( Photon photon, Atom atom ) {
        this.photon = photon;
        this.atom = atom;
    }

    //
    // Implementation of abstract methods
    //
    public void collide() {
        if( !photon.hasCollidedWithAtom( atom ) ) {
            atom.collideWithPhoton( photon );
            photon.collideWithAtom( atom );
        }
    }


    /**
     *
     * @param particleA
     * @param particleB
     * @return
     */
    public Collision createIfApplicable( Particle particleA, Particle particleB ) {
        Collision result = null;
        if( particleA instanceof Photon && particleB instanceof Atom ) {
            result = new PhotonAtomCollision( (Photon)particleA, (Atom)particleB );
        }
        else if( particleA instanceof Atom && particleB instanceof Photon ) {
            result = new PhotonAtomCollision( (Photon)particleB, (Atom)particleA );
        }
        return result;
    }

    //
    // Static fields and methods
    //
    static public void register() {
        CollisionFactory.addPrototype( new PhotonAtomCollision() );
    }
}
