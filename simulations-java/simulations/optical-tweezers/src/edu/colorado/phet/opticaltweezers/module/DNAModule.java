/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.module;

import edu.colorado.phet.common.phetcommon.view.ClockControlPanelWithTimeDisplay;
import edu.colorado.phet.opticaltweezers.OTResources;
import edu.colorado.phet.opticaltweezers.defaults.DNADefaults;
import edu.colorado.phet.opticaltweezers.defaults.PhysicsDefaults;
import edu.colorado.phet.opticaltweezers.model.*;
import edu.colorado.phet.opticaltweezers.persistence.OTConfig;
import edu.colorado.phet.opticaltweezers.view.DNAStrandNode;

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
            bead.setDtSubdivisionThreshold( DNADefaults.BEAD_DT_SUBDIVISION_THRESHOLD_RANGE.getDefault() );
            bead.setNumberOfDtSubdivisions( DNADefaults.BEAD_NUMBER_OF_DT_SUBDIVISIONS_RANGE.getDefault() );
            bead.setBrownianMotionScale( DNADefaults.BEAD_BROWNIAN_MOTION_SCALE_RANGE.getDefault() );
            bead.setBrownianMotionEnabled( PhysicsDefaults.BEAD_BROWNIAN_MOTION_ENABLED );
            
            // Laser
            Laser laser = _model.getLaser();
            laser.setPosition( DNADefaults.LASER_POSITION );
            laser.setPower( DNADefaults.LASER_POWER_RANGE.getDefault() );
            laser.setRunning( DNADefaults.LASER_RUNNING );
            laser.setTrapForceRatio( DNADefaults.LASER_TRAP_FORCE_RATIO.getDefault() );
            
            // Fluid
            Fluid fluid = _model.getFluid();
            fluid.setEnabled( DNADefaults.FLUID_ENABLED );
            fluid.setSpeed( DNADefaults.FLUID_SPEED_RANGE.getDefault() );
            fluid.setViscosity( DNADefaults.FLUID_VISCOSITY_RANGE.getDefault() );
            fluid.setTemperature( DNADefaults.FLUID_TEMPERATURE_RANGE.getDefault() );
            
            // DNA Strand
            DNAStrand dnaStrand = _model.getDNAStrand();
            dnaStrand.setSpringConstant( DNADefaults.DNA_SPRING_CONSTANT_RANGE.getDefault() );
            dnaStrand.setDragCoefficient( DNADefaults.DNA_DRAG_COEFFICIENT_RANGE.getDefault() );
            dnaStrand.setKickConstant( DNADefaults.DNA_KICK_CONSTANT_RANGE.getDefault() );
            dnaStrand.setNumberOfEvolutionsPerClockTick( DNADefaults.DNA_NUMBER_OF_EVOLUTIONS_PER_CLOCK_STEP_RANGE.getDefault() );
            dnaStrand.setEvolutionDt( DNADefaults.DNA_EVOLUTION_DT_RANGE.getDefault() );
            dnaStrand.setFluidDragCoefficient( DNADefaults.DNA_FLUID_DRAG_COEFFICIENT_RANGE.getDefault() );
            dnaStrand.initializeStrand();
        }
        
        // View 
        {
            // DNA Strand node
            DNAStrandNode dnaStrandNode = _canvas.getDNAStrandNode();
            dnaStrandNode.setPivotsVisible( DNADefaults.DNA_PIVOTS_VISIBLE );
            dnaStrandNode.setExtensionVisible( DNADefaults.DNA_EXTENSION_VISIBLE );
        }
        
        // Control panel settings that are view-related
        {
            _controlPanel.getClockStepControlPanel().setClockStep( DNADefaults.CLOCK_DT_RANGE.getDefault() );
            _controlPanel.getForcesControlPanel().setTrapForceSelected( DNADefaults.TRAP_FORCE_SELECTED );
            _controlPanel.getForcesControlPanel().setHorizontalTrapForceChoice( DNADefaults.HORIZONTAL_TRAP_FORCE_CHOICE );
            _controlPanel.getForcesControlPanel().setDragForceSelected( DNADefaults.FLUID_DRAG_FORCE_SELECTED );
            _controlPanel.getForcesControlPanel().setDNAForceSelected( DNADefaults.DNA_FORCE_SELECTED );
            _controlPanel.setRulerSelected( DNADefaults.RULER_SELECTED );
            _controlPanel.getAdvancedControlPanel().setAdvancedVisible( DNADefaults.ADVANCED_VISIBLE );
            _controlPanel.getAdvancedControlPanel().setFluidControlSelected( DNADefaults.FLUID_CONTROLS_SELECTED );
            _controlPanel.getAdvancedControlPanel().setMomentumChangeSelected( DNADefaults.MOMENTUM_CHANGE_MODEL_SELECTED );
            _controlPanel.getDeveloperControlPanel().getVectorsPanel().setValuesVisible( DNADefaults.VECTOR_VALUES_VISIBLE );
            _controlPanel.getDeveloperControlPanel().getVectorsPanel().setComponentsVisible( DNADefaults.VECTOR_COMPONENTS_VISIBLE );
        }
    }

    public void save( OTConfig appConfig ) {
        // TODO Auto-generated method stub
    }

    public void load( OTConfig appConfig ) {
        // TODO Auto-generated method stub
    }
}
