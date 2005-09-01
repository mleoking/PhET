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

import java.util.*;
import java.awt.geom.Point2D;

/**
 * FluorescentLightModel
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class DischargeLampModel extends LaserModel {

    //-----------------------------------------------------------------
    // Class data 
    //-----------------------------------------------------------------
    public static final double MAX_VOLTAGE = 10;
    public static final int MAX_STATES = 6;

    //-----------------------------------------------------------------
    // Instance data
    //-----------------------------------------------------------------
    private List atoms = new ArrayList();
    private AtomicState[] atomicStates;
    private ArrayList electrons = new ArrayList();
    private ArrayList electronSources = new ArrayList( );
    private ArrayList electronSinks = new ArrayList( );
    private ElectronAtomCollisionExpert electronAtomCollisionExpert = new ElectronAtomCollisionExpert();
    private Spectrometer spectrometer;
    private Vector2D electronAcceleration = new Vector2D.Double();
    private ResonatingCavity tube;
    private Plate leftHandPlate;
    private Plate rightHandPlate;
    private HeatingElement leftHandHeatingElement;
    private HeatingElement rightHandHeatingElement;
    private double current;
    private double maxCurrent;
    private ElementProperties elementProperties;


    public DischargeLampModel() {

        // This is the place to set the mean lifetime for the various atomic states
//        MiddleEnergyState.instance().setMeanLifetime( .00001 );

        atomicStates = new AtomicStateFactory().createAtomicStates( 2 );

        // Make the plates
        leftHandPlate = new Plate( this,
                                      DischargeLampsConfig.CATHODE_LINE.getP1(),
                                      DischargeLampsConfig.CATHODE_LINE.getP2() );
        leftHandPlate.addStateChangeListener( new ElectrodeStateChangeListener() );

        rightHandPlate = new Plate( this,
                                  DischargeLampsConfig.ANODE_LINE.getP1(),
                                  DischargeLampsConfig.ANODE_LINE.getP2() );
        rightHandPlate.addStateChangeListener( new ElectrodeStateChangeListener() );

        // Make the heating elements
        leftHandHeatingElement = new HeatingElement();
        leftHandHeatingElement.setPosition( DischargeLampsConfig.CATHODE_LOCATION.getX(),
                                            DischargeLampsConfig.CATHODE_LOCATION.getY() );
        rightHandHeatingElement = new HeatingElement();
        rightHandHeatingElement.setPosition( DischargeLampsConfig.ANODE_LOCATION.getX(),
                                            DischargeLampsConfig.ANODE_LOCATION.getY() );


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
            Atom atom = (Atom)modelElement;
            atoms.add( atom );
        }
        if( modelElement instanceof Electron ) {
            Electron electron = (Electron)modelElement;
            electron.setAcceleration( getElectronAcceleration() );
            electrons.add( electron );
        }

        if( modelElement instanceof ElectronSink ) {
            ElectronSink sink = (ElectronSink)modelElement;
            electronSinks.add( sink );
            for( int i = 0; i < electronSources.size(); i++ ) {
                ElectronSource source = (ElectronSource)electronSources.get( i );
                source.addListener( sink );
            }
        }

        if( modelElement instanceof ElectronSource ) {
            ElectronSource source = (ElectronSource)modelElement;
            electronSources.add( source );
            for( int i = 0; i < electronSinks.size(); i++ ) {
                ElectronSink sink = (ElectronSink)electronSinks.get( i );
                source.addListener( sink );
            }
        }
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
        atomicStates = new AtomicStateFactory().createAtomicStates( numLevels, atomicStates );
        for( int i = 0; i < atoms.size(); i++ ) {
            Atom atom = (Atom)atoms.get( i );
            atom.setStates( atomicStates );
        }
        changeListenerProxy.energyLevelsChanged( new ChangeEvent( this ) );
    }

    public void setElementProperties( ElementProperties elementProperties ) {
        this.elementProperties = elementProperties;
        atomicStates = elementProperties.getStates();
        for( int i = 0; i < atoms.size(); i++ ) {
            DischargeLampAtom atom = (DischargeLampAtom)atoms.get( i );
            atom.setElementProperties( elementProperties );
        }
        changeListenerProxy.energyLevelsChanged( new ChangeEvent( this ) );
    }

    public AtomicState[] getAtomicStates() {
        return atomicStates;
    }

    public ResonatingCavity getTube() {
        return tube;
    }

    public Spectrometer getSpectrometer() {
        return spectrometer;
    }

    protected void setElectronAcceleration( double potentialDiff ) {
        double plateToPlateDist = rightHandPlate.getPosition().distance( leftHandPlate.getPosition() );
        electronAcceleration.setComponents( potentialDiff / plateToPlateDist, 0 );
    }

    public Vector2D getElectronAcceleration() {
        return electronAcceleration;
    }

    public List getAtoms() {
        return atoms;
    }

    public ElementProperties getElementProperties() {
        return elementProperties;
    }

    public void setMaxCurrent( double maxCurrent ) {
        this.maxCurrent = maxCurrent;
    }

    public void setCurrent( double value ) {
        this.current = value;
        leftHandHeatingElement.setTemperature( 0 );
        rightHandHeatingElement.setTemperature( 0 );
        double temperature = 255 * value / maxCurrent;
        if( leftHandPlate.getPotential() > rightHandPlate.getPotential() ) {
            leftHandPlate.setCurrent( value );
            rightHandPlate.setCurrent( 0 );
            leftHandHeatingElement.setTemperature( temperature );
        }
        else {
            rightHandPlate.setCurrent( value );
            leftHandPlate.setCurrent( 0 );
            rightHandHeatingElement.setTemperature( temperature );
        }
    }

    public Plate getLeftHandPlate() {
        return leftHandPlate;
    }

    public Plate getRightHandPlate() {
        return rightHandPlate;
    }

    public HeatingElement getLeftHandHeatingElement() {
        return leftHandHeatingElement;
    }

    public HeatingElement getRightHandHeatingElement() {
        return rightHandHeatingElement;
    }

    private void setHeatingElementsEnabled( boolean heatingElementsEnabled ) {
        leftHandHeatingElement.setIsEnabled( heatingElementsEnabled );
        rightHandHeatingElement.setIsEnabled( heatingElementsEnabled );
    }

    /**
     * Sets the electron production mode to continuous or single-shot. Also enables/disables
     * the heating elements.
     * @param electronProductionMode
     */
    public void setElectronProductionMode( Object electronProductionMode ) {
        for( int i = 0; i < electronSources.size(); i++ ) {
            ElectronSource electronSource = (ElectronSource)electronSources.get( i );
            electronSource.setElectronProductionMode( electronProductionMode );
        }
        setHeatingElementsEnabled( electronProductionMode == ElectronSource.CONTINUOUS_MODE );
    }

    public void setVoltage( double voltage ) {
        if( voltage > 0 ) {
            leftHandPlate.setPotential( voltage );
            rightHandPlate.setPotential( 0 );
        }
        else {
            leftHandPlate.setPotential( 0 );
            rightHandPlate.setPotential( -voltage );
        }
        changeListenerProxy.voltageChanged( new ChangeEvent( this ) );
    }

    public double getVoltage() {
        return leftHandPlate.getPotential() - rightHandPlate.getPotential();
    }

    //-----------------------------------------------------------------
    // Event handling
    //-----------------------------------------------------------------

    /**
     * Handles changes in the electrode potentials
     */
    private class ElectrodeStateChangeListener implements Electrode.StateChangeListener {
        public void potentialChanged( Electrode.StateChangeEvent event ) {
            double potentialDiff = leftHandPlate.getPotential() - rightHandPlate.getPotential();

            // Determine the acceleration that electrons will experience
            setElectronAcceleration( potentialDiff );
            for( int i = 0; i < electrons.size(); i++ ) {
                Electron electron = (Electron)electrons.get( i );
                electron.setAcceleration( getElectronAcceleration() );
            }

            // Calling setCurrent() ensures that the current flows in the correct direction
            setCurrent( current );
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
        void voltageChanged( ChangeEvent event );
    }

    static public class ChangeListenerAdapter implements ChangeListener {
        public void energyLevelsChanged( ChangeEvent event ) {
        }

        public void voltageChanged( ChangeEvent event ) {
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

