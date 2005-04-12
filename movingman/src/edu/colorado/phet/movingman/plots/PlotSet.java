/** Sam Reid*/
package edu.colorado.phet.movingman.plots;

import edu.colorado.phet.chart.Range2D;
import edu.colorado.phet.chart.controllers.VerticalChartSlider;
import edu.colorado.phet.movingman.MovingManModule;
import edu.colorado.phet.movingman.model.Man;
import edu.colorado.phet.movingman.model.MovingManModel;
import edu.colorado.phet.movingman.plotdevice.PlotDevice;
import edu.colorado.phet.movingman.plotdevice.PlotDeviceData;
import edu.colorado.phet.movingman.plotdevice.PlotDeviceListenerAdapter;
import edu.colorado.phet.movingman.view.MovingManApparatusPanel;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Rectangle2D;

/**
 * User: Sam Reid
 * Date: Oct 19, 2004
 * Time: 6:54:48 PM
 * Copyright (c) Oct 19, 2004 by Sam Reid
 */
public class PlotSet {
    private MMPlotSuite positionSuite;
    private MMPlotSuite velSuite;
    private MMPlotSuite accSuite;

    private MMPlot positionPlot;
    private MMPlot velocityPlot;
    private MMPlot accelerationPlot;
    private MovingManModule module;
    private MovingManModel movingManModel;
    private MovingManApparatusPanel movingManApparatusPanel;

    public PlotSet( final MovingManModule module,
                    final MovingManApparatusPanel movingManApparatusPanel ) {
        this.movingManApparatusPanel = movingManApparatusPanel;
        this.movingManModel = module.getMovingManModel();
        this.module = module;
        double minTime = movingManModel.getMinTime();
        double maxPositionView = 12;
        double maxVelocity = 25;
        double maxAccel = 10;
        double xshiftVelocity = movingManModel.getNumSmoothingPosition() / 2;
        double xshiftAcceleration = ( movingManModel.getNumVelocitySmoothPoints() + movingManModel.getNumSmoothingPosition() ) / 2;

        BasicStroke plotStroke2 = new BasicStroke( 3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND );

        positionPlot = new MMPlot( module, movingManApparatusPanel, "Position", "x" );

        positionPlot.addPlotDeviceData( new PlotDeviceData( positionPlot, module.getMovingManModel().getPosition().getSmoothedDataSeries(), Color.blue, "Position", plotStroke2 ) );
//        positionPlot = new MMPlot( SimStrings.get( "PlotSet.PositionLabel" ), module,
//                                   movingManModel.getPosition().getSmoothedDataSeries(), module.getRecordingTimer(),
//                                   Color.blue, plotStroke, positionInputBox, getBuffer(), 0,
//                                   SimStrings.get( "PlotSet.MetersAbbreviation" ),
//                                   SimStrings.get( "PlotSet.PositionAbbreviation" ) + "=" );
//        final MMPlot.TextBox positionBox = positionPlot.getTextBox();
        ManSetter positionSetter = new ManSetter() {
            public void setValue( Man man, double value ) {
                man.setX( value );
            }
        };

        positionPlot.setPaintYLines( new double[]{5, 10} );
        final SliderHandler positionHandler = new SliderHandler( module, positionSetter );
        positionPlot.addListener( new PlotDeviceListenerAdapter() {
            public void sliderDragged( double dragValue ) {
                positionHandler.valueChanged( dragValue );
            }
        } );

//        positionPlot.addSliderListener( new SliderHandler( module, positionSetter ) );


        velocityPlot = new MMPlot( module, movingManApparatusPanel, "Velocity", "v" );

//        velocityPlot = new MMPlot( SimStrings.get( "PlotSet.VelocityLabel" ), module,
//                                   movingManModel.getVelocitySeries().getSmoothedDataSeries(), module.getRecordingTimer(),
//                                   Color.red, plotStroke, velocityInputBox, getBuffer(), xshiftVelocity,
//                                   SimStrings.get( "PlotSet.MetersPerSecondAbbreviation" ),
//                                   SimStrings.get( "PlotSet.VelocityAbbreviation" ) + "=" );
        velocityPlot.addPlotDeviceData( new PlotDeviceData( velocityPlot, module.getMovingManModel().getVelocitySeries().getSmoothedDataSeries(), Color.red, "Velocity", plotStroke2 ) );
        velocityPlot.setChartRange( new Rectangle2D.Double( minTime, -maxVelocity, movingManModel.getMaxTime() - minTime, maxVelocity * 2 ) );
        velocityPlot.setPaintYLines( new double[]{10, 20} );
        ManSetter velSetter = new ManSetter() {
            public void setValue( Man man, double value ) {
                man.setVelocity( value );
                man.setAcceleration( 0.0 );
            }
        };
        final SliderHandler velHandler = new SliderHandler( module, velSetter );
        velocityPlot.addListener( new PlotDeviceListenerAdapter() {
            public void sliderDragged( double dragValue ) {
                velHandler.valueChanged( dragValue );
            }
        } );

        accelerationPlot = new MMPlot( module, movingManApparatusPanel, "Acceleration", "a" );

//        accelerationPlot = new MMPlot( SimStrings.get( "PlotSet.AccelerationLabel" ), module,
//                                       movingManModel.getAcceleration().getSmoothedDataSeries(), module.getRecordingTimer(),
//                                       Color.black, plotStroke, accelInputBox, getBuffer(), xshiftAcceleration,
//                                       SimStrings.get( "PlotSet.MetersPerSecondSquaredAbbreviation" ),
//                                       SimStrings.get( "PlotSet.AccelerationAbbreviation" ) + "=" );
//        accelerationPlot.addSuperScript( "2" );
        Color green = new Color( 40, 165, 50 );
        accelerationPlot.addPlotDeviceData( new PlotDeviceData( accelerationPlot, module.getMovingManModel().getAcceleration().getSmoothedDataSeries(), green, "Acceleration", plotStroke2 ) );
        accelerationPlot.setChartRange( new Range2D( 0, -50, 20, 50 ) );
        accelerationPlot.setPaintYLines( new double[]{10, 20, 30, 40, 50} );
//        accelerationPlot.setMagnitude( 12 );


        ManSetter accSetter = new ManSetter() {
            public void setValue( Man man, double value ) {
                man.setAcceleration( value );
            }
        };

        final SliderHandler accelHandler = new SliderHandler( module, accSetter );
        accelerationPlot.addListener( new PlotDeviceListenerAdapter() {
            public void sliderDragged( double dragValue ) {
                accelHandler.valueChanged( dragValue );
            }
        } );
//        accelerationPlot.addSliderListener( new SliderHandler( module, accSetter ) );

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
//        velocityPlot.getVerticalChartSlider().getSlider().addMouseListener( new MouseAdapter() {
//            public void mouseReleased( MouseEvent e ) {
//                if( module.getMan().getX() >= 9.9 && velocityPlot.getVerticalChartSlider().getValue() > 0 ) {
//                    velocityPlot.getVerticalChartSlider().setValue( 0.0 );
//                }
//            }
//        } );

        positionSuite = new MMPlotSuite( movingManApparatusPanel, positionPlot );
        velSuite = new MMPlotSuite( movingManApparatusPanel, velocityPlot );
        accSuite = new MMPlotSuite( movingManApparatusPanel, accelerationPlot );

        positionSuite.getTextBox().addKeyListener( new TextHandler( positionSuite.getTextBox(), module, positionSetter ) );
        accSuite.getTextBox().addKeyListener( new TextHandler( accSuite.getTextBox(), module, accSetter ) );
        velSuite.getTextBox().addKeyListener( new TextHandler( velSuite.getTextBox(), module, velSetter ) );


        MMPlotSuite.Listener listener = new MMPlotSuite.Listener() {
            public void plotVisibilityChanged() {
                movingManApparatusPanel.relayout();
            }
        };
        positionSuite.addListener( listener );
        velSuite.addListener( listener );
        accSuite.addListener( listener );
        movingManApparatusPanel.addGraphic( positionSuite, 3 );//todo fix buffering.
        movingManApparatusPanel.addGraphic( velSuite, 4 );
        movingManApparatusPanel.addGraphic( accSuite, 5 );
    }

    public void setNumSmoothingPoints( int n ) {
        double velocityOffset = n / 2 * MovingManModule.getTimeScale();
        double accelOffset = n * MovingManModule.getTimeScale();
        velocityPlot.setShift( velocityOffset );
        accelerationPlot.setShift( accelOffset );
    }

    public PlotDevice getAccelerationPlot() {
        return accelerationPlot;
    }

    public PlotDevice getPositionPlot() {
        return positionPlot;
    }

    public PlotDevice getVelocityPlot() {
        return velocityPlot;
    }

    public void updateSliders() {
        positionPlot.updateSlider();
        velocityPlot.updateSlider();
        accelerationPlot.updateSlider();
    }

    public void cursorMovedToTime( double time, int index ) {
        //TODO fix this.
//        positionPlot.cursorMovedToTime( time, index );
//        velocityPlot.cursorMovedToTime( time, index );
//        accelerationPlot.cursorMovedToTime( time, index );
    }

    public void setCursorsVisible( boolean visible ) {
        positionPlot.setCursorVisible( visible );
        velocityPlot.setCursorVisible( visible );
        accelerationPlot.setCursorVisible( visible );
    }

    public void reset() {
        getPositionPlot().reset();
        getVelocityPlot().reset();
        getAccelerationPlot().reset();
    }

    public void enterTextBoxValues() {
        if( positionSuite.getTextBox().isChangedByUser() ) {
            try {
                String x = positionSuite.getTextBox().getText();
                double xVal = Double.parseDouble( x );
                module.getMan().setX( xVal );
            }
            catch( NumberFormatException nfe ) {
                positionPlot.setTextValue( module.getMan().getX() );
            }
            positionSuite.getTextBox().clearChangedByUser();
        }
        if( velSuite.getTextBox().isChangedByUser() ) {
            try {
                String v = velSuite.getTextBox().getText();
                double vVal = Double.parseDouble( v );
                module.getMan().setVelocity( vVal );
            }
            catch( NumberFormatException nfe ) {
                positionPlot.setTextValue( module.getMan().getX() );
            }
            velSuite.getTextBox().clearChangedByUser();
        }
        if( accSuite.getTextBox().isChangedByUser() ) {
            try {
                String a = accSuite.getTextBox().getText();
                double aVal = Double.parseDouble( a );
                module.getMan().setAcceleration( aVal );
            }
            catch( NumberFormatException nfe ) {
                positionPlot.setTextValue( module.getMan().getX() );
            }
            accSuite.getTextBox().clearChangedByUser();
        }
    }

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
            manSetter.setValue( module.getMan(), value );
            module.setNumSmoothingPoints( 12 );
        }
    }

    public static class TextHandler implements KeyListener {
        TextBox textBox;
        MovingManModule module;
        ManSetter manSetter;

        public TextHandler( TextBox textBox, MovingManModule module, ManSetter manSetter ) {
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
                manSetter.setValue( module.getMan(), value );
                module.setNumSmoothingPoints( 2 );
            }
        }
    }

    public MMPlotSuite getMinizablePositionPlot() {
        return positionSuite;
    }

    public MMPlotSuite getMinimizableVelocityPlot() {
        return velSuite;
    }

    public MMPlotSuite getMinimizableAccelerationPlot() {
        return accSuite;
    }
}