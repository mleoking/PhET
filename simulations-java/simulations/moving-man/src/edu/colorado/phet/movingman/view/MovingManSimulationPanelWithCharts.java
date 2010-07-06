package edu.colorado.phet.movingman.view;

import edu.colorado.phet.common.motion.charts.*;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.movingman.MovingManColorScheme;
import edu.colorado.phet.movingman.model.MovingMan;
import edu.colorado.phet.movingman.model.MovingManModel;
import edu.colorado.phet.movingman.model.MovingManState;
import edu.colorado.phet.recordandplayback.model.RecordAndPlaybackModel;
import edu.umd.cs.piccolox.pswing.PSwing;
import static edu.colorado.phet.movingman.MovingManStrings.*;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Rectangle2D;

/**
 * @author Sam Reid
 */
public class MovingManSimulationPanelWithCharts extends MovingManSimulationPanel {
    public MovingManSimulationPanelWithCharts(final MovingManModel model, final RecordAndPlaybackModel<MovingManState> recordAndPlaybackModel, MutableBoolean positiveToTheRight) {
        super(model, recordAndPlaybackModel, 100, positiveToTheRight);

        MinimizableControlChart positionMovingManChart;
        {
            int xMax = 10;
            //TODO: Factor out chart code if possible
            TemporalChart positionChart = new TemporalChart(new Rectangle2D.Double(0, -xMax, 20, xMax * 2), model.getChartCursor());
            {
                positionChart.addDataSeries(model.getPositionGraphSeries(), MovingManColorScheme.POSITION_COLOR);
            }
            final MotionSliderNode sliderNode = new TemporalChartSliderNode(positionChart, MovingManColorScheme.POSITION_COLOR);
            {
                model.addListener(new MovingManModel.Listener() {
                    public void mousePositionChanged() {
                        sliderNode.setValue(model.getMousePosition());
                    }
                });
                sliderNode.addListener(new MotionSliderNode.Adapter() {
                    public void sliderDragged(Double value) {
                        model.setMousePosition(value);
                    }

                    public void sliderThumbGrabbed() {
                        model.getMovingMan().setPositionDriven();
                    }
                });
            }

            final SimpleObserver updatePositionModeSelected = new SimpleObserver() {
                public void update() {
                    sliderNode.setHighlighted(model.getMovingMan().isPositionDriven());
                }
            };
            model.getMovingMan().addListener(new MovingMan.Listener() {
                public void changed() {
                    updatePositionModeSelected.update();
                }
            });
            updatePositionModeSelected.update();

            MovingManChartControl positionChartControl = new MovingManChartControl(POSITION, MovingManColorScheme.POSITION_COLOR, new TextBoxListener.Position(model), "m");
            {
                final GoButton goButton = new GoButton(recordAndPlaybackModel, model.getPositionMode());
                positionChartControl.addChild(goButton);
                goButton.setOffset(positionChartControl.getFullBounds().getMaxX() - goButton.getFullBounds().getWidth(), positionChartControl.getFullBounds().getMaxY());
            }
            positionMovingManChart = new MinimizableControlChart(POSITION,new ControlChart(positionChartControl, sliderNode, positionChart, new ChartZoomControlNode(positionChart)));
        }

        MinimizableControlChart velocityMovingManChart = null;
        {
            double vMax = 60 / 5;
            TemporalChart velocityChart = new TemporalChart(new Rectangle2D.Double(0, -vMax, 20, vMax * 2), model.getChartCursor());
            {
                velocityChart.addDataSeries(model.getVelocityGraphSeries(), MovingManColorScheme.VELOCITY_COLOR);
            }
            MovingManChartControl velocityChartControl = new MovingManChartControl(VELOCITY, MovingManColorScheme.VELOCITY_COLOR, new TextBoxListener.Velocity(model), UNITS_VELOCITY_ABBREVIATION);
            {
                final PSwing pSwing = new PSwing(new ShowVectorCheckBox(SHOW_VECTOR, model.getVelocityVectorVisible()));
                pSwing.setOffset(0, velocityChartControl.getFullBounds().getHeight());
                velocityChartControl.addChild(pSwing);
                final GoButton goButton = new GoButton(recordAndPlaybackModel, model.getVelocityMode());
                goButton.setOffset(velocityChartControl.getFullBounds().getMaxX() - goButton.getFullBounds().getWidth(), velocityChartControl.getFullBounds().getMaxY());
                velocityChartControl.addChild(goButton);
            }
            final MotionSliderNode chartSliderNode = new TemporalChartSliderNode(velocityChart, MovingManColorScheme.VELOCITY_COLOR);
            {
                model.getMovingMan().addListener(new MovingMan.Listener() {
                    public void changed() {
                        chartSliderNode.setValue(model.getMovingMan().getVelocity());
                    }
                });
                chartSliderNode.addListener(new MotionSliderNode.Adapter() {
                    public void sliderDragged(Double value) {
                        model.getMovingMan().setVelocity(value);
                    }

                    public void sliderThumbGrabbed() {
                        model.getMovingMan().setVelocityDriven();
                    }
                });
            }

            final SimpleObserver updateVelocityModeSelected = new SimpleObserver() {
                public void update() {
                    chartSliderNode.setHighlighted(model.getMovingMan().isVelocityDriven());
                }
            };
            model.getMovingMan().addListener(new MovingMan.Listener() {
                public void changed() {
                    updateVelocityModeSelected.update();
                }
            });
            updateVelocityModeSelected.update();
            velocityMovingManChart = new MinimizableControlChart(VELOCITY, new ControlChart(velocityChartControl, chartSliderNode, velocityChart, new ChartZoomControlNode(velocityChart)));
        }

        MinimizableControlChart accelerationMovingManChart = null;
        {

            double aMax = 60;
            TemporalChart accelerationChart = new TemporalChart(new Rectangle2D.Double(0, -aMax, 20, aMax * 2), model.getChartCursor());
            {
                accelerationChart.addDataSeries(model.getAccelerationGraphSeries(), MovingManColorScheme.ACCELERATION_COLOR);

            }
            final MotionSliderNode chartSliderNode = new TemporalChartSliderNode(accelerationChart, MovingManColorScheme.ACCELERATION_COLOR);
            {
                model.getMovingMan().addListener(new MovingMan.Listener() {
                    public void changed() {
                        chartSliderNode.setValue(model.getMovingMan().getAcceleration());
                    }
                });
                chartSliderNode.addListener(new MotionSliderNode.Adapter() {
                    public void sliderDragged(Double value) {
                        model.getMovingMan().setAcceleration(value);
                    }

                    public void sliderThumbGrabbed() {
                        model.getMovingMan().setAccelerationDriven();
                    }
                });
            }

            final SimpleObserver updateAccelerationModeSelected = new SimpleObserver() {
                public void update() {
                    chartSliderNode.setHighlighted(model.getMovingMan().isAccelerationDriven());
                }
            };
            model.getMovingMan().addListener(new MovingMan.Listener() {
                public void changed() {
                    updateAccelerationModeSelected.update();
                }
            });
            updateAccelerationModeSelected.update();

            MovingManChartControl accelerationChartControl = new MovingManChartControl(ACCELERATION, MovingManColorScheme.ACCELERATION_COLOR, new TextBoxListener.Acceleration(model), UNITS_ACCELERATION_ABBREVIATION);
            {
                final PSwing pSwing = new PSwing(new ShowVectorCheckBox(SHOW_VECTOR, model.getAccelerationVectorVisible()));
                pSwing.setOffset(0, accelerationChartControl.getFullBounds().getHeight());
                accelerationChartControl.addChild(pSwing);

                final GoButton goButton = new GoButton(recordAndPlaybackModel, model.getAccelerationMode());
                goButton.setOffset(accelerationChartControl.getFullBounds().getMaxX() - goButton.getFullBounds().getWidth(), accelerationChartControl.getFullBounds().getMaxY());
                accelerationChartControl.addChild(goButton);
            }
            ControlChart acc = new ControlChart(accelerationChartControl, chartSliderNode, accelerationChart, new ChartZoomControlNode(accelerationChart));
            accelerationMovingManChart = new MinimizableControlChart(ACCELERATION, acc);
        }
        final MultiControlChart multiChart = new MultiControlChart(new MinimizableControlChart[]{positionMovingManChart, velocityMovingManChart, accelerationMovingManChart});
        addScreenChild(multiChart);

        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                double topInset = getPlayAreaRulerNode().getFullBounds().getMaxY() + 10;
                multiChart.setSize(getWidth(), getHeight() - topInset);
                multiChart.setOffset(0, topInset);
            }
        });
        multiChart.setSize(getWidth(), getHeight());
    }
}