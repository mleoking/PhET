// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.jfreechartphet.piccolo.dynamic;

import java.awt.BasicStroke;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.nodes.PClip;

/**
 * DISCLAIMER: This class is under development and not ready for general use.
 * Renderer for DynamicJFreeChartNode
 *
 * @author Sam Reid
 */
public class PPathSeriesView extends SeriesView {
    private PPath pathNode;
    private BasicStroke stroke = new BasicStroke( 3 );
    private PNode root = new PNode();
    private PClip pathClip;

    private PropertyChangeListener listener;

    public void visibilityChanged() {
        updateAll();
    }

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
                updateAll();
            }
        } );
        listener = new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                updateAll();
            }
        };
    }

    protected PPath createPPath() {
        return new PPath();
    }

    public void dataAdded() {
        int itemCount = getSeries().getItemCount();
        float x = (float) getNodePoint( itemCount - 1 ).getX();
        float y = (float) getNodePoint( itemCount - 1 ).getY();
//        System.out.println( "getSeriesData().getSeries().getX( itemCount-1) = " + getSeriesData().getSeries().getX( itemCount - 1 ) );
//        System.out.println( "x = " + x );
        if ( pathNode.getPathReference().getCurrentPoint() == null ) {
            pathNode.moveTo( x, y );
        }
        else if ( getSeries().getItemCount() >= 2 ) {
            pathNode.lineTo( x, y );
        }
    }

    public void dataCleared() {
        pathNode.getPathReference().reset();
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
        updateAll();
    }

    protected void forceRepaintAll() {
        updateAll();
    }

    private void updateAll() {
        updateClip();
        updatePath();
    }

    private void updateClip() {
        pathClip.setPathTo( getDataArea() );//todo: this may need to be transformed when dynamicJFreeChartNode.isBuffered()==true
    }

    private void updatePath() {
        pathNode.setPathTo( toGeneralPath() );
    }

}
