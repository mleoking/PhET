/**
 * Class: AtomWallCollision
 * Class: ${PACKAGE}
 * User: Ron LeMaster
 * Date: Mar 28, 2003
 * Time: 11:01:44 AM
 */
package edu.colorado.phet.lasers.physics.collision;

import edu.colorado.phet.physics.collision.Wall;
import edu.colorado.phet.lasers.physics.atom.Atom;
import edu.colorado.phet.physics.Vector2D;
import edu.colorado.phet.physics.body.Particle;
import edu.colorado.phet.physics.collision.HardsphereCollision;
import edu.colorado.phet.physics.collision.CollisionFactory;
import edu.colorado.phet.physics.collision.Collision;
import edu.colorado.phet.physics.collision.SphereWallCollision;
import edu.colorado.phet.util.MathUtil;

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
