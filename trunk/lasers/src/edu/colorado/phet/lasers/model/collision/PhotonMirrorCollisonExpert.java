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
import edu.colorado.phet.lasers.model.mirror.Mirror;
import edu.colorado.phet.lasers.model.photon.Photon;

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;

public class PhotonMirrorCollisonExpert implements CollisionExpert {
    private Class[] classes = new Class[]{Photon.class, Mirror.class};
    private Object[] bodies = new Object[2];
    private Map classifiedBodies = new HashMap();

    public PhotonMirrorCollisonExpert() {
        classifiedBodies.put( classes[0], null );
        classifiedBodies.put( classes[1], null );
    }

    public boolean detectAndDoCollision( Collidable body1, Collidable body2 ) {
        if( CollisionUtil.areConformantToClasses( body1, body2, classes[0], classes[1] ) ) {
            bodies[0] = body1;
            bodies[1] = body2;
            CollisionUtil.classifyBodies( bodies, classifiedBodies );
            Mirror mirror = (Mirror)classifiedBodies.get( Mirror.class );
            Photon photon = (Photon)classifiedBodies.get( Photon.class );
            if( mirror != null && photon != null ) {
                Point2D photonPositionPrev = photon.getPositionPrev();
                Point2D photonPositionCurr = photon.getPosition();

                // Note: This test is very simple-minded. It assumes a vertical mirror that
                // is infinitely tall.
                if( mirror.reflects( photon )
                    && ( photonPositionCurr.getX() - mirror.getPosition().getX() )
                       * ( photonPositionPrev.getX() - mirror.getPosition().getX() ) <= 0 ) {
                    doCollision( photon, mirror );
                }
            }
        }
        return false;
    }

    private void doCollision( Photon photon, Mirror mirror ) {
        double dx = photon.getPosition().getX() - mirror.getPosition().getX();
        photon.setPosition( mirror.getPosition().getX() - dx, photon.getPosition().getY() );
        photon.setVelocity( -photon.getVelocity().getX(), photon.getVelocity().getY() );
    }
}
