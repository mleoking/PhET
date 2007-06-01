/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.module;

import edu.colorado.phet.common.phetcommon.view.ClockControlPanelWithTimeDisplay;
import edu.colorado.phet.opticaltweezers.OTResources;
import edu.colorado.phet.opticaltweezers.defaults.DNADefaults;
import edu.colorado.phet.opticaltweezers.model.*;
import edu.colorado.phet.opticaltweezers.persistence.OTConfig;

/**
 * DNAModule is the "Fun with DNA" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DNAModule extends AbstractModule {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private DNAModel _model;
    private DNACanvas _canvas;
    private DNAControlPanel _controlPanel;
    private ClockControlPanelWithTimeDisplay _clockControlPanel;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public DNAModule() {
        super( OTResources.getString( "title.funWithDNA" ), DNADefaults.CLOCK, DNADefaults.CLOCK_PAUSED );

        // Model
        OTClock clock = (OTClock) getClock();
        _model = new DNAModel( clock );
        
        // Canvas
        _canvas = new DNACanvas( _model );
        setSimulationPanel( _canvas );

        // Control Panel
        _controlPanel = new DNAControlPanel( this );
        setControlPanel( _controlPanel );
        
        // Clock controls
        _clockControlPanel = new ClockControlPanelWithTimeDisplay( (OTClock) getClock() );
        _clockControlPanel.setTimeFormat( DNADefaults.CLOCK_TIME_PATTERN );
        _clockControlPanel.setTimeColumns( DNADefaults.CLOCK_TIME_COLUMNS );
        _clockControlPanel.setUnits( OTResources.getString( "units.time" ) );
        setClockControlPanel( _clockControlPanel );
        
        // Set initial state
        resetAll();
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public DNAModel getDNAModel() {
        return _model;
    }
    
    public DNACanvas getDNACanvas() {
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
            clock.setPaused( DNADefaults.CLOCK_PAUSED );
            clock.setDt( DNADefaults.CLOCK_DT_RANGE.getDefault() );
            
            // Bead
            Bead bead = _model.getBead();
            bead.setPosition( DNADefaults.BEAD_POSITION );
            bead.setOrientation( DNADefaults.BEAD_ORIENTATION );
            
            // Laser
            Laser laser = _model.getLaser();
            laser.setPosition( DNADefaults.LASER_POSITION );
            laser.setPower( DNADefaults.LASER_POWER_RANGE.getDefault() );
            laser.setRunning( DNADefaults.LASER_RUNNING );
            
            // Fluid
            Fluid fluid = _model.getFluid();
            fluid.setSpeed( DNADefaults.FLUID_SPEED_RANGE.getDefault() );
            fluid.setViscosity( DNADefaults.FLUID_VISCOSITY_RANGE.getDefault() );
            fluid.setTemperature( DNADefaults.FLUID_TEMPERATURE_RANGE.getDefault() );
            
            // DNA Strand
            DNAStrand dnaStrand = _model.getDNAStrand();
            dnaStrand.setTailPosition( DNADefaults.DNA_STRAND_TAIL_POSITION );
        }
        
        // Control panel settings that are view-related
        {
            _controlPanel.getClockStepControlPanel().setClockStep( DNADefaults.CLOCK_DT_RANGE.getDefault() );
            _controlPanel.getForcesControlPanel().setTrapForceSelected( DNADefaults.TRAP_FORCE_SELECTED );
            _controlPanel.getForcesControlPanel().setHorizontalTrapForceChoice( DNADefaults.HORIZONTAL_TRAP_FORCE_CHOICE );
            _controlPanel.getForcesControlPanel().setDragForceSelected( DNADefaults.FLUID_DRAG_FORCE_SELECTED );
            _controlPanel.getForcesControlPanel().setBrownianForceSelected( DNADefaults.BROWNIAN_FORCE_SELECTED );
            _controlPanel.getForcesControlPanel().setDNAForceSelected( DNADefaults.DNA_FORCE_SELECTED );
            _controlPanel.setRulerSelected( DNADefaults.RULER_SELECTED );
            _controlPanel.getAdvancedControlPanel().setAdvancedVisible( DNADefaults.ADVANCED_VISIBLE );
            _controlPanel.getAdvancedControlPanel().setFluidControlSelected( DNADefaults.FLUID_CONTROLS_SELECTED );
            _controlPanel.getAdvancedControlPanel().setMomentumChangeSelected( DNADefaults.MOMENTUM_CHANGE_MODEL_SELECTED );
        }
    }

    public void save( OTConfig appConfig ) {
        // TODO Auto-generated method stub
    }

    public void load( OTConfig appConfig ) {
        // TODO Auto-generated method stub
    }
}
