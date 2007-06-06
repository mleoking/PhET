package edu.colorado.phet.common.jfreechartphet.piccolo.dynamic;

import org.jfree.chart.plot.XYPlot;

import java.awt.image.BufferedImage;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D;
import java.awt.geom.GeneralPath;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

import edu.umd.cs.piccolo.util.PBounds;

/**
 * Author: Sam Reid
* Jun 5, 2007, 6:04:09 PM
*/
public class BufferedSeriesView extends SeriesView {
    private BufferedImage lastFullPaint = null;
    private boolean origStateBuffered;
    private BasicStroke stroke = new BasicStroke( 4.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 1.0f );

    public BufferedSeriesView( DynamicJFreeChartNode dynamicJFreeChartNode, SeriesData seriesData ) {
        super( dynamicJFreeChartNode, seriesData );
        dynamicJFreeChartNode.addBufferedImagePropertyChangeListener( new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                paintAll();
            }
        } );
    }

    public void dataAdded() {
        if( getSeries().getItemCount() >= 2 ) {
            BufferedImage image = getDynamicJFreeChartNode().getBuffer();
            if( image != null ) {
                if( image != lastFullPaint ) {
                    paintAll();
                    lastFullPaint = image;
                }
                Graphics2D graphics2D = image.createGraphics();
                graphics2D.setPaint( getSeriesData().getColor() );

                graphics2D.setStroke( stroke );
                int itemCount = getSeries().getItemCount();
                Line2D.Double viewLine = new Line2D.Double( getNodePoint( itemCount - 2 ), getNodePoint( itemCount - 1 ) );
                setupRenderingHints( graphics2D );
//                    graphics2D.clip( getChartViewBounds() );
//                    System.out.println( "getChartViewBounds() = " + getChartViewBounds() );
                graphics2D.clip( getChartViewBounds() );
                graphics2D.draw( viewLine );

                Shape sh = stroke.createStrokedShape( viewLine );
                Rectangle2D bounds = sh.getBounds2D();
                if( getDynamicJFreeChartNode().isBuffered() ) {
                    bounds = new Rectangle2D.Double( bounds.getX() + getDynamicJFreeChartNode().getBounds().getX(), bounds.getY() + getDynamicJFreeChartNode().getBounds().getY(), bounds.getWidth(), bounds.getHeight() );
                }
                getDynamicJFreeChartNode().localToGlobal( bounds );
                getDynamicJFreeChartNode().getPhetPCanvas().getPhetRootNode().globalToScreen( bounds );
//                    System.out.println( "bounds = " + bounds );
                repaintPanel( bounds );
            }
        }
    }

    private void setupRenderingHints( Graphics2D graphics2D ) {
        graphics2D.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
    }

    //todo: is there a simpler way to do this?
    public Rectangle2D getChartViewBounds() {
        XYPlot xyPlot = getDynamicJFreeChartNode().getChart().getXYPlot();
        Rectangle2D rect = new Rectangle2D.Double( xyPlot.getDomainAxis().getLowerBound(),
                                                   xyPlot.getRangeAxis().getLowerBound(),
                                                   xyPlot.getDomainAxis().getUpperBound() - xyPlot.getDomainAxis().getLowerBound(),
                                                   xyPlot.getRangeAxis().getUpperBound() - xyPlot.getRangeAxis().getLowerBound() );
        return getDynamicJFreeChartNode().plotToNode( rect );
    }

    public void install() {
        super.install();
        paintAll();
        this.origStateBuffered = getDynamicJFreeChartNode().isBuffered();
        if( !origStateBuffered ) {
            getDynamicJFreeChartNode().setBuffered( true );
        }
    }

    private void paintAll() {
        BufferedImage image = getDynamicJFreeChartNode().getBuffer();
        if( image != null ) {
            Graphics2D graphics2D = image.createGraphics();
            graphics2D.setPaint( getSeriesData().getColor() );
            graphics2D.setStroke( stroke );
            GeneralPath path = toGeneralPath();
            setupRenderingHints( graphics2D );
            graphics2D.clip( getChartViewBounds() );
            graphics2D.draw( path );
            repaintPanel( new Rectangle2D.Double( 0, 0, getDynamicJFreeChartNode().getPhetPCanvas().getWidth(), getDynamicJFreeChartNode().getPhetPCanvas().getHeight() ) );
        }
    }

    protected void repaintPanel( Rectangle2D bounds ) {
        getDynamicJFreeChartNode().getPhetPCanvas().repaint( new PBounds( bounds ) );
    }

    private GeneralPath toGeneralPath() {
        GeneralPath path = new GeneralPath();
        if( getSeries().getItemCount() > 0 ) {
            path.moveTo( (float)getNodePoint( 0 ).getX(), (float)getNodePoint( 0 ).getY() );
        }
        for( int i = 1; i < getSeries().getItemCount(); i++ ) {
            path.lineTo( (float)getNodePoint( i ).getX(), (float)getNodePoint( i ).getY() );
        }
        return path;
    }
}
