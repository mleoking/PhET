/**
 * Class: AtomWallCollision
 * Class: ${PACKAGE}
 * User: Ron LeMaster
 * Date: Mar 28, 2003
 * Time: 11:01:44 AM
 */
package edu.colorado.phet.lasers.physics.collision;

import edu.colorado.phet.lasers.physics.mirror.PartialMirror;
import edu.colorado.phet.lasers.physics.mirror.Mirror;
import edu.colorado.phet.lasers.physics.photon.Photon;
import edu.colorado.phet.collision.Collision;
import edu.colorado.phet.common.model.Particle;

import java.awt.geom.Point2D;

public class PhotonMirrorCollision extends SphereWallCollision {

    private Photon photon;
    private Mirror mirror;

    /**
     * Provided so class can register a prototype with the CollisionFactory
     */
    private PhotonMirrorCollision() {
        //NOP
    }

    /**
     *
     * @param photon
     * @param mirror
     */
    public PhotonMirrorCollision( Photon photon, PartialMirror mirror ) {
        super( photon, mirror );
        this.photon = photon;
        this.mirror = mirror;
    }

    /**
     *
     * @param particleA
     * @param particleB
     * @return
     */
    public Collision createIfApplicable( Particle particleA, Particle particleB ) {
        Collision result = null;
        if( particleA instanceof Photon && particleB instanceof Mirror ) {
            result = new PhotonMirrorCollision( (Photon)particleA, (PartialMirror)particleB );
        }
        else if ( particleA instanceof Mirror && particleB instanceof Photon ) {
            result = new PhotonMirrorCollision( (Photon)particleB, (PartialMirror)particleA );
        }
        return result;
    }

    /**
     *
     */
    public void collide() {
        if( mirror.reflects( photon ) ) {
            super.collide();
        }
    }


    //
    // Static fields and methods
    //
    static public void register() {
        CollisionFactory.addPrototype( new PhotonMirrorCollision() );
    }
}
