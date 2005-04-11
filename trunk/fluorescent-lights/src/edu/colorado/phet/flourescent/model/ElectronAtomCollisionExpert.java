/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: 
 * Branch : $Name:  
 * Modified by : $Author: 
 * Revision : $Revision: 
 * Date modified : $Date: 
 */

package edu.colorado.phet.flourescent.model;

import edu.colorado.phet.collision.Collidable;
import edu.colorado.phet.collision.SphereSphereExpert;
import edu.colorado.phet.collision.CollisionExpert;
import edu.colorado.phet.collision.CollisionUtil;
import edu.colorado.phet.lasers.model.atom.Atom;
import edu.colorado.phet.lasers.model.photon.Photon;
import edu.colorado.phet.lasers.controller.LaserConfig;
import edu.colorado.phet.common.math.Vector2D;

import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D;
import java.awt.*;
import java.util.Map;
import java.util.HashMap;

/**
 *
 */
public class ElectronAtomCollisionExpert implements CollisionExpert {
    private Object[] bodies = new Object[2];
    private Map classifiedBodies = new HashMap();
    private Photon utilPhoton = Photon.create( new Point2D.Double( ), new Vector2D.Double( ) );

    /**
     *
     */
    public ElectronAtomCollisionExpert() {
        classifiedBodies.put( Electron.class, null );
        classifiedBodies.put( DischargeLampAtom.class, null );
    }

    /**
     *
     * @param body1
     * @param body2
     * @return
     */
    public boolean detectAndDoCollision( Collidable body1, Collidable body2 ) {
        if( CollisionUtil.areConformantToClasses( body1, body2, Electron.class, DischargeLampAtom.class ) ) {
            bodies[0] = body1;
            bodies[1] = body2;
            CollisionUtil.classifyBodies( bodies, classifiedBodies );
            DischargeLampAtom atom = (DischargeLampAtom)classifiedBodies.get( DischargeLampAtom.class );
            Electron electron = (Electron)classifiedBodies.get( Electron.class );
            if( atom != null && electron != null ) {
                double prevDistSq = electron.getPositionPrev().distanceSq( atom.getPosition() );
                double distSq = electron.getPosition().distanceSq( atom.getPosition() );
                double atomRadSq = atom.getRadius() * atom.getRadius();
                if( distSq <= atomRadSq && prevDistSq > atomRadSq ) {
                    atom.collideWithElectron( electron );
//                    utilPhoton.setWavelength( Photon.RED );
//                    atom.collideWithPhoton( utilPhoton );
                }
            }
        }
        return false;
    }
}