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
import edu.colorado.phet.lasers.model.atom.*;
import edu.colorado.phet.lasers.model.collision.PhotonAtomCollisonExpert;
import edu.colorado.phet.lasers.model.collision.PhotonMirrorCollisonExpert;
import edu.colorado.phet.lasers.model.mirror.Mirror;
import edu.colorado.phet.lasers.model.photon.CollimatedBeam;
import edu.colorado.phet.lasers.model.photon.Photon;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class LaserModel extends BaseModel implements Photon.LeftSystemEventListener {

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
    private CollisionMechanism collisionMechanism;
    private int numPhotons;

    /**
     *
     */
    public LaserModel() {

        // Set up the system of collision experts
        collisionMechanism = new CollisionMechanism();
        collisionMechanism.addCollisionExpert( new SphereSphereExpert() );
        collisionMechanism.addCollisionExpert( new PhotonAtomCollisonExpert() );
        collisionMechanism.addCollisionExpert( new SphereBoxExpert() );
        collisionMechanism.addCollisionExpert( new PhotonMirrorCollisonExpert() );

        addModelElement( new CollisionAgent() );
    }

    public void addModelElement( ModelElement modelElement ) {
        super.addModelElement( modelElement );
        if( modelElement instanceof Collidable ) {
            bodies.add( modelElement );
        }
        if( modelElement instanceof Photon ) {
            photons.add( modelElement );
            // we have to listen for photons leaving the system when they
            // are absorbed by atoms
            ( (Photon)modelElement ).addLeftSystemListener( this );
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
    }

    public void removeModelElement( ModelElement modelElement ) {
        super.removeModelElement( modelElement );
        if( modelElement instanceof Collidable ) {
            bodies.remove( modelElement );
        }
        if( modelElement instanceof Atom ) {
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
        numPhotons = 0;
        for( int i = 0; i < bodies.size(); i++ ) {
            Object obj = bodies.get( i );
            if( obj instanceof Photon ) {
                numPhotons++;
                Photon photon = (Photon)obj;
                Point2D position = photon.getPosition();
                if( !boundingRectangle.contains( position.getX(), position.getY() ) ) {
                    // We don't need to remove the element right now. The photon will
                    // fire an event that we will catch
                    photon.removeFromSystem();
                }
            }
        }
    }

    public void reset() {
        getPumpingBeam().setPhotonsPerSecond( 0 );
        getSeedBeam().setPhotonsPerSecond( 0 );
        for( Iterator iterator = bodies.iterator(); iterator.hasNext(); ) {
            Object obj = iterator.next();
            if( obj instanceof Atom ) {
                Atom atom = (Atom)obj;
                atom.setState( GroundState.instance() );
            }
        }
        Photon photon = null;
        while( !photons.isEmpty() ) {
            photon = (Photon)photons.get( 0 );
            photon.removeFromSystem();
        }
        numPhotons = 0;
    }

    //----------------------------------------------------------------
    // Getters and setters
    //----------------------------------------------------------------        
    public ResonatingCavity getResonatingCavity() {
        return resonatingCavity;
    }

    public void setResonatingCavity( ResonatingCavity resonatingCavity ) {
        this.resonatingCavity = resonatingCavity;
    }

    public CollimatedBeam getSeedBeam() {
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

    public void setBounds( Rectangle2D bounds ) {
        boundingRectangle.setRect( bounds );
    }

    public int getNumPhotons() {
        return numPhotons;
    }

    ////////////////////////////////////////////////////////////////////////////////
    // LeftSystemEvent Handling
    //

    public void leftSystemEventOccurred( Photon.LeftSystemEvent event ) {
        removeModelElement( event.getPhoton() );
    }

    ///////////////////////////////////////////////////////////////////////////////
    // Inner classes
    //
    private class CollisionMechanism {
        private ArrayList collisionExperts = new ArrayList();

        void addCollisionExpert( CollisionExpert expert ) {
            collisionExperts.add( expert );
        }

        void removeCollisionExpert( CollisionExpert expert ) {
            collisionExperts.remove( expert );
        }

        /**
         * Detects and computes collisions between the items in two lists of collidable objects
         *
         * @param collidablesA
         * @param collidablesB
         */
        void doIt( List collidablesA, List collidablesB ) {
            for( int i = 0; i < collidablesA.size(); i++ ) {
                Collidable collidable1 = (Collidable)collidablesA.get( i );
                if( !( collidable1 instanceof Photon )
                    || ( resonatingCavity.getBounds().contains( ( (Photon)collidable1 ).getPosition() ) )
                    || ( resonatingCavity.getBounds().contains( ( (Photon)collidable1 ).getPositionPrev() ) ) ) {
                    for( int j = 0; j < collidablesB.size(); j++ ) {
                        Collidable collidable2 = (Collidable)collidablesB.get( j );
                        if( collidable1 != collidable2
                            && ( !( collidable2 instanceof Photon )
                                 || ( resonatingCavity.getBounds().contains( ( (Photon)collidable2 ).getPosition() ) ) ) ) {
                            for( int k = 0; k < collisionExperts.size(); k++ ) {
                                CollisionExpert collisionExpert = (CollisionExpert)collisionExperts.get( k );
                                collisionExpert.detectAndDoCollision( collidable1, collidable2 );
                            }
                        }
                    }
                }
            }
        }

        /**
         * Detects and computes collisions between the items in a list of collidables and a specified
         * collidaqble.
         *
         * @param collidablesA
         * @param body
         */
        void doIt( List collidablesA, Collidable body ) {
            for( int i = 0; i < collidablesA.size(); i++ ) {
                Collidable collidable1 = (Collidable)collidablesA.get( i );
                if( !( collidable1 instanceof Photon )
                    || ( resonatingCavity.getBounds().contains( ( (Photon)collidable1 ).getPosition() ) )
                    || ( resonatingCavity.getBounds().contains( ( (Photon)collidable1 ).getPositionPrev() ) ) ) {
                    for( int k = 0; k < collisionExperts.size(); k++ ) {
                        CollisionExpert collisionExpert = (CollisionExpert)collisionExperts.get( k );
                        collisionExpert.detectAndDoCollision( collidable1, body );
                    }
                }
            }
        }

    }

    /**
     * Takes care of getting all collisions taken care of. Does a special thing to make sure an atom
     * can't be hit by more than one photon in a single time step.
     */
    private class CollisionAgent implements ModelElement {
        PhotonAtomCollisonExpert phtonAtomExpert = new PhotonAtomCollisonExpert();
        int numSections = 6;
        double sectionWidth;
        private ArrayList[] cavitySections;

        public void stepInTime( double dt ) {

            // If the lists of atoms in each section haven't been created yet, do it now
//            if( cavitySections == null ) {
//
//                numSections = (int)( resonatingCavity.getWidth() / ( Atom.getS_radius() * 2 )) - 1;
//                sectionWidth = resonatingCavity.getWidth() / numSections;
//                cavitySections = new ArrayList[numSections];
//                for( int i = 0; i < cavitySections.length; i++ ) {
//                    cavitySections[i] = new ArrayList();
//                }
//            }

            // Assign every atom to one or two regions of the cavity, based on its position
//            for( int i = 0; i < cavitySections.length; i++ ) {
//                ArrayList section = cavitySections[i];
//                section.clear();
//            }
//            for( int j = 0; j < atoms.size(); j++ ) {
//                Atom atom = (Atom)atoms.get( j );
//                double d0 = atom.getPosition().getX() - atom.getRadius() - resonatingCavity.getMinX();
//                double d1 = atom.getPosition().getX() + atom.getRadius() - resonatingCavity.getMinX();
//                int section0 = Math.max( 0, (int)( d0 / sectionWidth ));
//                int section1 = Math.min( numSections - 1, (int)( d1 / sectionWidth ));
//                cavitySections[section0].add( atom );
//                if( section1 != section0 ) {
//                    cavitySections[section1].add( atom );
//                }
//            }

            // Test each photon against the atoms in the section the photon is in
            for( int i = 0; i < photons.size(); i++ ) {
                Photon photon = (Photon)photons.get( i );
                if( !( photon instanceof Photon )
                    || ( resonatingCavity.getBounds().contains( photon.getPosition() ) )
                    || ( resonatingCavity.getBounds().contains( photon.getPositionPrev() ) ) ) {
//
//                    int k = 0;
//                    int section = (int)( ( photon.getPosition().getX() - resonatingCavity.getMinX() ) / sectionWidth );
//                    if( section >= 0 && section < numSections ) {
//                        List atomsInSection = cavitySections[section];
//                        for( int j = 0; j < atomsInSection.size(); j++ ) {
//                            k = j;
//                            Atom atom = (Atom)atomsInSection.get( j );
//                            AtomicState s1 = atom.getState();
//                            phtonAtomExpert.detectAndDoCollision( photon, atom );
//                            AtomicState s2 = atom.getState();
//                            if( s1 != s2 ) {
//                                break;
//                            }
//                        }
//                      System.out.println( "k = " + k );
//                    }

                    for( int j = 0; j < atoms.size(); j++ ) {
                        Atom atom = (Atom)atoms.get( j );
                        AtomicState s1 = atom.getState();
                        phtonAtomExpert.detectAndDoCollision( photon, atom );
                        AtomicState s2 = atom.getState();
                        if( s1 != s2 ) {
                            break;
                        }
                    }
                }
            }
            collisionMechanism.doIt( photons, mirrors );
            collisionMechanism.doIt( atoms, resonatingCavity );
        }

        private boolean isNearMirror( Photon photon, double dt ) {
            for( int i = 0; i < mirrors.size(); i++ ) {
                Mirror mirror = (Mirror)mirrors.get( i );
                if( Math.abs( photon.getPosition().getX() - mirror.getPosition().getX() ) <
                    Math.abs( photon.getVelocity().getX() * dt ) * 2 ) {
                    return true;
                }
            }
            return false;
        }

    }
}
