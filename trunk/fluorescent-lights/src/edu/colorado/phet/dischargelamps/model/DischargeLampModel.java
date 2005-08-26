/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.dischargelamps.model;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.lasers.model.LaserModel;
import edu.colorado.phet.lasers.model.atom.Atom;
import edu.colorado.phet.lasers.model.atom.AtomicState;
import edu.colorado.phet.dischargelamps.DischargeLampsConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * FluorescentLightModel
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class DischargeLampModel extends LaserModel implements Electrode.StateChangeListener {

    //-----------------------------------------------------------------
    // Instance data
    //-----------------------------------------------------------------
    private List atoms = new ArrayList();
    private int numEnergyLevels;
    private AtomicState[] atomicStates;
    private List electrons = new ArrayList();
    private ElectronAtomCollisionExpert electronAtomCollisionExpert = new ElectronAtomCollisionExpert();
    private ElectronSource cathode;
    private ElectronSink anode;
    private Spectrometer spectrometer;
    private Vector2D electronAcceleration = new Vector2D.Double();


    public DischargeLampModel() {
        // This is the place to set the mean lifetime for the various atomic states
//        MiddleEnergyState.instance().setMeanLifetime( .00001 );

        cathode = new ElectronSource( this,
                                      DischargeLampsConfig.CATHODE_LINE.getP1(),
                                      DischargeLampsConfig.CATHODE_LINE.getP2() );
        addModelElement( cathode );
        setCathode( cathode );

        anode = new ElectronSink( this,
                                  DischargeLampsConfig.ANODE_LINE.getP1(),
                                  DischargeLampsConfig.ANODE_LINE.getP2() );
        addModelElement( anode );
        setAnode( anode );

        spectrometer = new Spectrometer();
    }

    /**
     * Detects and handles collisions between atoms and electrons
     *
     * @param dt
     */
    public void stepInTime( double dt ) {
        super.stepInTime( dt );

        // Check for collisions between electrons and atoms
        for( int i = 0; i < atoms.size(); i++ ) {
            Atom atom = (Atom)atoms.get( i );
            for( int j = 0; j < electrons.size(); j++ ) {
                Electron electron = (Electron)electrons.get( j );
                electronAtomCollisionExpert.detectAndDoCollision( atom, electron );
            }
        }
    }

    /**
     * Extends the parent behavior by detecting the addition of certain types of
     * model elements
     *
     * @param modelElement
     */
    public void addModelElement( ModelElement modelElement ) {
        super.addModelElement( modelElement );
        if( modelElement instanceof Atom ) {
            atoms.add( modelElement );
        }
        if( modelElement instanceof Electron ) {
            Electron electron = (Electron)modelElement;
            electron.setAcceleration( getElectronAcceleration() );
            electrons.add( electron );
        }

        // TODO: The DischargeLamps simulation may count on these lines being here.
        // break. It should be taken out, and DischargeLamps should use setAnode() and
        // setcathode()
//        if( modelElement instanceof ElectronSource ) {
//            cathode = (ElectronSource)modelElement;
//            cathode.addStateChangeListener( this );
//        }
//        if( modelElement instanceof ElectronSink ) {
//            anode = (ElectronSink)modelElement;
//            anode.addStateChangeListener( this );
//        }
    }

    /**
     * Extends the parent behavior by detecting the removal of certain types of
     * model elements
     *
     * @param modelElement
     */
    public void removeModelElement( ModelElement modelElement ) {
        super.removeModelElement( modelElement );
        if( modelElement instanceof Atom ) {
            atoms.remove( modelElement );
        }
        if( modelElement instanceof Electron ) {
            electrons.remove( modelElement );
        }
    }

    //----------------------------------------------------------------
    // Getters and setters
    //----------------------------------------------------------------

    public void setNumAtomicEnergyLevels( int numLevels ) {
        numEnergyLevels = numLevels;
        atomicStates = new AtomicStateFactory().createAtomicStates( numLevels );
        for( int i = 0; i < atoms.size(); i++ ) {
            Atom atom = (Atom)atoms.get( i );
            atom.setStates( atomicStates );
        }
    }

    // Todo: Get rid of this. The clients of this should not be making atomic states
    public void setAtomicEnergyStates( AtomicState[] atomicStates ) {
        this.atomicStates = atomicStates;
        for( int i = 0; i < atoms.size(); i++ ) {
            Atom atom = (Atom)atoms.get( i );
            atom.setStates( atomicStates );
        }
    }

    public void setAnode( ElectronSink anode ) {
        this.anode = anode;
        anode.addStateChangeListener( this );
    }

    public void setCathode( ElectronSource cathode ) {
        this.cathode = cathode;
        cathode.addStateChangeListener( this );
    }

    public ElectronSource getCathode() {
        return cathode;
    }

    public ElectronSink getAnode() {
        return anode;
    }

    public Spectrometer getSpectrometer() {
        return spectrometer;
    }

    protected void setElectronAcceleration( double potentialDiff ) {
        double cathodeToAnodeDist = anode.getPosition().distance( cathode.getPosition() );
        electronAcceleration.setComponents( potentialDiff / cathodeToAnodeDist, 0 );
    }

    private Vector2D getElectronAcceleration() {
        return electronAcceleration;
    }

    public List getAtoms() {
        return atoms;
    }


    //-----------------------------------------------------------------
    // Event handling
    //-----------------------------------------------------------------

    /**
     * Handles changes in the electrode potentials
     *
     * @param event
     */
    public void stateChanged( Electrode.StateChangeEvent event ) {
        double potentialDiff = cathode.getPotential() - anode.getPotential();
        setElectronAcceleration( potentialDiff );
        for( int i = 0; i < electrons.size(); i++ ) {
            Electron electron = (Electron)electrons.get( i );
            electron.setAcceleration( getElectronAcceleration() );
        }
    }

    public AtomicState[] getAtomicStates() {
        return atomicStates;
    }
}

