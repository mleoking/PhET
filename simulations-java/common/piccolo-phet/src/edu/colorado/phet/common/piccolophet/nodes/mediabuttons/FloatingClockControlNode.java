package edu.colorado.phet.common.piccolophet.nodes.mediabuttons;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.util.Function1;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.VoidFunction0;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Shows the play/pause button and step button without a container, useful when the
 * clock control buttons should be displayed in the play area.
 *
 * @author Sam Reid
 */
public class FloatingClockControlNode extends PNode {
    private final PlayPauseButton playPauseButton;
    private final StepButton stepButton;

    public FloatingClockControlNode( final Property<Boolean> clockRunning,//property to indicate whether the clock should be running or not; this value is mediated by a Property<Boolean> since this needs to also be 'and'ed with whether the module is active for multi-tab simulations.
                                     final Property<String> timeReadout,
                                     final VoidFunction0 step ) {//steps the clock when 'step' is pressed which the sim is paused
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

            protected double getDisabledImageRescaleOpScale() {
                return 1;
            }
        };
        addChild( playPauseButton );
        addChild( stepButton );

        addChild( new PText() {{
            setFont( new PhetFont( 24, true ) );
            setTextPaint( Color.white );
            timeReadout.addObserver( new SimpleObserver() {
                public void update() {
                    setText( timeReadout.getValue() );
                    setOffset( stepButton.getFullBounds().getMaxX() + 5, stepButton.getFullBounds().getCenterY() - getFullBounds().getHeight() / 2 );
                }
            } );
        }} );
    }

    public FloatingClockControlNode( Property<Boolean> clockRunning, final Function1<Double, String> timeReadout, final IClock clock ) {
        this( clockRunning, new Property<String>( timeReadout.apply( clock.getSimulationTime() ) ) {{
            clock.addClockListener( new ClockAdapter() {
                public void simulationTimeChanged( ClockEvent clockEvent ) {
                    setValue( timeReadout.apply( clock.getSimulationTime() ) );
                }
            } );
        }}, new VoidFunction0() {
            public void apply() {
                clock.stepClockWhilePaused();
            }
        } );
    }
}
