/**
 * Class: PhotonAtomCollisonExpert
 * Class: edu.colorado.phet.lasers.model.collision
 * User: Ron LeMaster
 * Date: Oct 23, 2004
 * Time: 7:34:16 AM
 * Latest Change:
 *      $Author$
 *      $Date$
 *      $Name$
 *      $Revision$
 */
package edu.colorado.phet.lasers.model.collision;

import edu.colorado.phet.collision.Collidable;
import edu.colorado.phet.collision.CollisionExpert;
import edu.colorado.phet.collision.CollisionUtil;
import edu.colorado.phet.lasers.model.atom.Atom;
import edu.colorado.phet.lasers.model.photon.Photon;

import java.util.HashMap;
import java.util.Map;

public class PhotonAtomCollisonExpert implements CollisionExpert {
    private Object[] bodies = new Object[2];
    private Map classifiedBodies = new HashMap();

    public PhotonAtomCollisonExpert() {
        classifiedBodies.put( Photon.class, null );
        classifiedBodies.put( Atom.class, null );
    }

    public boolean detectAndDoCollision( Collidable body1, Collidable body2 ) {
        if( CollisionUtil.areConformantToClasses( body1, body2, Photon.class, Atom.class ) ) {
            bodies[0] = body1;
            bodies[1] = body2;
            CollisionUtil.classifyBodies( bodies, classifiedBodies );
            Atom atom = (Atom)classifiedBodies.get( Atom.class );
            Photon photon = (Photon)classifiedBodies.get( Photon.class );
            if( atom != null && photon != null ) {
                if( photon.getPosition().distanceSq( atom.getPosition() ) < atom.getRadius() * atom.getRadius() ) {
                    atom.collideWithPhoton( photon );
                }
            }
        }

        return false;
    }
}
