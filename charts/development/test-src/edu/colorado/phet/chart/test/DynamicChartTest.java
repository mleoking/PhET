/**
 * Class: ChartTest
 * Package: edu.colorado.phet
 * Author: Another Guy
 * Date: Sep 15, 2004
 */
package edu.colorado.phet.chart.test;

import edu.colorado.phet.chart.*;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.BasicGraphicsSetup;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;

public class DynamicChartTest {

    static ArrayList toPaint = new ArrayList();

    public static void main( String[] args ) {
        final ApparatusPanel apparatusPanel = new ApparatusPanel() {
            public void repaint( long tm, int x, int y, int width, int height ) {
//                super.repaint( tm, x, y, width, height );
                Rectangle repaint = new Rectangle( x, y, width, height );
                toPaint.add( repaint );
            }
        };
        BasicGraphicsSetup gs = new BasicGraphicsSetup();

        apparatusPanel.addGraphicsSetup( gs );
        Range2D range = new Range2D( -10, -10, 10, 10 );
        Rectangle viewBounds = new Rectangle( 10, 10, 100, 100 );
        final Chart chart = new Chart( apparatusPanel, range, viewBounds );
        chart.getHorizonalGridlines().setMinorGridlinesVisible( false );
        chart.getVerticalGridlines().setMinorGridlinesVisible( false );
        chart.getHorizonalGridlines().setMajorGridlinesVisible( true );
        chart.getVerticalGridlines().setMajorGridlinesVisible( true );
        chart.getVerticalGridlines().setMajorTickSpacing( 3 );
        chart.getVerticalGridlines().setMajorGridlinesColor( Color.gray );
        chart.getHorizonalGridlines().setMajorGridlinesColor( Color.gray );

        //TODO change:
        //chart.getXAxis().setMajorTickFont( new Font( "Lucida Sans", Font.BOLD, 14 ) );
        chart.getXAxis().getMajorGrid().setFont( new Font( "Lucida Sans", Font.BOLD, 14 ) );
//        chart.getVerticalGridlines().setMajorGridlinesStroke( new BasicStroke( 1, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND, 1.0f, new float[]{5, 5}, 0 ) );
//        chart.getHorizonalGridlines().setMajorGridlinesStroke( new BasicStroke( 1, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND, 1.0f, new float[]{5, 5}, 0 ) );

        JFrame frame = new JFrame( "ChartTest" );
        frame.setContentPane( apparatusPanel );
        frame.setSize( 800, 800 );
        apparatusPanel.addGraphic( chart );

//        DataSet ds = new DataSet();
//        DataSetGraphic dsg = new ScatterPlot( ds, new ScatterPlot.CirclePaint( Color.green, 3, false ) );
//        chart.addDataSetGraphic( dsg );

        final int insets = 20;
        apparatusPanel.addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                Rectangle viewBounds = new Rectangle( insets, insets, e.getComponent().getWidth() - insets * 2, e.getComponent().getHeight() - insets * 2 );
                chart.setViewBounds( viewBounds );

                Color leftColor = new Color( 255, 255, 255 );
                Color rightColor = new Color( 140, 160, 255 );
                chart.setBackground( new GradientPaint( 0, 0, leftColor, e.getComponent().getWidth(), e.getComponent().getHeight(), rightColor, false ) );
            }
        } );

        final DataSet sin2 = new DataSet();
        DataSetGraphic sinGraphic = new LinePlot( sin2,
//                                                  new BasicStroke( 3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f, new float[]{8, 8}, 0 )
                                                  new BasicStroke( 3 )
                                                  , Color.blue );
        chart.addDataSetGraphic( sinGraphic );
        double dx2 = .01;
        for( double x = 0; x < 5; x += dx2 ) {
            double y = 10 * Math.sin( x / 2 );
            sin2.addPoint( x, y );
        }

        final DataSet sin3 = new DataSet();
        DataSetGraphic sinGraphic3 = new LinePlot( sin3, new BasicStroke( 3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f, new float[]{8, 8}, 0 ), Color.red );
        chart.addDataSetGraphic( sinGraphic3 );
        double dx3 = .01;
        for( double x = 0; x < 5; x += dx3 ) {
            double y = 10 * Math.sin( x / 2 );
            sin3.addPoint( x, y );
        }

        frame.setVisible( true );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        Thread dataThread = new Thread( new Runnable() {
            public void run() {
                double x = 5;
                while( true ) {
                    try {
                        Thread.sleep( 20 );
                    }
                    catch( InterruptedException e ) {
                        e.printStackTrace();
                    }
                    {
                        double y = 10 * Math.sin( x / 2 );
                        sin2.addPoint( x, y );
                        while( sin2.size() > 100 ) {
                            sin2.removePoint( 0 );
                        }
                    }
                    {
                        double y = 10 * Math.cos( x / 3 );
                        sin3.addPoint( x, Math.abs( y ) );
                        while( sin3.size() > 100 ) {
                            sin3.removePoint( 0 );
                        }
                    }
                    Range2D dataBounds = chart.getDataRange();
                    //the golden ratio = 1.61803399
                    chart.setRange( dataBounds.getScaledRange( 1.2, 1.2 ) );
                    apparatusPanel.paintImmediately( union() );
//                    apparatusPanel.repaint( union() );
                    x += .15;
                }
            }
        } );
        dataThread.start();
    }

    private static Rectangle union() {
        if( toPaint.size() == 0 ) {
            return new Rectangle();
        }
        else {
            Rectangle union = (Rectangle)toPaint.get( 0 );
            for( int i = 1; i < toPaint.size(); i++ ) {
                Rectangle rectangle = (Rectangle)toPaint.get( i );
                union = union.union( rectangle );
            }
            return union;
        }
    }
}
