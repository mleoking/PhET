// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.nodes.mediabuttons;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ButtonNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Shows the play/pause button and step button without a container, useful when the
 * clock control buttons should be displayed in the play area.
 *
 * @author Sam Reid
 */
public class FloatingClockControlNode extends PNode {
    private final double DISABLED_IMAGE_RESCALE_OP_SCALE = 1;
    private final PlayPauseButton playPauseButton;
    private final StepButton stepButton;

    public FloatingClockControlNode( SettableProperty<Boolean> clockRunning, final Function1<Double, String> timeReadout, final IClock clock, final String resetString,
                                     ObservableProperty<Color> timeReadoutColor ) {
        this( clockRunning, timeReadout == null ? null : new Property<String>( timeReadout.apply( clock.getSimulationTime() ) ) {{
            clock.addClockListener( new ClockAdapter() {
                @Override
                public void simulationTimeChanged( ClockEvent clockEvent ) {
                    setValue( timeReadout.apply( clock.getSimulationTime() ) );
                }
            } );
        }}, new VoidFunction0() {
            public void apply() {
                clock.stepClockWhilePaused();
            }
        }, new VoidFunction0() {
            public void apply() {
                clock.setSimulationTime( 0.0 );
            }
        },
              new Property<Double>( clock.getSimulationTime() ) {{
                  clock.addClockListener( new ClockAdapter() {
                      public void simulationTimeChanged( ClockEvent clockEvent ) {
                          setValue( clock.getSimulationTime() );
                      }
                  } );
              }}, resetString, timeReadoutColor );
    }

    public FloatingClockControlNode( final SettableProperty<Boolean> clockRunning,//property to indicate whether the clock should be running or not; this value is mediated by a Property<Boolean> since this needs to also be 'and'ed with whether the module is active for multi-tab simulations.
                                     final Property<String> timeReadout,
                                     final VoidFunction0 step,//steps the clock when 'step' is pressed which the sim is paused
                                     final VoidFunction0 resetTime,
                                     final Property<Double> simulationTime, final String resetString, final ObservableProperty<Color> timeReadoutColor ) {
        playPauseButton = new PlayPauseButton( 80 ) {{
            setPlaying( clockRunning.getValue() );
            final Listener updatePlayPauseButtons = new Listener() {
                public void playbackStateChanged() {
                    clockRunning.setValue( isPlaying() );
                }
            };
            addListener( updatePlayPauseButtons );
            updatePlayPauseButtons.playbackStateChanged();//Sync immediately
            clockRunning.addObserver( new SimpleObserver() {
                public void update() {
                    setPlaying( clockRunning.getValue() );
                }
            } );
        }};
        stepButton = new StepButton( 60 ) {
            {
                setOffset( playPauseButton.getFullBounds().getMaxX() + 5, playPauseButton.getFullBounds().getCenterY() - getFullBounds().getHeight() / 2 );
                final PlayPauseButton.Listener updateEnabled = new PlayPauseButton.Listener() {
                    public void playbackStateChanged() {
                        setEnabled( !playPauseButton.isPlaying() );
                    }
                };
                clockRunning.addObserver( new SimpleObserver() {
                    public void update() {
                        updateEnabled.playbackStateChanged();
                    }
                } );
                playPauseButton.addListener( updateEnabled );
                updateEnabled.playbackStateChanged();
                addListener( new Listener() {
                    public void buttonPressed() {
                        if ( isEnabled() ) {
                            step.apply();
                        }
                    }
                } );
            }

            @Override
            protected double getDisabledImageRescaleOpScale() {
                return DISABLED_IMAGE_RESCALE_OP_SCALE;
            }
        };
        addChild( playPauseButton );
        addChild( stepButton );

        if ( timeReadout != null ) {
            final PText readoutNode = new PText() {{
                setFont( new PhetFont( 24, true ) );
                timeReadoutColor.addObserver( new SimpleObserver() {
                    public void update() {
                        setTextPaint( timeReadoutColor.getValue() );
                    }
                } );
                timeReadout.addObserver( new SimpleObserver() {
                    public void update() {
                        setText( timeReadout.getValue() );
                        setOffset( stepButton.getFullBounds().getMaxX() + 5, stepButton.getFullBounds().getCenterY() - getFullBounds().getHeight() / 2 );
                    }
                } );
            }};
            addChild( readoutNode );
            addChild( new ButtonNode( resetString ) {{
                setOffset( readoutNode.getFullBounds().getCenterX() - getFullBounds().getWidth() / 2, readoutNode.getFullBounds().getMaxY() );
                addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        resetTime.apply();
                    }
                } );
                simulationTime.addObserver( new SimpleObserver() {
                    public void update() {
                        setEnabled( simulationTime.getValue() > 0 );
                    }
                } );
            }} );
        }
    }

    public PlayPauseButton getPlayPauseButton() {
        return playPauseButton;
    }

    /**
     * This class should be used when adding a rewind button to the control
     * panel.  It provides a consistent look and feel as well as a default
     * location to the left of the play button.
     */
    public class FloatingRewindButton extends RewindButton {

        public FloatingRewindButton() {
            super( 60 );

            // Set default position, can be moved by client if needed.
            setOffset(
                    getPlayPauseButton().getFullBounds().getMinX() - getFullBounds().getWidth() - 5,
                    getPlayPauseButton().getFullBounds().getCenterY() - getFullBounds().getHeight() / 2 );
        }

        @Override
        protected double getDisabledImageRescaleOpScale() {
            return DISABLED_IMAGE_RESCALE_OP_SCALE;
        }
    }

}