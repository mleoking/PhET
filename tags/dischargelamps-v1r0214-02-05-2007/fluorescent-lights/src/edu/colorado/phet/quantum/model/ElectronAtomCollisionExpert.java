/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: 
 * Branch : $Name:  
 * Modified by : $Author: 
 * Revision : $Revision: 
 * Date modified : $Date: 
 */

package edu.colorado.phet.quantum.model;

import edu.colorado.phet.collision.Collidable;
import edu.colorado.phet.collision.CollisionExpert;
import edu.colorado.phet.collision.CollisionUtil;
import edu.colorado.phet.dischargelamps.model.DischargeLampAtom;

import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class ElectronAtomCollisionExpert implements CollisionExpert {
    private Object[] bodies = new Object[2];
    private Map classifiedBodies = new HashMap();
    private Class[] classes = new Class[]{Electron.class, DischargeLampAtom.class};
    private Ellipse2D atomArea = new Ellipse2D.Double();
    private Rectangle2D electronPath = new Rectangle2D.Double();

    /**
     *
     */
    public ElectronAtomCollisionExpert() {
        classifiedBodies.put( Electron.class, null );
        classifiedBodies.put( DischargeLampAtom.class, null );
    }

    /**
     * @param body1
     * @param body2
     * @return
     */
    public boolean detectAndDoCollision( Collidable body1, Collidable body2 ) {
        bodies[0] = body1;
        bodies[1] = body2;
        CollisionUtil.classifyBodies( bodies, classes, classifiedBodies );
        DischargeLampAtom atom = (DischargeLampAtom)classifiedBodies.get( DischargeLampAtom.class );
        Electron electron = (Electron)classifiedBodies.get( Electron.class );
        if( atom != null && electron != null ) {

            // Do simple check
            double prevDistSq = electron.getPositionPrev().distanceSq( atom.getPosition() );
            double distSq = electron.getPosition().distanceSq( atom.getPosition() );
            double atomRadSq = ( atom.getRadius() + electron.getRadius() ) * ( atom.getRadius() + electron.getRadius() );
            if( distSq <= atomRadSq && prevDistSq > atomRadSq ) {
                atom.collideWithElectron( electron );
                return false;
            }

            // Do more complicated check that will detect if the electron passed through the atom during
            // the time step, but isn't currently within the atom
            atomArea.setFrame( atom.getPosition().getX() - atom.getBaseRadius(),
                               atom.getPosition().getY() - atom.getBaseRadius(),
                               atom.getBaseRadius() * 2,
                               atom.getBaseRadius() * 2 );
            electronPath.setRect( electron.getPositionPrev().getX(),
                                  electron.getPositionPrev().getY(),
                                  electron.getPosition().getX() - electron.getPositionPrev().getX(),
                                  1 );
            if( atomArea.intersects( electronPath ) ) {
                atom.collideWithElectron( electron );
                return false;
            }
        }
        return false;
    }
}