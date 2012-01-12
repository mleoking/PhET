// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.opticaltweezers.module.motors;

import java.awt.Frame;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.piccolophet.help.HelpBalloon;
import edu.colorado.phet.common.piccolophet.help.HelpPane;
import edu.colorado.phet.opticaltweezers.OTResources;
import edu.colorado.phet.opticaltweezers.control.*;
import edu.colorado.phet.opticaltweezers.control.developer.DeveloperControlPanel;
import edu.colorado.phet.opticaltweezers.defaults.MotorsDefaults;
import edu.colorado.phet.opticaltweezers.model.*;
import edu.colorado.phet.opticaltweezers.module.OTAbstractModule;
import edu.colorado.phet.opticaltweezers.persistence.MotorsConfig;
import edu.colorado.phet.opticaltweezers.view.DNAStrandNode;

/**
 * MotorsModule is the "Molecular Motors" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MotorsModule extends OTAbstractModule {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private MotorsModel _model;
    private MotorsCanvas _canvas;
    private MotorsControlPanel _controlPanel;
    private OTClockControlPanel _clockControlPanel;

    private boolean _fluidControlsWasSelected; // for supporting persistence of Fluid Control dialog state
    private boolean _positionHistogramWasSelected; // for supporting persistence of Position Histogram dialog state

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public MotorsModule( Frame parentFrame ) {
        super( OTResources.getString( "title.molecularMotors" ), MotorsDefaults.CLOCK );

        // Model
        OTClock clock = (OTClock) getClock();
        _model = new MotorsModel( clock );

        // Canvas
        _canvas = new MotorsCanvas( _model );
        setSimulationPanel( _canvas );

        // Control Panel
        _controlPanel = new MotorsControlPanel( this, parentFrame );
        setControlPanel( _controlPanel );

        // Clock controls
        _clockControlPanel = new OTClockControlPanel( (OTClock) getClock() );
        _clockControlPanel.setTimeColumns( MotorsDefaults.CLOCK_TIME_COLUMNS );
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
        reset();
        _fluidControlsWasSelected = _controlPanel.getMiscControlPanel().isFluidControlsSelected();
        _positionHistogramWasSelected = _controlPanel.getChartsControlPanel().isPositionHistogramSelected();
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

    /**
     * Resets the module.
     */
    public void reset() {

        // Model
        {
            // Clock
            OTClock clock = _model.getClock();
            clock.setDt( MotorsDefaults.DEFAULT_DT );
            setClockRunningWhenActive( MotorsDefaults.CLOCK_RUNNING );

            // Bead
            Bead bead = _model.getBead();
            bead.setPosition( MotorsDefaults.BEAD_POSITION );
            bead.setOrientation( MotorsDefaults.BEAD_ORIENTATION );
            bead.setDtSubdivisionThreshold( MotorsDefaults.BEAD_DT_SUBDIVISION_THRESHOLD_RANGE.getDefault() );
            bead.setNumberOfDtSubdivisions( MotorsDefaults.BEAD_NUMBER_OF_DT_SUBDIVISIONS_RANGE.getDefault() );
            bead.setBrownianMotionScale( MotorsDefaults.BEAD_BROWNIAN_MOTION_SCALE_RANGE.getDefault() );
            bead.setBrownianMotionEnabled( MotorsDefaults.BEAD_BROWNIAN_MOTION_ENABLED );
            bead.setVerletDtSubdivisionThreshold( MotorsDefaults.BEAD_VERLET_DT_SUBDIVISION_THRESHOLD_RANGE.getDefault() );
            bead.setVerletNumberOfDtSubdivisions( MotorsDefaults.BEAD_VERLET_NUMBER_OF_DT_SUBDIVISIONS_RANGE.getDefault() );
            bead.setVerletAccelerationScale( MotorsDefaults.BEAD_VERLET_ACCELERATION_SCALE_RANGE.getDefault() );
            bead.setVacuumFastThreshold( MotorsDefaults.BEAD_VACUUM_FAST_THRESHOLD_RANGE.getDefault() );
            bead.setVacuumFastDt( MotorsDefaults.BEAD_VACUUM_FAST_DT_RANGE.getDefault() );
            bead.setVacuumFastPower( MotorsDefaults.BEAD_VACUUM_FAST_POWER_RANGE.getDefault() );
            
            // Invisible bead, attached to free end of DNA strand
            Bead invisibleBead = _model.getInvisibleBead();
            invisibleBead.setPosition( MotorsDefaults.INVISIBLE_BEAD_POSITION );
            invisibleBead.setOrientation( MotorsDefaults.INVISIBLE_BEAD_ORIENTATION );
            invisibleBead.setDtSubdivisionThreshold( MotorsDefaults.INVISIBLE_BEAD_DT_SUBDIVISION_THRESHOLD_RANGE.getDefault() );
            invisibleBead.setNumberOfDtSubdivisions( MotorsDefaults.INVISIBLE_BEAD_NUMBER_OF_DT_SUBDIVISIONS_RANGE.getDefault() );
            invisibleBead.setBrownianMotionScale( MotorsDefaults.INVISIBLE_BEAD_BROWNIAN_MOTION_SCALE_RANGE.getDefault() );
            invisibleBead.setBrownianMotionEnabled( MotorsDefaults.INVISIBLE_BEAD_BROWNIAN_MOTION_ENABLED );
            invisibleBead.setVerletDtSubdivisionThreshold( MotorsDefaults.INVISIBLE_BEAD_VERLET_DT_SUBDIVISION_THRESHOLD_RANGE.getDefault() );
            invisibleBead.setVerletNumberOfDtSubdivisions( MotorsDefaults.INVISIBLE_BEAD_VERLET_NUMBER_OF_DT_SUBDIVISIONS_RANGE.getDefault() );
            invisibleBead.setVerletAccelerationScale( MotorsDefaults.INVISIBLE_BEAD_VERLET_ACCELERATION_SCALE_RANGE.getDefault() );
            invisibleBead.setVacuumFastThreshold( MotorsDefaults.INVISIBLE_BEAD_VACUUM_FAST_THRESHOLD_RANGE.getDefault() );
            invisibleBead.setVacuumFastDt( MotorsDefaults.INVISIBLE_BEAD_VACUUM_FAST_DT_RANGE.getDefault() );
            invisibleBead.setVacuumFastPower( MotorsDefaults.INVISIBLE_BEAD_VACUUM_FAST_POWER_RANGE.getDefault() );
            
            // Laser
            Laser laser = _model.getLaser();
            laser.setPosition( MotorsDefaults.LASER_POSITION );
            laser.setPower( MotorsDefaults.LASER_POWER_RANGE.getDefault() );
            laser.setRunning( MotorsDefaults.LASER_RUNNING );
            laser.setTrapForceRatio( MotorsDefaults.LASER_TRAP_FORCE_RATIO.getDefault() );
            laser.setElectricFieldScale( MotorsDefaults.LASER_ELECTRIC_FIELD_SCALE_RANGE.getDefault() );

            // Fluid
            Fluid fluid = _model.getFluid();
            fluid.setATPConcentration( MotorsDefaults.FLUID_APT_CONCENTRATION_RANGE.getDefault() );
            fluid.setSpeed( MotorsDefaults.FLUID_SPEED_RANGE.getDefault() );
            fluid.setViscosity( MotorsDefaults.FLUID_VISCOSITY_RANGE.getDefault() );
            fluid.setTemperature( MotorsDefaults.FLUID_TEMPERATURE_RANGE.getDefault() );

            // DNA Strand attached to bead
            DNAStrand dnaStrandBead = _model.getDNAStrandBead();
            dnaStrandBead.setContourLength( MotorsDefaults.DNA_BEAD_CONTOUR_LENGTH );
            dnaStrandBead.setSpringConstant( MotorsDefaults.DNA_SPRING_CONSTANT_RANGE.getDefault() );
            dnaStrandBead.setDragCoefficient( MotorsDefaults.DNA_DRAG_COEFFICIENT_RANGE.getDefault() );
            dnaStrandBead.setKickConstant( MotorsDefaults.DNA_KICK_CONSTANT_RANGE.getDefault() );
            dnaStrandBead.setNumberOfEvolutionsPerClockTick( MotorsDefaults.DNA_NUMBER_OF_EVOLUTIONS_PER_CLOCK_STEP_RANGE.getDefault() );
            dnaStrandBead.setEvolutionDt( MotorsDefaults.DNA_EVOLUTION_DT_RANGE.getDefault() );
            dnaStrandBead.setFluidDragCoefficient( MotorsDefaults.DNA_FLUID_DRAG_COEFFICIENT_RANGE.getDefault() );
            dnaStrandBead.initializePivots();
            
            // DNA Strand with free end
            DNAStrand dnaStrandFree = _model.getDNAStrandFree();
            dnaStrandFree.setContourLength( MotorsDefaults.DNA_FREE_CONTOUR_LENGTH );
            dnaStrandFree.setSpringConstant( MotorsDefaults.DNA_SPRING_CONSTANT_RANGE.getDefault() );
            dnaStrandFree.setDragCoefficient( MotorsDefaults.DNA_DRAG_COEFFICIENT_RANGE.getDefault() );
            dnaStrandFree.setKickConstant( MotorsDefaults.DNA_KICK_CONSTANT_RANGE.getDefault() );
            dnaStrandFree.setNumberOfEvolutionsPerClockTick( MotorsDefaults.DNA_NUMBER_OF_EVOLUTIONS_PER_CLOCK_STEP_RANGE.getDefault() );
            dnaStrandFree.setEvolutionDt( MotorsDefaults.DNA_EVOLUTION_DT_RANGE.getDefault() );
            dnaStrandFree.setFluidDragCoefficient( MotorsDefaults.DNA_FLUID_DRAG_COEFFICIENT_RANGE.getDefault() );
            dnaStrandFree.initializePivots();

            // Enzyme
            _model.getEnzymeA().setEnabled( MotorsDefaults.ENZYME_A_SELECTED );
            _model.getEnzymeB().setEnabled( MotorsDefaults.ENZYME_B_SELECTED );
            _model.resetDNA();
        }

        // View
        {
            // DNA Strand node
            DNAStrandNode dnaStrandBeadNode = _canvas.getDNAStrandBeadNode();
            dnaStrandBeadNode.setPivotsVisible( MotorsDefaults.DNA_PIVOTS_VISIBLE );
            dnaStrandBeadNode.setExtensionVisible( MotorsDefaults.DNA_EXTENSIONS_VISIBLE );
            
            DNAStrandNode dnaStrandFreeNode = _canvas.getDNAStrandFreeNode();
            dnaStrandFreeNode.setPivotsVisible( MotorsDefaults.DNA_PIVOTS_VISIBLE );
            dnaStrandFreeNode.setExtensionVisible( MotorsDefaults.DNA_EXTENSIONS_VISIBLE );
        }

        // Control panel settings that are view-related
        {
            _controlPanel.getSimulationSpeedControlPanel().setSimulationSpeed( MotorsDefaults.DEFAULT_DT );

            EnzymeControlPanel enzymeControlPanel = _controlPanel.getEnzymeControlPanel();
            enzymeControlPanel.setEnzymeASelected( MotorsDefaults.ENZYME_A_SELECTED );
            enzymeControlPanel.setEnzymeBSelected( MotorsDefaults.ENZYME_B_SELECTED );

            ForcesControlPanel forcesControlPanel = _controlPanel.getForcesControlPanel();
            forcesControlPanel.setTrapForceSelected( MotorsDefaults.TRAP_FORCE_SELECTED );
            forcesControlPanel.setDragForceSelected( MotorsDefaults.FLUID_DRAG_FORCE_SELECTED );
            forcesControlPanel.setDNAForceSelected( MotorsDefaults.DNA_FORCE_SELECTED );
            forcesControlPanel.setShowValuesSelected( MotorsDefaults.SHOW_FORCE_VALUES );
            forcesControlPanel.setKeepTrapForceConstantSelected( MotorsDefaults.CONSTANT_TRAP_FORCE );

            ChartsControlPanel chartsControlPanel = _controlPanel.getChartsControlPanel();
            chartsControlPanel.setPositionHistogramSelected( MotorsDefaults.POSITION_HISTOGRAM_SELECTED );
            chartsControlPanel.setPotentialEnergySelected( MotorsDefaults.POTENTIAL_ENERGY_CHART_SELECTED );

            MiscControlPanel miscControlPanel = _controlPanel.getMiscControlPanel();
            miscControlPanel.setRulerSelected( MotorsDefaults.RULER_SELECTED );
            miscControlPanel.setFluidControlsSelected( MotorsDefaults.FLUID_CONTROLS_SELECTED );

            DeveloperControlPanel developerControlPanel = _controlPanel.getDeveloperControlPanel();
            developerControlPanel.getVectorsPanel().setComponentsVisible( MotorsDefaults.FORCE_VECTOR_COMPONENTS_VISIBLE );
        }
    }

    //----------------------------------------------------------------------------
    // Persistence
    //----------------------------------------------------------------------------
    
    public MotorsConfig save() {

        MotorsConfig config = new MotorsConfig();

        // Module
        config.setActive( isActive() );

        // Model
        {
            MotorsModel model = getMotorsModel();

            // Clock
            OTClock clock = model.getClock();
            config.setClockDt( clock.getDt() );
            config.setClockRunning( getClockRunningWhenActive() );

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
        }

        // Control panel settings
        {
            EnzymeControlPanel enzymeControlPanel = _controlPanel.getEnzymeControlPanel();
            config.setEnzymeASelected( enzymeControlPanel.isEnzymeASelected() );
            config.setEnzymeBSelected( enzymeControlPanel.isEnzymeBSelected() );

            ForcesControlPanel forcesControlPanel = _controlPanel.getForcesControlPanel();
            config.setTrapForceSelected( forcesControlPanel.isTrapForceSelected() );
            config.setDragForceSelected( forcesControlPanel.isDragForceSelected() );
            config.setDnaForceSelected( forcesControlPanel.isDNAForceSelected() );
            config.setBrownianForceEnabled( forcesControlPanel.isBrownianMotionSelected() );
            config.setShowForceValuesSelected( forcesControlPanel.isShowValuesSelected() );
            config.setKeepTrapForceConstantSelected( forcesControlPanel.isKeepTrapForceConstantSelected() );

            ChartsControlPanel chartsControlPanel = _controlPanel.getChartsControlPanel();
            if ( isActive() ) {
                config.setPositionHistogramSelected( chartsControlPanel.isPositionHistogramSelected() );
            }
            else {
                config.setPositionHistogramSelected( _positionHistogramWasSelected );
            }
            config.setPotentialEnergySelected( chartsControlPanel.isPotentialChartSelected() );

            MiscControlPanel miscControlPanel = _controlPanel.getMiscControlPanel();
            config.setRulerSelected( miscControlPanel.isRulerSelected() );
            if ( isActive() ) {
                config.setFluidControlsSelected( miscControlPanel.isFluidControlsSelected() );
            }
            else {
                config.setFluidControlsSelected( _fluidControlsWasSelected );
            }
        }
        
        return config;
    }

    public void load( MotorsConfig config ) {

        // Module
        if ( config.isActive() ) {
            PhetApplication.getInstance().setActiveModule( this );
        }

        // Model
        {
            MotorsModel model = getMotorsModel();

            // Clock
            OTClock clock = model.getClock();
            clock.setDt( config.getClockDt() );
            setClockRunningWhenActive( config.isClockRunning() );

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
        }

        // Control panel settings
        {
            EnzymeControlPanel enzymeControlPanel = _controlPanel.getEnzymeControlPanel();
            enzymeControlPanel.setEnzymeASelected( config.isEnzymeASelected() );
            enzymeControlPanel.setEnzymeBSelected( config.isEnzymeBSelected() );
            _model.resetDNA();

            ForcesControlPanel forcesControlPanel = _controlPanel.getForcesControlPanel();
            forcesControlPanel.setTrapForceSelected( config.isTrapForceSelected() );
            forcesControlPanel.setDragForceSelected( config.isDragForceSelected() );
            forcesControlPanel.setDNAForceSelected( config.isDnaForceSelected() );
            forcesControlPanel.setBrownianMotionSelected( config.isBrownianForceEnabled() );
            forcesControlPanel.setShowValuesSelected( config.isShowForceValuesSelected() );
            forcesControlPanel.setKeepTrapForceConstantSelected( config.isKeepTrapForceConstantSelected() );

            ChartsControlPanel chartsControlPanel = _controlPanel.getChartsControlPanel();
            if ( isActive() ) {
                chartsControlPanel.setPositionHistogramSelected( config.isPositionHistogramSelected() );
            }
            else {
                _positionHistogramWasSelected = config.isPositionHistogramSelected();
            }
            chartsControlPanel.setPotentialEnergySelected( config.isPotentialEnergySelected() );

            MiscControlPanel miscControlPanel = _controlPanel.getMiscControlPanel();
            miscControlPanel.setRulerSelected( config.isRulerSelected() );
            if ( isActive() ) {
                miscControlPanel.setFluidControlsSelected( config.isFluidControlsSelected() );
            }
            else {
                _fluidControlsWasSelected = config.isFluidControlsSelected();
            }
        }
    }
}
