/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.lasers.model;

import edu.colorado.phet.collision.Collidable;
import edu.colorado.phet.collision.CollisionExpert;
import edu.colorado.phet.collision.SphereBoxExpert;
import edu.colorado.phet.collision.SphereSphereExpert;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.util.EventChannel;
import edu.colorado.phet.lasers.controller.LaserConfig;
import edu.colorado.phet.lasers.model.atom.*;
import edu.colorado.phet.lasers.model.collision.PhotonAtomCollisonExpert;
import edu.colorado.phet.lasers.model.collision.PhotonMirrorCollisonExpert;
import edu.colorado.phet.lasers.model.mirror.Mirror;
import edu.colorado.phet.lasers.model.mirror.PartialMirror;
import edu.colorado.phet.lasers.model.photon.CollimatedBeam;
import edu.colorado.phet.lasers.model.photon.Photon;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.*;

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
    private PartialMirror rightMirror;
    private PartialMirror leftMirror;
    private CollisionMechanism collisionMechanism;
    private int numPhotons;

    // Counters for the number of atoms in each state
    private int numGroundStateAtoms;
    private int numMiddleStateAtoms;
    private int numHighStateAtoms;

    private GroundState groundState = new GroundState();
    private MiddleEnergyState middleEnergyState = new MiddleEnergyState();
    private HighEnergyState highEnergyState = new HighEnergyState();

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

        // Set the default relationships between the states
        groundState.setNextHigherEnergyState( middleEnergyState );
        middleEnergyState.setNextLowerEnergyState( groundState );
        middleEnergyState.setNextHigherEnergyState( highEnergyState );
        highEnergyState.setNextLowerEnergyState( middleEnergyState );
        highEnergyState.setNextHigherEnergyState( AtomicState.MaxEnergyState.instance() );
    }

    public void addModelElement( ModelElement modelElement ) {
        super.addModelElement( modelElement );
        if( modelElement instanceof Collidable ) {
            bodies.add( modelElement );
        }
        if( modelElement instanceof Photon ) {
            addPhoton( modelElement );

        }
        if( modelElement instanceof Atom ) {
            addAtom( modelElement );
        }
        if( modelElement instanceof Mirror ) {
            mirrors.add( modelElement );
        }
        if( modelElement instanceof ResonatingCavity ) {
            this.resonatingCavity = (ResonatingCavity)modelElement;
        }
    }

    private void addAtom( ModelElement modelElement ) {
        atoms.add( modelElement );
        Atom atom = (Atom)modelElement;
        atom.addChangeListener( new AtomChangeListener() );
        if( atom.getCurrState() == getGroundState() ) {
            numGroundStateAtoms++;
        }
        if( atom.getCurrState() == getMiddleEnergyState() ) {
            numMiddleStateAtoms++;
        }
        if( atom.getCurrState() == getHighEnergyState() ) {
            numHighStateAtoms++;
        }
    }

    private void addPhoton( ModelElement modelElement ) {
        photons.add( modelElement );
        // we have to listen for photons leaving the system when they
        // are absorbed by atoms
        Photon photon = (Photon)modelElement;
        ( (Photon)modelElement ).addLeftSystemListener( this );

        // If the photon is moving nearly horizontally, consider it to be lasing
        if( Math.abs( photon.getVelocity().getAngle() ) < angleWindow
            || Math.abs( photon.getVelocity().getAngle() - Math.PI ) < angleWindow ) {
            lasingPhotons.add( photon );
            laserListenerProxy.lasingPopulationChanged( new LaserEvent( this ) );
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
                atom.setCurrState( getGroundState() );
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
        getHighEnergyState().setMeanLifetime( time );
    }

    public void setMiddleEnergyMeanLifetime( double time ) {
        getMiddleEnergyState().setMeanLifetime( time );
    }

    public int getNumGroundStateAtoms() {
        return numGroundStateAtoms;
    }

    public int getNumMiddleStateAtoms() {
        return numMiddleStateAtoms;
    }

    public int getNumHighStateAtoms() {
        return numHighStateAtoms;
    }

    public void setBounds( Rectangle2D bounds ) {
        boundingRectangle.setRect( bounds );
    }

    public int getNumPhotons() {
        return numPhotons;
    }

    public GroundState getGroundState() {
        return groundState;
    }

    public MiddleEnergyState getMiddleEnergyState() {
        return middleEnergyState;
    }

    public HighEnergyState getHighEnergyState() {
        return highEnergyState;
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
        PhotonAtomCollisonExpert photonAtomExpert = new PhotonAtomCollisonExpert();
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
//                            AtomicState s1 = atom.getCurrState();
//                            photonAtomExpert.detectAndDoCollision( photon, atom );
//                            AtomicState s2 = atom.getCurrState();
//                            if( s1 != s2 ) {
//                                break;
//                            }
//                        }
//                      System.out.println( "k = " + k );
//                    }

                    for( int j = 0; j < atoms.size(); j++ ) {
                        Atom atom = (Atom)atoms.get( j );
                        AtomicState s1 = atom.getCurrState();
                        photonAtomExpert.detectAndDoCollision( photon, atom );
                        AtomicState s2 = atom.getCurrState();
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

    ////////////////////////////////////////////////////////////////////////////////
    // Event Handling
    //
    private HashSet lasingPhotons = new HashSet();
    private double angleWindow = LaserConfig.PHOTON_CHEAT_ANGLE;

    private EventChannel laserEventChannel = new EventChannel( LaserListener.class );
    private LaserListener laserListenerProxy = (LaserListener)laserEventChannel.getListenerProxy();

    public void addLaserListener( LaserListener listener ) {
        laserEventChannel.addListener( listener );
    }

    public void removeLaserListener( LaserListener listener ) {
        laserEventChannel.removeListener( listener );
    }

    public class LaserEvent extends EventObject {
        public LaserEvent( Object source ) {
            super( source );
        }

        public int getLasingPopulation() {
            return lasingPhotons.size();
        }
    }

    public interface LaserListener extends EventListener {
        void lasingPopulationChanged( LaserEvent event );
    }

    public void leftSystemEventOccurred( Photon.LeftSystemEvent event ) {
        Photon photon = event.getPhoton();
        if( lasingPhotons.contains( photon ) ) {
            lasingPhotons.remove( photon );
            laserListenerProxy.lasingPopulationChanged( new LaserEvent( this ) );
        }

        removeModelElement( event.getPhoton() );
    }

    /**
     * Keeps track of number of atoms in each state
     */
    private class AtomChangeListener implements Atom.ChangeListener {

        public void stateChanged( Atom.ChangeEvent event ) {
            AtomicState prevState = event.getPrevState();
            AtomicState currState = event.getCurrState();
            if( prevState instanceof GroundState ) {
                numGroundStateAtoms--;
            }
            if( prevState instanceof MiddleEnergyState ) {
                numMiddleStateAtoms--;
            }
            if( prevState instanceof HighEnergyState ) {
                numHighStateAtoms--;
            }
            if( currState instanceof GroundState ) {
                numGroundStateAtoms++;
            }
            if( currState instanceof MiddleEnergyState ) {
                numMiddleStateAtoms++;
            }
            if( currState instanceof HighEnergyState ) {
                numHighStateAtoms++;
            }
        }
    }

}
