package edu.colorado.phet.movingmanii.view;

import edu.colorado.phet.common.motion.tests.ColorArrows;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.movingmanii.MovingManColorScheme;
import edu.colorado.phet.movingmanii.charts.CursorNode;
import edu.colorado.phet.movingmanii.charts.MovingManChart;
import edu.colorado.phet.movingmanii.charts.MovingManChartSliderNode;
import edu.colorado.phet.movingmanii.model.MovingMan;
import edu.colorado.phet.movingmanii.model.MovingManModel;
import edu.colorado.phet.movingmanii.model.MovingManState;
import edu.colorado.phet.recordandplayback.model.RecordAndPlaybackModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolox.pswing.PSwing;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Rectangle2D;

/**
 * @author Sam Reid
 */
public class MovingManSimulationPanelWithCharts extends MovingManSimulationPanel {
    protected MovingManChart positionChart;
    protected MovingManChart velocityChart;
    protected MovingManChart accelerationChart;
    protected ChartControl positionChartControl;
    protected ChartControl velocityChartControl;
    protected ChartControl accelerationChartControl;

    public MovingManSimulationPanelWithCharts(final MovingManModel model, final RecordAndPlaybackModel<MovingManState> recordAndPlaybackModel) {
        super(model, recordAndPlaybackModel, 100);
        int xMax = 10;
        {
            //TODO: Factor out chart code if possible
            positionChart = new MovingManChart(new Rectangle2D.Double(0, -xMax, 20, xMax * 2));
            {
                positionChart.addDataSeries(model.getPositionSeries(), MovingManColorScheme.POSITION_COLOR, 0);
                positionChart.addChild(new CursorNode(model.getChartCursor(), positionChart));
                positionChart.addInputEventListener(new PBasicInputEventHandler() {
                    public void mouseDragged(PInputEvent event) {
                        //todo: make it possible to draw a curve
                    }
                });
            }
            addScreenChild(positionChart);
            PNode positionThumb = new PImage(ColorArrows.createArrow(MovingManColorScheme.POSITION_COLOR));
            final MovingManSliderNode chartSliderNode = new MovingManChartSliderNode(positionChart, positionThumb, MovingManColorScheme.POSITION_COLOR);
            addScreenChild(chartSliderNode);
            final SimpleObserver updateSliderLocation = new SimpleObserver() {
                public void update() {
                    chartSliderNode.setOffset(positionChart.getOffset().getX() - chartSliderNode.getFullBounds().getWidth() - 20, positionChart.getOffset().getY());
                }
            };
            updateSliderLocation.update();
            positionChart.getViewDimension().addObserver(updateSliderLocation);
            final SimpleObserver sliderVisibleSetter = new SimpleObserver() {
                public void update() {
                    chartSliderNode.setVisible(positionChart.getMaximized().getValue());
                }
            };
            sliderVisibleSetter.update();
            positionChart.getMaximized().addObserver(sliderVisibleSetter);
            chartSliderNode.setOffset(positionChart.getOffset().getX() - chartSliderNode.getFullBounds().getWidth() - 20, positionChart.getOffset().getY());
            model.addListener(new MovingManModel.Listener() {
                public void mousePositionChanged() {
                    chartSliderNode.setValue(model.getMousePosition());
                }
            });
            chartSliderNode.addListener(new MovingManSliderNode.Adapter() {
                public void sliderDragged(double value) {
                    model.setMousePosition(value);
                }

                public void sliderThumbGrabbed() {
                    model.getMovingMan().setPositionDriven();
                }
            });
            updatePositionModeSelected(model, chartSliderNode);
            model.getMovingMan().addListener(new MovingMan.Listener() {
                public void changed() {
                    updatePositionModeSelected(model, chartSliderNode);
                }
            });

            positionChartControl = new ChartControl("Position", MovingManColorScheme.POSITION_COLOR, new TextBoxListener.Position(model), chartSliderNode, positionChart, "m");
            addScreenChild(positionChartControl);
        }

        {//add the velocity chart
            double vMax = 60 / 5;
            velocityChart = new MovingManChart(new Rectangle2D.Double(0, -vMax, 20, vMax * 2));
            velocityChart.addDataSeries(model.getVelocitySeries(), MovingManColorScheme.VELOCITY_COLOR, MovingManModel.DERIVATIVE_RADIUS);
            velocityChart.addChild(new CursorNode(model.getChartCursor(), velocityChart));
            addScreenChild(velocityChart);

            PNode positionThumb = new PImage(ColorArrows.createArrow(MovingManColorScheme.VELOCITY_COLOR));
            final MovingManSliderNode chartSliderNode = new MovingManChartSliderNode(velocityChart, positionThumb, MovingManColorScheme.VELOCITY_COLOR);
            addScreenChild(chartSliderNode);
            final SimpleObserver sliderVisibleSetter = new SimpleObserver() {
                public void update() {
                    chartSliderNode.setVisible(velocityChart.getMaximized().getValue());
                }
            };
            velocityChart.getMaximized().addObserver(sliderVisibleSetter);
            sliderVisibleSetter.update();
            final SimpleObserver updateSliderLocation = new SimpleObserver() {
                public void update() {
                    chartSliderNode.setOffset(velocityChart.getOffset().getX() - chartSliderNode.getFullBounds().getWidth() - 20, velocityChart.getOffset().getY());
                }
            };
            updateSliderLocation.update();
            velocityChart.getViewDimension().addObserver(updateSliderLocation);
            model.getMovingMan().addListener(new MovingMan.Listener() {
                public void changed() {
                    chartSliderNode.setValue(model.getMovingMan().getVelocity());
                }
            });
            chartSliderNode.addListener(new MovingManSliderNode.Adapter() {
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
            addScreenChild(velocityChartControl);
        }

        //Add the acceleration chart
        {
            double aMax = 60;
            accelerationChart = new MovingManChart(new Rectangle2D.Double(0, -aMax, 20, aMax * 2));
            accelerationChart.addDataSeries(model.getAccelerationSeries(), MovingManColorScheme.ACCELERATION_COLOR, MovingManModel.DERIVATIVE_RADIUS * 2);
            accelerationChart.addChild(new CursorNode(model.getChartCursor(), accelerationChart));
            addScreenChild(accelerationChart);

            PNode positionThumb = new PImage(ColorArrows.createArrow(MovingManColorScheme.ACCELERATION_COLOR));
            final MovingManSliderNode chartSliderNode = new MovingManChartSliderNode(accelerationChart, positionThumb, MovingManColorScheme.ACCELERATION_COLOR);
            final SimpleObserver sliderVisibleSetter = new SimpleObserver() {
                public void update() {
                    chartSliderNode.setVisible(accelerationChart.getMaximized().getValue());
                }
            };
            accelerationChart.getMaximized().addObserver(sliderVisibleSetter);
            sliderVisibleSetter.update();
            addScreenChild(chartSliderNode);
            final SimpleObserver updateSliderLocation = new SimpleObserver() {
                public void update() {
                    chartSliderNode.setOffset(accelerationChart.getOffset().getX() - chartSliderNode.getFullBounds().getWidth() - 20, accelerationChart.getOffset().getY());
                }
            };
            updateSliderLocation.update();
            accelerationChart.getViewDimension().addObserver(updateSliderLocation);

            model.getMovingMan().addListener(new MovingMan.Listener() {
                public void changed() {
                    chartSliderNode.setValue(model.getMovingMan().getAcceleration());
                }
            });
            chartSliderNode.addListener(new MovingManSliderNode.Adapter() {
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
                double roomForZoomButtons = 55;
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
    }

    private void updateAccelerationModeSelected(MovingManModel model, MovingManSliderNode chartSliderNode) {
        chartSliderNode.setHighlighted(model.getMovingMan().isAccelerationDriven());
    }

    private void updateVelocityModeSelected(MovingManModel model, MovingManSliderNode chartSliderNode) {
        chartSliderNode.setHighlighted(model.getMovingMan().isVelocityDriven());
    }

    private void updatePositionModeSelected(MovingManModel model, MovingManSliderNode chartSliderNode) {
        chartSliderNode.setHighlighted(model.getMovingMan().isPositionDriven());
    }
}
