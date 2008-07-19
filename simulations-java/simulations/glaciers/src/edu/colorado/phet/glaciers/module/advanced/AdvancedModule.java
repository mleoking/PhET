/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers.module.advanced;

import java.awt.Frame;

import edu.colorado.phet.common.piccolophet.help.HelpBalloon;
import edu.colorado.phet.common.piccolophet.help.HelpPane;
import edu.colorado.phet.glaciers.GlaciersApplication;
import edu.colorado.phet.glaciers.GlaciersConstants;
import edu.colorado.phet.glaciers.GlaciersStrings;
import edu.colorado.phet.glaciers.control.ClimateControlPanel;
import edu.colorado.phet.glaciers.control.GraphsControlPanel;
import edu.colorado.phet.glaciers.control.MiscControlPanel;
import edu.colorado.phet.glaciers.control.ViewControlPanel;
import edu.colorado.phet.glaciers.control.MiscControlPanel.MiscControlPanelAdapter;
import edu.colorado.phet.glaciers.model.Climate;
import edu.colorado.phet.glaciers.model.Glacier;
import edu.colorado.phet.glaciers.model.GlaciersClock;
import edu.colorado.phet.glaciers.model.GlaciersModel;
import edu.colorado.phet.glaciers.module.GlaciersModule;
import edu.colorado.phet.glaciers.persistence.AdvancedConfig;
import edu.colorado.phet.glaciers.view.GlaciersPlayArea;

/**
 * This is the "Advanced" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class AdvancedModule extends GlaciersModule {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private final GlaciersModel _model;
    private final GlaciersPlayArea _playArea;
    private final AdvancedControlPanel _controlPanel;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public AdvancedModule( Frame parentFrame ) {
        super( GlaciersStrings.TITLE_ADVANCED, new GlaciersClock() );
        
        // Model
        GlaciersClock clock = (GlaciersClock) getClock();
        _model = new GlaciersModel( clock );

        // Play Area
        _playArea = new GlaciersPlayArea( _model );
        setSimulationPanel( _playArea );

        // Put our control panel where the clock control panel normally goes
        _controlPanel = new AdvancedControlPanel( _model, _playArea );
        setClockControlPanel( _controlPanel );
        _controlPanel.getMiscControlPanel().addMiscControlPanelListener( new MiscControlPanelAdapter() {
            public void resetAllButtonPressed() {
                resetAll();
            }
            public void setHelpEnabled( boolean enabled ) {
                System.out.println( "BasicModule.setHelpEnabled " + enabled );//XXX
                AdvancedModule.this.setHelpEnabled( enabled );
            }
        });
        
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
        
        // Model ---------------------------------------------
        
        _model.reset();
        setClockRunningWhenActive( GlaciersConstants.CLOCK_RUNNING );

        // Controls ---------------------------------------------

        Glacier glacier = _model.getGlacier();
        Climate climate = _model.getClimate();
        
        ViewControlPanel viewControlPanel = _controlPanel.getViewControlPanel();
        viewControlPanel.setEnglishUnitsSelected( true );
        viewControlPanel.setEquilibriumLineSelected( false );
        viewControlPanel.setIceFlowSelected( false );
        viewControlPanel.setSnowfallSelected( true );
        viewControlPanel.setCoordinatesSelected( false );
        
        ClimateControlPanel climateControlPanel = _controlPanel.getClimateControlPanel();
        climateControlPanel.setSnowfall( climate.getSnowfall() );
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

    public AdvancedConfig save() {

        AdvancedConfig config = new AdvancedConfig();

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

    public void load( AdvancedConfig config ) {

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
