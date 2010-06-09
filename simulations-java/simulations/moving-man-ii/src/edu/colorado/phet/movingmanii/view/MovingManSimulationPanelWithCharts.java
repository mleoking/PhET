package edu.colorado.phet.movingmanii.view;

import edu.colorado.phet.common.motion.tests.ColorArrows;
import edu.colorado.phet.common.phetcommon.util.DefaultDecimalFormat;
import edu.colorado.phet.movingmanii.MovingManColorScheme;
import edu.colorado.phet.movingmanii.charts.CursorNode;
import edu.colorado.phet.movingmanii.charts.MovingManChart;
import edu.colorado.phet.movingmanii.charts.MovingManChartSliderNode;
import edu.colorado.phet.movingmanii.charts.TextBox;
import edu.colorado.phet.movingmanii.model.MovingMan;
import edu.colorado.phet.movingmanii.model.MovingManModel;
import edu.colorado.phet.movingmanii.model.MovingManState;
import edu.colorado.phet.recordandplayback.model.RecordAndPlaybackModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;

import java.awt.geom.Rectangle2D;

/**
 * @author Sam Reid
 */
public class MovingManSimulationPanelWithCharts extends MovingManSimulationPanel {

    public MovingManSimulationPanelWithCharts(final MovingManModel model, final RecordAndPlaybackModel<MovingManState> recordAndPlaybackModel) {
        super(model, recordAndPlaybackModel, 100);
        int chartHeight = 115;
        int xMax = 10;
        int chartWidth = 780;
        int chartX = 150;
        int chartInsetY = 30;//distance between chart bodies, not including labels
        double chartsY = getPlayAreaRulerNode().getFullBounds().getMaxY() + 10;//top of 1st chart
        {
            final MovingManChart positionChart = new MovingManChart(new Rectangle2D.Double(0, -xMax, 20, xMax * 2), chartWidth, chartHeight);
            {
                positionChart.setOffset(chartX, chartsY);
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

            final TextBox textBox = new TextBox("Position", MovingManColorScheme.POSITION_COLOR);
            textBox.setOffset(chartSliderNode.getFullBounds().getX() - textBox.getFullBounds().getWidth() - 10, chartSliderNode.getOffset().getY());
            addScreenChild(textBox);
            model.getMovingMan().addListener(new MovingMan.Listener() {
                public void changed() {
                    updatePositionTextBox(textBox, model);
                }
            });
            updatePositionTextBox(textBox, model);
            textBox.addListener(new TextBox.Listener() {
                public void changed() {
                    model.getMovingMan().setPositionDriven();
//                    model.getMovingMan().setPosition(Double.parseDouble(textBox.getText()));
                    model.setMousePosition(Double.parseDouble(textBox.getText()));
                }
            });
        }

        {//add the velocity chart
            double vMax = 60 / 5;
            final MovingManChart velocityChart = new MovingManChart(new Rectangle2D.Double(0, -vMax, 20, vMax * 2), chartWidth, chartHeight);
            velocityChart.setOffset(chartX, chartsY + chartHeight + chartInsetY);
            velocityChart.addDataSeries(model.getVelocitySeries(), MovingManColorScheme.VELOCITY_COLOR, MovingManModel.DERIVATIVE_RADIUS);
            velocityChart.addChild(new CursorNode(model.getChartCursor(), velocityChart));
            addScreenChild(velocityChart);

            PNode positionThumb = new PImage(ColorArrows.createArrow(MovingManColorScheme.VELOCITY_COLOR));
            final MovingManSliderNode chartSliderNode = new MovingManChartSliderNode(velocityChart, positionThumb, MovingManColorScheme.VELOCITY_COLOR);
            addScreenChild(chartSliderNode);
            chartSliderNode.setOffset(velocityChart.getOffset().getX() - chartSliderNode.getFullBounds().getWidth() - 20, velocityChart.getOffset().getY());
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

            final TextBox textBox = new TextBox("Velocity", MovingManColorScheme.VELOCITY_COLOR);
            textBox.setOffset(chartSliderNode.getFullBounds().getX() - textBox.getFullBounds().getWidth() - 10, chartSliderNode.getOffset().getY());
            addScreenChild(textBox);
            model.getMovingMan().addListener(new MovingMan.Listener() {
                public void changed() {
                    updateVelocityTextBox(textBox, model);
                }
            });
            updateVelocityTextBox(textBox, model);
            textBox.addListener(new TextBox.Listener() {
                public void changed() {
                    model.getMovingMan().setVelocityDriven();
                    model.getMovingMan().setVelocity(Double.parseDouble(textBox.getText()));
                }
            });
        }

        //Add the acceleration chart
        {
            double aMax = 60;
            final MovingManChart accelerationChart = new MovingManChart(new Rectangle2D.Double(0, -aMax, 20, aMax * 2), chartWidth, chartHeight);
            accelerationChart.setOffset(chartX, chartsY + (chartHeight + chartInsetY) * 2);
            accelerationChart.addDataSeries(model.getAccelerationSeries(), MovingManColorScheme.ACCELERATION_COLOR, MovingManModel.DERIVATIVE_RADIUS * 2);
            accelerationChart.addChild(new CursorNode(model.getChartCursor(), accelerationChart));
            addScreenChild(accelerationChart);

            PNode positionThumb = new PImage(ColorArrows.createArrow(MovingManColorScheme.ACCELERATION_COLOR));
            final MovingManSliderNode chartSliderNode = new MovingManChartSliderNode(accelerationChart, positionThumb, MovingManColorScheme.ACCELERATION_COLOR);
            addScreenChild(chartSliderNode);
            chartSliderNode.setOffset(accelerationChart.getOffset().getX() - chartSliderNode.getFullBounds().getWidth() - 20, accelerationChart.getOffset().getY());
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


            final TextBox textBox = new TextBox("Acceleration", MovingManColorScheme.ACCELERATION_COLOR);
            textBox.setOffset(chartSliderNode.getFullBounds().getX() - textBox.getFullBounds().getWidth() - 10, chartSliderNode.getOffset().getY());
            addScreenChild(textBox);
            model.getMovingMan().addListener(new MovingMan.Listener() {
                public void changed() {
                    updateAccelerationTextBox(textBox, model);
                }
            });
            updateAccelerationTextBox(textBox, model);
            textBox.addListener(new TextBox.Listener() {
                public void changed() {
                    model.getMovingMan().setAccelerationDriven();
                    model.getMovingMan().setAcceleration(Double.parseDouble(textBox.getText()));
                }
            });
        }

    }

    private void updateAccelerationTextBox(TextBox textBox, MovingManModel model) {
        textBox.setText(new DefaultDecimalFormat("0.00").format(model.getMovingMan().getAcceleration()));
    }

    private void updateVelocityTextBox(TextBox textBox, MovingManModel model) {
        textBox.setText(new DefaultDecimalFormat("0.00").format(model.getMovingMan().getVelocity()));
    }

    private void updatePositionTextBox(TextBox textBox, MovingManModel model) {
        textBox.setText(new DefaultDecimalFormat("0.00").format(model.getMovingMan().getPosition()));
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
