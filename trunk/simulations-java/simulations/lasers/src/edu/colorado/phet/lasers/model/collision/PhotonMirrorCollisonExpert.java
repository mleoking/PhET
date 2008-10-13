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

import java.awt.geom.Line2D;
import java.util.HashMap;
import java.util.Map;

import edu.colorado.phet.common.collision.Collidable;
import edu.colorado.phet.common.collision.CollisionExpert;
import edu.colorado.phet.common.collision.CollisionUtil;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.quantum.model.Photon;
import edu.colorado.phet.lasers.LasersConfig;
import edu.colorado.phet.lasers.model.mirror.Mirror;

public class PhotonMirrorCollisonExpert implements CollisionExpert {
    private Class[] classes = new Class[]{Photon.class, Mirror.class};
    private Object[] bodies = new Object[2];
    private Map classifiedBodies = new HashMap();

    public PhotonMirrorCollisonExpert() {
        classifiedBodies.put( classes[0], null );
        classifiedBodies.put( classes[1], null );
    }

    public boolean detectAndDoCollision( Collidable body1, Collidable body2 ) {
        if ( CollisionUtil.areConformantToClasses( body1, body2, classes[0], classes[1] ) ) {
            bodies[0] = body1;
            bodies[1] = body2;
            CollisionUtil.classifyBodies( bodies, classifiedBodies );
            Mirror mirror = (Mirror) classifiedBodies.get( Mirror.class );
            Photon photon = (Photon) classifiedBodies.get( Photon.class );
            if ( mirror != null && photon != null ) {
                boolean photonPathIntersectsMirror = Line2D.linesIntersect( photon.getPositionPrev().getX(),
                                                                            photon.getPositionPrev().getY(),
                                                                            photon.getPosition().getX(),
                                                                            photon.getPosition().getY(),
                                                                            mirror.getPosition().getX(),
                                                                            mirror.getBounds().getMinY(),
                                                                            mirror.getPosition().getX(),
                                                                            mirror.getBounds().getMaxY() );
                if ( photonPathIntersectsMirror && mirror.reflects( photon ) ) {
                    if ( mirror.reflects( photon ) ) {
                        doCollision( photon, mirror );
                    }
                }
            }
        }
        return false;
    }

    /**
     * This collision implementation "cheats" to make photons reflect horizontally if they
     * are close to horizontal
     *
     * @param photon
     * @param mirror
     */
    private void doCollision( Photon photon, Mirror mirror ) {
        double cheatFactor = LasersConfig.PHOTON_CHEAT_ANGLE;
        double dx = photon.getPosition().getX() - mirror.getPosition().getX();
        photon.setPosition( mirror.getPosition().getX() - dx, photon.getPosition().getY() );
        double vx = 0;
        double vy = 0;
        if ( Math.abs( photon.getVelocity().getAngle() % Math.PI ) < cheatFactor ) {
            vx = -photon.getVelocity().getMagnitude() * MathUtil.getSign( photon.getVelocity().getX() );
            vy = 0;
        }
        else {
            vx = -photon.getVelocity().getX();
            vy = photon.getVelocity().getY();
        }
        photon.setVelocity( vx, vy );
    }
}
