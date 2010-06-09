package edu.colorado.phet.movingmanii;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.movingmanii.model.MovingMan;
import edu.colorado.phet.movingmanii.model.MovingManModel;
import edu.colorado.phet.movingmanii.model.MovingManState;
import edu.colorado.phet.movingmanii.view.MovingManSimulationPanel;
import edu.colorado.phet.movingmanii.view.MovingManSliderNode;
import edu.colorado.phet.movingmanii.view.PlayAreaSliderControl;
import edu.colorado.phet.recordandplayback.model.RecordAndPlaybackModel;
import edu.umd.cs.piccolox.pswing.PSwing;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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

            final JCheckBox showVectorBox = new JCheckBox("Velocity Vector", model.getVelocityVectorVisible().getValue());
            showVectorBox.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    model.getVelocityVectorVisible().setValue(showVectorBox.isSelected());
                }
            });
            model.getVelocityVectorVisible().addObserver(new SimpleObserver() {
                public void update() {
                    showVectorBox.setSelected(model.getVelocityVectorVisible().getValue());
                }
            });
            showVectorBox.setOpaque(false);//todo: does this work on mac?
            PSwing pSwing = new PSwing(showVectorBox);
            pSwing.setOffset(velocitySlider.getFullBounds().getMaxX() + 10, velocitySlider.getFullBounds().getCenterY() - pSwing.getFullBounds().getHeight() / 2);
            addScreenChild(pSwing);
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

            final JCheckBox showVectorBox = new JCheckBox("Acceleration Vector", model.getAccelerationVectorVisible().getValue());
            showVectorBox.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    model.getAccelerationVectorVisible().setValue(showVectorBox.isSelected());
                }
            });
            model.getAccelerationVectorVisible().addObserver(new SimpleObserver() {
                public void update() {
                    showVectorBox.setSelected(model.getAccelerationVectorVisible().getValue());
                }
            });
            showVectorBox.setOpaque(false);
            PSwing pSwing = new PSwing(showVectorBox);
            pSwing.setOffset(accelerationSlider.getFullBounds().getMaxX() + 10, accelerationSlider.getFullBounds().getCenterY() - pSwing.getFullBounds().getHeight() / 2);
            addScreenChild(pSwing);
        }
    }
}
