/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.lasers.model.collision;

import edu.colorado.phet.collision.Collision;
import edu.colorado.phet.collision.SphereWallCollision;
import edu.colorado.phet.collision.Wall;
import edu.colorado.phet.common.model.Particle;
import edu.colorado.phet.lasers.model.atom.Atom;

public class AtomWallCollision extends SphereWallCollision {

    private Atom atom;

    /**
     * Provided so class can register a prototype with the CollisionFactory
     */
    private AtomWallCollision() {
        // NOP
    }

    public AtomWallCollision( Atom atom, Wall wall ) {
    }

    /**
     * @param particleA
     * @param particleB
     * @return
     */
    public Collision createIfApplicable( Particle particleA, Particle particleB ) {
        Collision result = null;
        if( particleA instanceof Atom && particleB instanceof Wall ) {
            result = new AtomWallCollision( (Atom)particleA, (Wall)particleB );
        }
        else if( particleB instanceof Wall && particleB instanceof Atom ) {
            result = new AtomWallCollision( (Atom)particleB, (Wall)particleA );
        }
        return result;
    }
}
