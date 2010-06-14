package edu.colorado.phet.movingmanii.view;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.PlayPauseButton;
import edu.colorado.phet.movingmanii.model.MutableBoolean;
import edu.colorado.phet.recordandplayback.model.RecordAndPlaybackModel;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public class GoButton extends PNode {
    public GoButton(final RecordAndPlaybackModel recordAndPlaybackModel, PNode parent, final MutableBoolean modeSelected) {
        final PlayPauseButton button = new PlayPauseButton(30);
        SimpleObserver updateButtonState = new SimpleObserver() {
            public void update() {
                button.setPlaying(!recordAndPlaybackModel.isPaused());
            }
        };
        recordAndPlaybackModel.addObserver(updateButtonState);
        updateButtonState.update();
        button.addListener(new PlayPauseButton.Listener() {
            public void playbackStateChanged() {
                recordAndPlaybackModel.setPaused(!button.isPlaying());
            }
        });
        final SimpleObserver observer = new SimpleObserver() {
            public void update() {
                button.setVisible(modeSelected.getValue());
            }
        };
        observer.update();
        modeSelected.addObserver(observer);
        recordAndPlaybackModel.addObserver(observer);
        button.setOffset(parent.getFullBounds().getCenterX() - button.getFullBounds().getWidth() / 2, parent.getFullBounds().getMaxY());
        addChild(button);
    }
}
