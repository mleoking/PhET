package edu.colorado.phet.common.jfreechartphet.piccolo.dynamic;

import edu.colorado.phet.common.piccolophet.nodes.IncrementalPPath;
import edu.umd.cs.piccolo.util.PBounds;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Author: Sam Reid
 * Jun 5, 2007, 6:04:09 PM
 */
public class IncrementalPPathSeriesView extends SeriesView {
    private IncrementalPPath incrementalPPath;
    private BasicStroke stroke = new BasicStroke( 3 );

    public IncrementalPPathSeriesView( final DynamicJFreeChartNode dynamicJFreeChartNode, SeriesData seriesData ) {
        super( dynamicJFreeChartNode, seriesData );
        incrementalPPath = new IncrementalPPath( dynamicJFreeChartNode.getPhetPCanvas() );
        incrementalPPath.setStroke( stroke );
        incrementalPPath.setStrokePaint( seriesData.getColor() );
        dynamicJFreeChartNode.getPhetPCanvas().addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                updateAll();
            }
        } );
        dynamicJFreeChartNode.addBufferedPropertyChangeListener( new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                bufferChanged();
            }
        } );
    }

    private void bufferChanged() {
        if( dynamicJFreeChartNode.isBuffered() ) {
            incrementalPPath.setOffset( dynamicJFreeChartNode.getBounds().getX(), dynamicJFreeChartNode.getBounds().getY() );
        }
        else {
            incrementalPPath.setOffset( 0, 0 );
        }
    }

    public void dataAdded() {
        int itemCount = getSeries().getItemCount();
        if( incrementalPPath.getPathReference().getCurrentPoint() == null ) {
            incrementalPPath.moveTo( (float)getNodePoint( itemCount - 1 ).getX(), (float)getNodePoint( itemCount - 1 ).getY() );
        }
        else if( getSeries().getItemCount() >= 2 ) {
            incrementalPPath.lineTo( (float)getNodePoint( itemCount - 1 ).getX(), (float)getNodePoint( itemCount - 1 ).getY() );
        }
    }

    public void uninstall() {
        super.uninstall();
        super.getDynamicJFreeChartNode().removeChild( incrementalPPath );
    }

    public void install() {
        super.install();
        getDynamicJFreeChartNode().addChild( incrementalPPath );
        updateAll();
    }

    private void updateAll() {
        incrementalPPath.setPathTo( toGeneralPath() );
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
