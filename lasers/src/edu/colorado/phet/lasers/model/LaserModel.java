/**
 * Class: LaserModel
 * Package: edu.colorado.phet.lasers.model
 * Author: Another Guy
 * Date: Mar 21, 2003
 * Latest Change:
 *      $Author$
 *      $Date$
 *      $Name$
 *      $Revision$
 */
package edu.colorado.phet.lasers.model;

import edu.colorado.phet.collision.*;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.lasers.controller.LaserConfig;
import edu.colorado.phet.lasers.model.atom.Atom;
import edu.colorado.phet.lasers.model.collision.AtomWallCollision;
import edu.colorado.phet.lasers.model.collision.PhotonAtomCollision;
import edu.colorado.phet.lasers.model.collision.PhotonMirrorCollision;
import edu.colorado.phet.lasers.model.collision.PhotonAtomCollisonExpert;
import edu.colorado.phet.lasers.model.photon.CollimatedBeam;
import edu.colorado.phet.lasers.model.photon.Photon;
import edu.colorado.phet.mechanics.Body;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;

public class LaserModel extends BaseModel {
//public class LaserModel extends PhysicalSystem {

    static public Point2D ORIGIN = new Point2D.Double( 100, 300 );
    static private int width = 800;
    static private int height = 800;
    static private int minX = (int)LaserConfig.ORIGIN.getX() - 50;
    static private int minY = (int)LaserConfig.ORIGIN.getY() - height / 2;
    static private Rectangle2D boundingRectangle = new Rectangle2D.Double( minX,
                                                                           minY,
                                                                           width,
                                                                           height );

    private CollimatedBeam stimulatingBeam;
    private CollimatedBeam pumpingBeam;
    private ResonatingCavity resonatingCavity;
    private List bodies = new LinkedList();

    /**
     *
     */
    public LaserModel() {
//        super( LaserConfig.instance() );
//        this.addLaw( CollisionLaw.instance() );
//
//        // Set up collision classes
//        new SphereSphereContactDetector();
//        new SphereWallContactDetector();
//
//        AtomAtomCollision.register();
//        PhotonMirrorCollision.register();
//        AtomWallCollision.register();
//        PhotonAtomCollision.register();

        final CollisionMechanism collisionMechanism = new CollisionMechanism();
        collisionMechanism.addCollisionExpert( new SphereSphereExpert() );
        collisionMechanism.addCollisionExpert( new PhotonAtomCollisonExpert() );
        collisionMechanism.addCollisionExpert( new SphereBoxExpert() );
        this.addModelElement( new ModelElement() {
            public void stepInTime( double dt ) {
                collisionMechanism.doIt( bodies );
            }
        } );
    }

    public void addModelElement( ModelElement modelElement ) {
        super.addModelElement( modelElement );
        if( modelElement instanceof Body ) {
            bodies.add( modelElement );
        }
        if( modelElement instanceof ResonatingCavity ) {
            this.resonatingCavity = (ResonatingCavity)modelElement;
        }
    }

    public void removeModelElement( ModelElement modelElement ) {
        super.removeModelElement( modelElement );
        if( modelElement instanceof Body ) {
            bodies.remove( modelElement );
        }
    }

    public void stepInTime( double dt ) {
        super.stepInTime( dt );

        // Check to see if any photons need to be taken out of the system
//        List bodies = this.getBodies();
        for( int i = 0; i < bodies.size(); i++ ) {
            Object obj = bodies.get( i );
            if( obj instanceof Photon ) {
                Photon photon = (Photon)obj;
                Point2D position = photon.getPosition();
                if( !boundingRectangle.contains( position.getX(), position.getY() ) ) {
                    removeModelElement( photon );
//                    RemoveParticleCmd cmd = new RemoveParticleCmd( photon );
//                    this.addPrepCmd( cmd );
                }
            }
        }
    }

    public ResonatingCavity getResonatingCavity() {
        return resonatingCavity;
    }

    public void setResonatingCavity( ResonatingCavity resonatingCavity ) {
        this.resonatingCavity = resonatingCavity;
    }

    /**
     * @return
     */
    public CollimatedBeam getStimulatingBeam() {
        return stimulatingBeam;
    }

    /**
     * @param stimulatingBeam
     */
    public void setStimulatingBeam( CollimatedBeam stimulatingBeam ) {
        if( stimulatingBeam != null ) {
            removeModelElement( stimulatingBeam );
//            this.removeBody( stimulatingBeam );
        }
        addModelElement( stimulatingBeam );
//        addBody( stimulatingBeam );
        this.stimulatingBeam = stimulatingBeam;
    }


    /**
     * @return
     */
    public CollimatedBeam getPumpingBeam() {
        return pumpingBeam;
    }

    /**
     * @param pumpingBeam
     */
    public void setPumpingBeam( CollimatedBeam pumpingBeam ) {
        if( pumpingBeam != null ) {
            removeModelElement( pumpingBeam );
//            this.removeBody( pumpingBeam );
        }
        addModelElement( pumpingBeam );
//        addBody( pumpingBeam );
        this.pumpingBeam = pumpingBeam;
    }

    /**
     * @param time
     */
    public void setHighEnergySpontaneousEmissionTime( double time ) {
        Atom.setHighEnergySpontaneousEmissionTime( time );
//        Atom.setHighEnergySpontaneousEmissionTime( 1000 );
    }

    /**
     * @param time
     */
    public void setMiddleEnergySpontaneousEmissionTime( double time ) {
        Atom.setMiddleEnergySpontaneousEmissionTime( time );
    }

    /**
     * @return
     */
    public int getNumGroundStateAtoms() {
        return Atom.getNumGroundStateAtoms();
    }

    /**
     * @return
     */
    public int getNumMiddleStateAtoms() {
        return Atom.getNumMiddleStateAtoms();
    }

    /**
     * @return
     */
    public int getNumHighStateAtoms() {
        return Atom.getNumHighStateAtoms();
    }

    /***
     *
     */
    public void removeAtoms() {
//        List bodies = getBodies();
        for( int i = 0; i < bodies.size(); i++ ) {
            ModelElement modelElement = (ModelElement)bodies.get( i );
//            PhysicalEntity modelElement = (PhysicalEntity)bodies.get( i );
            if( modelElement instanceof Atom ) {
                removeModelElement( modelElement );
//                RemoveParticleCmd cmd = new RemoveParticleCmd( (Atom)modelElement );
//                this.addPrepCmd( cmd );
            }
        }
    }

    public void removePhotons() {
//        List bodies = getBodies();
        for( int i = 0; i < bodies.size(); i++ ) {
            ModelElement modelElement = (ModelElement)bodies.get( i );
//            PhysicalEntity modelElement = (PhysicalEntity)bodies.get( i );
            if( modelElement instanceof Photon ) {
                removeModelElement( modelElement );
//                RemoveParticleCmd cmd = new RemoveParticleCmd( (Photon)modelElement );
//                this.addPrepCmd( cmd );
            }
        }
    }


    private class CollisionMechanism {
        private ArrayList collisionExperts = new ArrayList();

        void addCollisionExpert( CollisionExpert expert ) {
            collisionExperts.add( expert );
        }

        void removeCollisionExpert( CollisionExpert expert ) {
            collisionExperts.remove( expert );
        }

        void doIt( List collidables ) {
            for( int i = 0; i < collidables.size() - 1; i++ ) {
                Collidable collidable1 = (Collidable)collidables.get( i );
                for( int j = i + 0; j < collidables.size(); j++ ) {
                    Collidable collidable2 = (Collidable)collidables.get( j );
                    for( int k = 0; k < collisionExperts.size(); k++ ) {
                        CollisionExpert collisionExpert = (CollisionExpert)collisionExperts.get( k );
                        collisionExpert.detectAndDoCollision( collidable1, collidable2 );
                    }
                }

            }
        }
    }
}
