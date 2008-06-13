/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.eatingandexercise.module.fitness;

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
import edu.colorado.phet.eatingandexercise.FitnessApplication;
import edu.colorado.phet.eatingandexercise.FitnessConstants;
import edu.colorado.phet.eatingandexercise.FitnessResources;
import edu.colorado.phet.eatingandexercise.FitnessStrings;
import edu.colorado.phet.eatingandexercise.control.CaloricItem;
import edu.colorado.phet.eatingandexercise.defaults.ExampleDefaults;
import edu.colorado.phet.eatingandexercise.model.CalorieSet;
import edu.colorado.phet.eatingandexercise.model.FitnessUnits;
import edu.colorado.phet.eatingandexercise.model.Human;
import edu.colorado.phet.eatingandexercise.persistence.FitnessConfig;

public class FitnessModule extends PiccoloModule {

    private FitnessModel _model;
    private FitnessCanvas _canvas;
    private ClockControlPanel _clockControlPanel;
    private JFrame parentFrame;
    private boolean inited = false;
    private boolean everStarted = false;
    private FitnessClock fitnessClock;

    private int numAddedItems = 0;

    public FitnessModule( final JFrame parentFrame ) {
        super( FitnessStrings.TITLE_FITNESS_MODULE, new FitnessClock(), FitnessDefaults.STARTS_PAUSED );
        this.parentFrame = parentFrame;

        // Model
        fitnessClock = (FitnessClock) getClock();
        fitnessClock.addClockListener( new ClockAdapter() {
            public void clockStarted( ClockEvent clockEvent ) {
                everStarted = true;
            }
        } );
        _model = new FitnessModel( fitnessClock );

        // Canvas
        _canvas = new FitnessCanvas( _model, parentFrame );
        _canvas.addEditorClosedListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                activateStartButtonWiggleMe();
            }
        } );
        _model.getHuman().getSelectedExercise().addListener( new CalorieSet.Listener() {
            public void itemAdded( CaloricItem item ) {
                incrementAddedItems();
            }

            public void itemRemoved( CaloricItem item ) {
            }
        } );

        _model.getHuman().getSelectedFoods().addListener( new CalorieSet.Listener() {
            public void itemAdded( CaloricItem item ) {
                incrementAddedItems();
            }

            public void itemRemoved( CaloricItem item ) {
            }
        } );
        setSimulationPanel( _canvas );

        // Control Panel
//        _controlPanel = new FitnessControlPanel( this, parentFrame );
        setControlPanel( null );
        setLogoPanelVisible( false );

        // Clock controls
        _clockControlPanel = new ClockControlPanel( getClock() ) {
            public void setTimeDisplay( double time ) {
                super.setTimeDisplay( FitnessUnits.secondsToYears( time ) );
            }
        };
        _clockControlPanel.setRestartButtonVisible( true );
        _clockControlPanel.setTimeDisplayVisible( true );
        _clockControlPanel.setUnits( FitnessStrings.UNITS_TIME );
        _clockControlPanel.setTimeColumns( ExampleDefaults.CLOCK_TIME_COLUMNS );
        _clockControlPanel.setRestartButtonVisible( false );
        _clockControlPanel.setStepButtonText( FitnessResources.getString( "time.next-month" ) );
        _clockControlPanel.setTimeFormat( "0.0" );
        JButton button = new JButton( FitnessResources.getString( "time.reset-all" ) );
        button.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                _model.resetAll();
                _canvas.resetAll();
            }
        } );

        JButton disclaimerButton = new JButton( FitnessResources.getString( "disclaimer" ) );
        disclaimerButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                JOptionPane.showMessageDialog( parentFrame, FitnessStrings.DISCLAIMER );
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
            final MotionHelpBalloon motionHelpBalloon = new DefaultWiggleMe( _canvas, FitnessResources.getString( "time.start" ) );
            fitnessClock.addClockListener( new ClockAdapter() {
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
        FitnessClock clock = _model.getClock();
        clock.resetSimulationTime();
        clock.setDt( FitnessDefaults.CLOCK_DT );
        setClockRunningWhenActive( !FitnessDefaults.STARTS_PAUSED );
    }

    //----------------------------------------------------------------------------
    // Persistence
    //----------------------------------------------------------------------------

    public FitnessConfig save() {

        FitnessConfig config = new FitnessConfig();

        // Module
        config.setActive( isActive() );

        // Clock
//        ConstantDtClock clock = _model.getClock();
//        config.setClockDt( clock.getDt() );
//        config.setClockRunning( getClockRunningWhenActive() );

        // FitnessModelElement
//        Human fitnessModelElement = _model.getHuman();
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
//        ConstantDtClock clock = _model.getClock();
//        clock.setDt( config.getClockDt() );
//        setClockRunningWhenActive( config.isClockRunning() );

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

    public void applicationStarted() {
        _canvas.applicationStarted();
    }
}