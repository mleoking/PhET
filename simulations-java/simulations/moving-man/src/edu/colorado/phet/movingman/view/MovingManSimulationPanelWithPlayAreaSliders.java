package edu.colorado.phet.movingman.view;

import edu.colorado.phet.common.motion.charts.MotionSliderNode;
import edu.colorado.phet.common.motion.charts.MutableBoolean;
import edu.colorado.phet.common.motion.charts.TextBox;
import edu.colorado.phet.movingman.MovingManColorScheme;
import edu.colorado.phet.movingman.model.MovingMan;
import edu.colorado.phet.movingman.model.MovingManModel;
import edu.colorado.phet.movingman.model.MovingManState;
import edu.colorado.phet.recordandplayback.model.RecordAndPlaybackModel;
import edu.umd.cs.piccolox.pswing.PSwing;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * @author Sam Reid
 */
public class MovingManSimulationPanelWithPlayAreaSliders extends MovingManSimulationPanel {
    protected PlayAreaSliderControl positonSlider;
    protected PlayAreaSliderControl velocitySlider;
    protected PlayAreaSliderControl accelerationSlider;
    public static int DISTANCE_BETWEEN_SLIDERS = 20;

    public MovingManSimulationPanelWithPlayAreaSliders(final MovingManModel model, final RecordAndPlaybackModel<MovingManState> recordAndPlaybackModel, MutableBoolean positiveToTheRight) {
        super(model, recordAndPlaybackModel, 100, positiveToTheRight);
        {
            //TODO: factor out code with the sliders + text boxes used in the chart module

            final TextBox positionTextBox = new TextBox();
            new TextBoxListener.Position(model).addListeners(positionTextBox);

            positonSlider = new PlayAreaSliderControl(-10, 10, model.getMousePosition(), "Position", "m", MovingManColorScheme.POSITION_COLOR, positionTextBox);
            model.getMovingMan().addListener(new MovingMan.Listener() {
                public void changed() {
                    positonSlider.setHighlighted(model.getMovingMan().isPositionDriven());
                }
            });
            positonSlider.setHighlighted(model.getMovingMan().isPositionDriven());
            model.addListener(new MovingManModel.Listener() {
                public void mousePositionChanged() {
                    positonSlider.setValue(model.getMousePosition());
                }
            });
            positonSlider.addListener(new MotionSliderNode.Adapter() {
                public void sliderDragged(double value) {
                    model.getMovingMan().setPositionDriven();
                    model.setMousePosition(value);
                }
            });
            ComponentAdapter relayout = new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    positonSlider.setOffset(getWidth() / 2 - positonSlider.getFullBounds().getWidth() / 2, 100 + getRulerHeight() + DISTANCE_BETWEEN_SLIDERS);
                }
            };
            addComponentListener(relayout);
            relayout.componentResized(null);
            addScreenChild(positonSlider);
        }

        {
            final TextBox textBox = new TextBox();
            {
                new TextBoxListener.Velocity(model).addListeners(textBox);
            }
            velocitySlider = new PlayAreaSliderControl(-10, 10, model.getMovingMan().getVelocity(), "Velocity", "m/s", MovingManColorScheme.VELOCITY_COLOR, textBox);
            model.getMovingMan().addListener(new MovingMan.Listener() {
                public void changed() {
                    velocitySlider.setValue(model.getMovingMan().getVelocity());
                    velocitySlider.setHighlighted(model.getMovingMan().isVelocityDriven());
                }
            });
            velocitySlider.addListener(new MotionSliderNode.Adapter() {
                public void sliderDragged(double value) {
                    model.getMovingMan().setVelocityDriven();//todo: only if user caused the change, not if model caused the change
                    model.getMovingMan().setVelocity(value);
                }
            });
            ComponentAdapter relayout = new ComponentAdapter() {
                public void componentResized(ComponentEvent e) {
                    velocitySlider.setOffset(getWidth() / 2 - velocitySlider.getFullBounds().getWidth() / 2, positonSlider.getFullBounds().getMaxY() + DISTANCE_BETWEEN_SLIDERS);
                }
            };
            relayout.componentResized(null);
            addComponentListener(relayout);
            addScreenChild(velocitySlider);

            final PSwing pSwing = new PSwing(new ShowVectorCheckBox("Velocity Vector", model.getVelocityVectorVisible()));
            pSwing.setOffset(velocitySlider.getFullBounds().getMaxX() + 10, velocitySlider.getFullBounds().getCenterY() - pSwing.getFullBounds().getHeight() / 2);
            addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    pSwing.setOffset(velocitySlider.getFullBounds().getMaxX() + 10, velocitySlider.getFullBounds().getCenterY() - pSwing.getFullBounds().getHeight() / 2);
                }
            });
            addScreenChild(pSwing);
        }

        {
            final TextBox box = new TextBox();
            {
                new TextBoxListener.Acceleration(model).addListeners(box);
            }
            accelerationSlider = new PlayAreaSliderControl(-10, 10, model.getMovingMan().getAcceleration(), "Acceleration", "m/s/s", MovingManColorScheme.ACCELERATION_COLOR, box);
            model.getMovingMan().addListener(new MovingMan.Listener() {
                public void changed() {
                    accelerationSlider.setValue(model.getMovingMan().getAcceleration());
                    accelerationSlider.setHighlighted(model.getMovingMan().isAccelerationDriven());
                }
            });
            accelerationSlider.addListener(new MotionSliderNode.Adapter() {
                public void sliderDragged(double value) {
                    model.getMovingMan().setAccelerationDriven();
                    model.getMovingMan().setAcceleration(value);
                }
            });
            ComponentAdapter relayout = new ComponentAdapter() {
                public void componentResized(ComponentEvent e) {
                    accelerationSlider.setOffset(getWidth() / 2 - accelerationSlider.getFullBounds().getWidth() / 2, velocitySlider.getFullBounds().getMaxY() + DISTANCE_BETWEEN_SLIDERS);
                }
            };
            relayout.componentResized(null);
            addComponentListener(relayout);
            addScreenChild(accelerationSlider);

            final PSwing pSwing = new PSwing(new ShowVectorCheckBox("Acceleration Vector", model.getAccelerationVectorVisible()));
            pSwing.setOffset(accelerationSlider.getFullBounds().getMaxX() + 10, accelerationSlider.getFullBounds().getCenterY() - pSwing.getFullBounds().getHeight() / 2);
            addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    pSwing.setOffset(accelerationSlider.getFullBounds().getMaxX() + 10, accelerationSlider.getFullBounds().getCenterY() - pSwing.getFullBounds().getHeight() / 2);
                }
            });
            addScreenChild(pSwing);
        }
        ComponentAdapter relayout = new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                getLayer().setOffset(0, getHeight() / 2 - 100);//100 is height of play area
            }
        };
        addComponentListener(relayout);
        relayout.componentResized(null);
    }
}
