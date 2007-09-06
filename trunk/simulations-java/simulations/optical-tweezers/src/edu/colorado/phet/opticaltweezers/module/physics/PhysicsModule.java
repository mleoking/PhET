/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.module.physics;

import edu.colorado.phet.common.phetcommon.application.NonPiccoloPhetApplication;
import edu.colorado.phet.common.piccolophet.help.HelpBalloon;
import edu.colorado.phet.common.piccolophet.help.HelpPane;
import edu.colorado.phet.opticaltweezers.OTResources;
import edu.colorado.phet.opticaltweezers.control.*;
import edu.colorado.phet.opticaltweezers.control.developer.DeveloperControlPanel;
import edu.colorado.phet.opticaltweezers.defaults.PhysicsDefaults;
import edu.colorado.phet.opticaltweezers.model.*;
import edu.colorado.phet.opticaltweezers.module.AbstractModule;
import edu.colorado.phet.opticaltweezers.persistence.OTConfig;
import edu.colorado.phet.opticaltweezers.persistence.PhysicsConfig;

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

    private boolean _fluidControlsWasSelected;
    private boolean _positionHistogramWasSelected;

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
        _clockControlPanel = new OTClockControlPanel( (OTClock) getClock() );
        _clockControlPanel.setTimeColumns( PhysicsDefaults.CLOCK_TIME_COLUMNS );
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
            clock.setDt( PhysicsDefaults.DEFAULT_DT );
            if ( isActive() ) {
                clock.setPaused( PhysicsDefaults.CLOCK_PAUSED );
            }

            // Bead
            Bead bead = _model.getBead();
            bead.setPosition( PhysicsDefaults.BEAD_POSITION );
            bead.setOrientation( PhysicsDefaults.BEAD_ORIENTATION );
            bead.setDtSubdivisionThreshold( PhysicsDefaults.BEAD_DT_SUBDIVISION_THRESHOLD_RANGE.getDefault() );
            bead.setNumberOfDtSubdivisions( PhysicsDefaults.BEAD_NUMBER_OF_DT_SUBDIVISIONS_RANGE.getDefault() );
            bead.setBrownianMotionScale( PhysicsDefaults.BEAD_BROWNIAN_MOTION_SCALE_RANGE.getDefault() );
            bead.setBrownianMotionEnabled( PhysicsDefaults.BEAD_BROWNIAN_MOTION_ENABLED );
            bead.setVerletDtSubdivisionThreshold( PhysicsDefaults.BEAD_VERLET_DT_SUBDIVISION_THRESHOLD_RANGE.getDefault() );
            bead.setVerletNumberOfDtSubdivisions( PhysicsDefaults.BEAD_VERLET_NUMBER_OF_DT_SUBDIVISIONS_RANGE.getDefault() );
            bead.setVerletAccelerationScale( PhysicsDefaults.BEAD_VERLET_ACCELERATION_SCALE_RANGE.getDefault() );
            bead.setVacuumFastThreshold( PhysicsDefaults.BEAD_VACUUM_FAST_THRESHOLD_RANGE.getDefault() );
            bead.setVacuumFastDt( PhysicsDefaults.BEAD_VACUUM_FAST_DT_RANGE.getDefault() );
            bead.setVacuumFastPower( PhysicsDefaults.BEAD_VACUUM_FAST_POWER_RANGE.getDefault() );

            // Laser
            Laser laser = _model.getLaser();
            laser.setPosition( PhysicsDefaults.LASER_POSITION );
            laser.setPower( PhysicsDefaults.LASER_POWER_RANGE.getDefault() );
            laser.setRunning( PhysicsDefaults.LASER_RUNNING );
            laser.setTrapForceRatio( PhysicsDefaults.LASER_TRAP_FORCE_RATIO.getDefault() );
            laser.setElectricFieldScale( PhysicsDefaults.LASER_ELECTRIC_FIELD_SCALE_RANGE.getDefault() );

            // Fluid
            Fluid fluid = _model.getFluid();
            fluid.setSpeed( PhysicsDefaults.FLUID_SPEED_RANGE.getDefault() );
            fluid.setViscosity( PhysicsDefaults.FLUID_VISCOSITY_RANGE.getDefault() );
            fluid.setTemperature( PhysicsDefaults.FLUID_TEMPERATURE_RANGE.getDefault() );
        }

        // Control panel settings that are view-related
        {
            _controlPanel.getSimulationSpeedControlPanel().setSimulationSpeed( PhysicsDefaults.DEFAULT_DT );

            LaserDisplayControlPanel laserDisplayControlPanel = _controlPanel.getLaserDisplayControlPanel();
            laserDisplayControlPanel.setDisplaySelection( PhysicsDefaults.LASER_BEAM_VISIBLE, PhysicsDefaults.LASER_ELECTRIC_FIELD_VISIBLE );

            ChargeControlPanel chargeControlPanel = _controlPanel.getChargeControlPanel();
            chargeControlPanel.setHiddenSelected( PhysicsDefaults.CHARGE_HIDDEN_SELECTED );
            chargeControlPanel.setDistributionSelected( PhysicsDefaults.CHARGE_DISTRIBUTION_SELECTED );
            chargeControlPanel.setExcessSelected( PhysicsDefaults.CHARGE_EXCESS_SELECTED );

            ForcesControlPanel forcesControlPanel = _controlPanel.getForcesControlPanel();
            forcesControlPanel.setTrapForceSelected( PhysicsDefaults.TRAP_FORCE_SELECTED );
            forcesControlPanel.setDragForceSelected( PhysicsDefaults.FLUID_DRAG_FORCE_SELECTED );

            ChartsControlPanel chartsControlPanel = _controlPanel.getChartsControlPanel();
            chartsControlPanel.setPositionHistogramSelected( PhysicsDefaults.POSITION_HISTOGRAM_SELECTED );
            chartsControlPanel.setPotentialEnergySelected( PhysicsDefaults.POTENTIAL_ENERGY_CHART_SELECTED );

            MiscControlPanel miscControlPanel = _controlPanel.getMiscControlPanel();
            miscControlPanel.setRulerSelected( PhysicsDefaults.RULER_SELECTED );
            miscControlPanel.setFluidSelected( PhysicsDefaults.BEAD_IN_FLUID_SELECTED );
            miscControlPanel.setVacuumSelected( PhysicsDefaults.BEAD_IN_VACUUM_SELECTED );
            miscControlPanel.setFluidControlsSelected( PhysicsDefaults.FLUID_CONTROLS_SELECTED );

            DeveloperControlPanel developerControlPanel = _controlPanel.getDeveloperControlPanel();
            developerControlPanel.getVectorsPanel().setComponentsVisible( PhysicsDefaults.FORCE_VECTOR_COMPONENTS_VISIBLE );
            developerControlPanel.getBeadPanel().setChargeMotionScale( PhysicsDefaults.CHARGE_MOTION_SCALE_RANGE.getDefault() );
        }
    }

    public void save( OTConfig appConfig ) {

        PhysicsConfig config = appConfig.getPhysicsConfig();
        PhysicsModel model = getPhysicsModel();

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
            LaserDisplayControlPanel laserDisplayControlPanel = _controlPanel.getLaserDisplayControlPanel();
            config.setLaserBeamSelected( laserDisplayControlPanel.isBeamSelected() );
            config.setLaserElectricFieldSelected( laserDisplayControlPanel.isElectricFieldSelected() );

            ChargeControlPanel chargeControlPanel = _controlPanel.getChargeControlPanel();
            config.setChargeDistributionSelected( chargeControlPanel.isDistributionSelected() );
            config.setChargeExcessSelected( chargeControlPanel.isExcessSelected() );

            ForcesControlPanel forcesControlPanel = _controlPanel.getForcesControlPanel();
            config.setTrapForceSelected( forcesControlPanel.isTrapForceSelected() );
            config.setDragForceSelected( forcesControlPanel.isDragForceSelected() );
            config.setBrownianForceEnabled( forcesControlPanel.isBrownianMotionSelected() );

            ChartsControlPanel chartsControlPanel = _controlPanel.getChartsControlPanel();
            config.setPositionHistogramSelected( chartsControlPanel.isPositionHistogramSelected() );
            config.setPotentialEnergySelected( chartsControlPanel.isPotentialChartSelected() );

            MiscControlPanel miscControlPanel = _controlPanel.getMiscControlPanel();
            config.setRulerSelected( miscControlPanel.isRulerSelected() );
            config.setFluidSelected( miscControlPanel.isFluidSelected() );
            config.setVacuumSelected( miscControlPanel.isVacuumSelected() );
            config.setFluidControlsSelected( miscControlPanel.isFluidControlsSelected() );
        }
    }

    public void load( OTConfig appConfig ) {

        PhysicsConfig config = appConfig.getPhysicsConfig();
        PhysicsModel model = getPhysicsModel();

        // Module
        if ( config.isActive() ) {
            NonPiccoloPhetApplication.instance().setActiveModule( this );
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
            LaserDisplayControlPanel laserDisplayControlPanel = _controlPanel.getLaserDisplayControlPanel();
            laserDisplayControlPanel.setDisplaySelection( config.isLaserBeamSelected(), config.isLaserElectricFieldSelected() );

            ChargeControlPanel chargeControlPanel = _controlPanel.getChargeControlPanel();
            chargeControlPanel.setDistributionSelected( config.isChargeDistributionSelected() );
            chargeControlPanel.setExcessSelected( config.isChargeExcessSelected() );

            ForcesControlPanel forcesControlPanel = _controlPanel.getForcesControlPanel();
            forcesControlPanel.setTrapForceSelected( config.isTrapForceSelected() );
            forcesControlPanel.setDragForceSelected( config.isDragForceSelected() );
            forcesControlPanel.setBrownianMotionSelected( config.isBrownianForceEnabled() );

            ChartsControlPanel chartsControlPanel = _controlPanel.getChartsControlPanel();
            chartsControlPanel.setPositionHistogramSelected( config.isPositionHistogramSelected() );
            chartsControlPanel.setPotentialEnergySelected( config.isPotentialEnergySelected() );

            MiscControlPanel miscControlPanel = _controlPanel.getMiscControlPanel();
            miscControlPanel.setRulerSelected( config.isRulerSelected() );
            miscControlPanel.setFluidSelected( config.isFluidSelected() );
            miscControlPanel.setVacuumSelected( config.isVacuumSelected() );
            miscControlPanel.setFluidControlsSelected( config.isFluidControlsSelected() );
        }
    }
}
