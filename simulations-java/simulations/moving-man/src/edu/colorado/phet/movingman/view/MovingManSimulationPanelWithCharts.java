package edu.colorado.phet.movingman.view;

import edu.colorado.phet.common.motion.charts.MotionSliderNode;
import edu.colorado.phet.common.motion.charts.MutableBoolean;
import edu.colorado.phet.common.motion.charts.TemporalChart;
import edu.colorado.phet.common.motion.charts.TemporalChartSliderNode;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.movingman.MovingManColorScheme;
import edu.colorado.phet.movingman.model.MovingMan;
import edu.colorado.phet.movingman.model.MovingManModel;
import edu.colorado.phet.movingman.model.MovingManState;
import edu.colorado.phet.recordandplayback.model.RecordAndPlaybackModel;
import edu.umd.cs.piccolox.pswing.PSwing;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Rectangle2D;

/**
 * @author Sam Reid
 */
public class MovingManSimulationPanelWithCharts extends MovingManSimulationPanel {
    protected TemporalChart positionChart;
    protected TemporalChart velocityChart;
    protected TemporalChart accelerationChart;
    protected ChartControl positionChartControl;
    protected ChartControl velocityChartControl;
    protected ChartControl accelerationChartControl;

    public MovingManSimulationPanelWithCharts(final MovingManModel model, final RecordAndPlaybackModel<MovingManState> recordAndPlaybackModel, MutableBoolean positiveToTheRight) {
        super(model, recordAndPlaybackModel, 100, positiveToTheRight);
        int xMax = 10;
        //TODO: Factor out chart code if possible
        positionChart = new TemporalChart(new Rectangle2D.Double(0, -xMax, 20, xMax * 2), model.getChartCursor());
        {
            positionChart.addDataSeries(model.getPositionGraphSeries(), MovingManColorScheme.POSITION_COLOR);

            final MotionSliderNode sliderNode = new TemporalChartSliderNode(positionChart, MovingManColorScheme.POSITION_COLOR);
            {
                model.addListener(new MovingManModel.Listener() {
                    public void mousePositionChanged() {
                        sliderNode.setValue(model.getMousePosition());
                    }
                });
                sliderNode.addListener(new MotionSliderNode.Adapter() {
                    public void sliderDragged(double value) {
                        model.setMousePosition(value);
                    }

                    public void sliderThumbGrabbed() {
                        model.getMovingMan().setPositionDriven();
                    }
                });
            }
            addScreenChild(sliderNode);

            updatePositionModeSelected(model, sliderNode);
            model.getMovingMan().addListener(new MovingMan.Listener() {
                public void changed() {
                    updatePositionModeSelected(model, sliderNode);
                }
            });
            positionChartControl = new ChartControl("Position", MovingManColorScheme.POSITION_COLOR, new TextBoxListener.Position(model), sliderNode, positionChart, "m");
            positionChartControl.addChild(new GoButton(recordAndPlaybackModel, positionChartControl, model.getPositionMode()));
            addScreenChild(positionChartControl);
        }
        addScreenChild(positionChart);

        double vMax = 60 / 5;
        velocityChart = new TemporalChart(new Rectangle2D.Double(0, -vMax, 20, vMax * 2), model.getChartCursor());
        {
            velocityChart.addDataSeries(model.getVelocityGraphSeries(), MovingManColorScheme.VELOCITY_COLOR);
            addScreenChild(velocityChart);

            final MotionSliderNode chartSliderNode = new TemporalChartSliderNode(velocityChart, MovingManColorScheme.VELOCITY_COLOR);
            addScreenChild(chartSliderNode);
            model.getMovingMan().addListener(new MovingMan.Listener() {
                public void changed() {
                    chartSliderNode.setValue(model.getMovingMan().getVelocity());
                }
            });
            chartSliderNode.addListener(new MotionSliderNode.Adapter() {
                public void sliderDragged(double value) {
                    model.getMovingMan().setVelocity(value);
                }

                public void sliderThumbGrabbed() {
                    model.getMovingMan().setVelocityDriven();
                }
            });
            updateVelocityModeSelected(model, chartSliderNode);
            model.getMovingMan().addListener(new MovingMan.Listener() {
                public void changed() {
                    updateVelocityModeSelected(model, chartSliderNode);
                }
            });

            velocityChartControl = new ChartControl("Velocity", MovingManColorScheme.VELOCITY_COLOR, new TextBoxListener.Velocity(model), chartSliderNode, velocityChart, "m/s");
            final PSwing pSwing = new PSwing(new ShowVectorCheckBox("Show Vector", model.getVelocityVectorVisible()));
            pSwing.setOffset(0, velocityChartControl.getFullBounds().getHeight());
            velocityChartControl.addChild(pSwing);
            velocityChartControl.addChild(new GoButton(recordAndPlaybackModel, velocityChartControl, model.getVelocityMode()));
            addScreenChild(velocityChartControl);
        }

        double aMax = 60;
        accelerationChart = new TemporalChart(new Rectangle2D.Double(0, -aMax, 20, aMax * 2), model.getChartCursor());
        {
            accelerationChart.addDataSeries(model.getAccelerationGraphSeries(), MovingManColorScheme.ACCELERATION_COLOR);
            addScreenChild(accelerationChart);

            final MotionSliderNode chartSliderNode = new TemporalChartSliderNode(accelerationChart, MovingManColorScheme.ACCELERATION_COLOR);

            model.getMovingMan().addListener(new MovingMan.Listener() {
                public void changed() {
                    chartSliderNode.setValue(model.getMovingMan().getAcceleration());
                }
            });
            chartSliderNode.addListener(new MotionSliderNode.Adapter() {
                public void sliderDragged(double value) {
                    model.getMovingMan().setAcceleration(value);
                }

                public void sliderThumbGrabbed() {
                    model.getMovingMan().setAccelerationDriven();
                }
            });
            updateAccelerationModeSelected(model, chartSliderNode);
            model.getMovingMan().addListener(new MovingMan.Listener() {
                public void changed() {
                    updateAccelerationModeSelected(model, chartSliderNode);
                }
            });

            accelerationChartControl = new ChartControl("Acceleration", MovingManColorScheme.ACCELERATION_COLOR, new TextBoxListener.Acceleration(model), chartSliderNode, accelerationChart, "m/s/s");
            final PSwing pSwing = new PSwing(new ShowVectorCheckBox("Show Vector", model.getAccelerationVectorVisible()));
            pSwing.setOffset(0, accelerationChartControl.getFullBounds().getHeight());
            accelerationChartControl.addChild(pSwing);

            //Todo: move these mode listeners to model?
            accelerationChartControl.addChild(new GoButton(recordAndPlaybackModel, accelerationChartControl, model.getAccelerationMode()));
            addScreenChild(accelerationChartControl);
        }

        ComponentAdapter updateLayout = new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                double maxChartControlWidth = Math.max(Math.max(positionChartControl.getFullBounds().getWidth(), velocityChartControl.getFullBounds().getWidth()), accelerationChartControl.getFullBounds().getWidth());
                double padding = 75;//width of slider plus graph range labels
                final double chartX = maxChartControlWidth + padding;
                final double chartsY = getPlayAreaRulerNode().getFullBounds().getMaxY() + 10;//top of 1st chart
                double availableHeightForCharts = getHeight() - chartsY;
                double sizePerChart = availableHeightForCharts / 3;
                double roomForZoomButtons = Math.max(Math.max(positionChart.getZoomControlWidth(), velocityChart.getZoomControlWidth()), accelerationChart.getZoomControlWidth());
                double chartWidth = getWidth() - chartX - roomForZoomButtons;
                double chartHeight = sizePerChart - 24;//for inset

                positionChart.setOffset(chartX, chartsY);
                positionChart.setViewDimension(chartWidth, chartHeight);

                velocityChart.setOffset(chartX, chartsY + sizePerChart);
                velocityChart.setViewDimension(chartWidth, chartHeight);

                accelerationChart.setOffset(chartX, chartsY + sizePerChart * 2);
                accelerationChart.setViewDimension(chartWidth, chartHeight);
            }
        };
        updateLayout.componentResized(null);
        addComponentListener(updateLayout);

        /**
         * Synchronize the chart domains.
         */
        positionChart.getDataModelBounds().addObserver(new SimpleObserver() {
            public void update() {
                velocityChart.getDataModelBounds().setHorizontalRange(positionChart.getDataModelBounds().getMinX(), positionChart.getDataModelBounds().getMaxX());
                accelerationChart.getDataModelBounds().setHorizontalRange(positionChart.getDataModelBounds().getMinX(), positionChart.getDataModelBounds().getMaxX());
            }
        });

        velocityChart.getDataModelBounds().addObserver(new SimpleObserver() {
            public void update() {
                positionChart.getDataModelBounds().setHorizontalRange(velocityChart.getDataModelBounds().getMinX(), velocityChart.getDataModelBounds().getMaxX());
                accelerationChart.getDataModelBounds().setHorizontalRange(velocityChart.getDataModelBounds().getMinX(), velocityChart.getDataModelBounds().getMaxX());
            }
        });

        accelerationChart.getDataModelBounds().addObserver(new SimpleObserver() {
            public void update() {
                positionChart.getDataModelBounds().setHorizontalRange(accelerationChart.getDataModelBounds().getMinX(), accelerationChart.getDataModelBounds().getMaxX());
                velocityChart.getDataModelBounds().setHorizontalRange(accelerationChart.getDataModelBounds().getMinX(), accelerationChart.getDataModelBounds().getMaxX());
            }
        });

        //Only show the topmost horizontal zoom button
        SimpleObserver updateHorizontalZoomVisibility = new SimpleObserver() {
            public void update() {
                if (positionChart.getMaximized().getValue()) {
                    positionChart.setHorizontalZoomButtonsVisible(true);
                    velocityChart.setHorizontalZoomButtonsVisible(false);
                    accelerationChart.setHorizontalZoomButtonsVisible(false);
                } else if (velocityChart.getMaximized().getValue()) {
                    positionChart.setHorizontalZoomButtonsVisible(false);
                    velocityChart.setHorizontalZoomButtonsVisible(true);
                    accelerationChart.setHorizontalZoomButtonsVisible(false);
                } else {
                    positionChart.setHorizontalZoomButtonsVisible(false);
                    velocityChart.setHorizontalZoomButtonsVisible(false);
                    accelerationChart.setHorizontalZoomButtonsVisible(true);
                }
            }
        };
        positionChart.getMaximized().addObserver(updateHorizontalZoomVisibility);
        velocityChart.getMaximized().addObserver(updateHorizontalZoomVisibility);
        accelerationChart.getMaximized().addObserver(updateHorizontalZoomVisibility);
        updateHorizontalZoomVisibility.update();
    }

    private void updateAccelerationModeSelected(MovingManModel model, MotionSliderNode chartSliderNode) {
        chartSliderNode.setHighlighted(model.getMovingMan().isAccelerationDriven());
    }

    private void updateVelocityModeSelected(MovingManModel model, MotionSliderNode chartSliderNode) {
        chartSliderNode.setHighlighted(model.getMovingMan().isVelocityDriven());
    }

    private void updatePositionModeSelected(MovingManModel model, MotionSliderNode chartSliderNode) {
        chartSliderNode.setHighlighted(model.getMovingMan().isPositionDriven());
    }
}
