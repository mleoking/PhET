package edu.colorado.phet.movingmanii.view;

import edu.colorado.phet.common.motion.tests.ColorArrows;
import edu.colorado.phet.common.phetcommon.util.DefaultDecimalFormat;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.movingmanii.MovingManColorScheme;
import edu.colorado.phet.movingmanii.charts.ChartSliderNode;
import edu.colorado.phet.movingmanii.charts.CursorNode;
import edu.colorado.phet.movingmanii.charts.MovingManChart;
import edu.colorado.phet.movingmanii.charts.TextBox;
import edu.colorado.phet.movingmanii.model.MovingMan;
import edu.colorado.phet.movingmanii.model.MovingManModel;
import edu.colorado.phet.movingmanii.model.MovingManState;
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
    public MovingManSimulationPanel(final MovingManModel model, final RecordAndPlaybackModel<MovingManState> recordAndPlaybackModel) {
        setBackground(new Color(200, 240, 200));
        int chartHeight = 130;
        int xMax = 10;
        int chartWidth = 780;
        int chartX = 150;
        int chartInsetY = 30;
        {
            final MovingManChart positionChart = new MovingManChart(new Rectangle2D.Double(0, -xMax, 20, xMax * 2), chartWidth, chartHeight);
            {
                positionChart.setOffset(chartX, 100);
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
            final ChartSliderNode chartSliderNode = new ChartSliderNode(positionChart, positionThumb, MovingManColorScheme.POSITION_COLOR);
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
            velocityChart.setOffset(chartX, 100 + chartHeight + chartInsetY);
            velocityChart.addDataSeries(model.getVelocitySeries(), MovingManColorScheme.VELOCITY_COLOR, MovingManModel.DERIVATIVE_RADIUS);
            velocityChart.addChild(new CursorNode(model.getChartCursor(), velocityChart));
            addScreenChild(velocityChart);

            PNode positionThumb = new PImage(ColorArrows.createArrow(MovingManColorScheme.VELOCITY_COLOR));
            final ChartSliderNode chartSliderNode = new ChartSliderNode(velocityChart, positionThumb, MovingManColorScheme.VELOCITY_COLOR);
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
            accelerationChart.setOffset(chartX, 100 + (chartHeight + chartInsetY) * 2);
            accelerationChart.addDataSeries(model.getAccelerationSeries(), MovingManColorScheme.ACCELERATION_COLOR, MovingManModel.DERIVATIVE_RADIUS * 2);
            accelerationChart.addChild(new CursorNode(model.getChartCursor(), accelerationChart));
            addScreenChild(accelerationChart);

            PNode positionThumb = new PImage(ColorArrows.createArrow(MovingManColorScheme.ACCELERATION_COLOR));
            final ChartSliderNode chartSliderNode = new ChartSliderNode(accelerationChart, positionThumb, MovingManColorScheme.ACCELERATION_COLOR);
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

        final MovingManNode manNode = new MovingManNode(model.getMovingMan(), model);
        manNode.addInputEventListener(new PBasicInputEventHandler() {
            public void mousePressed(PInputEvent event) {
                recordAndPlaybackModel.startRecording();
            }
        });
        addScreenChild(manNode);

        int arrowTailWidth = 7;
        //Add Velocity vector to play area
        final PlayAreaVector velocityVector = new PlayAreaVector("Velocity", MovingManColorScheme.VELOCITY_COLOR, arrowTailWidth);
        addScreenChild(velocityVector);
        final double arrowY = 100;
        final double arrowDY = arrowTailWidth / 2 + 2;
        model.getMovingMan().addListener(new MovingMan.Listener() {
            public void changed() {
                double startX = manNode.modelToView(model.getMovingMan().getPosition());
                double velocityScale = 0.2;
                double endX = manNode.modelToView(model.getMovingMan().getPosition() + model.getMovingMan().getVelocity() * velocityScale);
                velocityVector.setArrow(startX, arrowY - arrowDY, endX, arrowY - arrowDY);
            }
        });

        //Add Acceleration vector to play area
        final PlayAreaVector accelerationVector = new PlayAreaVector("Velocity", MovingManColorScheme.ACCELERATION_COLOR, arrowTailWidth);
        addScreenChild(accelerationVector);
        model.getMovingMan().addListener(new MovingMan.Listener() {
            public void changed() {
                double startX = manNode.modelToView(model.getMovingMan().getPosition());
                double accelerationScale = 0.2 * 0.2;
                double endX = manNode.modelToView(model.getMovingMan().getPosition() + model.getMovingMan().getAcceleration() * accelerationScale);
                accelerationVector.setArrow(startX, arrowY + arrowDY, endX, arrowY + arrowDY);
            }
        });
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
