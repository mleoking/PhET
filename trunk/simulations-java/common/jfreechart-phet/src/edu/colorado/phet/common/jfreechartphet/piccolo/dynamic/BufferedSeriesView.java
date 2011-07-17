// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.jfreechartphet.piccolo.dynamic;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import edu.umd.cs.piccolo.util.PBounds;

/**
 * DISCLAIMER: This class is under development and not ready for general use.
 * Renderer for DynamicJFreeChartNode
 *
 * @author Sam Reid
 */
public class BufferedSeriesView extends SeriesView {
    public static final BasicStroke DEFAULT_STROKE = new BasicStroke( 3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 1.0f );
//    private BasicStroke DEFAULT_STROKE = new BasicStroke( 3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 1.0f, new float[]{10, 10}, 0 );

    //    private PhetPPath debugRegion = new PhetPPath( new BasicStroke( 3 ), Color.blue );
    private boolean updateAllEnabled = false;
    private double lastLineLength = 0;

    public void visibilityChanged() {
        paintAll();
    }

    public BufferedSeriesView( DynamicJFreeChartNode dynamicJFreeChartNode, SeriesData seriesData ) {
        super( dynamicJFreeChartNode, seriesData );
        dynamicJFreeChartNode.addBufferedImagePropertyChangeListener( new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                paintAll();
            }
        } );
//        dynamicJFreeChartNode.getPhetPCanvas().addScreenChild( debugRegion );
        dynamicJFreeChartNode.getPhetPCanvas().addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                paintAll();
            }
        } );
    }

    public void dataAdded() {
        if ( getSeries().getItemCount() >= 2 ) {
            //require that the chart is onscreen before expending effort
            //todo: iterating over the entire parent hierarchy every time step for every visible series may be too expensive
            if ( getDynamicJFreeChartNode().getBuffer() != null && getSeriesData().isVisible() && getDynamicJFreeChartNode().getVisible() && getDynamicJFreeChartNode().isDescendentOfRoot() ) {
                drawPoint( 0 );
            }
        }
    }

    public void dataCleared() {
    }

    private void drawPoint( int index ) {
        updateStroke();
        BufferedImage image = getDynamicJFreeChartNode().getBuffer();
        Graphics2D graphics2D = image.createGraphics();
        graphics2D.setPaint( getSeriesData().getColor() );

        graphics2D.setStroke( getSeriesData().getStroke() );
        int itemCount = getSeries().getItemCount();
        Line2D.Double viewLine = new Line2D.Double( getNodePoint( itemCount - 2 - index ), getNodePoint( itemCount - 1 - index ) );
        lastLineLength = viewLine.getP1().distance( viewLine.getP2() );
        setupRenderingHints( graphics2D );

        graphics2D.clip( translateDataArea() );
        graphics2D.draw( viewLine );

        Shape sh = getSeriesData().getStroke().createStrokedShape( viewLine );
        Rectangle2D bounds = sh.getBounds2D();
        getDynamicJFreeChartNode().localToGlobal( bounds );
        getDynamicJFreeChartNode().getPhetPCanvas().getPhetRootNode().globalToScreen( bounds );
        repaintPanel( translateDown( bounds ) );
    }

    private void updateStroke() {
//        System.out.println( "DEFAULT_STROKE.getDashArray() = " + DEFAULT_STROKE.getDashArray().length );
        Stroke stroke = getSeriesData().getStroke();
        if ( stroke instanceof BasicStroke ) {
            BasicStroke basicStroke = (BasicStroke) stroke;
            if ( basicStroke.getDashArray() != null ) {
                getSeriesData().setStroke( new BasicStroke( basicStroke.getLineWidth(), basicStroke.getEndCap(), basicStroke.getLineJoin(), basicStroke.getMiterLimit(),
                                                            basicStroke.getDashArray(), (float) ( basicStroke.getDashPhase() + lastLineLength ) ) );
            }
        }
    }

    private void resetStrokePhase() {
//        System.out.println( "DEFAULT_STROKE.getDashArray() = " + DEFAULT_STROKE.getDashArray().length );
        Stroke stroke = getSeriesData().getStroke();
        if ( stroke instanceof BasicStroke ) {
            BasicStroke basicStroke = (BasicStroke) stroke;
            if ( basicStroke.getDashArray() != null ) {
                getSeriesData().setStroke( new BasicStroke( basicStroke.getLineWidth(), basicStroke.getEndCap(), basicStroke.getLineJoin(), basicStroke.getMiterLimit(),
                                                            basicStroke.getDashArray(), 0.0f ) );
            }
        }
    }

    private Rectangle2D translateDown( Rectangle2D d ) {
        return new Rectangle2D.Double( d.getX() + getDX(),
                                       d.getY() + getDY(),
                                       d.getWidth(), d.getHeight() );
    }

    private Shape translateDataArea() {
        Rectangle2D d = getDataArea();
        return new Rectangle2D.Double( d.getX() - getDX(),
                                       d.getY() - getDY(),
                                       d.getWidth(), d.getHeight() );
    }

    private double getDX() {
        return getDynamicJFreeChartNode().getBounds().getX();
    }

    private double getDY() {
        return getDynamicJFreeChartNode().getBounds().getY();
    }

    //toGeneralPath calls our overriden getNodePoint
    public Point2D getNodePoint( int i ) {
        return new Point2D.Double( super.getNodePoint( i ).getX() - getDX(),
                                   super.getNodePoint( i ).getY() - getDY() );
    }

    protected void forceRepaintAll() {
        BufferedImage image = getDynamicJFreeChartNode().getBuffer();
        if ( image != null ) {
            lastLineLength = 0;
            updateStroke();
            resetStrokePhase();

            Graphics2D graphics2D = image.createGraphics();
            graphics2D.setPaint( getSeriesData().getColor() );
            graphics2D.setStroke( getSeriesData().getStroke() );
            setupRenderingHints( graphics2D );
            graphics2D.clip( getDataArea() );
            if ( getSeriesData().isVisible() ) {
                graphics2D.draw( translateDown( toGeneralPath() ) );//toGeneralPath calls our overriden getNodePoint
            }
            //todo: the following line seems unnecessary in Rotation, and slows performance
//            repaintPanel( translateDown( new Rectangle2D.Double( 0, 0, getDynamicJFreeChartNode().getPhetPCanvas().getWidth(), getDynamicJFreeChartNode().getPhetPCanvas().getHeight() ) ) );
        }
    }

    private void setupRenderingHints( Graphics2D graphics2D ) {
        graphics2D.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
    }

    public void install() {
        super.install();
        getDynamicJFreeChartNode().updateAll();
    }

    public void uninstall() {
        super.uninstall();
    }

    private void paintAll() {
        if ( updateAllEnabled ) {
            forceRepaintAll();
        }
    }

    private Shape translateDown( GeneralPath d ) {
        return AffineTransform.getTranslateInstance( getDX(), getDY() ).createTransformedShape( d );
    }

    protected void repaintPanel( Rectangle2D bounds ) {
        Rectangle2D dataArea = getDataArea();
        getDynamicJFreeChartNode().localToGlobal( dataArea );
        Rectangle2D b = bounds.createIntersection( dataArea );
        getDynamicJFreeChartNode().getPhetPCanvas().repaint( new PBounds( b ) );
    }

}
