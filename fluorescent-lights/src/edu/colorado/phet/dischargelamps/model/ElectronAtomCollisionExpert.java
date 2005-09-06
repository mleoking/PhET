/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: 
 * Branch : $Name:  
 * Modified by : $Author: 
 * Revision : $Revision: 
 * Date modified : $Date: 
 */

package edu.colorado.phet.dischargelamps.model;

import edu.colorado.phet.collision.Collidable;
import edu.colorado.phet.collision.CollisionExpert;
import edu.colorado.phet.collision.CollisionUtil;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class ElectronAtomCollisionExpert implements CollisionExpert {
    private Object[] bodies = new Object[2];
    private Map classifiedBodies = new HashMap();
    private Class[] classes = new Class[]{Electron.class, DischargeLampAtom.class};

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
            double prevDistSq = electron.getPositionPrev().distanceSq( atom.getPosition() );
            double distSq = electron.getPosition().distanceSq( atom.getPosition() );
            double atomRadSq = ( atom.getRadius() + electron.getRadius() ) * ( atom.getRadius() + electron.getRadius() );
            if( distSq <= atomRadSq && prevDistSq > atomRadSq ) {

                System.out.println( "electron.getEnergy() = " + electron.getEnergy() );
                atom.collideWithElectron( electron );
            }
        }
        return false;
    }
}