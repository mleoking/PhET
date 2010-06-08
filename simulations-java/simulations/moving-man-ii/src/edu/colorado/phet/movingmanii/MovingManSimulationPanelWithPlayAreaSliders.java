package edu.colorado.phet.movingmanii;

import edu.colorado.phet.movingmanii.model.MovingManModel;
import edu.colorado.phet.movingmanii.model.MovingManState;
import edu.colorado.phet.movingmanii.view.MovingManSimulationPanel;
import edu.colorado.phet.movingmanii.view.PlayAreaSliderControl;
import edu.colorado.phet.recordandplayback.model.RecordAndPlaybackModel;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * @author Sam Reid
 */
public class MovingManSimulationPanelWithPlayAreaSliders extends MovingManSimulationPanel {
    public MovingManSimulationPanelWithPlayAreaSliders(final MovingManModel model, final RecordAndPlaybackModel<MovingManState> recordAndPlaybackModel) {
        super(model, recordAndPlaybackModel);
        final PlayAreaSliderControl playAreaSliderControl = new PlayAreaSliderControl();
        model.addListener(new MovingManModel.Listener() {
            public void mousePositionChanged() {
//                System.out.println("model changed: "+model.getMousePosition());
                playAreaSliderControl.setValue(model.getMousePosition());
            }
        });
        playAreaSliderControl.addListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                model.getMovingMan().setPositionDriven();
                double value = playAreaSliderControl.getValue();
//                System.out.println("Slider dragged: "+value );
                model.setMousePosition(value);
            }
        });
        playAreaSliderControl.setOffset(400, 200);
        addScreenChild(playAreaSliderControl);
    }
}
