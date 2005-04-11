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

    public ElectronAtomCollisionExpert() {
        classifiedBodies.put( Electron.class, null );
        classifiedBodies.put( Atom.class, null );
    }

    public boolean detectAndDoCollision( Collidable body1, Collidable body2 ) {
        if( CollisionUtil.areConformantToClasses( body1, body2, Electron.class, Atom.class ) ) {
            bodies[0] = body1;
            bodies[1] = body2;
            CollisionUtil.classifyBodies( bodies, classifiedBodies );
            Atom atom = (Atom)classifiedBodies.get( Atom.class );
            Electron electron = (Electron)classifiedBodies.get( Electron.class );
            if( atom != null && electron != null ) {
                if( electron.getPosition().distanceSq( atom.getPosition() ) < atom.getRadius() * atom.getRadius() ) {
                    utilPhoton.setWavelength( Photon.RED );
                    atom.collideWithPhoton( utilPhoton );
                }
            }
        }

        return false;
    }
}

/*
public class ElectronAtomCollisionExpert extends SphereSphereExpert {
    private Ellipse2D utilEllipse = new Ellipse2D.Double( );
    private Rectangle2D utilRect = new Rectangle2D.Double( );

    public boolean detectAndDoCollision( Collidable bodyA, Collidable bodyB ) {
        Atom atom = null;
        Electron electron = null;
        if( bodyA instanceof Atom && bodyB instanceof Electron ) {
            atom = (Atom)bodyA;
            electron = (Electron)bodyB;
        }
        if( bodyB instanceof Atom && bodyA instanceof Electron ) {
            atom = (Atom)bodyB;
            electron = (Electron)bodyA;
        }
        if( atom != null && electron != null ) {
            utilEllipse.setFrameFromCenter( atom.getPosition().getX(), atom.getPosition().getY(),
                                            atom.getPosition().getX() + Atom.getS_radius(),
                                            atom.getPosition().getY() + Atom.getS_radius() );
            utilRect.set
            return super.detectAndDoCollision( bodyA, bodyB );
        }
        return false;
    }
}
*/