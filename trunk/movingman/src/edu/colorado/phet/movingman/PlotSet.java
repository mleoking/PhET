/** Sam Reid*/
package edu.colorado.phet.movingman;

import edu.colorado.phet.chart.controllers.VerticalChartSlider;
import edu.colorado.phet.movingman.plots.MMPlot;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Rectangle2D;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Oct 19, 2004
 * Time: 6:54:48 PM
 * Copyright (c) Oct 19, 2004 by Sam Reid
 */
public class PlotSet {
    private MMPlot positionPlot;
    private MMPlot velocityPlot;
    private MMPlot accelerationPlot;
    private MovingManModule module;
    private MovingManModel movingManModel;

    static interface ManSetter {
        void setValue( Man man, double value );
    }

    static class SliderHandler implements VerticalChartSlider.Listener {
        private MovingManModule module;
        ManSetter manSetter;

        public SliderHandler( MovingManModule module, ManSetter manSetter ) {
            this.module = module;
            this.manSetter = manSetter;
        }

        public void valueChanged( double value ) {
            module.setRecordMode();
//            module.getMan().setX( value );
            manSetter.setValue( module.getMan(), value );
            module.setNumSmoothingPoints( 12 );
        }
    }

    static class TextHandler implements KeyListener {
        MMPlot.TextBox textBox;
        MovingManModule module;
        ManSetter manSetter;

        public TextHandler( MMPlot.TextBox textBox, MovingManModule module, ManSetter manSetter ) {
            this.textBox = textBox;
            this.module = module;
            this.manSetter = manSetter;
        }

        public void keyTyped( KeyEvent e ) {
        }

        public void keyPressed( KeyEvent e ) {
        }

        public void keyReleased( KeyEvent e ) {
            if( e.getKeyCode() == KeyEvent.VK_ENTER ) {
                String str = textBox.getText();
                double value = Double.parseDouble( str );
//                module.getMan().setX( value );
                manSetter.setValue( module.getMan(), value );
                module.setNumSmoothingPoints( 2 );
            }
        }
    }

    public PlotSet( final MovingManModule module ) throws IOException {
        this.movingManModel = module.getMovingManModel();
        this.module = module;
        double minTime = movingManModel.getMinTime();
        double maxPositionView = 12;
        double maxVelocity = 25;
        double maxAccel = 10;
        double xshiftVelocity = movingManModel.getNumSmoothingPosition() / 2;
        double xshiftAcceleration = ( movingManModel.getNumVelocitySmoothPoints() + movingManModel.getNumSmoothingPosition() ) / 2;

        Stroke plotStroke = new BasicStroke( 3.0f );
        Rectangle2D.Double positionInputBox = new Rectangle2D.Double( minTime, -maxPositionView, movingManModel.getMaxTime() - minTime, maxPositionView * 2 );

        positionPlot = new MMPlot( "Position", module, movingManModel.getPosition().getSmoothedDataSeries(), module.getRecordingTimer(), Color.blue, plotStroke, positionInputBox, module.getBackground(), 0, "m", "x=" );
        final MMPlot.TextBox positionBox = positionPlot.getTextBox();
        TextHandler textHandler = new TextHandler( positionBox, module, new ManSetter() {
            public void setValue( Man man, double value ) {
                man.setX( value );
            }
        } );
        positionBox.addKeyListener( textHandler );

        positionPlot.setPaintYLines( new double[]{5, 10} );
        module.getBackground().addGraphic( positionPlot, 3 );
        positionPlot.addSliderListener( new SliderHandler( module, new ManSetter() {
            public void setValue( Man man, double value ) {
                man.setX( value );
            }
        } ) );
//        positionPlot.addSliderListener( new VerticalChartSlider.Listener() {
//            public void valueChanged( double value ) {
//                module.setRecordMode();
//                module.getMan().setX( value );
//                module.setNumSmoothingPoints( 12 );
//            }
//        } );

        Rectangle2D.Double velocityInputBox = new Rectangle2D.Double( minTime, -maxVelocity, movingManModel.getMaxTime() - minTime, maxVelocity * 2 );
        velocityPlot = new MMPlot( "Velocity", module, movingManModel.getVelocitySeries().getSmoothedDataSeries(), module.getRecordingTimer(), Color.red, plotStroke, velocityInputBox, module.getBackground(), xshiftVelocity, "m/s", "v=" );
        velocityPlot.setMagnitude( 12 );
        velocityPlot.setPaintYLines( new double[]{5, 10} );
        module.getBackground().addGraphic( velocityPlot, 4 );
        final MMPlot.TextBox velocityBox = velocityPlot.getTextBox();
        velocityBox.addKeyListener( new TextHandler( velocityBox, module, new ManSetter() {
            public void setValue( Man man, double value ) {
                man.setVelocity( value );
                man.setAcceleration( 0.0 );
            }
        } ) );
        velocityPlot.addSliderListener( new SliderHandler( module, new ManSetter() {
            public void setValue( Man man, double value ) {
                man.setVelocity( value );
                man.setAcceleration( 0.0 );
            }
        } ) );
//        velocityPlot.addSliderListener( new VerticalChartSlider.Listener() {
//            public void valueChanged( double value ) {
//                module.setRecordMode();
//                module.getMan().setVelocity( value );
//                module.getMan().setAcceleration( 0 );
//                module.setNumSmoothingPoints( 12 );
//            }
//        } );

        Rectangle2D.Double accelInputBox = new Rectangle2D.Double( minTime, -maxAccel, movingManModel.getMaxTime() - minTime, maxAccel * 2 );
        accelerationPlot = new MMPlot( "Acceleration", module, movingManModel.getAcceleration().getSmoothedDataSeries(), module.getRecordingTimer(), Color.black, plotStroke, accelInputBox, module.getBackground(), xshiftAcceleration, "m/s^2", "a=" );
        module.getBackground().addGraphic( accelerationPlot, 5 );

        accelerationPlot.setPaintYLines( new double[]{5, 10} );
        accelerationPlot.setMagnitude( 12 );

        final MMPlot.TextBox accelBox = accelerationPlot.getTextBox();
        accelBox.addKeyListener( new TextHandler( accelBox, module, new ManSetter() {
            public void setValue( Man man, double value ) {
                man.setAcceleration( value );
            }
        } ) );
        accelerationPlot.addSliderListener( new SliderHandler( module, new ManSetter() {
            public void setValue( Man man, double value ) {
                man.setAcceleration( value );
            }
        } ) );
//        accelerationPlot.addSliderListener( new VerticalChartSlider.Listener() {
//            public void valueChanged( double value ) {
//                module.setRecordMode();
//                module.getMan().setAcceleration( value );
//                module.setNumSmoothingPoints( 12 );
//            }
//        } );

        module.getMan().addListener( new Man.Listener() {
            public void positionChanged( double x ) {
                if( module.isPaused() ) {
                    positionPlot.valueChanged( x );
                }
            }

            public void velocityChanged( double velocity ) {
                if( module.isPaused() ) {
                    velocityPlot.valueChanged( velocity );
                }
            }

            public void accelerationChanged( double acceleration ) {
                if( module.isPaused() ) {
                    accelerationPlot.valueChanged( acceleration );
                }
            }
        } );
    }

    public void setNumSmoothingPoints( int n ) {
//        System.out.println( "n = " + n );
        double velocityOffset = n / 2 * MovingManModule.TIME_SCALE;
        double accelOffset = n * MovingManModule.TIME_SCALE;
        velocityPlot.setShift( velocityOffset );
        accelerationPlot.setShift( accelOffset );
    }

    public MMPlot getAccelerationPlot() {
        return accelerationPlot;
    }

    public MMPlot getPositionPlot() {
        return positionPlot;
    }

    public MMPlot getVelocityPlot() {
        return velocityPlot;
    }

    public void updateSliders() {
        positionPlot.updateSlider();
        velocityPlot.updateSlider();
        accelerationPlot.updateSlider();
    }

    public void cursorMovedToTime( double time, int index ) {
        positionPlot.cursorMovedToTime( time, index );
        velocityPlot.cursorMovedToTime( time, index );
        accelerationPlot.cursorMovedToTime( time, index );
    }

    public void setCursorsVisible( boolean visible ) {
        positionPlot.setCursorVisible( visible );
        velocityPlot.setCursorVisible( visible );
        accelerationPlot.setCursorVisible( visible );
    }

    public void reset() {
        this.getPositionPlot().reset();
        this.getVelocityPlot().reset();
        this.getAccelerationPlot().reset();
    }


}