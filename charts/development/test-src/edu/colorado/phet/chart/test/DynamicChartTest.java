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

        chart.getXAxis().setMajorTickFont( new Font( "Lucida Sans", Font.BOLD, 14 ) );
        chart.getXAxis().setMajorTicksVisible( true );
        chart.getXAxis().setMinorTicksVisible( true );
        chart.getXAxis().setMinorTickSpacing( .5 );
        chart.getXAxis().setMinorTickStroke( new BasicStroke( .5f ) );

        JFrame frame = new JFrame( "ChartTest" );
        frame.setContentPane( apparatusPanel );
        frame.setSize( 800, 800 );
        apparatusPanel.addGraphic( chart );

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

        final DataSet dataSet1 = new DataSet();
        DataSetGraphic sinGraphic = new LinePlot( dataSet1, new BasicStroke( 3 ), Color.blue );
        chart.addDataSetGraphic( sinGraphic );

        final DataSet dataSet2 = new DataSet();
        DataSetGraphic sinGraphic3 = new LinePlot( dataSet2,
                                                   new BasicStroke( 3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f, new float[]{8, 8}, 0 ),
                                                   Color.red );
        chart.addDataSetGraphic( sinGraphic3 );

        frame.setVisible( true );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        Thread dataThread = new Thread( new Runnable() {
            public void run() {
                double x = -5;
                while( true ) {
                    try {
                        Thread.sleep( 20 );
                    }
                    catch( InterruptedException e ) {
                        e.printStackTrace();
                    }
                    {
                        double y = 10 * Math.sin( x / 2 );
                        dataSet1.addPoint( x, y );
                        while( dataSet1.size() > 100 ) {
                            dataSet1.removePoint( 0 );
                        }
                    }
                    {
                        double y = 10 * Math.cos( x / 3 );
//                        if( dataSet2.size() == 0 ) {
//                            dataSet2.addPoint( x, Math.abs( 1.1 ) );
//                        }
//                        else {
//                        double y = dataSet2.getLastPoint().getY() * dataSet2.getLastPoint().getY();
                        dataSet2.addPoint( x, Math.abs( y ) );
                        while( dataSet2.size() > 100 ) {
                            dataSet2.removePoint( 0 );
//                            }
                        }
                    }
                    Range2D dataBounds = chart.getDataRange();
                    if( dataBounds.getWidth() > 0 && dataBounds.getHeight() > 0 ) {
                        chart.setRange( dataBounds.getScaledRange( 1.2, 1.2 ) );
                    }
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
