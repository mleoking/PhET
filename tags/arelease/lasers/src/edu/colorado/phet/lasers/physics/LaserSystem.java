/**
 * Class: LaserSystem
 * Package: edu.colorado.phet.lasers.physics
 * Author: Another Guy
 * Date: Mar 21, 2003
 */
package edu.colorado.phet.lasers.physics;

import edu.colorado.phet.lasers.controller.LaserConfig;
import edu.colorado.phet.lasers.physics.atom.Atom;
import edu.colorado.phet.lasers.physics.collision.AtomAtomCollision;
import edu.colorado.phet.lasers.physics.collision.AtomWallCollision;
import edu.colorado.phet.lasers.physics.collision.PhotonAtomCollision;
import edu.colorado.phet.lasers.physics.collision.PhotonMirrorCollision;
import edu.colorado.phet.lasers.physics.photon.CollimatedBeam;
import edu.colorado.phet.lasers.physics.photon.Photon;
import edu.colorado.phet.physics.PhysicalSystem;
import edu.colorado.phet.physics.Vector2D;
import edu.colorado.phet.physics.body.PhysicalEntity;
import edu.colorado.phet.physics.collision.CollisionLaw;
import edu.colorado.phet.physics.collision.SphereSphereContactDetector;
import edu.colorado.phet.physics.collision.SphereWallContactDetector;
import edu.colorado.phet.controller.command.RemoveParticleCmd;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

public class LaserSystem extends PhysicalSystem {

    private CollimatedBeam stimulatingBeam;
    private CollimatedBeam pumpingBeam;
    private ResonatingCavity resonatingCavity;

    /**
     *
     */
    public LaserSystem() {
        super( LaserConfig.instance() );
        this.addLaw( CollisionLaw.instance() );

        // Set up collision classes
        new SphereSphereContactDetector();
        new SphereWallContactDetector();

        AtomAtomCollision.register();
        PhotonMirrorCollision.register();
        AtomWallCollision.register();
        PhotonAtomCollision.register();
    }

    public void stepInTime( float dt ) {
        super.stepInTime( dt );

        // Check to see if any photons need to be taken out of the system
        List bodies = this.getBodies();
        for( int i = 0; i < bodies.size(); i++ ) {
            Object obj = bodies.get( i );
            if( obj instanceof Photon ) {
                Photon photon = (Photon)obj;
                Vector2D position = photon.getPosition();
                if( !boundingRectangle.contains( position.getX(), position.getY() )) {
                    edu.colorado.phet.controller.command.RemoveParticleCmd cmd = new edu.colorado.phet.controller.command.RemoveParticleCmd( photon );
                    this.addPrepCmd( cmd );
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
     *
     * @return
     */
    public CollimatedBeam getStimulatingBeam() {
        return stimulatingBeam;
    }

    /**
     *
     * @param stimulatingBeam
     */
    public void setStimulatingBeam( CollimatedBeam stimulatingBeam ) {
        if( stimulatingBeam != null ) {
            this.removeBody( stimulatingBeam );
        }
        addBody( stimulatingBeam );
        this.stimulatingBeam = stimulatingBeam;
    }


    /**
     *
     * @return
     */
    public CollimatedBeam getPumpingBeam() {
        return pumpingBeam;
    }

    /**
     *
     * @param pumpingBeam
     */
    public void setPumpingBeam( CollimatedBeam pumpingBeam ) {
        if( pumpingBeam != null ) {
            this.removeBody( pumpingBeam );
        }
        addBody( pumpingBeam );
        this.pumpingBeam = pumpingBeam;
    }

    /**
     *
     * @param time
     */
    public void setHighEnergySpontaneousEmissionTime( float time ) {
        Atom.setHighEnergySpontaneousEmissionTime( time );
    }

    /**
     *
     * @param time
     */
    public void setMiddleEnergySpontaneousEmissionTime( float time ) {
        Atom.setMiddleEnergySpontaneousEmissionTime( time );
    }

    /**
     *
     * @return
     */
    public int getNumGroundStateAtoms() {
        return Atom.getNumGroundStateAtoms();
    }

    /**
     *
     * @return
     */
    public int getNumMiddleStateAtoms() {
        return Atom.getNumMiddleStateAtoms();
    }

    /**
     *
     * @return
     */
    public int getNumHighStateAtoms() {
        return Atom.getNumHighStateAtoms();
    }

    /***
     *
     */
    public void removeAtoms() {
        List bodies = getBodies();
        for( int i = 0; i < bodies.size(); i++ ) {
            PhysicalEntity physicalEntity = (PhysicalEntity)bodies.get( i );
            if( physicalEntity instanceof Atom ) {
                RemoveParticleCmd cmd = new RemoveParticleCmd( (Atom)physicalEntity );
                this.addPrepCmd( cmd );
            }
        }
    }

    public void removePhotons() {
        List bodies = getBodies();
        for( int i = 0; i < bodies.size(); i++ ) {
            PhysicalEntity physicalEntity = (PhysicalEntity)bodies.get( i );
            if( physicalEntity instanceof Photon ) {
                RemoveParticleCmd cmd = new RemoveParticleCmd( (Photon)physicalEntity );
                this.addPrepCmd( cmd );
            }
        }
    }


    //
    // Static fields and methods
    //
    static public Point2D.Float ORIGIN = new Point2D.Float( 100, 300 );
    static private int width = 800;
    static private int height = 800;
    static private int minX = (int)LaserConfig.ORIGIN.getX() - 50;
    static private int minY = (int)LaserConfig.ORIGIN.getY() - height / 2;
    static private Rectangle2D.Float boundingRectangle = new Rectangle2D.Float( minX,
                                                                                minY,
                                                                                width,
                                                                                height );
}
