package edu.colorado.phet.movingmanii;

import edu.colorado.phet.movingmanii.model.MovingMan;
import edu.colorado.phet.movingmanii.model.MovingManModel;
import edu.colorado.phet.movingmanii.model.MovingManState;
import edu.colorado.phet.movingmanii.view.MovingManSimulationPanel;
import edu.colorado.phet.movingmanii.view.MovingManSliderNode;
import edu.colorado.phet.movingmanii.view.PlayAreaSliderControl;
import edu.colorado.phet.recordandplayback.model.RecordAndPlaybackModel;

/**
 * @author Sam Reid
 */
public class MovingManSimulationPanelWithPlayAreaSliders extends MovingManSimulationPanel {
    public MovingManSimulationPanelWithPlayAreaSliders(final MovingManModel model, final RecordAndPlaybackModel<MovingManState> recordAndPlaybackModel) {
        super(model, recordAndPlaybackModel);
        {
            final PlayAreaSliderControl positionSlider = new PlayAreaSliderControl(-10, 10, model.getMousePosition(), "Position", "m", MovingManColorScheme.POSITION_COLOR);
            model.addListener(new MovingManModel.Listener() {
                public void mousePositionChanged() {
                    positionSlider.setValue(model.getMousePosition());
                }
            });
            positionSlider.addListener(new MovingManSliderNode.Adapter() {
                public void sliderDragged(double value) {
                    model.getMovingMan().setPositionDriven();
                    model.setMousePosition(value);
                }
            });
            positionSlider.setOffset(400, 200);
            addScreenChild(positionSlider);
        }

        {
            final PlayAreaSliderControl velocitySlider = new PlayAreaSliderControl(-50, 50, model.getMovingMan().getVelocity(), "Velocity", "m/s", MovingManColorScheme.VELOCITY_COLOR);
            model.getMovingMan().addListener(new MovingMan.Listener() {
                public void changed() {
                    velocitySlider.setValue(model.getMovingMan().getVelocity());
                }
            });
            velocitySlider.addListener(new MovingManSliderNode.Adapter() {
                public void sliderDragged(double value) {
                    model.getMovingMan().setVelocityDriven();//todo: only if user caused the change, not if model caused the change
                    model.getMovingMan().setVelocity(value);
                }
            });
            velocitySlider.setOffset(400, 300);
            addScreenChild(velocitySlider);
        }

        {
            final PlayAreaSliderControl accelerationSlider = new PlayAreaSliderControl(-50, 50, model.getMovingMan().getAcceleration(), "Acceleration", "m/s/s", MovingManColorScheme.ACCELERATION_COLOR);
            model.getMovingMan().addListener(new MovingMan.Listener() {
                public void changed() {
                    accelerationSlider.setValue(model.getMovingMan().getAcceleration());
                }
            });
            accelerationSlider.addListener(new MovingManSliderNode.Adapter() {
                public void sliderDragged(double value) {
                    model.getMovingMan().setAccelerationDriven();
                    model.getMovingMan().setAcceleration(value);
                }
            });
            accelerationSlider.setOffset(400, 400);
            addScreenChild(accelerationSlider);
        }
    }
}
