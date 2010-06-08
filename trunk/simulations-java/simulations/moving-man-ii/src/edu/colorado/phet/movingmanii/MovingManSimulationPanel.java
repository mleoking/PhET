package edu.colorado.phet.movingmanii;

import edu.colorado.phet.common.motion.tests.ColorArrows;
import edu.colorado.phet.common.phetcommon.util.DefaultDecimalFormat;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.recordandplayback.model.RecordAndPlaybackModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * @author Sam Reid
 */
public class MovingManSimulationPanel extends PhetPCanvas {
    MovingManSimulationPanel(final MovingManModel model, final RecordAndPlaybackModel<MovingManState> recordAndPlaybackModel) {
        setBackground(new Color(200, 240, 200));
        int chartHeight = 130;
        int xMax = 10;
        int chartWidth = 780;
        int chartX = 150;
        int chartInsetY = 30;
        {
            Color positionColor = Color.red;
            final MovingManChart positionChart = new MovingManChart(new Rectangle2D.Double(0, -xMax, 20, xMax * 2), chartWidth, chartHeight);
            {
                positionChart.setOffset(chartX, 100);
                positionChart.addDataSeries(model.getPositionSeries(), positionColor);
                positionChart.addChild(new CursorNode(model.getChartCursor(), positionChart));
                positionChart.addInputEventListener(new PBasicInputEventHandler() {
                    public void mouseDragged(PInputEvent event) {
                        //todo: make it possible to draw a curve
                    }
                });
            }
            addScreenChild(positionChart);
            PNode positionThumb = new PImage(ColorArrows.createArrow(positionColor));
            final ChartSliderNode chartSliderNode = new ChartSliderNode(positionChart, positionThumb, positionColor);
            addScreenChild(chartSliderNode);
            chartSliderNode.setOffset(positionChart.getOffset().getX() - chartSliderNode.getFullBounds().getWidth() - 20, positionChart.getOffset().getY());
            model.getMovingMan().addListener(new MovingMan.Listener() {
                public void changed() {
                    chartSliderNode.setValue(model.getMovingMan().getPosition());
                }
            });
            chartSliderNode.addListener(new ChartSliderNode.Adapter() {
                public void sliderDragged(double value) {
                    model.getMovingMan().setPosition(value);
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

            final TextBox textBox = new TextBox("Position", positionColor);
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
                    model.getMovingMan().setPosition(Double.parseDouble(textBox.getText()));
                }
            });
        }

        {//add the velocity chart
            double vMax = 60 / 5;
            Color velocityColor = Color.green;
            final MovingManChart velocityChart = new MovingManChart(new Rectangle2D.Double(0, -vMax, 20, vMax * 2), chartWidth, chartHeight);
            velocityChart.setOffset(chartX, 100 + chartHeight + chartInsetY);
            velocityChart.addDataSeries(model.getVelocitySeries(), velocityColor);
            velocityChart.addChild(new CursorNode(model.getChartCursor(), velocityChart));
            addScreenChild(velocityChart);

            PNode positionThumb = new PImage(ColorArrows.createArrow(velocityColor));
            final ChartSliderNode chartSliderNode = new ChartSliderNode(velocityChart, positionThumb, velocityColor);
            addScreenChild(chartSliderNode);
            chartSliderNode.setOffset(velocityChart.getOffset().getX() - chartSliderNode.getFullBounds().getWidth() - 20, velocityChart.getOffset().getY());
            model.getMovingMan().addListener(new MovingMan.Listener() {
                public void changed() {
                    chartSliderNode.setValue(model.getMovingMan().getVelocity());
                }
            });
            chartSliderNode.addListener(new ChartSliderNode.Adapter() {
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

            final TextBox textBox = new TextBox("Velocity", velocityColor);
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
            double aMax = 200 / 5;
            Color accelColor = Color.blue;
            final MovingManChart accelerationChart = new MovingManChart(new Rectangle2D.Double(0, -aMax, 20, aMax * 2), chartWidth, chartHeight);
            accelerationChart.setOffset(chartX, 100 + (chartHeight + chartInsetY) * 2);
            accelerationChart.addDataSeries(model.getAccelerationSeries(), accelColor);
            accelerationChart.addChild(new CursorNode(model.getChartCursor(), accelerationChart));
            addScreenChild(accelerationChart);

            PNode positionThumb = new PImage(ColorArrows.createArrow(accelColor));
            final ChartSliderNode chartSliderNode = new ChartSliderNode(accelerationChart, positionThumb, accelColor);
            addScreenChild(chartSliderNode);
            chartSliderNode.setOffset(accelerationChart.getOffset().getX() - chartSliderNode.getFullBounds().getWidth() - 20, accelerationChart.getOffset().getY());
            model.getMovingMan().addListener(new MovingMan.Listener() {
                public void changed() {
                    chartSliderNode.setValue(model.getMovingMan().getAcceleration());
                }
            });
            chartSliderNode.addListener(new ChartSliderNode.Adapter() {
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


            final TextBox textBox = new TextBox("Acceleration", accelColor);
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

        MovingManNode manNode = new MovingManNode(model.getMovingMan(), model);
        manNode.addInputEventListener(new PBasicInputEventHandler(){
            public void mousePressed(PInputEvent event) {
                recordAndPlaybackModel.startRecording();
            }
        });
        addScreenChild(manNode);
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

    private void updateAccelerationModeSelected(MovingManModel model, ChartSliderNode chartSliderNode) {
        chartSliderNode.setSelected(model.getMovingMan().isAccelerationDriven());
    }

    private void updateVelocityModeSelected(MovingManModel model, ChartSliderNode chartSliderNode) {
        chartSliderNode.setSelected(model.getMovingMan().isVelocityDriven());
    }

    private void updatePositionModeSelected(MovingManModel model, ChartSliderNode chartSliderNode) {
        chartSliderNode.setSelected(model.getMovingMan().isPositionDriven());
    }
}
