/**
 * Class: AtomWallCollision
 * Class: ${PACKAGE}
 * User: Ron LeMaster
 * Date: Mar 28, 2003
 * Time: 11:01:44 AM
 */
package edu.colorado.phet.lasers.physics.collision;

import edu.colorado.phet.lasers.physics.atom.Atom;
import edu.colorado.phet.collision.Collision;
import edu.colorado.phet.collision.Wall;
import edu.colorado.phet.common.model.Particle;

import java.awt.geom.Point2D;

public class AtomWallCollision extends SphereWallCollision {

    private Atom atom;

    /**
     * Provided so class can register a prototype with the CollisionFactory
     */
    private AtomWallCollision() {
        // NOP
    }

    public AtomWallCollision( Atom atom, Wall wall ) {
        super( atom, wall );
    }

    /**
     *
     * @param particleA
     * @param particleB
     * @return
     */
    public Collision createIfApplicable( Particle particleA, Particle particleB ) {
        Collision result = null;
        if( particleA instanceof Atom && particleB instanceof Wall ) {
            result = new AtomWallCollision( (Atom)particleA, (Wall)particleB );
        }
        else if ( particleB instanceof Wall && particleB instanceof Atom ) {
            result = new AtomWallCollision( (Atom)particleB, (Wall)particleA );
        }
        return result;
    }

    //
    // Static fields and methods
    //
    static public void register() {
        CollisionFactory.addPrototype( new AtomWallCollision() );
    }
}
