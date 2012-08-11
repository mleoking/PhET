// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.control;

import java.text.DecimalFormat;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.Spacer;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.PlayPauseButton;
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.StepButton;
import edu.colorado.phet.common.piccolophet.nodes.slider.HSliderNode;
import edu.colorado.phet.lwjglphet.utils.GLSwingForwardingClock;
import edu.colorado.phet.lwjglphet.utils.LWJGLUtils;
import edu.colorado.phet.platetectonics.PlateTectonicsResources.Strings;
import edu.colorado.phet.platetectonics.PlateTectonicsSimSharing.UserComponents;
import edu.colorado.phet.platetectonics.model.TectonicsClock;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

import static edu.colorado.phet.platetectonics.PlateTectonicsConstants.PANEL_TITLE_FONT;

/**
 * Time control for the motion tab. When in manual mode, only displays a time readout. When in auto mode, displays the play/pause button,
 * step button, and a slider to control speed.
 */
public class TectonicsTimeControl extends PNode {

    // this property is handled in the Swing EDT
    private Property<Double> speedProperty = new Property<Double>( 1.0 );

    private static final double SLIDER_MIN = 0.1;
    private static final double SLIDER_MAX = 10;
    private final TectonicsClock lwjglClock;

    public TectonicsTimeControl( final TectonicsClock lwjglClock, final Property<Boolean> isAutoMode ) {
        this.lwjglClock = lwjglClock;
        final IClock swingClock = new GLSwingForwardingClock( lwjglClock );

        final DecimalFormat timeFormat = new DecimalFormat( "0" );

        PNode timeSlider = new TimeSlider( lwjglClock, isAutoMode );

        // play/pause button.
        final PlayPauseButton playPauseButton = new PlayPauseButton( (int) ( 100 * 0.7 * 0.7 ) ) {{
            // initial state
            setPlaying( !swingClock.isPaused() );

            // control the clock
            addListener( new Listener() {
                public void playbackStateChanged() {
                    if ( swingClock.isPaused() ) {
                        swingClock.start();
                    }
                    else {
                        swingClock.pause();
                    }
                }
            } );

            // listen to the clock event changes (and disable it when it is at the time limit)
            lwjglClock.addClockListener( new ClockAdapter() {
                @Override public void clockStarted( ClockEvent clockEvent ) {
                    SwingUtilities.invokeLater( new Runnable() {
                        public void run() {
                            setPlaying( true );
                        }
                    } );
                }

                @Override public void clockPaused( ClockEvent clockEvent ) {
                    SwingUtilities.invokeLater( new Runnable() {
                        public void run() {
                            setPlaying( false );
                        }
                    } );
                }

                @Override public void simulationTimeChanged( ClockEvent clockEvent ) {
                    final boolean atMax = isAtTimeLimit();
                    SwingUtilities.invokeLater( new Runnable() {
                        public void run() {
                            setEnabled( !atMax );
                            if ( atMax ) {
                                setPlaying( false );
                            }
                        }
                    } );
                }
            } );
        }};

        // step button
        final StepButton stepButton = new StepButton( (int) ( playPauseButton.getButtonDimension().width * 0.8 ) ) {{

            // control the clock
            addListener( new Listener() {
                public void buttonPressed() {
                    swingClock.stepClockWhilePaused();
                }
            } );

            // disable it when the clock is running OR when it is at the time limit
            lwjglClock.addClockListener( new ClockAdapter() {
                private boolean shouldBeEnabled() {
                    return lwjglClock.isPaused() && !isAtTimeLimit();
                }

                private void updateState() {
                    final boolean enabled = shouldBeEnabled();
                    SwingUtilities.invokeLater( new Runnable() {
                        public void run() {
                            setEnabled( enabled );
                        }
                    } );
                }

                @Override public void clockStarted( ClockEvent clockEvent ) {
                    updateState();
                }

                @Override public void clockPaused( ClockEvent clockEvent ) {
                    updateState();
                }

                @Override public void simulationTimeChanged( ClockEvent clockEvent ) {
                    updateState();
                }
            } );
        }};

        addChild( new HBox( 12, HBox.CENTER_ALIGNED,
                            new VBox( VBox.CENTER_ALIGNED,
                                      new PText( Strings.TIME_ELAPSED ) {{
                                          setFont( PANEL_TITLE_FONT );
                                      }},
                                      new HBox( 12, HBox.CENTER_ALIGNED,
                                                new PText( "0" ) {{
                                                    // update the time readout whenever the clock changes
                                                    lwjglClock.addClockListener( new ClockAdapter() {
                                                        @Override public void simulationTimeChanged( ClockEvent clockEvent ) {
                                                            final double simulationTime = lwjglClock.getSimulationTime();
                                                            SwingUtilities.invokeLater( new Runnable() {
                                                                public void run() {
                                                                    setText( timeFormat.format( simulationTime ) );
                                                                    repaint();
                                                                }
                                                            } );
                                                        }
                                                    } );
                                                }},
                                                new PText( Strings.MILLION_YEARS ) {{
                                                    setFont( new PhetFont( 12 ) );
                                                }}
                                      ) ),
                            timeSlider,
                            playPauseButton,
                            stepButton ) );

        isAutoMode.addObserver( new SimpleObserver() {
            public void update() {
                final boolean isAuto = isAutoMode.get();
                SwingUtilities.invokeLater( new Runnable() {
                    public void run() {
                        playPauseButton.setTransparency( isAuto ? 1 : 0 );
                        playPauseButton.setPickable( isAuto );
                        stepButton.setTransparency( isAuto ? 1 : 0 );
                        stepButton.setPickable( isAuto );
                        repaint();

                        final double speed = speedProperty.get();

                        LWJGLUtils.invoke( new Runnable() {
                            public void run() {
                                lwjglClock.setTimeMultiplier( isAuto ? speed : 1 );
                            }
                        } );
                    }
                } );
            }
        } );
    }

    public void resetAll() {
        // speed property is accessed in the Swing EDT, and our resetAll() runs in the LWJGL thread
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                speedProperty.reset();
            }
        } );
    }

    // this function run from LWJGL thread
    private boolean isAtTimeLimit() {
        return lwjglClock.getSimulationTime() >= lwjglClock.getTimeLimit();
    }

    private class TimeSlider extends HSliderNode {
        public TimeSlider( final TectonicsClock clock, final Property<Boolean> isAutoMode ) {
            super( UserComponents.timeSpeedSlider, SLIDER_MIN, SLIDER_MAX, 5, 100, speedProperty, new Property<Boolean>( true ) );

            addLabel( SLIDER_MIN, new PText( Strings.TIME_SLOW ) );
            addLabel( SLIDER_MAX, new PText( Strings.TIME_FAST ) );

            speedProperty.addObserver( new SimpleObserver() {
                public void update() {
                    // our clock is running in the LWJGL thread, so we need to wrap it
                    LWJGLUtils.invoke( new Runnable() {
                        public void run() {
                            clock.setTimeMultiplier( speedProperty.get() );
                        }
                    } );
                }
            } );

            isAutoMode.addObserver( new SimpleObserver() {
                public void update() {
                    setVisible( isAutoMode.get() );
                }
            } );
        }
    }
}
