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

import edu.colorado.phet.collision.Collidable;
import edu.colorado.phet.collision.CollisionExpert;
import edu.colorado.phet.collision.SphereBoxExpert;
import edu.colorado.phet.collision.SphereSphereExpert;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.lasers.controller.LaserConfig;
import edu.colorado.phet.lasers.model.atom.Atom;
import edu.colorado.phet.lasers.model.atom.AtomicState;
import edu.colorado.phet.lasers.model.atom.HighEnergyState;
import edu.colorado.phet.lasers.model.atom.MiddleEnergyState;
import edu.colorado.phet.lasers.model.collision.PhotonAtomCollisonExpert;
import edu.colorado.phet.lasers.model.collision.PhotonMirrorCollisonExpert;
import edu.colorado.phet.lasers.model.mirror.Mirror;
import edu.colorado.phet.lasers.model.photon.CollimatedBeam;
import edu.colorado.phet.lasers.model.photon.Photon;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class LaserModel extends BaseModel implements Atom.Listener {

    static public Point2D ORIGIN = new Point2D.Double( 100, 300 );
    static private int width = 800;
    static private int height = 800;
    static private int minX = (int)LaserConfig.ORIGIN.getX() - 50;
    static private int minY = (int)LaserConfig.ORIGIN.getY() - height / 2;

    private CollimatedBeam stimulatingBeam;
    private CollimatedBeam pumpingBeam;
    private ResonatingCavity resonatingCavity;
    private List bodies = new LinkedList();
    private Rectangle2D boundingRectangle = new Rectangle2D.Double( minX,
                                                                    minY,
                                                                    width,
                                                                    height );
    private ArrayList photons = new ArrayList();
    private ArrayList atoms = new ArrayList();
    private ArrayList mirrors = new ArrayList();

    /**
     *
     */
    public LaserModel() {

        // Set up the system of collision experts
        final CollisionMechanism collisionMechanism = new CollisionMechanism();
        collisionMechanism.addCollisionExpert( new SphereSphereExpert() );
        collisionMechanism.addCollisionExpert( new PhotonAtomCollisonExpert() );
        collisionMechanism.addCollisionExpert( new SphereBoxExpert() );
        collisionMechanism.addCollisionExpert( new PhotonMirrorCollisonExpert() );
        this.addModelElement( new ModelElement() {
            public void stepInTime( double dt ) {
                collisionMechanism.doIt( photons, atoms );
                collisionMechanism.doIt( photons, mirrors );
            }
        } );
    }

    public void addModelElement( ModelElement modelElement ) {
        super.addModelElement( modelElement );
        if( modelElement instanceof Collidable ) {
            bodies.add( modelElement );
        }
        if( modelElement instanceof Photon ) {
            photons.add( modelElement );
        }
        if( modelElement instanceof Atom ) {
            atoms.add( modelElement );
        }
        if( modelElement instanceof Mirror ) {
            mirrors.add( modelElement );
        }
        if( modelElement instanceof ResonatingCavity ) {
            this.resonatingCavity = (ResonatingCavity)modelElement;
        }
        if( modelElement instanceof Atom ) {
            ( (Atom)modelElement ).addListener( this );
        }
    }

    public void removeModelElement( ModelElement modelElement ) {
        super.removeModelElement( modelElement );
        if( modelElement instanceof Collidable ) {
            bodies.remove( modelElement );
        }
        if( modelElement instanceof Atom ) {
            ( (Atom)modelElement ).removeListener( this );
            atoms.remove( modelElement );
        }
        if( modelElement instanceof Photon ) {
            photons.remove( modelElement );
        }
        if( modelElement instanceof Mirror ) {
            mirrors.remove( modelElement );
        }
    }

    public void stepInTime( double dt ) {
        super.stepInTime( dt );

        // Check to see if any photons need to be taken out of the system
        int photonCnt = 0;

        for( int i = 0; i < bodies.size(); i++ ) {
            Object obj = bodies.get( i );
            if( obj instanceof Photon ) {
                photonCnt++;
                Photon photon = (Photon)obj;
                Point2D position = photon.getPosition();
                if( !boundingRectangle.contains( position.getX(), position.getY() ) ) {
                    removeModelElement( photon );
                    photon.removeFromSystem();
                    photonCnt--;
                }
            }
        }
        System.out.println( "photonCnt = " + photonCnt );
    }

    public ResonatingCavity getResonatingCavity() {
        return resonatingCavity;
    }

    public void setResonatingCavity( ResonatingCavity resonatingCavity ) {
        this.resonatingCavity = resonatingCavity;
    }

    public CollimatedBeam getStimulatingBeam() {
        return stimulatingBeam;
    }

    public void setStimulatingBeam( CollimatedBeam stimulatingBeam ) {
        if( stimulatingBeam != null ) {
            removeModelElement( stimulatingBeam );
        }
        addModelElement( stimulatingBeam );
        this.stimulatingBeam = stimulatingBeam;
    }

    public CollimatedBeam getPumpingBeam() {
        return pumpingBeam;
    }

    public void setPumpingBeam( CollimatedBeam pumpingBeam ) {
        if( pumpingBeam != null ) {
            removeModelElement( pumpingBeam );
        }
        addModelElement( pumpingBeam );
        this.pumpingBeam = pumpingBeam;
    }

    public void setHighEnergyMeanLifetime( double time ) {
        HighEnergyState.instance().setMeanLifetime( time );
    }

    public void setMiddleEnergyMeanLifetime( double time ) {
        MiddleEnergyState.instance().setMeanLifetime( time );
    }

    public int getNumGroundStateAtoms() {
        return Atom.getNumGroundStateAtoms();
    }

    public int getNumMiddleStateAtoms() {
        return Atom.getNumMiddleStateAtoms();
    }

    public int getNumHighStateAtoms() {
        return Atom.getNumHighStateAtoms();
    }

    public void removeAtoms() {
        for( int i = 0; i < bodies.size(); i++ ) {
            ModelElement modelElement = (ModelElement)bodies.get( i );
            if( modelElement instanceof Atom ) {
                removeModelElement( modelElement );
            }
        }
    }

    public void removePhotons() {
        for( int i = 0; i < bodies.size(); i++ ) {
            ModelElement modelElement = (ModelElement)bodies.get( i );
            if( modelElement instanceof Photon ) {
                removeModelElement( modelElement );
            }
        }
    }

    public void setBounds( Rectangle2D bounds ) {
        boundingRectangle.setRect( bounds );
    }


    private class CollisionMechanism {
        private ArrayList collisionExperts = new ArrayList();

        void addCollisionExpert( CollisionExpert expert ) {
            collisionExperts.add( expert );
        }

        void removeCollisionExpert( CollisionExpert expert ) {
            collisionExperts.remove( expert );
        }

        void doIt( List collidablesA, List collidablesB ) {
            for( int i = 0; i < collidablesA.size() - 1; i++ ) {
                Collidable collidable1 = (Collidable)collidablesA.get( i );
                if( !( collidable1 instanceof Photon )
                    || ( resonatingCavity.getBounds().contains( ( (Photon)collidable1 ).getPosition() ) ) ) {
                    for( int j = 0; j < collidablesB.size(); j++ ) {
                        Collidable collidable2 = (Collidable)collidablesB.get( j );
                        if( !( collidable2 instanceof Photon )
                            || ( resonatingCavity.getBounds().contains( ( (Photon)collidable2 ).getPosition() ) ) ) {
                            for( int k = 0; k < collisionExperts.size(); k++ ) {
                                CollisionExpert collisionExpert = (CollisionExpert)collisionExperts.get( k );
                                collisionExpert.detectAndDoCollision( collidable1, collidable2 );
                            }
                        }
                    }
                }
            }
        }
    }

    // Implementation of Atom.Listener
    public void photonEmitted( Atom atom, Photon photon ) {
    }

    public void leftSystem( Atom atom ) {
    }

    public void stateChanged( Atom atom, AtomicState oldState, AtomicState newState ) {
        if( oldState instanceof ModelElement ) {
            removeModelElement( (ModelElement)oldState );
        }
        if( newState instanceof ModelElement ) {
            addModelElement( (ModelElement)newState );
        }
    }
}
