/** Sam Reid*/
package edu.colorado.phet.movingman;

import edu.colorado.phet.chart.controllers.VerticalChartSlider;
import edu.colorado.phet.movingman.plots.MMPlot;
import edu.colorado.phet.movingman.plots.PlotAndText;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Oct 19, 2004
 * Time: 6:54:48 PM
 * Copyright (c) Oct 19, 2004 by Sam Reid
 */
public class PlotSet {
    private PlotAndText accelerationPlot;
    private PlotAndText positionPlot;
    private PlotAndText velocityPlot;
    private MovingManModule movingManModule;
    private MovingManModel movingManModel;

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

        final MMPlot positionGraphic = new MMPlot( "Position", module, movingManModel.getPosition().getSmoothedDataSeries(), module.getRecordingTimer(), Color.blue,
                                                   plotStroke, positionInputBox, module.getBackground(), 0 );
        positionGraphic.setPaintYLines( new double[]{5, 10} );
        Point textCoord = module.getLayout().getTextCoordinates( 0 );
        ValueGraphic positionString = new ValueGraphic( module, module.getRecordingTimer(), module.getPlaybackTimer(), movingManModel.getPosition().getSmoothedDataSeries(), "Position=", "m", textCoord.x, textCoord.y, positionGraphic );

        module.getBackground().addGraphic( positionGraphic, 3 );
        module.getApparatusPanel().addGraphic( positionString, 7 );

        positionPlot = new PlotAndText( positionGraphic, positionString );
        positionGraphic.addSliderListener( new VerticalChartSlider.Listener() {
            public void valueChanged( double value ) {
                module.setMode( module.getMotionMode() );
                module.getMan().setX( value );
                module.setPaused( false );
                module.getMovingManControlPanel().enablePause();
            }
        } );

        Rectangle2D.Double velocityInputBox = new Rectangle2D.Double( minTime, -maxVelocity, movingManModel.getMaxTime() - minTime, maxVelocity * 2 );
        final MMPlot velocityGraphic = new MMPlot( "Velocity", module, movingManModel.getVelocitySeries().getSmoothedDataSeries(), module.getRecordingTimer(), Color.red, plotStroke, velocityInputBox, module.getBackground(), xshiftVelocity );

        velocityGraphic.setPaintYLines( new double[]{10, 20} );
        ValueGraphic velocityString = new ValueGraphic( module, module.getRecordingTimer(), module.getPlaybackTimer(), movingManModel.getVelocitySeries().getSmoothedDataSeries(), "Velocity=", "m/s", textCoord.x, textCoord.y, velocityGraphic );

        module.getBackground().addGraphic( velocityGraphic, 4 );
        module.getApparatusPanel().addGraphic( velocityString, 7 );
        velocityPlot = new PlotAndText( velocityGraphic, velocityString );
        velocityGraphic.addSliderListener( new VerticalChartSlider.Listener() {
            public void valueChanged( double value ) {
                module.setMode( module.getMotionMode() );//acceleration needs to be the dependent variable now.
                module.getMan().setVelocity( value * MovingManModel.TIMER_SCALE );
                module.getMan().setAcceleration( 0 );
                module.setPaused( false );
                module.getMovingManControlPanel().enablePause();
            }
        } );

        Rectangle2D.Double accelInputBox = new Rectangle2D.Double( minTime, -maxAccel, movingManModel.getMaxTime() - minTime, maxAccel * 2 );
        MMPlot accelPlot = new MMPlot( "Acceleration", module, movingManModel.getAcceleration().getSmoothedDataSeries(), module.getRecordingTimer(), Color.black, plotStroke, accelInputBox, module.getBackground(), xshiftAcceleration );
        module.getBackground().addGraphic( accelPlot, 5 );

        accelPlot.setPaintYLines( new double[]{25, 50} );
        ValueGraphic accelString = new ValueGraphic( module, module.getRecordingTimer(), module.getPlaybackTimer(), movingManModel.getAcceleration().getSmoothedDataSeries(), "Acceleration=", "m/s^2", textCoord.x, textCoord.y, accelPlot );
        module.getApparatusPanel().addGraphic( accelString, 5 );
        accelerationPlot = new PlotAndText( accelPlot, accelString );
        accelPlot.addSliderListener( new VerticalChartSlider.Listener() {
            public void valueChanged( double value ) {
                module.setMode( module.getMotionMode() );
                module.getMan().setAcceleration( value * MovingManModel.TIMER_SCALE * MovingManModel.TIMER_SCALE );
                module.setPaused( false );
                module.getMovingManControlPanel().enablePause();
            }
        } );
    }

    public void setNumSmoothingPoints( int n ) {
        double xshiftVelocity = n * MovingManModel.TIMER_SCALE / 2;
        double xshiftAcceleration = ( n + n ) * MovingManModel.TIMER_SCALE / 2;
        velocityPlot.getPlot().setShift( xshiftVelocity );
        accelerationPlot.getPlot().setShift( xshiftAcceleration );
    }

    public void setPositionPlotMagnitude( double positionMagnitude ) {
        Rectangle2D.Double positionInputBox = new Rectangle2D.Double( movingManModel.getMinTime(), -positionMagnitude, movingManModel.getMaxTime() - movingManModel.getMinTime(), positionMagnitude * 2 );
        positionPlot.getPlot().setInputRange( positionInputBox );
        movingManModule.repaintBackground();
    }

    public void setVelocityPlotMagnitude( double maxVelocity ) {
        Rectangle2D.Double velInputBox = new Rectangle2D.Double( movingManModel.getMinTime(), -maxVelocity, movingManModel.getMaxTime() - movingManModel.getMinTime(), maxVelocity * 2 );
        velocityPlot.getPlot().setInputRange( velInputBox );
        movingManModule.repaintBackground();
    }

    public void setAccelerationPlotMagnitude( double maxAccel ) {
        Rectangle2D.Double accelInputBox = new Rectangle2D.Double( movingManModel.getMinTime(), -maxAccel, movingManModel.getMaxTime() - movingManModel.getMinTime(), maxAccel * 2 );
        accelerationPlot.getPlot().setInputRange( accelInputBox );
        movingManModule.repaintBackground();
    }

    public int getVisiblePlotCount() {
        int sum = 0;
        if( positionPlot.isVisible() ) {
            sum++;
        }
        if( velocityPlot.isVisible() ) {
            sum++;
        }
        if( accelerationPlot.isVisible() ) {
            sum++;
        }
        return sum;
    }

    public PlotAndText getAccelerationPlot() {
        return accelerationPlot;
    }

    public PlotAndText getPositionPlot() {
        return positionPlot;
    }

    public PlotAndText getVelocityPlot() {
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
