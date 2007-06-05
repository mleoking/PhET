package edu.colorado.phet.common.jfreechartphet.piccolo.dynamic;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.nodes.PClip;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * Author: Sam Reid
* Jun 5, 2007, 6:03:59 PM
*/
public class PiccoloSeriesView extends SeriesView {

    private PNode root = new PNode();
    private PhetPPath pathNode;
    private PClip pathClip;
    private PropertyChangeListener listener = new PropertyChangeListener() {
        public void propertyChange( PropertyChangeEvent evt ) {
            updateClip();
        }
    };

    public PiccoloSeriesView( DynamicJFreeChartNode dynamicJFreeChartNode, SeriesData seriesData ) {
        super( dynamicJFreeChartNode, seriesData );

        pathClip = new PClip();
        pathClip.setStrokePaint( null );//set to non-null for debugging clip area
        root.addChild( pathClip );

        pathNode = new PhetPPath( new BasicStroke( 3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f ), seriesData.getColor() );
        pathClip.addChild( pathNode );

        updateClip();
    }

    private void updateClip() {
        pathClip.setPathTo( dynamicJFreeChartNode.getDataArea() );//todo: this may need to be transformed when dynamicJFreeChartNode.isBuffered()==true
    }

    public void updateSeriesGraphic() {
        GeneralPath path = new GeneralPath();
        if( super.getSeries().getItemCount() > 0 ) {
            Point2D d = getNodePoint( 0 );
            path.moveTo( (float)d.getX(), (float)d.getY() );
            for( int i = 1; i < getSeries().getItemCount(); i++ ) {
                Point2D nodePoint = getNodePoint( i );
                path.lineTo( (float)nodePoint.getX(), (float)nodePoint.getY() );
            }
        }
        pathNode.setPathTo( path );
        if( dynamicJFreeChartNode.isBuffered() ) {
            pathNode.setOffset( dynamicJFreeChartNode.getBounds().getX(), dynamicJFreeChartNode.getBounds().getY() );
        }
        else {
            pathNode.setOffset( 0, 0 );
        }
    }

    public Point2D.Double getPoint( int i ) {
        return new Point2D.Double( getSeries().getX( i ).doubleValue(), getSeries().getY( i ).doubleValue() );
    }

    public Point2D getNodePoint( int i ) {
        return getDynamicJFreeChartNode().plotToNode( getPoint( i ) );
    }

    public void setClip( Rectangle2D clip ) {
        pathClip.setPathTo( clip );
    }

    public void uninstall() {
        super.uninstall();
        super.getDynamicJFreeChartNode().removeChild( root );
        dynamicJFreeChartNode.removeChartRenderingInfoPropertyChangeListener( listener );
        dynamicJFreeChartNode.removeBufferedImagePropertyChangeListener( listener );
    }

    public void install() {
        super.install();
        getDynamicJFreeChartNode().addChild( root );
        dynamicJFreeChartNode.addChartRenderingInfoPropertyChangeListener( listener );
        dynamicJFreeChartNode.addBufferedImagePropertyChangeListener( listener );
        updateClip();
        updateSeriesGraphic();
    }

    public void dataAdded() {
        updateSeriesGraphic();
    }
}
