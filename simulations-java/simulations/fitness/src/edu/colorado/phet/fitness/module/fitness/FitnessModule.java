/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.fitness.module.fitness;

import java.awt.Frame;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.ClockControlPanelWithTimeDisplay;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.fitness.FitnessApplication;
import edu.colorado.phet.fitness.FitnessStrings;
import edu.colorado.phet.fitness.FitnessConstants;
import edu.colorado.phet.fitness.FitnessResources;
import edu.colorado.phet.fitness.persistence.FitnessConfig;
import edu.colorado.phet.fitness.defaults.ExampleDefaults;
import edu.colorado.phet.fitness.model.SimTemplateClock;
import edu.colorado.phet.fitness.model.Human;

/**
 * FitnessModule is the "Fitness" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class FitnessModule extends PiccoloModule {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private FitnessModel _model;
    private FitnessCanvas _canvas;
    private FitnessControlPanel _controlPanel;
    private ClockControlPanelWithTimeDisplay _clockControlPanel;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public FitnessModule( Frame parentFrame ) {
        super( FitnessStrings.TITLE_FITNESS_MODULE, ExampleDefaults.CLOCK );

        // Model
        SimTemplateClock clock = (SimTemplateClock) getClock();
        _model = new FitnessModel( clock );

        // Canvas
        _canvas = new FitnessCanvas( _model );
        setSimulationPanel( _canvas );

        // Control Panel
        _controlPanel = new FitnessControlPanel( this, parentFrame );
        setControlPanel( _controlPanel );

        // Clock controls
        _clockControlPanel = new ClockControlPanelWithTimeDisplay( (SimTemplateClock) getClock() );
        _clockControlPanel.setUnits( FitnessStrings.UNITS_TIME );
        _clockControlPanel.setTimeColumns( ExampleDefaults.CLOCK_TIME_COLUMNS );
        setClockControlPanel( _clockControlPanel );

        // Controller
//        FitnessController controller = new FitnessController( _model, _canvas, _controlPanel );

        // Help
        if ( hasHelp() ) {
            //XXX add help items
        }

        // Set initial state
        reset();
    }

    //----------------------------------------------------------------------------
    // Module overrides
    //----------------------------------------------------------------------------

    /**
     * Resets the module.
     */
    public void reset() {

        // Clock
        SimTemplateClock clock = _model.getClock();
        clock.resetSimulationTime();
        clock.setDt( ExampleDefaults.CLOCK_DT );
        setClockRunningWhenActive( ExampleDefaults.CLOCK_RUNNING );

    }

    //----------------------------------------------------------------------------
    // Persistence
    //----------------------------------------------------------------------------

    public FitnessConfig save() {

        FitnessConfig config = new FitnessConfig();

        // Module
        config.setActive( isActive() );

        // Clock
        SimTemplateClock clock = _model.getClock();
        config.setClockDt( clock.getDt() );
        config.setClockRunning( getClockRunningWhenActive() );

        // FitnessModelElement
        Human fitnessModelElement = _model.getFitnessModelElement();
//        config.setFitnessModelElementPosition( fitnessModelElement.getPositionReference() );
//        config.setFitnessModelElementOrientation( fitnessModelElement.getOrientation() );

        // Control panel settings that are specific to the view
        //XXX

        return config;
    }

    public void load( FitnessConfig config ) {

        // Module
        if ( config.isActive() ) {
            FitnessApplication.instance().setActiveModule( this );
        }

        // Clock
        SimTemplateClock clock = _model.getClock();
        clock.setDt( config.getClockDt() );
        setClockRunningWhenActive( config.isClockRunning() );

        // FitnessModelElement
//        FitnessModelElement fitnessModelElement = _model.getFitnessModelElement();
//        fitnessModelElement.setPosition( config.getFitnessModelElementPosition() );
//        fitnessModelElement.setOrientation( config.getFitnessModelElementOrientation() );

        // Control panel settings that are specific to the view
        //XXX
    }

    public Human getHuman() {
        return _model.getFitnessModelElement();
    }

        public static void main( final String[] args ) {
        SwingUtilities.invokeLater( new Runnable() {

            public void run() {

                PhetApplicationConfig config = new PhetApplicationConfig( args, FitnessConstants.FRAME_SETUP, FitnessResources.getResourceLoader() );

                // Create the application.
                FitnessApplication app = new FitnessApplication( config );

                // Start the application.
                app.startApplication();
            }
        } );
    }
}