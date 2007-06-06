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
public class IncrementalPPathSeriesView extends SeriesView {
    private PPath pathNode;
    private BasicStroke stroke = new BasicStroke( 3 );
    private PNode root = new PNode();
//    private PhetPPath pathNode;

    private PClip pathClip;
    private PropertyChangeListener listener = new PropertyChangeListener() {
        public void propertyChange( PropertyChangeEvent evt ) {
            updateClip();
        }
    };

    public IncrementalPPathSeriesView( final DynamicJFreeChartNode dynamicJFreeChartNode, SeriesData seriesData ) {
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
    }


    private void updateClip() {
        pathClip.setPathTo( dynamicJFreeChartNode.getDataArea() );//todo: this may need to be transformed when dynamicJFreeChartNode.isBuffered()==true
    }

    protected PPath createPPath() {
        return new IncrementalPPath( dynamicJFreeChartNode.getPhetPCanvas() );
    }

    private void updateOffset() {
        if( dynamicJFreeChartNode.isBuffered() ) {
            pathNode.setOffset( dynamicJFreeChartNode.getBounds().getX(), dynamicJFreeChartNode.getBounds().getY() );
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
        dynamicJFreeChartNode.removeChartRenderingInfoPropertyChangeListener( listener );
        dynamicJFreeChartNode.removeBufferedImagePropertyChangeListener( listener );
    }

    public void install() {
        super.install();
        getDynamicJFreeChartNode().addChild( root );
        dynamicJFreeChartNode.addChartRenderingInfoPropertyChangeListener( listener );
        dynamicJFreeChartNode.addBufferedImagePropertyChangeListener( listener );
        updateClip();
//        updateSeriesGraphic();
        updateAll();
    }

    private void updateAll() {
        pathNode.setPathTo( toGeneralPath() );
    }

    protected void repaintPanel( Rectangle2D bounds ) {
        dynamicJFreeChartNode.getPhetPCanvas().repaint( new PBounds( bounds ) );
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
