/**
 * Class: PhotonAtomCollision
 * Package: edu.colorado.phet.lasers.model.collision
 * Author: Another Guy
 * Date: Mar 21, 2003
 */
package edu.colorado.phet.lasers.model.collision;

import edu.colorado.phet.lasers.model.atom.Atom;
import edu.colorado.phet.lasers.model.photon.Photon;
import edu.colorado.phet.collision.Collision;
import edu.colorado.phet.common.model.Particle;

public class PhotonAtomCollision implements Collision {

    private Photon photon;
    private Atom atom;


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
//    static public void register() {
//        CollisionFactory.addPrototype( new PhotonAtomCollision() );
//    }
}
