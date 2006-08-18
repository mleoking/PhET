/**
 * Class: ChartTest
 * Package: edu.colorado.phet
 * Author: Another Guy
 * Date: Sep 15, 2004
 */
package edu.colorado.phet.chart.test;

import edu.colorado.phet.chart.BufferedChart;
import edu.colorado.phet.chart.BufferedLinePlot;
import edu.colorado.phet.chart.Chart;
import edu.colorado.phet.chart.Range2D;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.ClockTickEvent;
import edu.colorado.phet.common.model.clock.ClockTickListener;
import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.BasicGraphicsSetup;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.Random;

public class BufferedChartTest {

    public static void main( String[] args ) {
        AbstractClock clock = new SwingTimerClock( 1, 30 );
//        final ApparatusPanel2 apparatusPanel = new ApparatusPanel2( clock );
        final ApparatusPanel apparatusPanel = new ApparatusPanel();
        clock.start();

        BasicGraphicsSetup gs = new BasicGraphicsSetup();
        apparatusPanel.addGraphicsSetup( gs );

        Range2D range = new Range2D( -10, -10, 10, 10 );
        Dimension chartSize = new Dimension( 680, 680 );
        final Chart chart = new Chart( apparatusPanel, range, chartSize );

        chart.getHorizonalGridlines().setMinorGridlinesVisible( false );
        chart.getHorizonalGridlines().setMajorGridlinesVisible( true );

        chart.getVerticalGridlines().setMinorGridlinesVisible( false );
        chart.getVerticalGridlines().setMajorGridlinesVisible( true );
        chart.getVerticalGridlines().setMajorTickSpacing( 3 );
        chart.getVerticalGridlines().setMajorGridlinesColor( Color.gray );
        chart.getHorizonalGridlines().setMajorGridlinesColor( Color.gray );

        chart.getXAxis().setMajorTickFont( new Font( "Lucida Sans", Font.BOLD, 14 ) );
        chart.getXAxis().setMajorTicksVisible( true );
        chart.getXAxis().setMinorTicksVisible( true );
        chart.getXAxis().setMinorTickSpacing( .5 );
        chart.getXAxis().setMinorTickStroke( new BasicStroke( .5f ) );

        final JFrame frame = new JFrame( "ChartTest" );
        frame.setContentPane( apparatusPanel );
        frame.setSize( 800, 800 );

        final BufferedChart bufferedChart = new BufferedChart( apparatusPanel, chart );
        final BufferedLinePlot bufferedLinePlot = new BufferedLinePlot( bufferedChart, new BasicStroke( 1 ), Color.red );
        bufferedLinePlot.lineTo( new Point2D.Double( 0, 0 ) );
        bufferedLinePlot.lineTo( new Point2D.Double( 10, 6 ) );
        bufferedLinePlot.lineTo( new Point2D.Double( 0, 8 ) );
        bufferedLinePlot.lineTo( new Point2D.Double( -10, -4 ) );
        bufferedLinePlot.lineTo( new Point2D.Double( 0, -6 ) );

        apparatusPanel.addGraphic( bufferedChart );
//        bufferedChart.scale( 0.5, 0.5 );
        bufferedChart.setLocation( 400, 400 );
        bufferedChart.centerRegistrationPoint();
        frame.setVisible( true );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        final Random random = new Random();
        clock.addClockTickListener( new ClockTickListener() {
            public void clockTicked( ClockTickEvent event ) {
                bufferedChart.rotate( Math.PI / 64 );
                bufferedLinePlot.lineTo( new Point2D.Double( random.nextInt( 20 ) - 10, random.nextInt( 20 ) - 10 ) );
            }
        } );
    }

}
