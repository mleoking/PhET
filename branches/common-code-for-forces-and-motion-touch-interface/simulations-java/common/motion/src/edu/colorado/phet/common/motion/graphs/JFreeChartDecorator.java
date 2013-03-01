// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.motion.graphs;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.util.ArrayList;

import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;

import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * Created by: Sam
 * Dec 5, 2007 at 4:58:41 PM
 */
public class JFreeChartDecorator extends JFreeChart {
    private ArrayList<JFreeChartDecoration> decorations = new ArrayList<JFreeChartDecoration>();

    public JFreeChartDecorator( String title, Font defaultTitleFont, XYPlot plot, boolean legend ) {
        super( title, defaultTitleFont, plot, legend );
    }

    public void draw( Graphics2D g2, Rectangle2D chartArea, Point2D anchor, ChartRenderingInfo info ) {
        super.draw( g2, chartArea, anchor, info );
        drawDecoration( g2, chartArea, anchor, info );
    }

    private void drawDecoration( Graphics2D g2, Rectangle2D area, Point2D anchor, ChartRenderingInfo info ) {
        for ( JFreeChartDecoration decoration : decorations ) {
            decoration.paint( this, g2, area, anchor, info );
        }
    }

    public void addJFreeChartNodeGraphic( JFreeChartDecoration decoration ) {
        decorations.add( decoration );
    }

    public static interface JFreeChartDecoration {
        void paint( JFreeChart chart, Graphics2D g2, Rectangle2D area, Point2D anchor, ChartRenderingInfo info );
    }

    public static class InChartTickMarks implements JFreeChartDecoration {
        private double min;
        private double tickIncrement;
        private int numTicks;
        private DecimalFormat format = new DecimalFormat( "0" );

        public InChartTickMarks( double min, double tickIncrement, int numTicks ) {
            this.min = min;
            this.tickIncrement = tickIncrement;
            this.numTicks = numTicks;
        }

        public void paint( JFreeChart chart, Graphics2D g2, Rectangle2D area, Point2D anchor, ChartRenderingInfo info ) {
            Rectangle2D da = info.getPlotInfo().getDataArea();
            for ( int i = 0; i < numTicks; i++ ) {
                double tickValue = min + i * tickIncrement;
                double x = chart.getXYPlot().getDomainAxis().valueToJava2D( tickValue, da, chart.getXYPlot().getDomainAxisEdge() );
                double y = chart.getXYPlot().getRangeAxis().valueToJava2D( 0, da, chart.getXYPlot().getRangeAxisEdge() );
                double dy = 5;
                PhetPPath path = new PhetPPath( new Line2D.Double( x, y - dy, x, y + dy ), new BasicStroke( 2 ), Color.black );
                path.fullPaint( new PPaintContext( g2 ) );
                PText tickLabel = new PText( format.format( tickValue ) );
                tickLabel.setOffset( path.getFullBounds().getCenterX() - tickLabel.getFullBounds().getWidth() / 2,
                                     path.getFullBounds().getMaxY() );
                tickLabel.fullPaint( new PPaintContext( g2 ) );
            }
        }
    }


    public static class DottedZeroLine implements JFreeChartDecoration {
        public void paint( JFreeChart chart, Graphics2D g2, Rectangle2D area, Point2D anchor, ChartRenderingInfo info ) {
            Rectangle2D da = info.getPlotInfo().getDataArea();
            double x0 = chart.getXYPlot().getDomainAxis().valueToJava2D( chart.getXYPlot().getDomainAxis().getRange().getLowerBound(), da, chart.getXYPlot().getDomainAxisEdge() );
            double x1 = chart.getXYPlot().getDomainAxis().valueToJava2D( chart.getXYPlot().getDomainAxis().getRange().getUpperBound(), da, chart.getXYPlot().getDomainAxisEdge() );
            double y = chart.getXYPlot().getRangeAxis().valueToJava2D( 0, da, chart.getXYPlot().getRangeAxisEdge() );
            PhetPPath path = new PhetPPath( new Line2D.Double( x0, y, x1, y ), new BasicStroke( 1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1f, new float[] { 12, 4 }, 0 ), Color.black );
            path.fullPaint( new PPaintContext( g2 ) );
        }
    }
}
