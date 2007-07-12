/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.module.physics;

import edu.colorado.phet.opticaltweezers.OTResources;
import edu.colorado.phet.opticaltweezers.control.OTClockControlPanel;
import edu.colorado.phet.opticaltweezers.model.*;
import edu.colorado.phet.opticaltweezers.module.AbstractModule;
import edu.colorado.phet.opticaltweezers.module.GlobalDefaults;
import edu.colorado.phet.opticaltweezers.persistence.OTConfig;

/**
 * PhysicsModule is the "Physics of Tweezers" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PhysicsModule extends AbstractModule {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private PhysicsModel _model;
    private PhysicsCanvas _canvas;
    private PhysicsControlPanel _controlPanel;
    private OTClockControlPanel _clockControlPanel;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public PhysicsModule() {
        super( OTResources.getString( "title.physicsOfTweezers" ), PhysicsDefaults.CLOCK, GlobalDefaults.CLOCK_PAUSED );

        // Model
        OTClock clock = (OTClock) getClock();
        _model = new PhysicsModel( clock );
        
        // Canvas
        _canvas = new PhysicsCanvas( _model );
        setSimulationPanel( _canvas );

        // Control Panel
        _controlPanel = new PhysicsControlPanel( this );
        setControlPanel( _controlPanel );
        
        // Clock controls
        _clockControlPanel = new OTClockControlPanel( (OTClock) getClock() );
        _clockControlPanel.setTimeColumns( GlobalDefaults.CLOCK_TIME_COLUMNS );
        setClockControlPanel( _clockControlPanel );
        
        // Set initial state
        resetAll();
    }
   
    //----------------------------------------------------------------------------
    // Mutators and accessors
    //----------------------------------------------------------------------------
    
    public PhysicsModel getPhysicsModel() {
        return _model;
    }
    
    public PhysicsCanvas getPhysicsCanvas() {
        return _canvas;
    }
    
    //----------------------------------------------------------------------------
    // Module overrides
    //----------------------------------------------------------------------------
    
    /**
     * Indicates whether this module has help.
     * 
     * @return true or false
     */
    public boolean hasHelp() {
        return false;
    }

    /**
     * Close all dialogs when switching to another module.
     */
    public void deactivate() {
        _controlPanel.closeAllDialogs();
        super.deactivate();
    }
    
    //----------------------------------------------------------------------------
    // AbstractModule implementation
    //----------------------------------------------------------------------------
    
    public void resetAll() {
        
        // Model
        {
            // Clock
            OTClock clock = _model.getClock();
            clock.setDt( GlobalDefaults.DEFAULT_DT );
            if ( isActive() ) {
                clock.setPaused( GlobalDefaults.CLOCK_PAUSED );
            }
            
            // Bead
            Bead bead = _model.getBead();
            bead.setPosition( PhysicsDefaults.BEAD_POSITION );
            bead.setOrientation( GlobalDefaults.BEAD_ORIENTATION );
            bead.setDtSubdivisionThreshold( GlobalDefaults.BEAD_DT_SUBDIVISION_THRESHOLD_RANGE.getDefault() );
            bead.setNumberOfDtSubdivisions( GlobalDefaults.BEAD_NUMBER_OF_DT_SUBDIVISIONS_RANGE.getDefault() );
            bead.setBrownianMotionScale( GlobalDefaults.BEAD_BROWNIAN_MOTION_SCALE_RANGE.getDefault() );
            bead.setBrownianMotionEnabled( GlobalDefaults.BEAD_BROWNIAN_MOTION_ENABLED );
            bead.setVerletAccelerationScale( GlobalDefaults.BEAD_VERLET_ACCELERATION_SCALE_RANGE.getDefault() );
            
            // Laser
            Laser laser = _model.getLaser();
            laser.setPosition( PhysicsDefaults.LASER_POSITION );
            laser.setPower( GlobalDefaults.LASER_POWER_RANGE.getDefault() );
            laser.setRunning( GlobalDefaults.LASER_RUNNING );
            laser.setTrapForceRatio( GlobalDefaults.LASER_TRAP_FORCE_RATIO.getDefault() );
            laser.setElectricFieldScale( GlobalDefaults.LASER_ELECTRIC_FIELD_SCALE_RANGE.getDefault() );
            
            // Fluid
            Fluid fluid = _model.getFluid();
            fluid.setEnabled( GlobalDefaults.FLUID_ENABLED );
            fluid.setSpeed( GlobalDefaults.FLUID_SPEED_RANGE.getDefault() );
            fluid.setViscosity( GlobalDefaults.FLUID_VISCOSITY_RANGE.getDefault() );
            fluid.setTemperature( GlobalDefaults.FLUID_TEMPERATURE_RANGE.getDefault() );
        }
        
        // Control panel settings that are view-related
        {
            _controlPanel.getSimulationSpeedControlPanel().setSimulationSpeed( GlobalDefaults.DEFAULT_DT );
            _controlPanel.getLaserDisplayControlPanel().setDisplaySelection( PhysicsDefaults.LASER_BEAM_VISIBLE, PhysicsDefaults.LASER_ELECTRIC_FIELD_VISIBLE );
            _controlPanel.getBeadChargeControlPanel().setChoice( PhysicsDefaults.BEAD_CHARGE_CHOICE );
            _controlPanel.getForcesControlPanel().setTrapForceSelected( PhysicsDefaults.TRAP_FORCE_SELECTED );
            _controlPanel.getForcesControlPanel().setHorizontalTrapForceChoice( PhysicsDefaults.HORIZONTAL_TRAP_FORCE_CHOICE );
            _controlPanel.getForcesControlPanel().setDragForceSelected( PhysicsDefaults.FLUID_DRAG_FORCE_SELECTED );
            _controlPanel.getChartsControlPanel().setPositionHistogramSelected( PhysicsDefaults.POSITION_HISTOGRAM_SELECTED );
            _controlPanel.getChartsControlPanel().setPotentialEnergySelected( PhysicsDefaults.POTENTIAL_ENERGY_CHART_SELECTED );
            _controlPanel.getMiscControlPanel().setRulerSelected( PhysicsDefaults.RULER_SELECTED );
            _controlPanel.getMiscControlPanel().setFluidControlsSelected( PhysicsDefaults.FLUID_CONTROLS_SELECTED );
            _controlPanel.getMiscControlPanel().setMomentumChangeSelected( PhysicsDefaults.MOMENTUM_CHANGE_MODEL_SELECTED );
            _controlPanel.getDeveloperControlPanel().getVectorsPanel().setValuesVisible( PhysicsDefaults.VECTOR_VALUES_VISIBLE );
            _controlPanel.getDeveloperControlPanel().getVectorsPanel().setComponentsVisible( PhysicsDefaults.VECTOR_COMPONENTS_VISIBLE );
        }
    }

    public void save( OTConfig appConfig ) {
        // TODO Auto-generated method stub
    }

    public void load( OTConfig appConfig ) {
        // TODO Auto-generated method stub
    }
}
