/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.photoelectric.model;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.util.PhysicsUtil;
import edu.colorado.phet.common.model.clock.ClockAdapter;
import edu.colorado.phet.common.model.clock.ClockEvent;
import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.common.util.EventChannel;
import edu.colorado.phet.common.util.ModelEventChannel;
import edu.colorado.phet.dischargelamps.DischargeLampsConfig;
import edu.colorado.phet.dischargelamps.model.*;
import edu.colorado.phet.quantum.model.PhotonEmittedListener;
import edu.colorado.phet.photoelectric.model.util.BeamIntensityMeter;
import edu.colorado.phet.quantum.model.PhotonSource;
import edu.colorado.phet.quantum.*;
import edu.colorado.phet.quantum.model.Beam;
import edu.colorado.phet.quantum.model.*;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.EventObject;
import java.util.List;

/**
 * PhotoelectricModel
 * <p/>
 * Builds on the DischargeLampModel.
 * <p/>
 * Uses a PhotoelectricTarget, which is an extension of Electrode
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class PhotoelectricModel extends DischargeLampModel {

    //----------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------
    public static final int ELECTRON_MODEL_SIMPLE = 1;
    public static final int ELECTRON_MODEL_REALISTIC = 2;

    // Factor to make the analytically reported current different than the photons-per-sec
    // that come from the beam
    public static double CURRENT_JIMMY_FACTOR = 0.015;

    // Factor to make voltage across electrodes display properly calibrated
    public static final double VOLTAGE_SCALE_FACTOR = 1;

    public static double MIN_VOLTAGE = -8;
    public static double MAX_VOLTAGE = 8;
    public static double MIN_WAVELENGTH = 100;
    public static double MAX_WAVELENGTH = 800;
    public static double MAX_PHOTONS_PER_SECOND = 500;
    public static double MAX_CURRENT = MAX_PHOTONS_PER_SECOND * CURRENT_JIMMY_FACTOR / 8;

    //----------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------

    private List photons = new ArrayList();
    private List electrons = new ArrayList();

    // Target specification
    private PhotoelectricTarget target;
    private double defaultTargetPotential = 0;

    private ArrayList electronSources = new ArrayList();
    private ArrayList electronSinks = new ArrayList();

    // Right-hand plate
    private Plate rightHandPlate;

    // Beam specification
    private Beam beam;
    private double defaultBeamWavelength = 400;
    private double beamWidth = 80;
    private double beamHeight = 1000;
    private double beamSourceToTargetDist = 260;
    private double beamAngle = Math.toRadians( 130 );
    private double beamFanout = Math.toRadians( 5 );
    private Ammeter ammeter;
    private BeamIntensityMeter beamIntensityMeter;
    private double current;
    private double voltage;
    private double wavelength;

    //----------------------------------------------------------------
    // Contructors and initialization
    //----------------------------------------------------------------

    /**
     *
     */
    public PhotoelectricModel( IClock clock ) {

        // Set the max and min voltage of the battery
        getBattery().setMaxVoltage( MAX_VOLTAGE );
        getBattery().setMinVoltage( MIN_VOLTAGE );

        // Create a photon beam and add a listener that will add the photons it produces to the model
        double alpha = beamAngle;
        Point2D beamLocation = new Point2D.Double( DischargeLampsConfig.CATHODE_LOCATION.getX() - Math.cos( alpha ) * beamSourceToTargetDist,
                                                   DischargeLampsConfig.CATHODE_LOCATION.getY() - Math.sin( alpha ) * beamSourceToTargetDist );
        beam = new Beam( defaultBeamWavelength,
                         beamLocation,
                         beamHeight,
                         beamWidth,
                         new Vector2D.Double( Math.cos( beamAngle ), Math.sin( beamAngle ) ),
                         MAX_PHOTONS_PER_SECOND,
                         beamFanout );
        addModelElement( beam );
        beam.setPhotonsPerSecond( 0 );
        beam.setEnabled( true );
        beam.addPhotonEmittedListener( new PhotonTracker() );
        beam.addRateChangeListener( new PhotonSource.RateChangeListener() {
            public void rateChangeOccurred( Beam.RateChangeEvent event ) {
                changeListenerProxy.beamIntensityChanged( new ChangeEvent( PhotoelectricModel.this ) );
            }
        } );

        // Create the right-hand plate
        rightHandPlate = new Plate( this,
                                    this,
                                    DischargeLampsConfig.ANODE_LINE.getP1(),
                                    DischargeLampsConfig.ANODE_LINE.getP2() );
        this.addModelElement( rightHandPlate );

        // Create the target plate.
        target = new PhotoelectricTarget( this, DischargeLampsConfig.CATHODE_LINE.getP1(),
                                          DischargeLampsConfig.CATHODE_LINE.getP2() );
        addModelElement( target );
        target.setPotential( defaultTargetPotential );
        target.addListener( new ElectronTracker() );
        target.addMaterialChangeListener( new PhotoelectricTarget.MaterialChangeListener() {
            public void materialChanged( PhotoelectricTarget.MaterialChangeEvent event ) {
                changeListenerProxy.targetMaterialChanged( new ChangeEvent( this ) );
            }
        } );

        // Set the elements in the parent class that correspond to the target and reight-hand plate
        setRightHandPlate( rightHandPlate );
        setLeftHandPlate( target );

        // Add a listener that will notify the target it if the anode's potential changes
        rightHandPlate.addStateChangeListener( new ElectrodeStateChangeListener() );
        target.addStateChangeListener( new ElectrodeStateChangeListener() );

        //----------------------------------------------------------------
        // Intrumentation
        //----------------------------------------------------------------

        // Add an ammeter to the right-hand-plate
        ammeter = new Ammeter( clock );
        getRightHandPlate().addElectronAbsorptionListener( new ElectronSink.ElectronAbsorptionListener() {
            public void electronAbsorbed( ElectronSink.ElectronAbsorptionEvent event ) {
                ammeter.recordElectron();
            }
        } );

        // Add an intensity meter for the beam
        beamIntensityMeter = new BeamIntensityMeter( clock );
        getBeam().addPhotonEmittedListener( new PhotonEmittedListener() {
            public void photonEmitted( PhotonEmittedEvent event ) {
                beamIntensityMeter.recordPhoton();
            }
        } );
    }

    /**
     * Handles production of photons from the cathode
     *
     * @param dt
     */
    public void stepInTime( double dt ) {

        super.stepInTime( dt );

        // Check for photons hitting the cathode
        for( int i = 0; i < photons.size(); i++ ) {
            Photon photon = (Photon)photons.get( i );
            if( target.isHitByPhoton( photon ) ) {
                target.handlePhotonCollision( photon );
                photon.removeFromSystem();
            }
        }

        // Check for changes is state, and notify listeners of changes
        if( getCurrent() != current ) {
            current = getCurrent();
            changeListenerProxy.currentChanged( new ChangeEvent( this ) );
        }
        if( getVoltage() != voltage ) {
            voltage = getVoltage();
            changeListenerProxy.voltageChanged( new ChangeEvent( this ) );
        }
        if( beam.getWavelength() != wavelength ) {
            wavelength = beam.getWavelength();
            changeListenerProxy.wavelengthChanged( new ChangeEvent( this ) );
        }

        // Check for electrons that get out of the tube (Only matters if the
        // electrons leave the target at an angle)
        for( int i = 0; i < electrons.size(); i++ ) {
            Electron electron = (Electron)electrons.get( i );
            if( !getTube().getBounds().contains( electron.getPosition() ) ) {
                electron.leaveSystem();
            }
        }
    }

    //----------------------------------------------------------------
    // Getters and setters 
    //----------------------------------------------------------------

    public PhotoelectricTarget getTarget() {
        return target;
    }

    public Beam getBeam() {
        return beam;
    }

    public Plate getRightHandPlate() {
        return rightHandPlate;
    }

    public Ammeter getAmmeter() {
        return ammeter;
    }

    public BeamIntensityMeter getBeamIntensityMeter() {
        return beamIntensityMeter;
    }

    public double getAnodePotential() {
        return rightHandPlate.getPotential() - target.getPotential();
    }

    public double getVoltage() {
        return -getAnodePotential();
    }

    /**
     * Tells the current as a function of the photon rate of the beam and the work function
     * of the target material.
     *
     * @return The current that will hit the cathode based on the electrons that are currently leaving
     * the anode
     */
    public double getCurrent() {
        return getCurrentForVoltage( getVoltage() );
    }

    public double getCurrentForVoltage( double voltage ) {
        double electronsPerSecondFromTarget = 0;
        double electronsPerSecondToAnode = 0;
        if( target.getMaterial().getEnergyAbsorptionStrategy() instanceof MetalEnergyAbsorptionStrategy ) {
            // The fraction of collisions that will kick off an electron is equal to the amount of energy each
            // photon has that is greater than the work function, divided by the absorption strategy's
            // total energy depth, with a ceiling of 1.
            double photonEnergyBeyondWorkFunction = PhysicsUtil.wavelengthToEnergy( beam.getWavelength() ) - target.getMaterial().getWorkFunction();
            double electronRateAsFractionOfPhotonRate = Math.min( photonEnergyBeyondWorkFunction / MetalEnergyAbsorptionStrategy.TOTAL_ENERGY_DEPTH,
                                                                  1 );
            electronsPerSecondFromTarget = electronRateAsFractionOfPhotonRate * beam.getPhotonsPerSecond();
            double retardingVoltage = voltage < 0 ? -voltage : 0;
            double fractionOfPhotonsMoreEnergeticThanRetardingVoltage = Math.max( 0,
                                                                                  Math.min( ( photonEnergyBeyondWorkFunction - retardingVoltage )
                                                                                            / MetalEnergyAbsorptionStrategy.TOTAL_ENERGY_DEPTH, 1 ) );
            electronsPerSecondToAnode = electronsPerSecondFromTarget * fractionOfPhotonsMoreEnergeticThanRetardingVoltage;
        }
        else {
            // If the stopping voltage is less than the voltage across the plates, we get a current
            // equal to the number of photons per second. (We assume there is one electron for every photon).
            // Otherwise, there is no current
            electronsPerSecondFromTarget = beam.getPhotonsPerSecond();
            electronsPerSecondToAnode = electronsPerSecondFromTarget;
            double retardingVoltage = voltage < 0 ? voltage : 0;
            electronsPerSecondToAnode = getStoppingVoltage() < retardingVoltage ? electronsPerSecondFromTarget : 0;
        }
        return electronsPerSecondToAnode * CURRENT_JIMMY_FACTOR;
    }

    /**
     * Returns the stopping voltage for electrons kicked off the current target material
     * by the current wavelength of light
     *
     * @return The stopping voltage
     */
    public double getStoppingVoltage() {
        double photonEnergy = PhysicsUtil.wavelengthToEnergy( beam.getWavelength() );
        double stoppingVoltage = getWorkFunction() - photonEnergy;
        return stoppingVoltage;
    }

    public double getWorkFunction() {
        return ( target.getMaterial().getWorkFunction() );
    }

    public double getWavelength() {
        return getBeam().getWavelength();
    }

    protected void setElectronAcceleration( double potentialDiff ) {
        super.setElectronAcceleration( potentialDiff * 0.2865,
                                       target.getPosition().distance( rightHandPlate.getPosition() ) );
    }

    //----------------------------------------------------------------
    // Listeners for tracking the creation and destruction of
    // certain model elements
    //----------------------------------------------------------------

    /**
     * Tracks the creation and destruction of photons
     */
    private class PhotonTracker implements PhotonEmittedListener, Photon.LeftSystemEventListener {
        public void photonEmitted( PhotonEmittedEvent event ) {
            Photon photon = event.getPhoton();
            addModelElement( photon );
            photons.add( photon );
            photon.addLeftSystemListener( this );
        }

        public void leftSystemEventOccurred( Photon.LeftSystemEvent event ) {
            photons.remove( event.getPhoton() );
        }
    }

    /**
     * Track the creation and destruction of electrions
     */
    private class ElectronTracker implements ElectronSource.ElectronProductionListener, Electron.ChangeListener {

        public void electronProduced( ElectronSource.ElectronProductionEvent event ) {
            Electron electron = event.getElectron();
            addModelElement( electron );
            electrons.add( electron );
            electron.addChangeListener( this );
        }

        public void leftSystem( Electron.ChangeEvent changeEvent ) {
            electrons.remove( changeEvent.getElectron() );
        }

        public void energyChanged( Electron.ChangeEvent changeEvent ) {
            // noop
        }
    }

    private class ElectrodeStateChangeListener implements Electrode.StateChangeListener {
        public void potentialChanged( Electrode.StateChangeEvent event ) {
            double potentialDiff = target.getPotential() - rightHandPlate.getPotential();

            // Determine the acceleration that electrons will experience
            setElectronAcceleration( potentialDiff );
            for( int i = 0; i < electrons.size(); i++ ) {
                Electron electron = (Electron)electrons.get( i );
                electron.setAcceleration( getElectronAcceleration() );
            }

            // Calling setCurrent() ensures that the current flows in the correct direction
            setCurrent( current );
        }
    }

    //-----------------------------------------------------------------
    // Events and listeners
    //-----------------------------------------------------------------
    private EventChannel changeEventChannel = new ModelEventChannel( ChangeListener.class );
    private ChangeListener changeListenerProxy = (ChangeListener)changeEventChannel.getListenerProxy();

    public void addChangeListener( ChangeListener listener ) {
        changeEventChannel.addListener( listener );
    }

    public void removeChangeListener( ChangeListener listener ) {
        changeEventChannel.removeListener( listener );
    }

    public class ChangeEvent extends EventObject {
        public ChangeEvent( Object source ) {
            super( source );
        }

        public PhotoelectricModel getPhotoelectricModel() {
            return (PhotoelectricModel)getSource();
        }
    }

    public interface ChangeListener extends EventListener {
        void currentChanged( ChangeEvent event );

        void voltageChanged( ChangeEvent event );

        void wavelengthChanged( ChangeEvent event );

        void targetMaterialChanged( ChangeEvent event );

        void beamIntensityChanged( ChangeEvent event );
    }

    public static class ChangeListenerAdapter implements ChangeListener {
        public void currentChanged( ChangeEvent event ) {
        }

        public void voltageChanged( ChangeEvent event ) {
        }

        public void wavelengthChanged( ChangeEvent event ) {
        }

        public void targetMaterialChanged( ChangeEvent event ) {
        }

        public void beamIntensityChanged( ChangeEvent event ) {
        }
    }
}
