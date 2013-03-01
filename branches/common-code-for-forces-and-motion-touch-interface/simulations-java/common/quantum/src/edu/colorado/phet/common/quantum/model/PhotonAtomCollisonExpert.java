// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.quantum.model;

import java.util.HashMap;
import java.util.Map;

import edu.colorado.phet.common.collision.Collidable;
import edu.colorado.phet.common.collision.CollisionExpert;
import edu.colorado.phet.common.collision.CollisionUtil;

public class PhotonAtomCollisonExpert implements CollisionExpert {
    private Object[] bodies = new Object[2];
    private Map classifiedBodies = new HashMap();

    public PhotonAtomCollisonExpert() {
        classifiedBodies.put( Photon.class, null );
        classifiedBodies.put( Atom.class, null );
    }

    public boolean detectAndDoCollision( Collidable body1, Collidable body2 ) {
        if ( CollisionUtil.areConformantToClasses( body1, body2, Photon.class, Atom.class ) ) {
            bodies[0] = body1;
            bodies[1] = body2;
            CollisionUtil.classifyBodies( bodies, classifiedBodies );
            Atom atom = (Atom) classifiedBodies.get( Atom.class );
            Photon photon = (Photon) classifiedBodies.get( Photon.class );
            if ( atom != null && photon != null ) {
                if ( photon.getPosition().distanceSq( atom.getPosition() ) < atom.getRadius() * atom.getRadius() ) {
                    atom.collideWithPhoton( photon );
                }
            }
        }
        return false;
    }
}
