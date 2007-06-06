package edu.colorado.phet.common.jfreechartphet.piccolo.dynamic;

import edu.colorado.phet.common.piccolophet.nodes.IncrementalPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.nodes.PClip;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Author: Sam Reid
 * Jun 5, 2007, 6:04:09 PM
 */
public class PPathSeriesView extends SeriesView {
    private PPath pathNode;
    private BasicStroke stroke = new BasicStroke( 3 );
    private PNode root = new PNode();
    private PClip pathClip;

    private PropertyChangeListener listener;

    public PPathSeriesView( final DynamicJFreeChartNode dynamicJFreeChartNode, SeriesData seriesData ) {
        super( dynamicJFreeChartNode, seriesData );

        pathNode = createPPath();
        pathNode.setStroke( stroke );
        pathNode.setStrokePaint( seriesData.getColor() );


        pathClip = new PClip();
        pathClip.setStrokePaint( null );//set to non-null for debugging clip area
        root.addChild( pathClip );

        pathClip.addChild( pathNode );

        updateClip();

        dynamicJFreeChartNode.getPhetPCanvas().addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                updateAll();
            }
        } );
        dynamicJFreeChartNode.addBufferedPropertyChangeListener( new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                updateOffset();
            }
        } );
        listener = new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                updateClip();
            }
        };
    }

    private void updateClip() {
        pathClip.setPathTo( getDataArea() );//todo: this may need to be transformed when dynamicJFreeChartNode.isBuffered()==true
    }

    protected PPath createPPath() {
        return new PPath( );
    }

    private void updateOffset() {
        if( getDynamicJFreeChartNode().isBuffered() ) {
            pathNode.setOffset( getDynamicJFreeChartNode().getBounds().getX(), getDynamicJFreeChartNode().getBounds().getY() );
        }
        else {
            pathNode.setOffset( 0, 0 );
        }
    }

    public void dataAdded() {
        int itemCount = getSeries().getItemCount();
        if( pathNode.getPathReference().getCurrentPoint() == null ) {
            pathNode.moveTo( (float)getNodePoint( itemCount - 1 ).getX(), (float)getNodePoint( itemCount - 1 ).getY() );
        }
        else if( getSeries().getItemCount() >= 2 ) {
            pathNode.lineTo( (float)getNodePoint( itemCount - 1 ).getX(), (float)getNodePoint( itemCount - 1 ).getY() );
        }
    }

    public void uninstall() {
        super.uninstall();
        super.getDynamicJFreeChartNode().removeChild( root );
        getDynamicJFreeChartNode().removeChartRenderingInfoPropertyChangeListener( listener );
        getDynamicJFreeChartNode().removeBufferedImagePropertyChangeListener( listener );
    }

    public void install() {
        super.install();
        getDynamicJFreeChartNode().addChild( root );
        getDynamicJFreeChartNode().addChartRenderingInfoPropertyChangeListener( listener );
        getDynamicJFreeChartNode().addBufferedImagePropertyChangeListener( listener );
        updateClip();
//        updateSeriesGraphic();
        updateAll();
    }

    private void updateAll() {
        pathNode.setPathTo( toGeneralPath() );
    }

    protected void repaintPanel( Rectangle2D bounds ) {
        getDynamicJFreeChartNode().getPhetPCanvas().repaint( new PBounds( bounds ) );
    }

    

}
