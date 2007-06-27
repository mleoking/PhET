/*
* The Physics Education Technology (PhET) project provides
* a suite of interactive educational simulations.
* Copyright (C) 2004-2006 University of Colorado.
*
* This program is free software; you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation; either version 2 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
* See the GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License along
* with this program; if not, write to the Free Software Foundation, Inc.,
* 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
*
* For additional licensing options, please contact PhET at phethelp@colorado.edu
*/
package edu.colorado.phet.common.jfreechartphet.piccolo.dynamic;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.nodes.PClip;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

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
        float x = (float)getNodePoint( itemCount - 1 ).getX();
        float y = (float)getNodePoint( itemCount - 1 ).getY();
        System.out.println( "getSeriesData().getSeries().getX( itemCount-1) = " + getSeriesData().getSeries().getX( itemCount - 1 ) );
        System.out.println( "x = " + x );
        if( pathNode.getPathReference().getCurrentPoint() == null ) {
            pathNode.moveTo( x, y );
        }
        else if( getSeries().getItemCount() >= 2 ) {
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
