/** Sam Reid*/
package edu.colorado.phet.movingman;

import edu.colorado.phet.chart.controllers.VerticalChartSlider;
import edu.colorado.phet.movingman.plots.MMPlot;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.text.DecimalFormat;

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
    private MovingManModule movingManModule;
    private MovingManModel movingManModel;
    private DecimalFormat formatter = new DecimalFormat( "0.00" );

    public PlotSet( final MovingManModule module ) throws IOException {
        this.movingManModel = module.getMovingManModel();
        this.movingManModule = module;
        double minTime = movingManModel.getMinTime();
        double maxPositionView = 12;
        double maxVelocity = 25;
        double maxAccel = 10;
        double xshiftVelocity = movingManModel.getNumSmoothingPosition() * MovingManModel.TIMER_SCALE / 2;
        double xshiftAcceleration = ( movingManModel.getNumVelocitySmoothPoints() + movingManModel.getNumSmoothingPosition() ) * MovingManModel.TIMER_SCALE / 2;

        Stroke plotStroke = new BasicStroke( 3.0f );
        Rectangle2D.Double positionInputBox = new Rectangle2D.Double( minTime, -maxPositionView, movingManModel.getMaxTime() - minTime, maxPositionView * 2 );

        positionPlot = new MMPlot( "Position", module, movingManModel.getPosition().getSmoothedDataSeries(), module.getRecordingTimer(), Color.blue,
                                   plotStroke, positionInputBox, module.getBackground(), 0, "m" );
        final MMPlot.TextBox positionBox = positionPlot.getTextBox();
        positionBox.addKeyListener( new KeyAdapter() {
            public void keyReleased( KeyEvent e ) {
                if( e.getKeyCode() == KeyEvent.VK_ENTER ) {
                    String str = positionBox.getText();
                    double value = Double.parseDouble( str );
                    positionBox.setText( formatter.format( value ) );
                    module.getMan().setX( value );
                }
            }
        } );

        final boolean DRAG_SLIDER_UNPAUSES = false;
        positionPlot.setPaintYLines( new double[]{5, 10} );
        module.getBackground().addGraphic( positionPlot, 3 );
        positionPlot.addSliderListener( new VerticalChartSlider.Listener() {
            public void valueChanged( double value ) {
                module.setMode( module.getMotionMode() );
                module.getMan().setX( value );
                if( DRAG_SLIDER_UNPAUSES ) {
                    module.setPaused( false );
                }
            }
        } );

        Rectangle2D.Double velocityInputBox = new Rectangle2D.Double( minTime, -maxVelocity, movingManModel.getMaxTime() - minTime, maxVelocity * 2 );
        velocityPlot = new MMPlot( "Velocity", module, movingManModel.getVelocitySeries().getSmoothedDataSeries(), module.getRecordingTimer(), Color.red, plotStroke, velocityInputBox, module.getBackground(), xshiftVelocity, "m/s" );
        velocityPlot.setMagnitude( 12 );
        velocityPlot.setPaintYLines( new double[]{5, 10} );
        module.getBackground().addGraphic( velocityPlot, 4 );
        final MMPlot.TextBox velocityBox = velocityPlot.getTextBox();
        velocityBox.addKeyListener( new KeyAdapter() {
            public void keyReleased( KeyEvent e ) {
                if( e.getKeyCode() == KeyEvent.VK_ENTER ) {
                    String str = velocityBox.getText();
                    double value = Double.parseDouble( str );
                    module.getMan().setVelocity( value * MovingManModel.TIMER_SCALE );
                    velocityBox.setText( formatter.format( value ) );
                    module.getMan().setAcceleration( 0 );
                }
            }
        } );
        velocityPlot.addSliderListener( new VerticalChartSlider.Listener() {
            public void valueChanged( double value ) {
                module.setMode( module.getMotionMode() );//acceleration needs to be the dependent variable now.
                module.getMan().setVelocity( value * MovingManModel.TIMER_SCALE );
                module.getMan().setAcceleration( 0 );
                if( DRAG_SLIDER_UNPAUSES ) {
                    module.setPaused( false );
                }
            }
        } );

        Rectangle2D.Double accelInputBox = new Rectangle2D.Double( minTime, -maxAccel, movingManModel.getMaxTime() - minTime, maxAccel * 2 );
        accelerationPlot = new MMPlot( "Acceleration", module, movingManModel.getAcceleration().getSmoothedDataSeries(), module.getRecordingTimer(), Color.black, plotStroke, accelInputBox, module.getBackground(), xshiftAcceleration, "m/s^2" );
        module.getBackground().addGraphic( accelerationPlot, 5 );

        accelerationPlot.setPaintYLines( new double[]{5, 10} );
        accelerationPlot.setMagnitude( 12 );

        final MMPlot.TextBox accelBox = accelerationPlot.getTextBox();
        accelBox.addKeyListener( new KeyAdapter() {
            public void keyReleased( KeyEvent e ) {
                if( e.getKeyCode() == KeyEvent.VK_ENTER ) {
                    String str = accelBox.getText();
                    double value = Double.parseDouble( str );
                    accelBox.setText( formatter.format( value ) );
                    module.getMan().setAcceleration( value * MovingManModel.TIMER_SCALE * MovingManModel.TIMER_SCALE );
                }
            }
        } );
        accelerationPlot.addSliderListener( new VerticalChartSlider.Listener() {
            public void valueChanged( double value ) {
                module.setMode( module.getMotionMode() );
                module.getMan().setAcceleration( value * MovingManModel.TIMER_SCALE * MovingManModel.TIMER_SCALE );
                if( DRAG_SLIDER_UNPAUSES ) {
                    module.setPaused( false );
                }
            }
        } );
    }

    public void setNumSmoothingPoints( int n ) {
        double xshiftVelocity = n * MovingManModel.TIMER_SCALE / 2;
        double xshiftAcceleration = ( n + n ) * MovingManModel.TIMER_SCALE / 2;
        velocityPlot.setShift( xshiftVelocity );
        accelerationPlot.setShift( xshiftAcceleration );
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

    public void cursorMovedToTime( double time ) {
        positionPlot.cursorMovedToTime( time );
        velocityPlot.cursorMovedToTime( time );
        accelerationPlot.cursorMovedToTime( time );
    }

    public void setCursorsVisible( boolean visible ) {
        positionPlot.setCursorVisible( visible );
        velocityPlot.setCursorVisible( visible );
        accelerationPlot.setCursorVisible( visible );
    }
}