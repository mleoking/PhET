/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers.module.basic;

import java.awt.Frame;

import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.common.piccolophet.help.HelpBalloon;
import edu.colorado.phet.common.piccolophet.help.HelpPane;
import edu.colorado.phet.glaciers.GlaciersApplication;
import edu.colorado.phet.glaciers.GlaciersStrings;
import edu.colorado.phet.glaciers.control.ClimateControlPanel;
import edu.colorado.phet.glaciers.control.GraphsControlPanel;
import edu.colorado.phet.glaciers.control.MiscControlPanel;
import edu.colorado.phet.glaciers.control.ViewControlPanel;
import edu.colorado.phet.glaciers.control.MiscControlPanel.MiscControlPanelAdapter;
import edu.colorado.phet.glaciers.defaults.BasicDefaults;
import edu.colorado.phet.glaciers.model.Climate;
import edu.colorado.phet.glaciers.model.Glacier;
import edu.colorado.phet.glaciers.model.GlaciersClock;
import edu.colorado.phet.glaciers.model.Valley;
import edu.colorado.phet.glaciers.persistence.BasicConfig;
import edu.colorado.phet.glaciers.view.ModelViewTransform;
import edu.colorado.phet.glaciers.view.PlayArea;

/**
 * BasicModule is the "Basic" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BasicModule extends PiccoloModule {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // ModelViewTransform (MVT) parameters
    private static final double MVT_X_SCALE = 0.062; // scale x by this amount when going from model to view
    private static final double MVT_Y_SCALE = 0.1; // scale y by this amount when going from model to view
    private static final double MVT_X_OFFSET = 0; // translate x by this amount when going from model to view
    private static final double MVT_Y_OFFSET = 0; // translate y by this amount when going from model to view
    private static final boolean MVT_FLIP_SIGN_X = false;
    private static final boolean MVT_FLIP_SIGN_Y = true;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private BasicModel _model;
    private PlayArea _playArea;
    private BasicControlPanel _controlPanel;
    private BasicController _controller;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public BasicModule( Frame parentFrame ) {
        super( GlaciersStrings.TITLE_BASIC, BasicDefaults.CLOCK );
        
        // we won't be using any of the standard subpanels
        setMonitorPanel( null );
        setSimulationPanel( null );
        setClockControlPanel( null );
        setLogoPanel( null );
        setControlPanel( null );
        setHelpPanel( null );

        // Model
        GlaciersClock clock = (GlaciersClock) getClock();
        Valley valley = new Valley();
        Climate climate = new Climate( BasicDefaults.TEMPERATURE_RANGE.getDefault(), BasicDefaults.SNOWFALL_RANGE.getDefault(), BasicDefaults.SNOWFALL_REFERENCE_ELEVATION_RANGE.getDefault() );
        Glacier glacier = new Glacier( valley, climate, clock );
        _model = new BasicModel( clock, glacier );

        // Play Area
        ModelViewTransform mvt = new ModelViewTransform( MVT_X_SCALE, MVT_Y_SCALE, MVT_X_OFFSET, MVT_Y_OFFSET, MVT_FLIP_SIGN_X, MVT_FLIP_SIGN_Y );
        _playArea = new PlayArea( _model, mvt );
        setSimulationPanel( _playArea );

        // Put our control panel where the clock control panel normally goes
        _controlPanel = new BasicControlPanel( clock );
        setClockControlPanel( _controlPanel );
        _controlPanel.getMiscControlPanel().addMiscControlPanelListener( new MiscControlPanelAdapter() {
            public void resetAllButtonPressed() {
                resetAll();
            }
            public void setHelpEnabled( boolean enabled ) {
                System.out.println( "BasicModule.setHelpEnabled " + enabled );//XXX
                BasicModule.this.setHelpEnabled( enabled );
            }
        });
        
        // Controller
        _controller = new BasicController( _model, _playArea, _controlPanel );

        // Help
        if ( hasHelp() ) {
            //XXX add help items
            HelpPane helpPane = getDefaultHelpPane();
            HelpBalloon equilibriumButtonHelp = new HelpBalloon( helpPane, GlaciersStrings.HELP_EQUILIBRIUM_BUTTON, HelpBalloon.BOTTOM_CENTER, 80 );
            helpPane.add( equilibriumButtonHelp );
            equilibriumButtonHelp.pointAt( _controlPanel.getMiscControlPanel().getEquilibriumButton() );
        }
        
        // Set initial state
        resetAll();
    }

    //----------------------------------------------------------------------------
    // Module overrides
    //----------------------------------------------------------------------------

    /**
     * Does this module have help
     * 
     * @return true or false
     */
    public boolean hasHelp() {
        return true;
    }
    
    /**
     * Enables or disables help.
     * 
     * @param enabled true or false
     */
    public void setHelpEnabled( boolean enabled ) {
        super.setHelpEnabled( enabled );
        _controlPanel.getMiscControlPanel().setHelpEnabled( enabled );
        GlaciersApplication.instance().getPhetFrame().getHelpMenu().setHelpSelected( enabled );
    }
    
    /**
     * Resets everything in the module.
     */
    public void resetAll() {
        
        super.reset();
        
        System.out.println( "BasicModule.resetAll" );//XXX

        // Model ---------------------------------------------
        
        // Clock
        GlaciersClock clock = _model.getClock();
        clock.setFrameRate( BasicDefaults.CLOCK_FRAME_RATE_RANGE.getDefault() );
        clock.resetSimulationTime();
        setClockRunningWhenActive( BasicDefaults.CLOCK_RUNNING );

        // Climate
        Climate climate = _model.getClimate();
        climate.setTemperature( BasicDefaults.TEMPERATURE_RANGE.getDefault() );
        climate.setSnowfall( BasicDefaults.SNOWFALL_RANGE.getDefault() );
        climate.setSnowfallReferenceElevation( BasicDefaults.SNOWFALL_REFERENCE_ELEVATION_RANGE.getDefault() );

        // Glacier
        Glacier glacier = _model.getGlacier();
        glacier.setSteadyState();

        // Tools
        _model.removeAllTools();
        
        // Controls ---------------------------------------------

        ViewControlPanel viewControlPanel = _controlPanel.getViewControlPanel();
        viewControlPanel.setEquilibriumLineSelected( true );//XXX for testing, default should be false
        viewControlPanel.setIceFlowSelected( false );
        viewControlPanel.setCoordinatesSelected( false );
        
        ClimateControlPanel climateControlPanel = _controlPanel.getClimateControlPanel();
        climateControlPanel.setSnowfall( climate.getSnowfall() );
        climateControlPanel.setSnowfallReferenceElevation( climate.getSnowfallReferenceElevation() );
        climateControlPanel.setTemperature( climate.getTemperature() );
        
        GraphsControlPanel graphsControlPanel = _controlPanel.getGraphsControlPanel();
        graphsControlPanel.setGlacierLengthVerusTimeSelected( false );
        graphsControlPanel.setEquilibriumLineAltitudeVersusTimeSelected( false );
        graphsControlPanel.setGlacialBudgetVersusElevationSelected( false );
        graphsControlPanel.setTemperatureVersusElevationSelected( false );
        
        MiscControlPanel miscControlPanel = _controlPanel.getMiscControlPanel();
        miscControlPanel.setEquilibriumButtonEnabled( !glacier.isSteadyState() );

        // View ---------------------------------------------
        
        _playArea.setEquilibriumLineVisible( viewControlPanel.isEquilibriumLineSelected() );
        _playArea.setIceFlowVisible( viewControlPanel.isIceFlowSelected() );
        _playArea.setAxesVisible( viewControlPanel.isCoordinatesSelected() );
    }
    
    //----------------------------------------------------------------------------
    // Persistence
    //----------------------------------------------------------------------------

    public BasicConfig save() {

        BasicConfig config = new BasicConfig();

        // Module
        config.setActive( isActive() );

        // Model
        {
            // Clock
            GlaciersClock clock = _model.getClock();
            config.setClockDt( clock.getDt() );
            config.setClockRunning( getClockRunningWhenActive() );
        }

        // Control panel settings that are view-related
        {
            //XXX
        }
        
        return config;
    }

    public void load( BasicConfig config ) {

        // Module
        if ( config.isActive() ) {
            GlaciersApplication.instance().setActiveModule( this );
        }

        // Model
        {
            // Clock
            GlaciersClock clock = _model.getClock();
            clock.setDt( config.getClockDt() );
            setClockRunningWhenActive( config.isClockRunning() );
        }

        // Control panel settings that are view-related
        {
            //XXX
        }
    }
}
