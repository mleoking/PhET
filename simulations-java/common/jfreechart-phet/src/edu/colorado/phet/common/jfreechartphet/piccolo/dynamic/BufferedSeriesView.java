package edu.colorado.phet.common.jfreechartphet.piccolo.dynamic;

import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.util.PBounds;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Author: Sam Reid
 * Jun 5, 2007, 6:04:09 PM
 */
public class BufferedSeriesView extends SeriesView {
    private BasicStroke stroke = new BasicStroke( 3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 1.0f );
    private PhetPPath debugRegion = new PhetPPath( new BasicStroke( 3 ), Color.blue );

    public BufferedSeriesView( DynamicJFreeChartNode dynamicJFreeChartNode, SeriesData seriesData ) {
        super( dynamicJFreeChartNode, seriesData );
        dynamicJFreeChartNode.addBufferedImagePropertyChangeListener( new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                paintAll();
            }
        } );
        dynamicJFreeChartNode.getPhetPCanvas().addScreenChild( debugRegion );
        dynamicJFreeChartNode.getPhetPCanvas().addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                paintAll();
            }
        } );
    }

    public void dataAdded() {
        if( getSeries().getItemCount() >= 2 ) {
            BufferedImage image = getDynamicJFreeChartNode().getBuffer();
            if( image != null ) {
                drawPoint( 0 );
//                for( int i = 0; i < Math.min( 50,getSeries().getItemCount() - 1); i++ ) {
//                    drawPoint( i );
//                }
            }
        }
    }

    private void drawPoint( int index ) {
        BufferedImage image = getDynamicJFreeChartNode().getBuffer();
        Graphics2D graphics2D = image.createGraphics();
        graphics2D.setPaint( getSeriesData().getColor() );

        graphics2D.setStroke( stroke );
        int itemCount = getSeries().getItemCount();
        Line2D.Double viewLine = new Line2D.Double( getNodePoint( itemCount - 2 - index ), getNodePoint( itemCount - 1 - index ) );
        setupRenderingHints( graphics2D );

        graphics2D.clip( translateDataArea() );
        graphics2D.draw( viewLine );

        Shape sh = stroke.createStrokedShape( viewLine );
        Rectangle2D bounds = sh.getBounds2D();
        getDynamicJFreeChartNode().localToGlobal( bounds );
        getDynamicJFreeChartNode().getPhetPCanvas().getPhetRootNode().globalToScreen( bounds );
        repaintPanel( translateDown( bounds ) );
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
        BufferedImage image = getDynamicJFreeChartNode().getBuffer();
        if( image != null ) {
            Graphics2D graphics2D = image.createGraphics();
            graphics2D.setPaint( getSeriesData().getColor() );
            graphics2D.setStroke( stroke );
            setupRenderingHints( graphics2D );
            graphics2D.clip( getDataArea() );
            graphics2D.draw( translateDown( toGeneralPath() ) );//toGeneralPath calls our overriden getNodePoint
            repaintPanel( translateDown( new Rectangle2D.Double( 0, 0, getDynamicJFreeChartNode().getPhetPCanvas().getWidth(), getDynamicJFreeChartNode().getPhetPCanvas().getHeight() ) ) );
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
