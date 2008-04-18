/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.fitness.module.fitness;

import java.awt.*;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.view.ClockControlPanel;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.fitness.FitnessApplication;
import edu.colorado.phet.fitness.FitnessConstants;
import edu.colorado.phet.fitness.FitnessResources;
import edu.colorado.phet.fitness.FitnessStrings;
import edu.colorado.phet.fitness.defaults.ExampleDefaults;
import edu.colorado.phet.fitness.model.Human;
import edu.colorado.phet.fitness.persistence.FitnessConfig;

public class FitnessModule extends PiccoloModule {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private FitnessModel _model;
    private FitnessCanvas _canvas;
    //    private FitnessControlPanel _controlPanel;
    private ClockControlPanel _clockControlPanel;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public FitnessModule( Frame parentFrame ) {
        super( FitnessStrings.TITLE_FITNESS_MODULE, new ConstantDtClock( 30, FitnessDefaults.CLOCK_DT ) );

        // Model
        _model = new FitnessModel( (ConstantDtClock) getClock() );

        // Canvas
        _canvas = new FitnessCanvas( _model );
        setSimulationPanel( _canvas );

        // Control Panel
//        _controlPanel = new FitnessControlPanel( this, parentFrame );
        setControlPanel( null );
        setLogoPanelVisible( false );

        // Clock controls
        _clockControlPanel = new ClockControlPanel( getClock() );
        _clockControlPanel.setRestartButtonVisible( true );
        _clockControlPanel.setTimeDisplayVisible( true );
        _clockControlPanel.setUnits( FitnessStrings.UNITS_TIME );
        _clockControlPanel.setTimeColumns( ExampleDefaults.CLOCK_TIME_COLUMNS );

//        JComponent timeSpeedSlider = createTimeSpeedSlider();

//        _clockControlPanel.addBetweenTimeDisplayAndButtons( timeSpeedSlider );
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

    //todo, move to phetcommon and consolidate with BSClockControls and HAClockControls
//    private JComponent createTimeSpeedSlider() {
//        JSlider _clockIndexSlider = new JSlider();
//        _clockIndexSlider.setMinimum( 0 );
//        int[] steps = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
//        _clockIndexSlider.setMaximum( steps.length - 1 );
//        _clockIndexSlider.setMajorTickSpacing( 1 );
//        _clockIndexSlider.setPaintTicks( true );
//        _clockIndexSlider.setPaintLabels( true );
//        _clockIndexSlider.setSnapToTicks( true );
//        _clockIndexSlider.setValue( 0 );
//
//        // Label the min "normal", the max "fast".
//        String normalString = "normal";
//        String fastString = "fast";
//        Hashtable labelTable = new Hashtable();
//        labelTable.put( new Integer( _clockIndexSlider.getMinimum() ), new JLabel( normalString ) );
//        labelTable.put( new Integer( _clockIndexSlider.getMaximum() ), new JLabel( fastString ) );
//        _clockIndexSlider.setLabelTable( labelTable );
//
//        // Set the slider's physical width
//        Dimension preferredSize = _clockIndexSlider.getPreferredSize();
//        Dimension size = new Dimension( 150, (int) preferredSize.getHeight() );
//        _clockIndexSlider.setPreferredSize( size );
//
//        return _clockIndexSlider;
//    }

    //----------------------------------------------------------------------------
    // Module overrides
    //----------------------------------------------------------------------------

    /**
     * Resets the module.
     */
    public void reset() {

        // Clock
        ConstantDtClock clock = _model.getClock();
        clock.resetSimulationTime();
        clock.setDt( FitnessDefaults.CLOCK_DT );
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
        ConstantDtClock clock = _model.getClock();
        config.setClockDt( clock.getDt() );
        config.setClockRunning( getClockRunningWhenActive() );

        // FitnessModelElement
        Human fitnessModelElement = _model.getHuman();
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
        ConstantDtClock clock = _model.getClock();
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
        return _model.getHuman();
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