/** Sam Reid*/
package edu.colorado.phet.movingman.plots;

import edu.colorado.phet.chart.Range2D;
import edu.colorado.phet.chart.controllers.VerticalChartSlider;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.movingman.MMFontManager;
import edu.colorado.phet.movingman.MovingManModule;
import edu.colorado.phet.movingman.model.Man;
import edu.colorado.phet.movingman.model.MovingManModel;
import edu.colorado.phet.movingman.plotdevice.PlotDeviceListenerAdapter;
import edu.colorado.phet.movingman.plotdevice.PlotDeviceSeries;
import edu.colorado.phet.movingman.view.MovingManApparatusPanel;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

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

//    private MMPlot positionPlot;
//    private MMPlot velocityPlot;
//    private MMPlot accelerationPlot;
    private MovingManModule module;
    private MovingManModel movingManModel;
    private MovingManApparatusPanel movingManApparatusPanel;
    private Font readoutFont = MMFontManager.getFontSet().getReadoutFont();
    private ArrayList listeners = new ArrayList();

    public PlotSet( final MovingManModule module,
                    final MovingManApparatusPanel movingManApparatusPanel ) {
        this.movingManApparatusPanel = movingManApparatusPanel;
        this.movingManModel = module.getMovingManModel();
        this.module = module;
        double maxPositionView = 12;
        double maxVelocity = 12;
        double maxAccel = 12;

        BasicStroke plotStroke2 = new BasicStroke( 3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND );

        final MMPlot positionPlot = new MMPlot( module, movingManApparatusPanel, "Position", "x" );
        positionPlot.setChartRange( new Range2D( movingManModel.getMinTime(), -maxPositionView, movingManModel.getMaxTime(), maxPositionView ) );
        final PlotDeviceSeries positionSeries = new PlotDeviceSeries( positionPlot,
                                                                      module.getMovingManModel().getPositionDataSuite().getSmoothedDataSeries(), Color.blue, "Position", plotStroke2, readoutFont, SimStrings.get( "PlotSet.MetersAbbreviation" ), "-99.9" );
        positionPlot.addPlotDeviceData( positionSeries );
//        positionPlot = new MMPlot( SimStrings.get( "PlotSet.PositionLabel" ), module,
//                                   movingManModel.getPosition().getSmoothedDataSeries(), module.getRecordingTimer(),
//                                   Color.blue, plotStroke, positionInputBox, getBuffer(), 0,
//                                   SimStrings.get( "PlotSet.MetersAbbreviation" ),
//                                   SimStrings.get( "PlotSet.PositionAbbreviation" ) + "=" );
//        final MMPlot.TextBox positionBox = positionPlot.getTextBox();
        ManSetter positionSetter = new ManSetter() {
            public void setValue( Man man, double value ) {
                man.setAcceleration( 0.0 );
                man.setVelocity( 0.0 );
                man.setPosition( value );
                positionSuite.valueChanged( value );
                notifyPositionControlMode();
            }
        };
        ManSetter velSetter = new ManSetter() {
            public void setValue( Man man, double value ) {
                man.setAcceleration( 0.0 );
                man.setVelocity( value );
                velSuite.valueChanged( value );
                notifyVelocityControlMode();
            }
        };
        ManSetter accSetter = new ManSetter() {
            public void setValue( Man man, double value ) {
                man.setAcceleration( value );
                accSuite.valueChanged( value );
                notifyAccelerationControlMode();
            }
        };


        final SliderHandler positionHandler = new SliderHandler( module, positionSetter ) {
            public void valueChanged( double value ) {
                if( value < -10 ) {
                    value = -10;//.0001;
                }
                if( value > 10 ) {
                    value = 10;//.0001;
                }
                super.valueChanged( value );
            }
        };
        positionPlot.addListener( new PlotDeviceListenerAdapter() {
            public void sliderDragged( double dragValue ) {
                positionHandler.valueChanged( dragValue );
            }
        } );

        final MMPlot velocityPlot = new MMPlot( module, movingManApparatusPanel, "Velocity", "v" );

//        velocityPlot = new MMPlot( SimStrings.get( "PlotSet.VelocityLabel" ), module,
//                                   movingManModel.getVelocitySeries().getSmoothedDataSeries(), module.getRecordingTimer(),
//                                   Color.red, plotStroke, velocityInputBox, getBuffer(), xshiftVelocity,
//                                   SimStrings.get( "PlotSet.MetersPerSecondAbbreviation" ),
//                                   SimStrings.get( "PlotSet.VelocityAbbreviation" ) + "=" );
        PlotDeviceSeries velSeries = new PlotDeviceSeries( velocityPlot, module.getMovingManModel().getVelocitySeries().getSmoothedDataSeries(),
                                                           Color.red, "Velocity", plotStroke2, readoutFont, SimStrings.get( "PlotSet.MetersPerSecondAbbreviation" ), "-99.9" );
        velocityPlot.addPlotDeviceData( velSeries );
//        velocityPlot.setChartRange( new Rectangle2D.Double( minTime, -maxVelocity, movingManModel.getMaxTime() - minTime, maxVelocity * 2 ) );
//        velocityPlot.setChartRange( new Rectangle2D.Double( minTime, -maxVelocity, movingManModel.getMaxTime() - minTime, maxVelocity * 2 ) );
//        velocityPlot.setChartRange( new Rectangle2D.Double( minTime, -maxVelocity, movingManModel.getMaxTime() - minTime, maxVelocity * 2 ) );
        velocityPlot.setChartRange( new Range2D( movingManModel.getMinTime(), -maxVelocity, movingManModel.getMaxTime(), maxVelocity ) );
//        velocityPlot.setPaintYLines( new double[]{10, 20} );

        final SliderHandler velHandler = new SliderHandler( module, velSetter );
        velocityPlot.addListener( new PlotDeviceListenerAdapter() {
            public void sliderDragged( double dragValue ) {
                velHandler.valueChanged( dragValue );
            }
        } );

        final MMPlot accelerationPlot = new MMPlot( module, movingManApparatusPanel, "Acceleration", "a" );

//        accelerationPlot = new MMPlot( SimStrings.get( "PlotSet.AccelerationLabel" ), module,
//                                       movingManModel.getAcceleration().getSmoothedDataSeries(), module.getRecordingTimer(),
//                                       Color.black, plotStroke, accelInputBox, getBuffer(), xshiftAcceleration,
//                                       SimStrings.get( "PlotSet.MetersPerSecondSquaredAbbreviation" ),
//                                       SimStrings.get( "PlotSet.AccelerationAbbreviation" ) + "=" );
//        accelerationPlot.addSuperScript( "2" );
        Color green = new Color( 40, 165, 50 );
//        String s ="<html>m/s<sup><font size=-1>2</font></sup></html>";
        accelerationPlot.addPlotDeviceData( new PlotDeviceSeries( accelerationPlot, module.getMovingManModel().getAccelerationDataSuite().getSmoothedDataSeries(),
                                                                  green, "Acceleration", plotStroke2, readoutFont, SimStrings.get( "PlotSet.MetersPerSecondSquaredAbbreviation" ), "-999.9" ) );
//        accelerationPlot.setChartRange( new Range2D( 0, -50, movingManModel.getMaxTime() - minTime, 50 ) );
        accelerationPlot.setChartRange( new Range2D( movingManModel.getMinTime(), -maxAccel, movingManModel.getMaxTime(), maxAccel ) );
//        accelerationPlot.setPaintYLines( new double[]{10, 20, 30, 40, 50} );
//        accelerationPlot.setMagnitude( 12 );

//        positionPlot.setPaintYLines( new double[]{5, 10} );
//        velocityPlot.setPaintYLines( new double[]{5, 10} );
//        accelerationPlot.setPaintYLines( new double[]{5, 10} );

        final SliderHandler accelHandler = new SliderHandler( module, accSetter );
        accelerationPlot.addListener( new PlotDeviceListenerAdapter() {
            public void sliderDragged( double dragValue ) {
                System.out.println( "dragValue = " + dragValue );
                accelHandler.valueChanged( dragValue );
            }
        } );
//        accelerationPlot.addSliderListener( new SliderHandler( module, accSetter ) );

        module.getMan().addListener( new Man.Adapter() {
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

        positionSuite = new MMPlotSuite( module, movingManApparatusPanel, positionPlot );
        velSuite = new MMPlotSuite( module, movingManApparatusPanel, velocityPlot );
        accSuite = new MMPlotSuite( module, movingManApparatusPanel, accelerationPlot );

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
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public static interface Listener {

        void setAccelerationControlMode();

        void setVelocityControlMode();

        void setPositionControlMode();
    }

    private void notifyAccelerationControlMode() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.setAccelerationControlMode();
        }
    }

    private void notifyVelocityControlMode() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.setVelocityControlMode();
        }
    }

    private void notifyPositionControlMode() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.setPositionControlMode();
        }
    }

    public MMPlot getAccelerationPlot() {
        return accSuite.getPlotDevice();
    }

    public MMPlot getPositionPlot() {
        return positionSuite.getPlotDevice();
    }

    public MMPlot getVelocityPlot() {
        return velSuite.getPlotDevice();
    }

    public void updateSliders() {
        getPositionPlot().updateSlider();
        getVelocityPlot().updateSlider();
        getAccelerationPlot().updateSlider();
    }

    public void cursorMovedToTime( double time, int index ) {
        //TODO fix this.
//        positionPlot.cursorMovedToTime( time, index );
//        velocityPlot.cursorMovedToTime( time, index );
//        accelerationPlot.cursorMovedToTime( time, index );
    }

    public void setCursorsVisible( boolean visible ) {
        getPositionPlot().setCursorVisible( visible );
        getVelocityPlot().setCursorVisible( visible );
        getAccelerationPlot().setCursorVisible( visible );
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
                module.getMan().setPosition( xVal );
            }
            catch( NumberFormatException nfe ) {
                getPositionPlot().setTextValue( module.getMan().getPosition() );
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
                getPositionPlot().setTextValue( module.getMan().getPosition() );
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
                getPositionPlot().setTextValue( module.getMan().getPosition() );
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
            module.setSmoothingSmooth();
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
                module.setSmoothingSharp();
            }
        }
    }

    public MMPlotSuite getPositionPlotSuite() {
        return positionSuite;
    }

    public MMPlotSuite getVelocityPlotSuite() {
        return velSuite;
    }

    public MMPlotSuite getAccelerationPlotSuite() {
        return accSuite;
    }


}