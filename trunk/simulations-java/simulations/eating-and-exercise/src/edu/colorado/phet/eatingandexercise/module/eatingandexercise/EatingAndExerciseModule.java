/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.eatingandexercise.module.eatingandexercise;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.view.ClockControlPanel;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.common.piccolophet.help.DefaultWiggleMe;
import edu.colorado.phet.common.piccolophet.help.HelpPane;
import edu.colorado.phet.common.piccolophet.help.MotionHelpBalloon;
import edu.colorado.phet.eatingandexercise.EatingAndExerciseApplication;
import edu.colorado.phet.eatingandexercise.EatingAndExerciseConstants;
import edu.colorado.phet.eatingandexercise.EatingAndExerciseResources;
import edu.colorado.phet.eatingandexercise.EatingAndExerciseStrings;
import edu.colorado.phet.eatingandexercise.control.CaloricItem;
import edu.colorado.phet.eatingandexercise.defaults.ExampleDefaults;
import edu.colorado.phet.eatingandexercise.model.CalorieSet;
import edu.colorado.phet.eatingandexercise.model.EatingAndExerciseUnits;
import edu.colorado.phet.eatingandexercise.model.Human;
import edu.colorado.phet.eatingandexercise.persistence.EatingAndExerciseConfig;

public class EatingAndExerciseModule extends PiccoloModule {

    private EatingAndExerciseModel _model;
    private EatingAndExerciseCanvas _canvas;
    private ClockControlPanel _clockControlPanel;
    private JFrame parentFrame;
    private boolean inited = false;
    private boolean everStarted = false;
    private EatingAndExerciseClock eatingAndExerciseClock;

    private int numAddedItems = 0;

    public EatingAndExerciseModule( final JFrame parentFrame ) {
        super( EatingAndExerciseStrings.TITLE_EATING_AND_EXERCISE_MODULE, new EatingAndExerciseClock(), EatingAndExerciseDefaults.STARTS_PAUSED );
        this.parentFrame = parentFrame;

        // Model
        eatingAndExerciseClock = (EatingAndExerciseClock) getClock();
        eatingAndExerciseClock.addClockListener( new ClockAdapter() {
            public void clockStarted( ClockEvent clockEvent ) {
                everStarted = true;
            }
        } );
        _model = new EatingAndExerciseModel( eatingAndExerciseClock );

        // Canvas
        _canvas = new EatingAndExerciseCanvas( _model, parentFrame );
        _canvas.addEditorClosedListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                activateStartButtonWiggleMe();
            }
        } );
        _model.getHuman().getSelectedExercise().addListener( new CalorieSet.Adapter() {
            public void itemAdded( CaloricItem item ) {
                incrementAddedItems();
            }
        } );

        _model.getHuman().getSelectedFoods().addListener( new CalorieSet.Adapter() {
            public void itemAdded( CaloricItem item ) {
                incrementAddedItems();
            }

        } );
        setSimulationPanel( _canvas );

        // Control Panel
        setControlPanel( null );
        setLogoPanelVisible( false );

        // Clock controls
        _clockControlPanel = new ClockControlPanel( getClock() ) {
            public void setTimeDisplay( double time ) {
                super.setTimeDisplay( EatingAndExerciseUnits.secondsToYears( time ) );
            }
        };
        _clockControlPanel.setRestartButtonVisible( true );
        _clockControlPanel.setTimeDisplayVisible( true );
        _clockControlPanel.setUnits( EatingAndExerciseStrings.UNITS_TIME );
        _clockControlPanel.setTimeColumns( ExampleDefaults.CLOCK_TIME_COLUMNS );
        _clockControlPanel.setRestartButtonVisible( false );
        _clockControlPanel.setStepButtonText( EatingAndExerciseResources.getString( "time.next-month" ) );
        _clockControlPanel.setTimeFormat( "0.0" );
        JButton button = new JButton( EatingAndExerciseResources.getString( "time.reset-all" ) );
        button.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                _model.resetAll();
                _canvas.resetAll();
            }
        } );

        JButton disclaimerButton = new JButton( EatingAndExerciseResources.getString( "disclaimer" ) );
        disclaimerButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                JOptionPane.showMessageDialog( parentFrame, EatingAndExerciseStrings.DISCLAIMER );
            }
        } );
        _clockControlPanel.add( disclaimerButton, 0 );
        _clockControlPanel.add( Box.createHorizontalStrut( 100 ), 1 );
        _clockControlPanel.add( button, 2 );

        setClockControlPanel( _clockControlPanel );

        setHelpEnabled( true );
        reset();
    }

    private void incrementAddedItems() {
        numAddedItems++;
        if ( numAddedItems >= 3 ) {
            activateStartButtonWiggleMe();
        }
    }

    private void activateStartButtonWiggleMe() {
        if ( !inited && !everStarted ) {
            final MotionHelpBalloon motionHelpBalloon = new DefaultWiggleMe( _canvas, EatingAndExerciseResources.getString( "time.start" ) );
            eatingAndExerciseClock.addClockListener( new ClockAdapter() {
                public void clockStarted( ClockEvent clockEvent ) {
                    if ( getDefaultHelpPane().getLayer().indexOfChild( motionHelpBalloon ) >= 0 ) {
                        getDefaultHelpPane().remove( motionHelpBalloon );
                    }
                }
            } );
            motionHelpBalloon.setArrowTailPosition( MotionHelpBalloon.BOTTOM_CENTER );
            motionHelpBalloon.setOffset( 800, 0 );
            motionHelpBalloon.animateTo( _clockControlPanel.getPlayPauseButton(), 15 );
            setHelpPane( new HelpPane( parentFrame ) );
            getDefaultHelpPane().add( motionHelpBalloon );
            inited = true;
        }
    }
//----------------------------------------------------------------------------
    // Module overrides
    //----------------------------------------------------------------------------

    /**
     * Resets the module.
     */
    public void reset() {
//        activate();
//        setHelpEnabled( true );

        // Clock
        EatingAndExerciseClock clock = _model.getClock();
        clock.resetSimulationTime();
        clock.setDt( EatingAndExerciseDefaults.CLOCK_DT );
        setClockRunningWhenActive( !EatingAndExerciseDefaults.STARTS_PAUSED );
    }

    //----------------------------------------------------------------------------
    // Persistence
    //----------------------------------------------------------------------------

    public EatingAndExerciseConfig save() {

        EatingAndExerciseConfig config = new EatingAndExerciseConfig();

        // Module
        config.setActive( isActive() );

        return config;
    }

    public void load( EatingAndExerciseConfig config ) {

        // Module
        if ( config.isActive() ) {
            EatingAndExerciseApplication.instance().setActiveModule( this );
        }

    }

    public Human getHuman() {
        return _model.getHuman();
    }

    public static void main( final String[] args ) {
        SwingUtilities.invokeLater( new Runnable() {

            public void run() {

                PhetApplicationConfig config = new PhetApplicationConfig( args, EatingAndExerciseConstants.FRAME_SETUP, EatingAndExerciseResources.getResourceLoader() );

                // Create the application.
                EatingAndExerciseApplication app = new EatingAndExerciseApplication( config );

                // Start the application.
                app.startApplication();
            }
        } );
    }

    public void applicationStarted() {
        _canvas.applicationStarted();
    }
}