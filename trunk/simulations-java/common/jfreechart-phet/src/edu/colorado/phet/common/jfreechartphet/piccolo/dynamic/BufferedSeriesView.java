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
            BufferedImage image = dynamicJFreeChartNode.getBuffer();
            if( image != null ) {
                if( image != lastFullPaint ) {
                    paintAll();
                    lastFullPaint = image;
                }
                Graphics2D graphics2D = image.createGraphics();
                graphics2D.setPaint( getSeriesData().getColor() );

                graphics2D.setStroke( stroke );
                int itemCount = getSeries().getItemCount();
                Line2D.Double viewLine = new Line2D.Double( getViewPoint( itemCount - 2 ), getViewPoint( itemCount - 1 ) );
                setupRenderingHints( graphics2D );
//                    graphics2D.clip( getChartViewBounds() );
//                    System.out.println( "getChartViewBounds() = " + getChartViewBounds() );
                graphics2D.clip( getChartViewBounds() );
                graphics2D.draw( viewLine );

                Shape sh = stroke.createStrokedShape( viewLine );
                Rectangle2D bounds = sh.getBounds2D();
                if( dynamicJFreeChartNode.isBuffered() ) {
                    bounds = new Rectangle2D.Double( bounds.getX() + dynamicJFreeChartNode.getBounds().getX(), bounds.getY() + dynamicJFreeChartNode.getBounds().getY(), bounds.getWidth(), bounds.getHeight() );
                }
                dynamicJFreeChartNode.localToGlobal( bounds );
                dynamicJFreeChartNode.getPhetPCanvas().getPhetRootNode().globalToScreen( bounds );
//                    System.out.println( "bounds = " + bounds );
                repaintPanel( bounds );
            }
        }
    }

//        public void dataAddedDemonstrateFailureForInterleaved() {
//            if( getSeries().getItemCount() >= 100 ) {
//                BufferedImage image = dynamicJFreeChartNode.getBuffer();
//                if( image != null ) {
//                    if( image != lastFullPaint ) {
//                        paintAll();
//                        lastFullPaint = image;
//                    }
//                    Graphics2D graphics2D = image.createGraphics();
//                    graphics2D.setPaint( getSeriesData().getColor() );
//
//                    graphics2D.setStroke( stroke );
//                    int itemCount = getSeries().getItemCount();
////                    Line2D.Double viewLine = new Line2D.Double( getViewPoint( itemCount - 2 ), getViewPoint( itemCount - 1 ) );
//                    GeneralPath viewPath = new GeneralPath();
//                    viewPath.moveTo( (float)getViewPoint( itemCount - 1 ).getX(), (float)getViewPoint( itemCount - 1 ).getY() );
//                    for( int i = 0; i < 100; i++ ) {
//                        viewPath.lineTo( (float)getViewPoint( itemCount - 1 - i ).getX(), (float)getViewPoint( itemCount - 1 - i ).getY() );
//                    }
//                    setupRenderingHints( graphics2D );
////                    graphics2D.clip( getChartViewBounds() );
////                    System.out.println( "getChartViewBounds() = " + getChartViewBounds() );
//                    graphics2D.clip( getChartViewBounds() );
//                    graphics2D.draw( viewPath );
//
//                    Shape sh = stroke.createStrokedShape( viewPath );
//                    Rectangle2D bounds = sh.getBounds2D();
//                    if( dynamicJFreeChartNode.isBuffered() ) {
//                        bounds = new Rectangle2D.Double( bounds.getX() + dynamicJFreeChartNode.getBounds().getX(), bounds.getY() + dynamicJFreeChartNode.getBounds().getY(), bounds.getWidth(), bounds.getHeight() );
//                    }
//                    dynamicJFreeChartNode.localToGlobal( bounds );
//                    dynamicJFreeChartNode.phetPCanvas.getPhetRootNode().globalToScreen( bounds );
////                    System.out.println( "bounds = " + bounds );
//                    repaintPanel( bounds );
//                }
//            }
//        }

    private void setupRenderingHints( Graphics2D graphics2D ) {
        graphics2D.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
    }

    //todo: is there a simpler way to do this?
    public Rectangle2D getChartViewBounds() {
        XYPlot xyPlot = dynamicJFreeChartNode.getChart().getXYPlot();
        Rectangle2D rect = new Rectangle2D.Double( xyPlot.getDomainAxis().getLowerBound(),
                                                   xyPlot.getRangeAxis().getLowerBound(),
                                                   xyPlot.getDomainAxis().getUpperBound() - xyPlot.getDomainAxis().getLowerBound(),
                                                   xyPlot.getRangeAxis().getUpperBound() - xyPlot.getRangeAxis().getLowerBound() );
        return dynamicJFreeChartNode.plotToNode( rect );
    }

    private Point2D getViewPoint( int index ) {
        return dynamicJFreeChartNode.plotToNode( getPoint( index ) );
    }

    private Point2D.Double getPoint( int index ) {
        return new Point2D.Double( getSeries().getX( index ).doubleValue(), getSeries().getY( index ).doubleValue() );
    }

    public void install() {
        super.install();
        paintAll();
        this.origStateBuffered = dynamicJFreeChartNode.isBuffered();
        if( !origStateBuffered ) {
            dynamicJFreeChartNode.setBuffered( true );
        }
    }

    private void paintAll() {
        BufferedImage image = dynamicJFreeChartNode.getBuffer();
        if( image != null ) {
            Graphics2D graphics2D = image.createGraphics();
            graphics2D.setPaint( getSeriesData().getColor() );
            graphics2D.setStroke( stroke );
            GeneralPath path = toGeneralPath();
            setupRenderingHints( graphics2D );
            graphics2D.clip( getChartViewBounds() );
            graphics2D.draw( path );
            repaintPanel( new Rectangle2D.Double( 0, 0, dynamicJFreeChartNode.getPhetPCanvas().getWidth(), dynamicJFreeChartNode.getPhetPCanvas().getHeight() ) );
        }
    }

    protected void repaintPanel( Rectangle2D bounds ) {
        dynamicJFreeChartNode.getPhetPCanvas().repaint( new PBounds( bounds ) );
        dynamicJFreeChartNode.setDebugBufferRegionPath( bounds );
    }

    private GeneralPath toGeneralPath() {
        GeneralPath path = new GeneralPath();
        if( getSeries().getItemCount() > 0 ) {
            path.moveTo( (float)getViewPoint( 0 ).getX(), (float)getViewPoint( 0 ).getY() );
        }
        for( int i = 1; i < getSeries().getItemCount(); i++ ) {
            path.lineTo( (float)getViewPoint( i ).getX(), (float)getViewPoint( i ).getY() );
        }
        return path;
    }
}
