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
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;

public class DynamicChartTest {

    static ArrayList toPaint = new ArrayList();
    private static double x = -5;

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
        chart.setLocation( 50, 50 );
        apparatusPanel.addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                Rectangle renderSize = new Rectangle( 0, 0, 600, 600 );
                chart.setViewBounds( renderSize );
                Color leftColor = new Color( 255, 255, 255 );
                Color rightColor = new Color( 140, 160, 255 );
                chart.setBackground( new GradientPaint( 0, 0, leftColor, e.getComponent().getWidth(), e.getComponent().getHeight(), rightColor, false ) );
            }
        } );

        final DataSet dataSet1 = new DataSet();
        DataSetGraphic sinGraphic = new LinePlot( apparatusPanel, chart, dataSet1, new BasicStroke( 3 ), Color.blue );
        chart.addDataSetGraphic( sinGraphic );

        final DataSet dataSet2 = new DataSet();

//        ScatterPlot.CircleFactory scatterPaintFactory = new ScatterPlot.CircleFactory( apparatusPanel, Color.red, 3 );
        ScatterPlot.ScatterPaintFactory scatterPaintFactory = new ScatterPlot.ScatterPaintFactory() {
            public PhetGraphic createScatterPoint( double x, double y ) {
                PhetImageGraphic pig = new PhetImageGraphic( apparatusPanel, "images/icons/java/media/Play24.gif" );
                pig.centerRegistrationPoint();
                pig.setLocation( (int)x, (int)y );
                pig.setRenderingHints( new RenderingHints( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC ) );
                pig.rotate( Math.random() * 2 * Math.PI );
                return pig;
            }
        };
        DataSetGraphic sinGraphic3 = new ScatterPlot( apparatusPanel, chart, dataSet2, scatterPaintFactory );
//        DataSetGraphic sinGraphic3 = new LinePlot( apparatusPanel, chart, dataSet2,
//                                                   new BasicStroke( 3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f, new float[]{8, 8}, 0 ),
//                                                   Color.red );
        chart.addDataSetGraphic( sinGraphic3 );

        frame.setVisible( true );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        Timer timer = new Timer( 30, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                x = step( x, dataSet1, dataSet2, chart, apparatusPanel );
            }
        } );
        timer.start();
    }

    private static double step( double x, final DataSet dataSet1, final DataSet dataSet2, final Chart chart, final ApparatusPanel2 apparatusPanel ) {
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
            dataSet2.addPoint( x, Math.abs( y ) );
            while( dataSet2.size() > 100 ) {
                dataSet2.removePoint( 0 );
            }
        }
        Range2D dataBounds = chart.getDataRange();//TODO should this be renamed to unconfuse chart.getRange()?
        if( dataBounds.getWidth() > 0 && dataBounds.getHeight() > 0 ) {
            chart.setRange( dataBounds.getScaledRange( 1.2, 1.2 ) );
        }
//                    apparatusPanel.paintImmediately( new Rectangle( apparatusPanel.getWidth(), apparatusPanel.getHeight() ) );
        apparatusPanel.paint();
        x += .15;
        return x;
    }

}
