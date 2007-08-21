/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.module.dna;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.piccolophet.help.HelpBalloon;
import edu.colorado.phet.common.piccolophet.help.HelpPane;
import edu.colorado.phet.opticaltweezers.OTResources;
import edu.colorado.phet.opticaltweezers.control.OTClockControlPanel;
import edu.colorado.phet.opticaltweezers.defaults.DNADefaults;
import edu.colorado.phet.opticaltweezers.model.*;
import edu.colorado.phet.opticaltweezers.module.AbstractModule;
import edu.colorado.phet.opticaltweezers.persistence.DNAConfig;
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
        _clockControlPanel = new OTClockControlPanel( (OTClock) getClock() );
        _clockControlPanel.setTimeColumns( DNADefaults.CLOCK_TIME_COLUMNS );
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
            clock.setDt( DNADefaults.DEFAULT_DT );
            if ( isActive() ) {
                clock.setPaused( DNADefaults.CLOCK_PAUSED );
            }
            
            // Bead
            Bead bead = _model.getBead();
            bead.setPosition( DNADefaults.BEAD_POSITION );
            bead.setOrientation( DNADefaults.BEAD_ORIENTATION );
            bead.setDtSubdivisionThreshold( DNADefaults.BEAD_DT_SUBDIVISION_THRESHOLD_RANGE.getDefault() );
            bead.setNumberOfDtSubdivisions( DNADefaults.BEAD_NUMBER_OF_DT_SUBDIVISIONS_RANGE.getDefault() );
            bead.setBrownianMotionScale( DNADefaults.BEAD_BROWNIAN_MOTION_SCALE_RANGE.getDefault() );
            bead.setBrownianMotionEnabled( DNADefaults.BEAD_BROWNIAN_MOTION_ENABLED );
            bead.setVerletDtSubdivisionThreshold( DNADefaults.BEAD_VERLET_DT_SUBDIVISION_THRESHOLD_RANGE.getDefault() );
            bead.setVerletNumberOfDtSubdivisions( DNADefaults.BEAD_VERLET_NUMBER_OF_DT_SUBDIVISIONS_RANGE.getDefault() );
            bead.setVerletAccelerationScale( DNADefaults.BEAD_VERLET_ACCELERATION_SCALE_RANGE.getDefault() );
            bead.setVacuumFastThreshold( DNADefaults.BEAD_VACUUM_FAST_THRESHOLD_RANGE.getDefault() );
            bead.setVacuumFastDt( DNADefaults.BEAD_VACUUM_FAST_DT_RANGE.getDefault() );
            bead.setVacuumFastPower( DNADefaults.BEAD_VACUUM_FAST_POWER_RANGE.getDefault() );
            
            // Laser
            Laser laser = _model.getLaser();
            laser.setPosition( DNADefaults.LASER_POSITION );
            laser.setPower( DNADefaults.LASER_POWER_RANGE.getDefault() );
            laser.setRunning( DNADefaults.LASER_RUNNING );
            laser.setTrapForceRatio( DNADefaults.LASER_TRAP_FORCE_RATIO.getDefault() );
            laser.setElectricFieldScale( DNADefaults.LASER_ELECTRIC_FIELD_SCALE_RANGE.getDefault() );
            
            // Fluid
            Fluid fluid = _model.getFluid();
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
            _controlPanel.getSimulationSpeedControlPanel().setSimulationSpeed( DNADefaults.DEFAULT_DT );
            _controlPanel.getForcesControlPanel().setTrapForceSelected( DNADefaults.TRAP_FORCE_SELECTED );
            _controlPanel.getForcesControlPanel().setDragForceSelected( DNADefaults.FLUID_DRAG_FORCE_SELECTED );
            _controlPanel.getForcesControlPanel().setDNAForceSelected( DNADefaults.DNA_FORCE_SELECTED );
            _controlPanel.getForcesControlPanel().setShowValuesSelected( DNADefaults.FORCE_VECTOR_VALUES_VISIBLE );
            _controlPanel.getChartsControlPanel().setPositionHistogramSelected( DNADefaults.POSITION_HISTOGRAM_SELECTED );
            _controlPanel.getChartsControlPanel().setPotentialEnergySelected( DNADefaults.POTENTIAL_ENERGY_CHART_SELECTED );
            _controlPanel.getMiscControlPanel().setRulerSelected( DNADefaults.RULER_SELECTED );
            _controlPanel.getMiscControlPanel().setFluidControlsSelected( DNADefaults.FLUID_CONTROLS_SELECTED );
            _controlPanel.getDeveloperControlPanel().getVectorsPanel().setComponentsVisible( DNADefaults.FORCE_VECTOR_COMPONENTS_VISIBLE );
        }
    }

    public void save( OTConfig appConfig ) {
        
        DNAConfig config = appConfig.getDNAConfig();
        DNAModel model = getDNAModel();
        
        // Module
        config.setActive( isActive() );
        
        // Clock
        OTClock clock = model.getClock();
        config.setClockRunning( clock.isRunning() );
        config.setClockDt( clock.getDt() );
        
        // Laser
        Laser laser = model.getLaser();
        config.setLaserX( laser.getX() );
        config.setLaserRunning( laser.isRunning() );
        config.setLaserPower( laser.getPower() );

        // Bead
        Bead bead = model.getBead();
        config.setBeadX( bead.getX() );
        config.setBeadY( bead.getY() );
        
        // Fluid
        Fluid fluid = model.getFluid();
        config.setFluidSpeed( fluid.getSpeed() );
        config.setFluidViscosity( fluid.getViscosity() );
        config.setFluidTemperature( fluid.getTemperature() );
        
        // Control panel settings
        config.setTrapForceSelected( _controlPanel.getForcesControlPanel().isTrapForceSelected() );
        config.setDragForceSelected( _controlPanel.getForcesControlPanel().isDragForceSelected() );
        config.setDnaForceSelected( _controlPanel.getForcesControlPanel().isDNAForceSelected() );
        config.setShowForceValuesSelected( _controlPanel.getForcesControlPanel().isShowValuesSelected() );
        config.setPositionHistogramSelected( _controlPanel.getChartsControlPanel().isPositionHistogramSelected() );
        config.setPotentialEnergySelected( _controlPanel.getChartsControlPanel().isPotentialChartSelected() );
        config.setRulerSelected( _controlPanel.getMiscControlPanel().isRulerSelected() );
        config.setFluidControlsSelected( _controlPanel.getMiscControlPanel().isFluidControlsSelected() );
    }

    public void load( OTConfig appConfig ) {

        DNAConfig config = appConfig.getDNAConfig();
        DNAModel model = getDNAModel();
        
        // Module
        if ( config.isActive() ) {
            PhetApplication.instance().setActiveModule( this );
        }
        
        // Clock
        OTClock clock = model.getClock();
        clock.setDt( config.getClockDt() );
        if ( isActive() ) {
            if ( config.isClockRunning() ) {
                getClock().start();
            }
            else {
                getClock().pause();
            }
        }
        
        // Laser
        Laser laser = model.getLaser();
        laser.setPosition( config.getLaserX(), laser.getY() );
        laser.setRunning( config.isLaserRunning() );
        laser.setPower( config.getLaserPower() );
    
        // Bead
        Bead bead = model.getBead();
        bead.setPosition( config.getBeadX(), config.getBeadY() );
        
        // Fluid
        Fluid fluid = model.getFluid();
        fluid.setSpeed( config.getFluidSpeed() );
        fluid.setViscosity( config.getFluidViscosity() );
        fluid.setTemperature( config.getFluidTemperature() );
        
        // Control panel settings
        _controlPanel.getForcesControlPanel().setTrapForceSelected( config.isTrapForceSelected() );
        _controlPanel.getForcesControlPanel().setDragForceSelected( config.isDragForceSelected() );
        _controlPanel.getForcesControlPanel().setDNAForceSelected( config.isDnaForceSelected() );
        _controlPanel.getForcesControlPanel().setShowValuesSelected( config.isShowForceValuesSelected() );
        _controlPanel.getChartsControlPanel().setPositionHistogramSelected( config.isPositionHistogramSelected() );
        _controlPanel.getChartsControlPanel().setPotentialEnergySelected( config.isPotentialEnergySelected() );
        _controlPanel.getMiscControlPanel().setRulerSelected( config.isRulerSelected() );
        _controlPanel.getMiscControlPanel().setFluidControlsSelected( config.isFluidControlsSelected() );
    }
}
