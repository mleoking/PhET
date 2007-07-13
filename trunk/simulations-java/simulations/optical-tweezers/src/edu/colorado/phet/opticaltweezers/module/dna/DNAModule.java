/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.module.dna;

import edu.colorado.phet.opticaltweezers.OTResources;
import edu.colorado.phet.opticaltweezers.control.OTClockControlPanel;
import edu.colorado.phet.opticaltweezers.model.*;
import edu.colorado.phet.opticaltweezers.module.AbstractModule;
import edu.colorado.phet.opticaltweezers.module.GlobalDefaults;
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
    private OTClockControlPanel _clockControlPanel;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public DNAModule() {
        super( OTResources.getString( "title.funWithDNA" ), DNADefaults.CLOCK, GlobalDefaults.CLOCK_PAUSED );

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
        _clockControlPanel = new OTClockControlPanel( (OTClock) getClock() );
        _clockControlPanel.setTimeColumns( GlobalDefaults.CLOCK_TIME_COLUMNS );
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
            clock.setDt( GlobalDefaults.DEFAULT_DT );
            if ( isActive() ) {
                clock.setPaused( GlobalDefaults.CLOCK_PAUSED );
            }
            
            // Bead
            Bead bead = _model.getBead();
            bead.setPosition( DNADefaults.BEAD_POSITION );
            bead.setOrientation( GlobalDefaults.BEAD_ORIENTATION );
            bead.setDtSubdivisionThreshold( GlobalDefaults.BEAD_DT_SUBDIVISION_THRESHOLD_RANGE.getDefault() );
            bead.setNumberOfDtSubdivisions( GlobalDefaults.BEAD_NUMBER_OF_DT_SUBDIVISIONS_RANGE.getDefault() );
            bead.setBrownianMotionScale( GlobalDefaults.BEAD_BROWNIAN_MOTION_SCALE_RANGE.getDefault() );
            bead.setBrownianMotionEnabled( GlobalDefaults.BEAD_BROWNIAN_MOTION_ENABLED );
            bead.setVerletDtSubdivisionThreshold( GlobalDefaults.BEAD_VERLET_DT_SUBDIVISION_THRESHOLD_RANGE.getDefault() );
            bead.setVerletNumberOfDtSubdivisions( GlobalDefaults.BEAD_VERLET_NUMBER_OF_DT_SUBDIVISIONS_RANGE.getDefault() );
            bead.setVerletAccelerationScale( GlobalDefaults.BEAD_VERLET_ACCELERATION_SCALE_RANGE.getDefault() );
            
            // Laser
            Laser laser = _model.getLaser();
            laser.setPosition( DNADefaults.LASER_POSITION );
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
            _controlPanel.getSimulationSpeedControlPanel().setSimulationSpeed( GlobalDefaults.DEFAULT_DT );
            _controlPanel.getForcesControlPanel().setTrapForceSelected( DNADefaults.TRAP_FORCE_SELECTED );
            _controlPanel.getForcesControlPanel().setHorizontalTrapForceChoice( DNADefaults.HORIZONTAL_TRAP_FORCE_CHOICE );
            _controlPanel.getForcesControlPanel().setDragForceSelected( DNADefaults.FLUID_DRAG_FORCE_SELECTED );
            _controlPanel.getForcesControlPanel().setDNAForceSelected( DNADefaults.DNA_FORCE_SELECTED );
            _controlPanel.getChartsControlPanel().setPositionHistogramSelected( DNADefaults.POSITION_HISTOGRAM_SELECTED );
            _controlPanel.getChartsControlPanel().setPotentialEnergySelected( DNADefaults.POTENTIAL_ENERGY_CHART_SELECTED );
            _controlPanel.getMiscControlPanel().setRulerSelected( DNADefaults.RULER_SELECTED );
            _controlPanel.getMiscControlPanel().setFluidControlsSelected( DNADefaults.FLUID_CONTROLS_SELECTED );
            _controlPanel.getMiscControlPanel().setMomentumChangeSelected( DNADefaults.MOMENTUM_CHANGE_MODEL_SELECTED );
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
