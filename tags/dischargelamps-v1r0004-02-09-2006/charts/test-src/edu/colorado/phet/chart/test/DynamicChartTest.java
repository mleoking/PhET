/**
 * Class: ChartTest
 * Package: edu.colorado.phet
 * Author: Another Guy
 * Date: Sep 15, 2004
 */
package edu.colorado.phet.chart.test;

import edu.colorado.phet.chart.*;
import edu.colorado.phet.common.model.clock.ClockAdapter;
import edu.colorado.phet.common.model.clock.ClockEvent;
import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.common.model.clock.SwingClock;
import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.graphics.mousecontrols.translation.TranslationEvent;
import edu.colorado.phet.common.view.graphics.mousecontrols.translation.TranslationListener;
import edu.colorado.phet.common.view.util.BasicGraphicsSetup;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class DynamicChartTest {

    private static double x = -5;

    public static void main( String[] args ) {
        IClock clock = new SwingClock( 1, 30 );
        final ApparatusPanel2 apparatusPanel = new ApparatusPanel2( clock );
        BasicGraphicsSetup gs = new BasicGraphicsSetup();
        apparatusPanel.addGraphicsSetup( gs );

        final Chart chart = new Chart( apparatusPanel, new Range2D( -10, -10, 10, 10 ), new Dimension( 400, 400 ) );
        chart.setLocation( 200, 5 );
        apparatusPanel.addComponentListener( new DynamicChartTest.GradientComputation( chart ) );

        chart.addTranslationListener( new TranslationListener() {
            public void translationOccurred( TranslationEvent translationEvent ) {
                chart.translate( translationEvent.getDx(), translationEvent.getDy() );
            }
        } );
        chart.setCursorHand();

        chart.getHorizonalGridlines().setMinorGridlinesVisible( false );
        chart.getHorizonalGridlines().setMajorGridlinesVisible( true );
        chart.getHorizonalGridlines().setMajorGridlinesColor( Color.gray );

        chart.getVerticalGridlines().setMinorGridlinesVisible( false );
        chart.getVerticalGridlines().setMajorGridlinesVisible( true );
        chart.getVerticalGridlines().setMajorTickSpacing( 3 );
        chart.getVerticalGridlines().setMajorGridlinesColor( Color.gray );

        chart.getXAxis().setMajorTickFont( new Font( "Lucida Sans", Font.BOLD, 16 ) );
        chart.getXAxis().setMajorTicksVisible( true );
        chart.getXAxis().setMinorTicksVisible( false );
        chart.getXAxis().setMinorTickSpacing( .5 );
        chart.getXAxis().setMinorTickStroke( new BasicStroke( .5f ) );

        JFrame frame = new JFrame( "ChartTest" );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setContentPane( apparatusPanel );
        frame.setSize( 800, 800 );
        apparatusPanel.addGraphic( chart );

        final DataSet dataSet1 = new DataSet();
        DataSetGraphic sinGraphic = new LinePlot( apparatusPanel, chart, dataSet1, new BasicStroke( 3 ), Color.blue );
        chart.addDataSetGraphic( sinGraphic );

        final DataSet dataSet2 = new DataSet();
        ScatterPlot.CircleFactory scatterPaintFactory = new ScatterPlot.CircleFactory( apparatusPanel, Color.red, 3 );
        DataSetGraphic sinGraphic3 = new ScatterPlot( apparatusPanel, chart, dataSet2, scatterPaintFactory );
        chart.addDataSetGraphic( sinGraphic3 );

        clock.addClockListener( new ClockAdapter() {
            public void clockTicked( ClockEvent event ) {
                apparatusPanel.handleUserInput();
                x = step( x, dataSet1, dataSet2, chart, apparatusPanel );
                apparatusPanel.paint();
            }
        } );
        frame.setVisible( true );
        clock.start();
    }

    private static double step( double x, final DataSet dataSet1, final DataSet dataSet2, final Chart chart, final ApparatusPanel2 apparatusPanel ) {
        {
            double y = 10 * Math.sin( x / 2 );
            dataSet1.addPoint( x, y );
            while( dataSet1.size() > 100 ) {
                dataSet1.removePoint( 0 );
            }
        }
        {
            double y = 10 * Math.cos( x / 3 );
            dataSet2.addPoint( x, Math.abs( y ) );
            while( dataSet2.size() > 100 ) {
                dataSet2.removePoint( 0 );
            }
        }
        Range2D dataBounds = chart.getDataRange();//TODO should this be renamed to unconfuse chart.getRange()?
        if( dataBounds.getWidth() > 0 && dataBounds.getHeight() > 0 ) {
            chart.setRange( dataBounds.getScaledRange( 1.2, 1.2 ) );
        }
        x += .15;
        return x;
    }

    public static class GradientComputation extends ComponentAdapter {
        Chart chart;

        public GradientComputation( Chart chart ) {
            this.chart = chart;
        }

        public void componentResized( ComponentEvent e ) {
            Color leftColor = new Color( 255, 255, 255 );
            Color rightColor = new Color( 140, 160, 255 );
            GradientPaint background = new GradientPaint( 0, 0, leftColor, e.getComponent().getWidth(), e.getComponent().getHeight(), rightColor, false );
            chart.setBackground( background );
        }
    }

}


