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

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.*;

import edu.colorado.phet.common.collision.Collidable;
import edu.colorado.phet.common.collision.CollisionExpert;
import edu.colorado.phet.common.collision.SphereBoxExpert;
import edu.colorado.phet.common.collision.SphereSphereExpert;
import edu.colorado.phet.common.phetcommon.model.ModelElement;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.util.EventChannel;
import edu.colorado.phet.common.phetcommon.util.SimpleObservable;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.quantum.QuantumConfig;
import edu.colorado.phet.common.quantum.model.*;
import edu.colorado.phet.lasers.controller.LasersConfig;
import edu.colorado.phet.lasers.model.atom.LaserElementProperties;
import edu.colorado.phet.lasers.model.atom.ThreeLevelElementProperties;
import edu.colorado.phet.lasers.model.atom.TwoLevelElementProperties;
import edu.colorado.phet.lasers.model.collision.PhotonMirrorCollisonExpert;
import edu.colorado.phet.lasers.model.mirror.Mirror;
import edu.colorado.phet.lasers.view.EnergyMatchDetector;
import edu.colorado.phet.lasers.view.MatchState;

public class LaserModel extends QuantumModel {
//public class  LaserModel extends BaseModel implements Photon.LeftSystemEventListener {

    //----------------------------------------------------------------
    // Class fields
    //----------------------------------------------------------------

    static public Point2D ORIGIN = new Point2D.Double( 100, 300 );
    static private int width = 800;
    static private int height = 800;
    static private int minX = (int) LasersConfig.ORIGIN.getX() - 50;
    static private int minY = (int) LasersConfig.ORIGIN.getY() - height / 2;

    //----------------------------------------------------------------
    // Instance fields and methods
    //----------------------------------------------------------------

    private Beam stimulatingBeam;
    private Beam pumpingBeam;
    private Tube tube;
    private List bodies = new LinkedList();
    private Rectangle2D boundingRectangle = new Rectangle2D.Double( minX,
                                                                    minY,
                                                                    width,
                                                                    height );
    private ArrayList photons = new ArrayList();
    private ArrayList atoms = new ArrayList();
    private ArrayList mirrors = new ArrayList();
    private CollisionMechanism collisionMechanism;
    private HashSet lasingPhotons = new HashSet();
    private double angleWindow = LasersConfig.PHOTON_CHEAT_ANGLE;

    private int numPhotons;

    // Counters for the number of atoms in each state
    private int numGroundStateAtoms;
    private int numMiddleStateAtoms;
    private int numHighStateAtoms;

    // Properties for two and three level atoms
    private LaserElementProperties twoLevelProperties = new TwoLevelElementProperties();
    private LaserElementProperties threeLevelProperties = new ThreeLevelElementProperties();

    // Replacement for behavior that was previously built into BaseModel
    private SimpleObservable observable = new SimpleObservable();
    private boolean modelPaused = false;

    public boolean isModelPaused() {
        return modelPaused;
    }

    public void setModelPaused( boolean modelPaused ) {
        this.modelPaused = modelPaused;
    }

    public LaserModel( double photonSpeedScale ) {
        super( photonSpeedScale );

        setCurrentElementProperties( twoLevelProperties );

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
        if ( modelElement instanceof Collidable ) {
            bodies.add( modelElement );
        }
        if ( modelElement instanceof Photon ) {
            addPhoton( modelElement );

        }
        if ( modelElement instanceof Atom ) {
            addAtom( modelElement );
        }
        if ( modelElement instanceof Mirror ) {
            mirrors.add( modelElement );
        }
        if ( modelElement instanceof Tube ) {
            this.tube = (Tube) modelElement;
        }
    }

    private void addAtom( ModelElement modelElement ) {
        atoms.add( modelElement );
        Atom atom = (Atom) modelElement;
        atom.addChangeListener( new AtomChangeListener() );
    }

    private void addPhoton( ModelElement modelElement ) {
        photons.add( modelElement );
        // we have to listen for photons leaving the system when they
        // are absorbed by atoms
        Photon photon = (Photon) modelElement;
        ( (Photon) modelElement ).addLeftSystemListener( this );

        // If the photon is moving nearly horizontally and is equal in energy to the transition between the
        // middle and ground states, consider it to be lasing
        if ( isLasingPhoton( photon ) ) {
            lasingPhotons.add( photon );
            changeListenerProxy.lasingPopulationChanged( new ChangeEvent( this ) );
        }
    }

    private boolean isLasingPhoton( Photon photon ) {
        double middleToGroundEnergyDiff = getMiddleEnergyState().getEnergyLevel() - getGroundState().getEnergyLevel();
        return ( Math.abs( photon.getVelocity().getAngle() % Math.PI ) < angleWindow
                 && ( Math.abs( photon.getEnergy() - middleToGroundEnergyDiff ) <= QuantumConfig.ENERGY_TOLERANCE ) );
//                && ( Math.abs( photon.getEnergy() - middleToGroundEnergyDiff ) <= LaserConfig.ENERGY_TOLERANCE ));
//        return ( ( Math.abs( photon.getVelocity().getAngle() ) < angleWindow
//                   || Math.abs( photon.getVelocity().getAngle() - Math.PI ) < angleWindow )
//                && ( Math.abs( photon.getEnergy() - middleToGroundEnergyDiff ) <= LaserConfig.ENERGY_TOLERANCE ));
    }

    public void removeModelElement( ModelElement modelElement ) {
        super.removeModelElement( modelElement );
        if ( modelElement instanceof Collidable ) {
            bodies.remove( modelElement );
        }
        if ( modelElement instanceof Atom ) {
            atoms.remove( modelElement );
        }
        if ( modelElement instanceof Photon ) {
            photons.remove( modelElement );
        }
        if ( modelElement instanceof Mirror ) {
            mirrors.remove( modelElement );
        }
    }

    public void reset() {
        getPumpingBeam().setPhotonsPerSecond( 0 );
        getSeedBeam().setPhotonsPerSecond( 0 );
        for ( Iterator iterator = bodies.iterator(); iterator.hasNext(); ) {
            Object obj = iterator.next();
            if ( obj instanceof Atom ) {
                Atom atom = (Atom) obj;
                atom.setCurrState( getGroundState() );
            }
        }
        Photon photon;
        while ( !photons.isEmpty() ) {
            photon = (Photon) photons.get( 0 );
            photon.removeFromSystem();
        }
        numPhotons = 0;
    }

    protected void addCollisionExpert( CollisionExpert collisionExpert ) {
        collisionMechanism.addCollisionExpert( collisionExpert );
    }


    public void update( ClockEvent event ) {
        if ( !modelPaused ) {
            super.update( event );

            // Check to see if any photons need to be taken out of the system
            numPhotons = 0;
            for ( int i = 0; i < bodies.size(); i++ ) {
                Object obj = bodies.get( i );
                if ( obj instanceof Photon ) {
                    numPhotons++;
                    Photon photon = (Photon) obj;
                    Point2D position = photon.getPosition();
                    if ( !boundingRectangle.contains( position.getX(), position.getY() ) ) {
                        // We don't need to remove the element right now. The photon will
                        // fire an event that we will catch
                        photon.removeFromSystem();
                    }
                }
            }

            observable.notifyObservers();
        }
    }

    /**
     * Added to make up for when BaseModel lost its SimpleObservable behavior
     *
     * @param observer
     */
    public void addObserver( SimpleObserver observer ) {
        observable.addObserver( observer );
    }

    //----------------------------------------------------------------
    // Getters and setters
    //----------------------------------------------------------------

    public void setNumEnergyLevels( int numLevels ) {

        switch( numLevels ) {
            case 2:
                setCurrentElementProperties( twoLevelProperties );
//                currentElementProperties = twoLevelProperties;
                getPumpingBeam().setEnabled( false );
                break;
            case 3:
                setCurrentElementProperties( threeLevelProperties );
//                currentElementProperties = threeLevelProperties;
                getPumpingBeam().setEnabled( true );
                break;
            default:
                throw new RuntimeException( "Invalid number of levels" );
        }
        // Set the available states of all the atoms
        for ( int i = 0; i < atoms.size(); i++ ) {
            Atom atom = (Atom) atoms.get( i );
            atom.setStates( getCurrentElementProperties().getStates() );
        }

        // Initialize the number of atoms in each level
        numGroundStateAtoms = 0;
        numMiddleStateAtoms = 0;
        numHighStateAtoms = 0;
        LaserElementProperties elementProperties = (LaserElementProperties) getCurrentElementProperties();
        for ( int i = 0; i < atoms.size(); i++ ) {
            Atom atom = (Atom) atoms.get( i );
            if ( atom.getCurrState() == elementProperties.getGroundState() ) {
                numGroundStateAtoms++;
            }
            if ( atom.getCurrState() == elementProperties.getMiddleEnergyState() ) {
                numMiddleStateAtoms++;
            }
            if ( atom.getCurrState() == elementProperties.getHighEnergyState() ) {
                numHighStateAtoms++;
            }
        }
        changeListenerProxy.atomicStatesChanged( new ChangeEvent( this ) );
    }

    public Tube getResonatingCavity() {
        return tube;
    }

    public void setResonatingCavity( Tube tube ) {
        this.tube = tube;
    }

    public Beam getSeedBeam() {
        return stimulatingBeam;
    }

    public void setStimulatingBeam( Beam stimulatingBeam ) {
        if ( stimulatingBeam != null ) {
            removeModelElement( stimulatingBeam );
        }
        addModelElement( stimulatingBeam );
        this.stimulatingBeam = stimulatingBeam;
    }

    public Beam getPumpingBeam() {
        return pumpingBeam;
    }

    public void setPumpingBeam( Beam pumpingBeam ) {
        if ( pumpingBeam != null ) {
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

//    public AtomicState getGroundState() {
//        return getCurrentElementProperties().getGroundState();
//    }

    //

    public AtomicState getMiddleEnergyState() {
        return getCurrentElementProperties().getStates()[1];
    }

    public AtomicState getHighEnergyState() {
        return ( (LaserElementProperties) getCurrentElementProperties() ).getHighEnergyState();
    }

    public AtomicState[] getStates() {
        return getCurrentElementProperties().getStates();
    }

    public int getNumLasingPhotons() {
        return lasingPhotons.size();
    }

    /*
    Returns the first match, or null if none.
     */
    public MatchState getMatch( Beam beam ) {
        AtomicState[] states = getStates();
        for ( int i = 0; i < states.length; i++ ) {
            AtomicState state = states[i];
            EnergyMatchDetector matchDetector = new EnergyMatchDetector( this, state, beam );
            MatchState matchState = matchDetector.getMatch();
            if ( matchState.isMatch() ) {
                return matchState;
            }
        }
        return null;
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

        /*
         * Detects and computes collisions between the items in two lists of collidable objects
         */
        void doIt( List collidablesA, List collidablesB ) {
            for ( int i = 0; i < collidablesA.size(); i++ ) {
                Collidable collidable1 = (Collidable) collidablesA.get( i );
                if ( !( collidable1 instanceof Photon )
                     || ( tube.getBounds().contains( ( (Photon) collidable1 ).getPosition() ) )
                     || ( tube.getBounds().contains( collidable1.getPositionPrev() ) ) ) {
                    for ( int j = 0; j < collidablesB.size(); j++ ) {
                        Collidable collidable2 = (Collidable) collidablesB.get( j );
                        if ( collidable1 != collidable2
                             && ( !( collidable2 instanceof Photon )
                                  || ( tube.getBounds().contains( ( (Photon) collidable2 ).getPosition() ) ) ) ) {
                            for ( int k = 0; k < collisionExperts.size(); k++ ) {
                                CollisionExpert collisionExpert = (CollisionExpert) collisionExperts.get( k );
                                collisionExpert.detectAndDoCollision( collidable1, collidable2 );
                            }
                        }
                    }
                }
            }
        }

        /*
         * Detects and computes collisions between the items in a list of collidables and a specified
         * collidable.
         */
        void doIt( List collidablesA, Collidable body ) {
            for ( int i = 0; i < collidablesA.size(); i++ ) {
                Collidable collidable1 = (Collidable) collidablesA.get( i );
                if ( !( collidable1 instanceof Photon )
                     || ( tube.getBounds().contains( ( (Photon) collidable1 ).getPosition() ) )
                     || ( tube.getBounds().contains( collidable1.getPositionPrev() ) ) ) {
                    for ( int k = 0; k < collisionExperts.size(); k++ ) {
                        CollisionExpert collisionExpert = (CollisionExpert) collisionExperts.get( k );
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

        public void stepInTime( double dt ) {

            // Test each photon against the atoms in the section the photon is in
            for ( int i = 0; i < photons.size(); i++ ) {
                Photon photon = (Photon) photons.get( i );
                if ( !( photon instanceof Photon )
                     || ( tube.getBounds().contains( photon.getPosition() ) )
                     || ( tube.getBounds().contains( photon.getPositionPrev() ) ) ) {

                    for ( int j = 0; j < atoms.size(); j++ ) {
                        Atom atom = (Atom) atoms.get( j );
                        AtomicState s1 = atom.getCurrState();
                        photonAtomExpert.detectAndDoCollision( photon, atom );
                        AtomicState s2 = atom.getCurrState();
                        if ( s1 != s2 ) {
                            break;
                        }
                    }
                }
            }
            collisionMechanism.doIt( photons, mirrors );
            collisionMechanism.doIt( atoms, tube );
        }
    }

    //----------------------------------------------------------------
    // Events and listeners
    //----------------------------------------------------------------

    private EventChannel laserEventChannel = new EventChannel( ChangeListener.class );
    private ChangeListener changeListenerProxy = (ChangeListener) laserEventChannel.getListenerProxy();

    public void addLaserListener( ChangeListener listener ) {
        laserEventChannel.addListener( listener );
    }

    public void removeLaserListener( ChangeListener listener ) {
        laserEventChannel.removeListener( listener );
    }

    public class ChangeEvent extends EventObject {
        public ChangeEvent( Object source ) {
            super( source );
        }

        public LaserModel getLaserModel() {
            return (LaserModel) getSource();
        }

        public int getLasingPopulation() {
            return lasingPhotons.size();
        }
    }

    public interface ChangeListener extends EventListener {
        void lasingPopulationChanged( ChangeEvent event );

        void atomicStatesChanged( ChangeEvent event );
    }

    public static class ChangeListenerAdapter implements ChangeListener {
        public void lasingPopulationChanged( ChangeEvent event ) {
        }

        public void atomicStatesChanged( ChangeEvent event ) {
        }
    }

    public void leftSystemEventOccurred( Photon.LeftSystemEvent event ) {
        Photon photon = event.getPhoton();
        if ( lasingPhotons.contains( photon ) ) {
            lasingPhotons.remove( photon );
            changeListenerProxy.lasingPopulationChanged( new ChangeEvent( this ) );
        }
        super.leftSystemEventOccurred( event );
//        removeModelElement( event.getPhoton() );
    }

    //----------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------

    /**
     * Keeps track of number of atoms in each state
     */
    private class AtomChangeListener extends Atom.ChangeListenerAdapter {

        public void stateChanged( Atom.ChangeEvent event ) {
            AtomicState prevState = event.getPrevState();
            AtomicState currState = event.getCurrState();
            LaserElementProperties elementProperties = (LaserElementProperties) getCurrentElementProperties();
            if ( prevState == elementProperties.getGroundState() ) {
                numGroundStateAtoms--;
            }
            if ( prevState == elementProperties.getMiddleEnergyState() ) {
                numMiddleStateAtoms--;
            }
            if ( prevState == elementProperties.getHighEnergyState() ) {
                numHighStateAtoms--;
            }
            if ( currState == elementProperties.getGroundState() ) {
                numGroundStateAtoms++;
            }
            if ( currState == elementProperties.getMiddleEnergyState() ) {
                numMiddleStateAtoms++;
            }
            if ( currState == elementProperties.getHighEnergyState() ) {
                numHighStateAtoms++;
            }
        }
    }
}
