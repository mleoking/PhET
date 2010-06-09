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
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * @author Sam Reid
 */
public class MovingManSimulationPanelWithPlayAreaSliders extends MovingManSimulationPanel {
    public MovingManSimulationPanelWithPlayAreaSliders(final MovingManModel model, final RecordAndPlaybackModel<MovingManState> recordAndPlaybackModel) {
        super(model, recordAndPlaybackModel, 100);
        {
            //TODO: factor out code with the sliders + text boxes used in the chart module
            final PlayAreaSliderControl slider = new PlayAreaSliderControl(-10, 10, model.getMousePosition(), "Position", "m", MovingManColorScheme.POSITION_COLOR);
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
            final PlayAreaSliderControl slider = new PlayAreaSliderControl(-50, 50, model.getMovingMan().getVelocity(), "Velocity", "m/s", MovingManColorScheme.VELOCITY_COLOR);
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
            final PSwing pSwing = new PSwing(showVectorBox);
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
            final PlayAreaSliderControl slider = new PlayAreaSliderControl(-50, 50, model.getMovingMan().getAcceleration(), "Acceleration", "m/s/s", MovingManColorScheme.ACCELERATION_COLOR);
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
            final PSwing pSwing = new PSwing(showVectorBox);
            pSwing.setOffset(slider.getFullBounds().getMaxX() + 10, slider.getFullBounds().getCenterY() - pSwing.getFullBounds().getHeight() / 2);
            addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    pSwing.setOffset(slider.getFullBounds().getMaxX() + 10, slider.getFullBounds().getCenterY() - pSwing.getFullBounds().getHeight() / 2);
                }
            });
            addScreenChild(pSwing);
        }
    }
}
