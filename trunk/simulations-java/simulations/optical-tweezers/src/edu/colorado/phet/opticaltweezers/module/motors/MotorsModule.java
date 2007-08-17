/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.module.motors;

import edu.colorado.phet.common.piccolophet.help.HelpBalloon;
import edu.colorado.phet.common.piccolophet.help.HelpPane;
import edu.colorado.phet.opticaltweezers.OTResources;
import edu.colorado.phet.opticaltweezers.control.OTClockControlPanel;
import edu.colorado.phet.opticaltweezers.defaults.GlobalDefaults;
import edu.colorado.phet.opticaltweezers.defaults.MotorsDefaults;
import edu.colorado.phet.opticaltweezers.model.*;
import edu.colorado.phet.opticaltweezers.module.AbstractModule;
import edu.colorado.phet.opticaltweezers.persistence.OTConfig;
import edu.colorado.phet.opticaltweezers.view.DNAStrandNode;

/**
 * MotorsModule is the "Molecular Motors" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MotorsModule extends AbstractModule {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private MotorsModel _model;
    private MotorsCanvas _canvas;
    private MotorsControlPanel _controlPanel;
    private OTClockControlPanel _clockControlPanel;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public MotorsModule() {
        super( OTResources.getString( "title.molecularMotors" ), MotorsDefaults.CLOCK, GlobalDefaults.CLOCK_PAUSED );

        // Model
        OTClock clock = (OTClock) getClock();
        _model = new MotorsModel( clock );
        
        // Canvas
        _canvas = new MotorsCanvas( _model );
        setSimulationPanel( _canvas );

        // Control Panel
        _controlPanel = new MotorsControlPanel( this );
        setControlPanel( _controlPanel );
        
        // Clock controls
        _clockControlPanel = new OTClockControlPanel( (OTClock) getClock() );
        _clockControlPanel.setTimeColumns( GlobalDefaults.CLOCK_TIME_COLUMNS );
        setClockControlPanel( _clockControlPanel );
        
        // Help
        if ( hasHelp() ) {
            HelpPane helpPane = getDefaultHelpPane();
            
            HelpBalloon beadHelp = new HelpBalloon( helpPane, OTResources.getString( "help.bead" ), HelpBalloon.RIGHT_CENTER, 20 );
            helpPane.add( beadHelp );
            beadHelp.pointAt( _canvas.getBeadNode(), _canvas );
            
            HelpBalloon laserHelp = new HelpBalloon( helpPane, OTResources.getString( "help.laser" ), HelpBalloon.RIGHT_CENTER, 20 );
            helpPane.add( laserHelp );
            laserHelp.pointAt( _canvas.getLaserNode().getLeftHandleNode(), _canvas );
            
            HelpBalloon rulerNode = new HelpBalloon( helpPane, OTResources.getString( "help.ruler" ), HelpBalloon.TOP_CENTER, 20 );
            helpPane.add( rulerNode );
            rulerNode.pointAt( _canvas.getRulerNode(), _canvas );
        }
        
        // Set initial state
        resetAll();
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public MotorsModel getMotorsModel() {
        return _model;
    }
    
    public MotorsCanvas getMotorsCanvas() {
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
        return true;
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
            bead.setPosition( MotorsDefaults.BEAD_POSITION );
            bead.setOrientation( GlobalDefaults.BEAD_ORIENTATION );
            bead.setDtSubdivisionThreshold( GlobalDefaults.BEAD_DT_SUBDIVISION_THRESHOLD_RANGE.getDefault() );
            bead.setNumberOfDtSubdivisions( GlobalDefaults.BEAD_NUMBER_OF_DT_SUBDIVISIONS_RANGE.getDefault() );
            bead.setBrownianMotionScale( GlobalDefaults.BEAD_BROWNIAN_MOTION_SCALE_RANGE.getDefault() );
            bead.setBrownianMotionEnabled( GlobalDefaults.BEAD_BROWNIAN_MOTION_ENABLED );
            bead.setVerletDtSubdivisionThreshold( GlobalDefaults.BEAD_VERLET_DT_SUBDIVISION_THRESHOLD_RANGE.getDefault() );
            bead.setVerletNumberOfDtSubdivisions( GlobalDefaults.BEAD_VERLET_NUMBER_OF_DT_SUBDIVISIONS_RANGE.getDefault() );
            bead.setVerletAccelerationScale( GlobalDefaults.BEAD_VERLET_ACCELERATION_SCALE_RANGE.getDefault() );
            bead.setVacuumFastThreshold( GlobalDefaults.BEAD_VACUUM_FAST_THRESHOLD_RANGE.getDefault() );
            bead.setVacuumFastDt( GlobalDefaults.BEAD_VACUUM_FAST_DT_RANGE.getDefault() );
            bead.setVacuumFastPower( GlobalDefaults.BEAD_VACUUM_FAST_POWER_RANGE.getDefault() );
            
            // Laser
            Laser laser = _model.getLaser();
            laser.setPosition( MotorsDefaults.LASER_POSITION );
            laser.setPower( GlobalDefaults.LASER_POWER_RANGE.getDefault() );
            laser.setRunning( GlobalDefaults.LASER_RUNNING );
            laser.setTrapForceRatio( GlobalDefaults.LASER_TRAP_FORCE_RATIO.getDefault() );
            laser.setElectricFieldScale( GlobalDefaults.LASER_ELECTRIC_FIELD_SCALE_RANGE.getDefault() );
            
            // Fluid
            Fluid fluid = _model.getFluid();
            fluid.setSpeed( GlobalDefaults.FLUID_SPEED_RANGE.getDefault() );
            fluid.setViscosity( GlobalDefaults.FLUID_VISCOSITY_RANGE.getDefault() );
            fluid.setTemperature( GlobalDefaults.FLUID_TEMPERATURE_RANGE.getDefault() );
            
            // DNA Strand
            DNAStrand dnaStrand = _model.getDNAStrand();
            dnaStrand.setSpringConstant( MotorsDefaults.DNA_SPRING_CONSTANT_RANGE.getDefault() );
            dnaStrand.setDragCoefficient( MotorsDefaults.DNA_DRAG_COEFFICIENT_RANGE.getDefault() );
            dnaStrand.setKickConstant( MotorsDefaults.DNA_KICK_CONSTANT_RANGE.getDefault() );
            dnaStrand.setNumberOfEvolutionsPerClockTick( MotorsDefaults.DNA_NUMBER_OF_EVOLUTIONS_PER_CLOCK_STEP_RANGE.getDefault() );
            dnaStrand.setEvolutionDt( MotorsDefaults.DNA_EVOLUTION_DT_RANGE.getDefault() );
            dnaStrand.setFluidDragCoefficient( MotorsDefaults.DNA_FLUID_DRAG_COEFFICIENT_RANGE.getDefault() );
            dnaStrand.initializeStrand();
        }
        
        // View 
        {
            // DNA Strand node
            DNAStrandNode dnaStrandNode = _canvas.getDNAStrandNode();
            dnaStrandNode.setPivotsVisible( MotorsDefaults.DNA_PIVOTS_VISIBLE );
            dnaStrandNode.setExtensionVisible( MotorsDefaults.DNA_EXTENSION_VISIBLE );
        }
        
        // Control panel settings that are view-related
        {
            _controlPanel.getSimulationSpeedControlPanel().setSimulationSpeed( GlobalDefaults.DEFAULT_DT );
            _controlPanel.getForcesControlPanel().setTrapForceSelected( MotorsDefaults.TRAP_FORCE_SELECTED );
            _controlPanel.getForcesControlPanel().setDragForceSelected( MotorsDefaults.FLUID_DRAG_FORCE_SELECTED );
            _controlPanel.getForcesControlPanel().setDNAForceSelected( MotorsDefaults.DNA_FORCE_SELECTED );
            _controlPanel.getChartsControlPanel().setPositionHistogramSelected( MotorsDefaults.POSITION_HISTOGRAM_SELECTED );
            _controlPanel.getChartsControlPanel().setPotentialEnergySelected( MotorsDefaults.POTENTIAL_ENERGY_CHART_SELECTED );
            _controlPanel.getMiscControlPanel().setRulerSelected( MotorsDefaults.RULER_SELECTED );
            _controlPanel.getMiscControlPanel().setFluidControlsSelected( MotorsDefaults.FLUID_CONTROLS_SELECTED );
            _controlPanel.getDeveloperControlPanel().getVectorsPanel().setValuesVisible( MotorsDefaults.VECTOR_VALUES_VISIBLE );
            _controlPanel.getDeveloperControlPanel().getVectorsPanel().setComponentsVisible( MotorsDefaults.VECTOR_COMPONENTS_VISIBLE );
        }
    }

    public void save( OTConfig appConfig ) {
        //XXX
    }

    public void load( OTConfig appConfig ) {
        //XXX
    }
}

