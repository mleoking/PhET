/**
 * Class: ChartTest
 * Package: edu.colorado.phet
 * Author: Another Guy
 * Date: Sep 15, 2004
 */
package edu.colorado.phet.chart.test;

import edu.colorado.phet.chart.*;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.util.BasicGraphicsSetup;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class ChartTest {

    public static void main( String[] args ) {
        final ApparatusPanel apparatusPanel = new ApparatusPanel();
        BasicGraphicsSetup gs = new BasicGraphicsSetup();

        apparatusPanel.addGraphicsSetup( gs );
        Range2D range = new Range2D( -10, -10, 10, 10 );
        Rectangle viewBounds = new Rectangle( 10, 10, 100, 100 );
        final Chart chart = new Chart( apparatusPanel, range, viewBounds );
        chart.getHorizonalGridlines().setVisible( false );
        chart.getVerticalGridlines().setVisible( false );

        chart.getHorizonalGridlines().setMajorGridlinesVisible( true );
        chart.getVerticalGridlines().setMajorGridlinesVisible( true );
        chart.getVerticalGridlines().setMajorTickSpacing( Math.pow( 10, 12 ) );
        chart.getHorizonalGridlines().setMajorTickSpacing( 1 );

        chart.getVerticalGridlines().setMajorGridlinesColor( Color.blue );
        chart.getHorizonalGridlines().setMajorGridlinesColor( Color.green );
        chart.getVerticalGridlines().setMajorGridlinesStroke( new BasicStroke( 1, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND, 1.0f, new float[]{5, 5}, 0 ) );
        chart.getHorizonalGridlines().setMajorGridlinesStroke( new BasicStroke( 1, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND, 1.0f, new float[]{5, 5}, 0 ) );

        JFrame frame = new JFrame( "ChartTest" );
        frame.setContentPane( apparatusPanel );
        frame.setSize( 800, 800 );
        apparatusPanel.addGraphic( chart );

        DataSet ds = new DataSet();
        DataSetGraphic dsg = new ScatterPlot( apparatusPanel, chart, ds, new ScatterPlot.CircleFactory( apparatusPanel, Color.green, 3 ) );
        chart.addDataSetGraphic( dsg );

        ComponentAdapter componentAdapter = new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                resized( apparatusPanel, chart );
            }
        };

        apparatusPanel.addComponentListener( componentAdapter );
        double dx = .01;
        for( double x = 0; x < 10; x += dx ) {
            double y = 10 * Math.sin( x );
            ds.addPoint( x, y );
        }

        DataSet sin2 = new DataSet();
        DataSetGraphic sinGraphic = new LinePlot( apparatusPanel, chart, sin2, new BasicStroke( 1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f, new float[]{4, 5}, 0 ), Color.blue );
        chart.addDataSetGraphic( sinGraphic );
        double dx2 = .01;
        for( double x = 0; x < 10; x += dx2 ) {
            double y = 10 * Math.sin( x / 2 );
            sin2.addPoint( x, y );
        }

        frame.setVisible( true );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        resized( apparatusPanel, chart );
    }

    private static void resized( ApparatusPanel e, Chart chart ) {

        Rectangle viewBounds = new Rectangle( 30, 30, e.getWidth() - 60, e.getHeight() - 60 );
        chart.setViewBounds( viewBounds );

        Color leftColor = new Color( 255, 255, 255 );
        Color rightColor = new Color( 140, 160, 255 );
        chart.setBackground( new GradientPaint( 0, 0, leftColor, e.getWidth(), e.getHeight(), rightColor, false ) );
    }
    /**
     * Original client side code
     *
     Chart chart = new Chart();
     DataSet ds = new DataSet();
     DataSetGraphic dsg = new LinePlot( ds, new BasicStroke( 1.0f ), Color.blue );
     chart.addDataSetGraphic( dsg );

     Point2D dataPoint = new Point2D.Double();
     ds.addPoint( dataPoint );
     ds.addPoint( 3.0, 4.0 );
     */

    /**Axis backup
     *     public static class Axis implements Graphic {
     Chart chart;
     Stroke majorStroke;
     Stroke minorStroke;
     Color color;
     double majorTickSpacing = 2.0;
     double minorTickSpacing = 1.0;
     int orientation;
     double crossesOtherAxisAt;
     int majorTickHeight = 6;
     int minorTickHeight = 6;

     public Axis( Chart chart, int orientation ) {
     this( chart, orientation, new BasicStroke( 2 ), Color.black );
     }

     public Axis( Chart chart, int orientation, Stroke stroke, Color color ) {
     this.chart = chart;
     this.orientation = orientation;
     this.majorStroke = stroke;
     this.color = color;
     this.crossesOtherAxisAt = 0.0;
     this.minorStroke = new BasicStroke( 0.5f );
     }

     public void paint( Graphics2D g ) {
     Stroke origStroke = g.getStroke();
     Color origColor = g.getColor();
     g.setStroke( majorStroke );
     g.setColor( color );
     switch( orientation ) {
     case HORIZONTAL:
     {
     Point2D.Double leftEndOfAxis = new Point2D.Double( chart.getRange().getMinX(), crossesOtherAxisAt );
     Point left = chart.transform( leftEndOfAxis );
     Point2D.Double rightEndOfAxis = new Point2D.Double( chart.getRange().getMaxX(), crossesOtherAxisAt );
     Point right = chart.transform( rightEndOfAxis );
     g.drawLine( left.x, left.y, right.x, right.y );
     g.setStroke( majorStroke );
     double[] majorGridLines = getGridLines( crossesOtherAxisAt, chart.getRange().getMinX(), chart.getRange().getMaxX(), majorTickSpacing );
     for( int i = 0; i < majorGridLines.length; i++ ) {
     double gridLineX = majorGridLines[i];
     int x = chart.transformX( gridLineX );
     int y = left.y;
     g.drawLine( x, y - majorTickHeight / 2, x, y + majorTickHeight / 2 );
     }
     g.setStroke( minorStroke );
     double[] minorGridLines = getGridLines( crossesOtherAxisAt, chart.getRange().getMinX(), chart.getRange().getMaxX(), minorTickSpacing );
     for( int i = 0; i < minorGridLines.length; i++ ) {
     double gridLineX = minorGridLines[i];
     int x = chart.transformX( gridLineX );
     int y = left.y;
     g.drawLine( x, y - minorTickHeight / 2, x, y + minorTickHeight / 2 );
     }
     break;
     }
     case VERTICAL:
     {
     Point2D.Double bottomEndOfAxis = new Point2D.Double( crossesOtherAxisAt, chart.getRange().getMinY() );
     Point bottom = chart.transform( bottomEndOfAxis );
     Point2D.Double topEndOfAxis = new Point2D.Double( crossesOtherAxisAt, chart.getRange().getMaxX() );
     Point top = chart.transform( topEndOfAxis );
     g.drawLine( bottom.x, bottom.y, top.x, top.y );
     double[] gridLines = getGridLines( crossesOtherAxisAt, chart.getRange().getMinY(), chart.getRange().getMaxY(), majorTickSpacing );
     for( int i = 0; i < gridLines.length; i++ ) {
     double gridLineY = gridLines[i];
     int x = bottom.x;
     int y = chart.transformY( gridLineY );
     g.drawLine( x - majorTickHeight / 2, y, x + majorTickHeight / 2, y );
     }
     break;
     }
     default:
     throw new RuntimeException( "invalid orientation" );
     }
     g.setStroke( origStroke );
     g.setColor( origColor );
     }
     }
     */
}
