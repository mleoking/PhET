package edu.colorado.phet.common.jfreechartphet.piccolo.dynamic;

import org.jfree.data.xy.XYSeries;

import java.awt.*;
import java.util.ArrayList;

/**
 * Author: Sam Reid
* Jun 5, 2007, 6:04:29 PM
*/
public class SeriesData {
    private String title;
    private Color color;
    private XYSeries series;
    private ArrayList listeners = new ArrayList();
    private static int index = 0;

    public SeriesData( String title, Color color ) {
        this( title, color, new XYSeries( title + " " + ( index++ ) ) );
    }

    public SeriesData( String title, Color color, XYSeries series ) {
        this.title = title;
        this.color = color;
        this.series = series;
    }

    public String getTitle() {
        return title;
    }

    public Color getColor() {
        return color;
    }

    public XYSeries getSeries() {
        return series;
    }

    public void addValue( double time, double value ) {
        series.add( time, value );
        notifyDataChanged();
    }

    public void removeListener( Listener listener ) {
        listeners.remove( listener );
    }

    public void clear() {
        series.clear();
        notifyDataChanged();
    }

    public static interface Listener {
        void dataAdded();
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void notifyDataChanged() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.dataAdded();
        }
    }
}
