/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.eatingandexercise.module.eatingandexercise;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.view.ClockControlPanel;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.phetcommon.view.ResetAllButton;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.common.piccolophet.help.DefaultWiggleMe;
import edu.colorado.phet.common.piccolophet.help.HelpPane;
import edu.colorado.phet.common.piccolophet.help.MotionHelpBalloon;
import edu.colorado.phet.eatingandexercise.EatingAndExerciseApplication;
import edu.colorado.phet.eatingandexercise.EatingAndExerciseConstants;
import edu.colorado.phet.eatingandexercise.EatingAndExerciseResources;
import edu.colorado.phet.eatingandexercise.EatingAndExerciseStrings;
import edu.colorado.phet.eatingandexercise.control.CaloricItem;
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
    private boolean showedInitialDragWiggleMe = false;

    public EatingAndExerciseModule( final PhetFrame parentFrame ) {
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

        HumanAudioPlayer humanAudioPlayer = new HumanAudioPlayer( _model.getHuman() );
        humanAudioPlayer.start();

        GameOverDialog gameOverDialog = new GameOverDialog( parentFrame, _model.getHuman(), this );
        gameOverDialog.start();

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

        _canvas.addFoodDraggedListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if ( !showedInitialDragWiggleMe ) {
                    showedInitialDragWiggleMe = true;
                    new DragToTargetHelpItem( EatingAndExerciseModule.this, _canvas, _canvas.getPlateNode(), EatingAndExerciseResources.getString( "put.food.on.plate" ),
                                              EatingAndExerciseModule.this.getHuman().getSelectedFoods() ).start();
                }
            }
        } );
        _canvas.addExerciseDraggedListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if ( !showedInitialDragWiggleMe ) {
                    showedInitialDragWiggleMe = true;
                    new DragToTargetHelpItem( EatingAndExerciseModule.this, _canvas, _canvas.getDiaryNode(), EatingAndExerciseResources.getString( "put.exercise.on.diary" ),
                                              EatingAndExerciseModule.this.getHuman().getSelectedExercise() ).start();
                }
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
        _clockControlPanel.setTimeColumns( 10 );
        _clockControlPanel.setRestartButtonVisible( false );
        _clockControlPanel.setStepButtonText( EatingAndExerciseResources.getString( "time.next-month" ) );
        _clockControlPanel.setTimeFormat( "0.0" );

        ResetAllButton resetButton = new ResetAllButton( new Resettable() {
            public void reset() {
                resetAll();
            }
        }, parentFrame );

        JButton disclaimerButton = new JButton( EatingAndExerciseResources.getString( "disclaimer" ) );
        disclaimerButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                JOptionPane.showMessageDialog( parentFrame, EatingAndExerciseStrings.DISCLAIMER );
            }
        } );
        _clockControlPanel.add( new EatingAndExerciseHelpButton(), 0 );
        _clockControlPanel.add( disclaimerButton, 1 );

        _clockControlPanel.add( Box.createHorizontalStrut( 100 ), 2 );
        _clockControlPanel.add( resetButton, 3 );

        setClockControlPanel( _clockControlPanel );

        setHelpEnabled( true );
        setHelpPane( new HelpPane( parentFrame ) );
        reset();
    }

    public void resetAll() {
        _model.resetAll();
        _canvas.resetAll();
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
            getDefaultHelpPane().add( motionHelpBalloon );
            inited = true;
        }
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