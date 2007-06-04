/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.module;

import edu.colorado.phet.common.phetcommon.view.ClockControlPanelWithTimeDisplay;
import edu.colorado.phet.opticaltweezers.OTResources;
import edu.colorado.phet.opticaltweezers.defaults.PhysicsDefaults;
import edu.colorado.phet.opticaltweezers.model.*;
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
    private ClockControlPanelWithTimeDisplay _clockControlPanel;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public PhysicsModule() {
        super( OTResources.getString( "title.physicsOfTweezers" ), PhysicsDefaults.CLOCK, PhysicsDefaults.CLOCK_PAUSED );

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
        _clockControlPanel = new ClockControlPanelWithTimeDisplay( (OTClock) getClock() );
        _clockControlPanel.setTimeFormat( PhysicsDefaults.CLOCK_TIME_PATTERN );
        _clockControlPanel.setTimeColumns( PhysicsDefaults.CLOCK_TIME_COLUMNS );
        _clockControlPanel.setUnits( OTResources.getString( "units.time" ) );
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
            clock.setPaused( PhysicsDefaults.CLOCK_PAUSED );
            clock.setDt( PhysicsDefaults.CLOCK_DT_RANGE.getDefault() );
            
            // Bead
            Bead bead = _model.getBead();
            bead.setPosition( PhysicsDefaults.BEAD_POSITION );
            bead.setOrientation( PhysicsDefaults.BEAD_ORIENTATION );
            bead.setDtSubdivisionThreshold( PhysicsDefaults.BEAD_DT_SUBDIVISION_THRESHOLD_RANGE.getDefault() );
            bead.setNumberOfDtSubdivisions( PhysicsDefaults.BEAD_NUMBER_OF_DT_SUBDIVISIONS_RANGE.getDefault() );
            bead.setBrownianMotionScale( PhysicsDefaults.BEAD_BROWNIAN_MOTION_SCALE_RANGE.getDefault() );
            
            // Laser
            Laser laser = _model.getLaser();
            laser.setPosition( PhysicsDefaults.LASER_POSITION );
            laser.setPower( PhysicsDefaults.LASER_POWER_RANGE.getDefault() );
            laser.setRunning( PhysicsDefaults.LASER_RUNNING );
            
            // Fluid
            Fluid fluid = _model.getFluid();
            fluid.setSpeed( PhysicsDefaults.FLUID_SPEED_RANGE.getDefault() );
            fluid.setViscosity( PhysicsDefaults.FLUID_VISCOSITY_RANGE.getDefault() );
            fluid.setTemperature( PhysicsDefaults.FLUID_TEMPERATURE_RANGE.getDefault() );
        }
        
        // Control panel settings that are view-related
        {
            _controlPanel.getClockStepControlPanel().setClockStep( PhysicsDefaults.CLOCK_DT_RANGE.getDefault() );
            _controlPanel.getLaserDisplayControlPanel().setChoice( PhysicsDefaults.LASER_DISPLAY_CHOICE );
            _controlPanel.getBeadChargeControlPanel().setChoice( PhysicsDefaults.BEAD_CHARGE_CHOICE );
            _controlPanel.getForcesControlPanel().setTrapForceSelected( PhysicsDefaults.TRAP_FORCE_SELECTED );
            _controlPanel.getForcesControlPanel().setHorizontalTrapForceChoice( PhysicsDefaults.HORIZONTAL_TRAP_FORCE_CHOICE );
            _controlPanel.getForcesControlPanel().setDragForceSelected( PhysicsDefaults.FLUID_DRAG_FORCE_SELECTED );
            _controlPanel.getForcesControlPanel().setBrownianForceSelected( PhysicsDefaults.BROWNIAN_FORCE_SELECTED );
            _controlPanel.getChartsControlPanel().setPositionHistogramSelected( PhysicsDefaults.POSITION_HISTOGRAM_SELECTED );
            _controlPanel.getChartsControlPanel().setPotentialEnergySelected( PhysicsDefaults.POTENTIAL_ENERGY_CHART_SELECTED );
            _controlPanel.setRulerSelected( PhysicsDefaults.RULER_SELECTED );
            _controlPanel.getAdvancedControlPanel().setAdvancedVisible( PhysicsDefaults.ADVANCED_VISIBLE );
            _controlPanel.getAdvancedControlPanel().setFluidControlSelected( PhysicsDefaults.FLUID_CONTROLS_SELECTED );
            _controlPanel.getAdvancedControlPanel().setMomentumChangeSelected( PhysicsDefaults.MOMENTUM_CHANGE_MODEL_SELECTED );
        }
    }

    public void save( OTConfig appConfig ) {
        // TODO Auto-generated method stub
    }

    public void load( OTConfig appConfig ) {
        // TODO Auto-generated method stub
    }
}
