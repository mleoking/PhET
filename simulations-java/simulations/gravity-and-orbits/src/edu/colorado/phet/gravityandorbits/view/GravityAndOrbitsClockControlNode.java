package edu.colorado.phet.gravityandorbits.view;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.PlayPauseButton;
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.StepButton;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * @author Sam Reid
 */
public class GravityAndOrbitsClockControlNode extends PNode {
    private final PlayPauseButton playPauseButton;
    private final StepButton stepButton;

    public GravityAndOrbitsClockControlNode( final IClock clock ) {
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
        }};
        stepButton = new StepButton( 60 ) {
            {
                setOffset( playPauseButton.getFullBounds().getMaxX() + 5, playPauseButton.getFullBounds().getCenterY() - getFullBounds().getHeight() / 2 );
                final PlayPauseButton.Listener updateEnabled = new PlayPauseButton.Listener() {
                    public void playbackStateChanged() {
                        setEnabled( !playPauseButton.isPlaying() );
                    }
                };
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
                    setText( (int) ( toEarthDays( clock.getSimulationTime() ) ) + " Earth Days" );
                    setOffset( stepButton.getFullBounds().getMaxX() + 5, stepButton.getFullBounds().getCenterY() - getFullBounds().getHeight() / 2 );
                }
            } );
        }} );
    }

    private double toEarthDays( double simulationTime ) {
        return simulationTime / 86400;
    }
}
