/**
 * Class: ChartTest
 * Package: edu.colorado.phet
 * Author: Another Guy
 * Date: Sep 15, 2004
 */
package edu.colorado.phet.chart.test;

import edu.colorado.phet.chart.*;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.BasicGraphicsSetup;
import edu.colorado.phet.common.view.phetgraphics.BufferedPhetGraphic2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;

public class BufferedChartTest {

    static ArrayList toPaint = new ArrayList();
    private static double x = -5;
    private static BufferedPhetGraphic2 bufferedPhetGraphic2;

    public static void main( String[] args ) {
        BaseModel model = new BaseModel();
        AbstractClock clock = new SwingTimerClock( 1, 30 );
        final ApparatusPanel2 apparatusPanel = new ApparatusPanel2( model, clock ) {
            public void repaint( long tm, int x, int y, int width, int height ) {
                Rectangle repaint = new Rectangle( x, y, width, height );
                toPaint.add( repaint );
            }
        };
        clock.start();
        BasicGraphicsSetup gs = new BasicGraphicsSetup();

        apparatusPanel.addGraphicsSetup( gs );
        Range2D range = new Range2D( -10, -10, 10, 10 );
        Rectangle viewBounds = new Rectangle( 10, 10, 100, 100 );
        final Chart chart = new Chart( apparatusPanel, range, viewBounds );

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

        final int insets = 20;
        chart.setLocation( 50, 50 );
        apparatusPanel.addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                Rectangle renderSize = new Rectangle( 0, 0, 600, 600 );
                chart.setViewBounds( renderSize );
                Color leftColor = new Color( 255, 255, 255 );
                Color rightColor = new Color( 140, 160, 255 );
                chart.setBackground( rightColor );
            }
        } );

        final DataSet dataSet1 = new DataSet();
        DataSetGraphic sinGraphic = new LinePlot( apparatusPanel, chart, dataSet1, new BasicStroke( 3 ), Color.blue );
        chart.addDataSetGraphic( sinGraphic );

        final DataSet dataSet2 = new DataSet();

        ScatterPlot.CircleFactory scatterPaintFactory = new ScatterPlot.CircleFactory( apparatusPanel, Color.red, 3 );
        DataSetGraphic sinGraphic3 = new ScatterPlot( apparatusPanel, chart, dataSet2, scatterPaintFactory );
        chart.addDataSetGraphic( sinGraphic3 );

        bufferedPhetGraphic2 = new BufferedPhetGraphic2( apparatusPanel, Color.yellow );
        bufferedPhetGraphic2.addGraphic( chart );

        fixBuffer( chart );
        apparatusPanel.addGraphic( bufferedPhetGraphic2 );

        apparatusPanel.addComponentListener( new ComponentAdapter() {
            public void componentShown( ComponentEvent e ) {
                fixBuffer( chart );
            }

            public void componentResized( ComponentEvent e ) {
                fixBuffer( chart );
            }
        } );
        frame.setVisible( true );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    }

    private static void fixBuffer( final Chart chart ) {
        Rectangle r = chart.getBounds();
        chart.translate( -r.x, -r.y );
        bufferedPhetGraphic2.setSize( chart.getWidth(), chart.getHeight() );
        bufferedPhetGraphic2.repaintBuffer();
    }
}
