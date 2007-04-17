/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.quantum.model;

import edu.colorado.phet.common.collision.Collision;
import edu.colorado.phet.common.collision.SphereWallCollision;
import edu.colorado.phet.common.collision.Wall;
import edu.colorado.phet.common.phetcommon.model.Particle;

public class AtomWallCollision extends SphereWallCollision {

    /**
     * @param particleA
     * @param particleB
     * @return
     */
    public Collision createIfApplicable( Particle particleA, Particle particleB ) {
        Collision result = null;
        if( particleA instanceof Atom && particleB instanceof Wall ) {
            result = new AtomWallCollision();
        }
        else if( particleB instanceof Wall && particleB instanceof Atom ) {
            result = new AtomWallCollision();
        }
        return result;
    }
}
