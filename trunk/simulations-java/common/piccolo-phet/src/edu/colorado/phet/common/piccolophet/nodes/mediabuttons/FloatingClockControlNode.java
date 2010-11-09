package edu.colorado.phet.common.piccolophet.nodes.mediabuttons;

import java.awt.*;

import edu.colorado.phet.buildanatom.modules.game.view.Function1;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
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

    public FloatingClockControlNode( final IClock clock,
                                     final Function1<Double, String> getTimeReadout ) {//The function used for displaying the time readout
        playPauseButton = new PlayPauseButton( 80 ) {{
            final Listener updatePlayPauseButtons = new Listener() {
                public void playbackStateChanged() {
                    if ( isPlaying() ) {
                        clock.start();
                    }
                    else {
                        clock.pause();
                    }
                }
            };
            addListener( updatePlayPauseButtons );
            updatePlayPauseButtons.playbackStateChanged();
            clock.addClockListener( new ClockAdapter() {
                public void clockStarted( ClockEvent clockEvent ) {
                    setPlaying( clock.isRunning() );
                }

                public void clockPaused( ClockEvent clockEvent ) {
                    setPlaying( clock.isRunning() );
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
                clock.addClockListener( new ClockAdapter() {
                    public void clockStarted( ClockEvent clockEvent ) {
                        updateEnabled.playbackStateChanged();
                    }

                    @Override
                    public void clockPaused( ClockEvent clockEvent ) {
                        updateEnabled.playbackStateChanged();
                    }
                } );
                playPauseButton.addListener( updateEnabled );
                updateEnabled.playbackStateChanged();
                addListener( new Listener() {
                    public void buttonPressed() {
                        if ( isEnabled() ) {
                            clock.stepClockWhilePaused();
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

            clock.addClockListener( new ClockAdapter() {
                public void simulationTimeChanged( ClockEvent clockEvent ) {
                    setText( getTimeReadout.apply( clock.getSimulationTime() ) );
                    setOffset( stepButton.getFullBounds().getMaxX() + 5, stepButton.getFullBounds().getCenterY() - getFullBounds().getHeight() / 2 );
                }
            } );
        }} );
    }
}
