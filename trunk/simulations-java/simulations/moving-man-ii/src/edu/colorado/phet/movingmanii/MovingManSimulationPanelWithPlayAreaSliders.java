package edu.colorado.phet.movingmanii;

import edu.colorado.phet.movingmanii.model.MovingMan;
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
        {
            final PlayAreaSliderControl positionSlider = new PlayAreaSliderControl(-10, 10, "Position", "m");
            model.addListener(new MovingManModel.Listener() {
                public void mousePositionChanged() {
                    positionSlider.setValue(model.getMousePosition());
                }
            });
            positionSlider.addListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    double value = positionSlider.getValue();//store and re-use since setPositionDriven fires an event, which changesthe value on the slider
                    model.getMovingMan().setPositionDriven();
                    model.setMousePosition(value);
                }
            });
            positionSlider.setOffset(400, 200);
            addScreenChild(positionSlider);
        }

        {
            final PlayAreaSliderControl velocitySlider = new PlayAreaSliderControl(-50, 50, "Velocity", "m/s");
            model.getMovingMan().addListener(new MovingMan.Listener() {
                public void changed() {
                    velocitySlider.setValue(model.getMovingMan().getVelocity());
                }
            });
            velocitySlider.addListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    double value = velocitySlider.getValue();
                    model.getMovingMan().setVelocityDriven();//todo: only if user caused the change, not if model caused the change
                    model.getMovingMan().setVelocity(value);
                }
            });
            velocitySlider.setOffset(400, 300);
            addScreenChild(velocitySlider);
        }

        {
            final PlayAreaSliderControl accelerationSlider = new PlayAreaSliderControl(-50, 50, "Acceleration", "m/s/s");
            model.getMovingMan().addListener(new MovingMan.Listener() {
                public void changed() {
                    accelerationSlider.setValue(model.getMovingMan().getAcceleration());
                }
            });
            accelerationSlider.addListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    double value = accelerationSlider.getValue();
                    model.getMovingMan().setAccelerationDriven();
                    model.getMovingMan().setAcceleration(value);
                }
            });
            accelerationSlider.setOffset(400, 400);
            addScreenChild(accelerationSlider);
        }
    }
}
