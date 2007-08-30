/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.module.dna;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.piccolophet.help.HelpBalloon;
import edu.colorado.phet.common.piccolophet.help.HelpPane;
import edu.colorado.phet.opticaltweezers.OTResources;
import edu.colorado.phet.opticaltweezers.control.ChartsControlPanel;
import edu.colorado.phet.opticaltweezers.control.ForcesControlPanel;
import edu.colorado.phet.opticaltweezers.control.MiscControlPanel;
import edu.colorado.phet.opticaltweezers.control.OTClockControlPanel;
import edu.colorado.phet.opticaltweezers.control.developer.DeveloperControlPanel;
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

    private boolean _fluidControlsWasSelected;
    private boolean _positionHistogramWasSelected;
    
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
        _fluidControlsWasSelected = _controlPanel.getMiscControlPanel().isFluidControlsSelected();
        _positionHistogramWasSelected = _controlPanel.getChartsControlPanel().isPositionHistogramSelected();
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
     * Open selected dialogs when this module is activated.
     */
    public void activate() {
        super.activate();
        _controlPanel.getMiscControlPanel().setFluidControlsSelected( _fluidControlsWasSelected );
        _controlPanel.getChartsControlPanel().setPositionHistogramSelected( _positionHistogramWasSelected );
    }
    
    /**
     * Close all dialogs when this module is deactivated.
     */
    public void deactivate() {
        _fluidControlsWasSelected = _controlPanel.getMiscControlPanel().isFluidControlsSelected();
        _positionHistogramWasSelected = _controlPanel.getChartsControlPanel().isPositionHistogramSelected();
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
            
            ForcesControlPanel forcesControlPanel = _controlPanel.getForcesControlPanel();
            forcesControlPanel.setTrapForceSelected( DNADefaults.TRAP_FORCE_SELECTED );
            forcesControlPanel.setDragForceSelected( DNADefaults.FLUID_DRAG_FORCE_SELECTED );
            forcesControlPanel.setDNAForceSelected( DNADefaults.DNA_FORCE_SELECTED );
            forcesControlPanel.setShowValuesSelected( DNADefaults.SHOW_FORCE_VALUES );
            
            ChartsControlPanel chartsControlPanel = _controlPanel.getChartsControlPanel();
            chartsControlPanel.setPositionHistogramSelected( DNADefaults.POSITION_HISTOGRAM_SELECTED );
            chartsControlPanel.setPotentialEnergySelected( DNADefaults.POTENTIAL_ENERGY_CHART_SELECTED );
            
            MiscControlPanel miscControlPanel = _controlPanel.getMiscControlPanel();
            miscControlPanel.setRulerSelected( DNADefaults.RULER_SELECTED );
            miscControlPanel.setFluidControlsSelected( DNADefaults.FLUID_CONTROLS_SELECTED );
            
            DeveloperControlPanel developerControlPanel = _controlPanel.getDeveloperControlPanel();
            developerControlPanel.getVectorsPanel().setComponentsVisible( DNADefaults.SHOW_FORCE_VALUES );
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
        {
            ForcesControlPanel forcesControlPanel = _controlPanel.getForcesControlPanel();
            config.setTrapForceSelected( forcesControlPanel.isTrapForceSelected() );
            config.setDragForceSelected( forcesControlPanel.isDragForceSelected() );
            config.setDnaForceSelected( forcesControlPanel.isDNAForceSelected() );
            config.setShowForceValuesSelected( forcesControlPanel.isShowValuesSelected() );
            
            ChartsControlPanel chartsControlPanel = _controlPanel.getChartsControlPanel();
            config.setPositionHistogramSelected( chartsControlPanel.isPositionHistogramSelected() );
            config.setPotentialEnergySelected( chartsControlPanel.isPotentialChartSelected() );
            
            MiscControlPanel miscControlPanel = _controlPanel.getMiscControlPanel();
            config.setRulerSelected( miscControlPanel.isRulerSelected() );
            config.setFluidControlsSelected( miscControlPanel.isFluidControlsSelected() );
        }
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
        {
            ForcesControlPanel forcesControlPanel = _controlPanel.getForcesControlPanel();
            forcesControlPanel.setTrapForceSelected( config.isTrapForceSelected() );
            forcesControlPanel.setDragForceSelected( config.isDragForceSelected() );
            forcesControlPanel.setDNAForceSelected( config.isDnaForceSelected() );
            forcesControlPanel.setShowValuesSelected( config.isShowForceValuesSelected() );
            
            ChartsControlPanel chartsControlPanel = _controlPanel.getChartsControlPanel();
            chartsControlPanel.setPositionHistogramSelected( config.isPositionHistogramSelected() );
            chartsControlPanel.setPotentialEnergySelected( config.isPotentialEnergySelected() );
            
            MiscControlPanel miscControlPanel = _controlPanel.getMiscControlPanel();
            miscControlPanel.setRulerSelected( config.isRulerSelected() );
            miscControlPanel.setFluidControlsSelected( config.isFluidControlsSelected() );
        }
    }
}
