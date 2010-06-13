package edu.colorado.phet.movingmanii.view;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.view.ResetAllButton;
import edu.colorado.phet.movingmanii.MovingManColorScheme;
import edu.colorado.phet.movingmanii.charts.TextBox;
import edu.colorado.phet.movingmanii.model.MovingMan;
import edu.colorado.phet.movingmanii.model.MovingManModel;
import edu.colorado.phet.movingmanii.model.MovingManState;
import edu.colorado.phet.recordandplayback.model.RecordAndPlaybackModel;
import edu.umd.cs.piccolox.pswing.PSwing;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * @author Sam Reid
 */
public class MovingManSimulationPanelWithPlayAreaSliders extends MovingManSimulationPanel {
    public MovingManSimulationPanelWithPlayAreaSliders(final MovingManModel model, final RecordAndPlaybackModel<MovingManState> recordAndPlaybackModel, Resettable resettable) {
        super(model, recordAndPlaybackModel, 100);
        {
            //TODO: factor out code with the sliders + text boxes used in the chart module

            final TextBox positionTextBox = new TextBox();
            new TextBoxListener.Position(model).addListeners(positionTextBox);

            final PlayAreaSliderControl slider = new PlayAreaSliderControl(-10, 10, model.getMousePosition(), "Position", "m", MovingManColorScheme.POSITION_COLOR, positionTextBox);
            model.getMovingMan().addListener(new MovingMan.Listener() {
                public void changed() {
                    slider.setHighlighted(model.getMovingMan().isPositionDriven());
                }
            });
            slider.setHighlighted(model.getMovingMan().isPositionDriven());
            model.addListener(new MovingManModel.Listener() {
                public void mousePositionChanged() {
                    slider.setValue(model.getMousePosition());
                }
            });
            slider.addListener(new MovingManSliderNode.Adapter() {
                public void sliderDragged(double value) {
                    model.getMovingMan().setPositionDriven();
                    model.setMousePosition(value);
                }
            });
            slider.setOffset(getWidth() / 2 - slider.getFullBounds().getWidth() / 2, 200);
            addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    slider.setOffset(getWidth() / 2 - slider.getFullBounds().getWidth() / 2, 200);
                }
            });
            addScreenChild(slider);
        }

        {
            final TextBox textBox = new TextBox();
            {
                new TextBoxListener.Velocity(model).addListeners(textBox);
            }
            final PlayAreaSliderControl slider = new PlayAreaSliderControl(-10, 10, model.getMovingMan().getVelocity(), "Velocity", "m/s", MovingManColorScheme.VELOCITY_COLOR, textBox);
            model.getMovingMan().addListener(new MovingMan.Listener() {
                public void changed() {
                    slider.setValue(model.getMovingMan().getVelocity());
                    slider.setHighlighted(model.getMovingMan().isVelocityDriven());
                }
            });
            slider.addListener(new MovingManSliderNode.Adapter() {
                public void sliderDragged(double value) {
                    model.getMovingMan().setVelocityDriven();//todo: only if user caused the change, not if model caused the change
                    model.getMovingMan().setVelocity(value);
                }
            });
            slider.setOffset(getWidth() / 2 - slider.getFullBounds().getWidth() / 2, 300);
            addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    slider.setOffset(getWidth() / 2 - slider.getFullBounds().getWidth() / 2, 300);
                }
            });
            addScreenChild(slider);


            final PSwing pSwing = new PSwing(new ShowVectorCheckBox("Velocity Vector", model.getVelocityVectorVisible()));
            pSwing.setOffset(slider.getFullBounds().getMaxX() + 10, slider.getFullBounds().getCenterY() - pSwing.getFullBounds().getHeight() / 2);
            addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    pSwing.setOffset(slider.getFullBounds().getMaxX() + 10, slider.getFullBounds().getCenterY() - pSwing.getFullBounds().getHeight() / 2);
                }
            });
            addScreenChild(pSwing);
        }

        {
            final TextBox box = new TextBox();
            {
                new TextBoxListener.Acceleration(model).addListeners(box);
            }
            final PlayAreaSliderControl slider = new PlayAreaSliderControl(-10, 10, model.getMovingMan().getAcceleration(), "Acceleration", "m/s/s", MovingManColorScheme.ACCELERATION_COLOR, box);
            model.getMovingMan().addListener(new MovingMan.Listener() {
                public void changed() {
                    slider.setValue(model.getMovingMan().getAcceleration());
                    slider.setHighlighted(model.getMovingMan().isAccelerationDriven());
                }
            });
            slider.addListener(new MovingManSliderNode.Adapter() {
                public void sliderDragged(double value) {
                    model.getMovingMan().setAccelerationDriven();
                    model.getMovingMan().setAcceleration(value);
                }
            });
            slider.setOffset(getWidth() / 2 - slider.getFullBounds().getWidth() / 2, 400);
            addComponentListener(new ComponentAdapter() {
                public void componentResized(ComponentEvent e) {
                    slider.setOffset(getWidth() / 2 - slider.getFullBounds().getWidth() / 2, 400);
                }
            });
            addScreenChild(slider);

            final PSwing pSwing = new PSwing(new ShowVectorCheckBox("Acceleration Vector", model.getAccelerationVectorVisible()));
            pSwing.setOffset(slider.getFullBounds().getMaxX() + 10, slider.getFullBounds().getCenterY() - pSwing.getFullBounds().getHeight() / 2);
            addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    pSwing.setOffset(slider.getFullBounds().getMaxX() + 10, slider.getFullBounds().getCenterY() - pSwing.getFullBounds().getHeight() / 2);
                }
            });
            addScreenChild(pSwing);
        }
        final PSwing resetAllButton = new PSwing(new ResetAllButton(resettable, this));
        addScreenChild(resetAllButton);
        ComponentAdapter adapter = new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                resetAllButton.setOffset(getWidth() - resetAllButton.getFullBounds().getWidth() - 2, getHeight() - resetAllButton.getFullBounds().getHeight() - 2);
            }
        };
        adapter.componentResized(null);
        addComponentListener(adapter);
    }
}
