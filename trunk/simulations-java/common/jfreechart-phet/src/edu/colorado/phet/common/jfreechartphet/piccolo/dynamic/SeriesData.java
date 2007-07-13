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

import java.awt.*;
import java.util.ArrayList;

/**
 * Represents the series that can be painted on a DynamicJFreeChartNode
 *
 * @author Sam Reid
 */
public class SeriesData {
    private String title;
    private Color color;
    private Stroke stroke;
    private XYSeries series;
    private ArrayList listeners = new ArrayList();
    private static int index = 0;
    private boolean visible = true;

    public SeriesData( String title, Color color,Stroke stroke ) {
        this( title, color, new XYSeries( title + " " + ( index++ ), false, true ),stroke );
    }

    public SeriesData( String title, Color color, XYSeries series,Stroke stroke ) {
        this.title = title;
        this.color = color;
        this.series = series;
        this.stroke = stroke;
    }

    public String getTitle() {
        return title;
    }

    public Color getColor() {
        return color;
    }

    public Stroke getStroke() {
        return stroke;
    }

    public XYSeries getSeries() {
        return series;
    }

    public void addValue( double time, double value ) {
        series.add( time, value );
        notifyDataAdded();
    }

    public void removeListener( Listener listener ) {
        listeners.remove( listener );
    }

    public void clear() {
        if( series.getItemCount() > 0 ) {
            series.clear();
            notifyDataCleared();
        }
    }

    private void notifyDataCleared() {
        for( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener)listeners.get( i ) ).dataCleared();
        }
    }

    public void setVisible( boolean visible ) {
        if( this.visible != visible ) {
            this.visible = visible;
            notifyVisibilityChanged();
        }
    }

    private void notifyVisibilityChanged() {
        for( int i = 0; i < listeners.size(); i++ ) {
            ((Listener)listeners.get( i )).visibilityChanged();
        }
    }

    public boolean isVisible() {
        return visible;
    }

    public void setStroke( Stroke stroke ) {
        this.stroke=stroke;
    }

    public static interface Listener {
        void dataAdded();

        void dataCleared();

        void visibilityChanged();
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void notifyDataAdded() {
        for( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener)listeners.get( i ) ).dataAdded();
        }
    }
}
