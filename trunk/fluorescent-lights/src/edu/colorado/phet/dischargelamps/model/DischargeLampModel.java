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
import edu.colorado.phet.common.util.EventChannel;
import edu.colorado.phet.lasers.model.LaserModel;
import edu.colorado.phet.lasers.model.ResonatingCavity;
import edu.colorado.phet.lasers.model.atom.Atom;
import edu.colorado.phet.lasers.model.atom.AtomicState;
import edu.colorado.phet.dischargelamps.DischargeLampsConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.EventObject;
import java.util.EventListener;
import java.awt.geom.Point2D;

/**
 * FluorescentLightModel
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class DischargeLampModel extends LaserModel {

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
    private ResonatingCavity tube;


    public DischargeLampModel() {
        // This is the place to set the mean lifetime for the various atomic states
//        MiddleEnergyState.instance().setMeanLifetime( .00001 );

        // Make the cathode
        cathode = new ElectronSource( this,
                                      DischargeLampsConfig.CATHODE_LINE.getP1(),
                                      DischargeLampsConfig.CATHODE_LINE.getP2() );
        addModelElement( cathode );
        cathode.addStateChangeListener( new ElectrodeStateChangeListener() );

        // Make the anode
        anode = new ElectronSink( this,
                                  DischargeLampsConfig.ANODE_LINE.getP1(),
                                  DischargeLampsConfig.ANODE_LINE.getP2() );
        anode.addStateChangeListener( new ElectrodeStateChangeListener() );
        addModelElement( anode );

        // Hook them together
        cathode.addListener( anode );

        // Make the discharge tube
        double x = DischargeLampsConfig.CATHODE_LOCATION.getX() - DischargeLampsConfig.ELECTRODE_INSETS.left;
        double y = DischargeLampsConfig.CATHODE_LOCATION.getY() - DischargeLampsConfig.CATHODE_LENGTH / 2
                   - DischargeLampsConfig.ELECTRODE_INSETS.top;
        double length = DischargeLampsConfig.ANODE_LOCATION.getX() - DischargeLampsConfig.CATHODE_LOCATION.getX()
                        + DischargeLampsConfig.ELECTRODE_INSETS.left + DischargeLampsConfig.ELECTRODE_INSETS.right;
        double height = DischargeLampsConfig.CATHODE_LENGTH
                        + DischargeLampsConfig.ELECTRODE_INSETS.top + DischargeLampsConfig.ELECTRODE_INSETS.bottom;
        Point2D tubeLocation = new Point2D.Double( x, y );
        tube = new ResonatingCavity( tubeLocation, length, height );
        addModelElement( tube );

        // Make the spectrometer
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
        changeListenerProxy.energyLevelsChanged( new ChangeEvent( this ) );
    }

    public void setAtomicElement( ElementProperties elementProperties ) {
        atomicStates = elementProperties.getStates();
        for( int i = 0; i < atoms.size(); i++ ) {
            DischargeLampAtom atom = (DischargeLampAtom)atoms.get( i );
            atom.setElementProperties( elementProperties );
        }
        changeListenerProxy.energyLevelsChanged( new ChangeEvent( this ) );
    }

    public void setAtomicStates( AtomicState[] states ) {
        atomicStates = states;
        for( int i = 0; i < atoms.size(); i++ ) {
            Atom atom = (Atom)atoms.get( i );
            atom.setStates( atomicStates );
        }
    }


    public AtomicState[] getAtomicStates() {
        return atomicStates;
    }

    public ElectronSource getCathode() {
        return cathode;
    }

    public ElectronSink getAnode() {
        return anode;
    }

    public ResonatingCavity getTube() {
        return tube;
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
     */
    private class ElectrodeStateChangeListener implements Electrode.StateChangeListener {
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

    //----------------------------------------------------------------
    // Event and listener definitions
    //----------------------------------------------------------------

    public class ChangeEvent extends EventObject {
        public ChangeEvent( Object source ) {
            super( source );
        }

        public DischargeLampModel getDischargeLampModelDischargeLampModel() {
            return (DischargeLampModel)getSource();
        }
    }

    public interface ChangeListener extends EventListener {
         void energyLevelsChanged( ChangeEvent event );
    }

    public class ChangeListenerAdapter implements ChangeListener {
        public void energyLevelsChanged( ChangeEvent event ) {
        }
    }

    private EventChannel changeEventChannel = new EventChannel( ChangeListener.class );
    private ChangeListener changeListenerProxy = (ChangeListener)changeEventChannel.getListenerProxy();

    public void addChangeListener( ChangeListener listener ) {
        changeEventChannel.addListener( listener );
    }

    public void removeChangeListener( ChangeListener listener ) {
        changeEventChannel.removeListener( listener );
    }
}

