package edu.colorado.phet.movingmanii.view;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.PlayPauseButton;
import edu.colorado.phet.movingmanii.model.MutableBoolean;
import edu.colorado.phet.recordandplayback.model.RecordAndPlaybackModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * @author Sam Reid
 */
public class GoButton extends PNode {
    public GoButton(final RecordAndPlaybackModel recordAndPlaybackModel, PNode parent, final MutableBoolean modeSelected) {
        final PlayPauseButton button = new PlayPauseButton(30) {
            public void setPlaying(boolean b) {
                super.setPlaying(false);
                //override so it always looks like a play button
            }
        };
        button.setPlaying(false);
        button.addInputEventListener(new PBasicInputEventHandler() {
            public void mousePressed(PInputEvent event) {
                recordAndPlaybackModel.setPaused(false);
            }
        });
        final SimpleObserver observer = new SimpleObserver() {
            public void update() {
                button.setVisible(recordAndPlaybackModel.isPaused() && modeSelected.getValue());
            }
        };
        observer.update();
        modeSelected.addObserver(observer);
        recordAndPlaybackModel.addObserver(observer);
        button.setOffset(parent.getFullBounds().getCenterX() - button.getFullBounds().getWidth() / 2, parent.getFullBounds().getMaxY());
        addChild(button);
    }
}
