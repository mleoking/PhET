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

import org.jfree.data.xy.XYSeries;

import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * DISCLAIMER: This class and subclasses are under development and not ready for general use.
 * Base class strategy for painting a data series.
 *
 * @author Sam Reid
 */
public abstract class SeriesView {
    private DynamicJFreeChartNode dynamicJFreeChartNode;
    private SeriesData seriesData;
    private SeriesData.Listener listener = new SeriesData.Listener() {
        public void dataAdded() {
            SeriesView.this.dataAdded();
        }
    };

    public SeriesView( DynamicJFreeChartNode dynamicJFreeChartNode, SeriesData seriesData ) {
        this.dynamicJFreeChartNode = dynamicJFreeChartNode;
        this.seriesData = seriesData;
    }

    public abstract void dataAdded();

    public void uninstall() {
        seriesData.removeListener( listener );
    }

    public void install() {
        seriesData.addListener( listener );
    }

    public DynamicJFreeChartNode getDynamicJFreeChartNode() {
        return dynamicJFreeChartNode;
    }

    public XYSeries getSeries() {
        return seriesData.getSeries();
    }

    public SeriesData getSeriesData() {
        return seriesData;
    }

    public Point2D.Double getPoint( int i ) {
        return new Point2D.Double( getSeries().getX( i ).doubleValue(), getSeries().getY( i ).doubleValue() );
    }

    public Point2D getNodePoint( int i ) {
        return getDynamicJFreeChartNode().plotToNode( getPoint( i ) );
    }

    public Rectangle2D getDataArea() {
        return getDynamicJFreeChartNode().getDataArea();
    }

    protected GeneralPath toGeneralPath() {
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
