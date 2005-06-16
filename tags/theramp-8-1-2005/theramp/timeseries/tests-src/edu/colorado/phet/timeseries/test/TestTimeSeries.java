/* Copyright 2004, Sam Reid */
package edu.colorado.phet.timeseries.test;

import edu.colorado.phet.chart.Range2D;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.ClockTickEvent;
import edu.colorado.phet.common.model.clock.ClockTickListener;
import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.BasicGraphicsSetup;
import edu.colorado.phet.common.view.phetcomponents.PhetJComponent;
import edu.colorado.phet.common.view.util.SwingUtils;
import edu.colorado.phet.timeseries.TimeSeries;
import edu.colorado.phet.timeseries.TimeSeriesModel;
import edu.colorado.phet.timeseries.TimeSeriesModelListenerAdapter;
import edu.colorado.phet.timeseries.TimeSeriesPlaybackPanel;
import edu.colorado.phet.timeseries.plot.PlotDeviceSeries;
import edu.colorado.phet.timeseries.plot.TimePlot;
import edu.colorado.phet.timeseries.plot.TimePlotSuite;

import javax.swing.*;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: May 12, 2005
 * Time: 12:17:53 PM
 * Copyright (c) May 12, 2005 by Sam Reid
 */

public class TestTimeSeries {
    public JFrame frame;
    public TestModel testModel;
    public AbstractClock clock;
    public ApparatusPanel2 apparatusPanel;
    public TimePlot timePlot;
    public TimeSeries timeSeries;
    public TimePlotSuite timePlotSuite;
    public TimeSeriesPlaybackPanel timeSeriesPlaybackPanel;

    public TestTimeSeries() {
        initModule();
        initTimePlot();

        clock.addClockTickListener( new ClockTickListener() {
            public void clockTicked( ClockTickEvent event ) {
                frame.getContentPane().repaint();
                apparatusPanel.handleUserInput();
                testModel.clockTicked( event );
                apparatusPanel.paint();
            }
        } );
    }

    private void initTimePlot() {
        timePlot = new TimePlot( apparatusPanel, testModel, new Range2D( 0, -10, 10, 10 ), "Test",
                                 "x", null, Color.blue );

        timeSeries = testModel.getPositionSeries();
        timePlot.addPlotDeviceData( new PlotDeviceSeries( timePlot, timeSeries, Color.blue, "Hello", new BasicStroke( 2 ), new Font( "Lucida Sans", Font.BOLD, 14 ), "units", "justifyText" ) );

        timePlotSuite = new TimePlotSuite( testModel, apparatusPanel, timePlot );
        apparatusPanel.addGraphic( timePlotSuite );
        apparatusPanel.setUseOffscreenBuffer( true );

//        timePlotSuite.setPlotVerticalParameters( 150, 200 );
        timePlotSuite.setSize( 400, 300 );

        testModel.addListener( new TimeSeriesModelListenerAdapter() {
            public void reset() {
                timePlotSuite.reset();
            }
        } );
        testModel.addListener( new TimeSeriesModelListenerAdapter() {
            public void recordingPaused() {
                timePlot.setCursorVisible( true );
                timePlot.getCursor().setX( 0 );
            }

            public void recordingFinished() {
                timePlot.setCursorVisible( true );
                timePlot.getCursor().setX( 0 );
            }

            public void recordingStarted() {
                timePlot.setCursorVisible( false );
            }
        } );
        timePlot.setCursorVisible( true );
    }

    private void initModule() {
        frame = new JFrame();
        PhetJComponent.init( frame );
        testModel = new TestModel();
        timeSeriesPlaybackPanel = new TimeSeriesPlaybackPanel( testModel );

        clock = new SwingTimerClock( 1, 30 );
        apparatusPanel = new ApparatusPanel2( clock );
        apparatusPanel.addGraphicsSetup( new BasicGraphicsSetup() );

        JPanel contentPane = new JPanel( new BorderLayout() );
        contentPane.add( apparatusPanel, BorderLayout.CENTER );
        contentPane.add( timeSeriesPlaybackPanel, BorderLayout.SOUTH );

        frame.setContentPane( contentPane );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setSize( 600, 600 );

        SwingUtils.centerWindowOnScreen( frame );
    }

    public static class TestModel extends TimeSeriesModel {
        private TimeSeries positionSeries = new TimeSeries();
        private double time = 0;

        public TestModel() {
            addListener( new TimeSeriesModelListenerAdapter() {
                public void reset() {
                    time = 0;
                    positionSeries.reset();
                }
            } );
        }

        public void repaintBackground() {
        }

        public void setCursorsVisible( boolean b ) {
        }

        public void updateModel( ClockTickEvent clEvent ) {
            positionSeries.addPoint( 10 * Math.sin( time ), time );
            time += clEvent.getDt() / 100.0;
            System.out.println( "time = " + time );
        }

        public TimeSeries getPositionSeries() {
            return positionSeries;
        }
    }

    public static void main( String[] args ) {
        new TestTimeSeries().start();
    }

    private void start() {
        frame.setVisible( true );
        clock.start();
        timePlotSuite.setPlotVerticalParameters( 150, 200 );
    }
}
