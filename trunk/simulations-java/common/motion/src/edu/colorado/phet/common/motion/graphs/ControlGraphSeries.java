package edu.colorado.phet.common.motion.graphs;

import edu.colorado.phet.common.jfreechartphet.piccolo.dynamic.BufferedSeriesView;
import edu.colorado.phet.common.motion.model.ITemporalVariable;
import edu.colorado.phet.common.motion.model.IVariable;
import edu.colorado.phet.rotation.model.SeriesVariable;

import java.awt.*;
import java.util.ArrayList;

/**
 * Author: Sam Reid
 * Jul 13, 2007, 2:50:27 PM
 */
public class ControlGraphSeries {
    private String title;
    private Color color;
    private String abbr;
    private SeriesVariable seriesVariable;
    private String units;
    private boolean visible = true;
    private Stroke stroke;
    private boolean editable;
    private String character;

    private ArrayList listeners = new ArrayList();

    public ControlGraphSeries( String title, Color color, String abbr, String units,
                               String character, SeriesVariable seriesVariable ) {
        this( title, color, abbr, units, BufferedSeriesView.DEFAULT_STROKE, character, seriesVariable );
    }

    public ControlGraphSeries( String title, Color color, String abbr, String units,
                               Stroke stroke, String character, SeriesVariable seriesVariable ) {
        this( title, color, abbr, units, stroke, false, character, seriesVariable );
    }

    public ControlGraphSeries( String title, Color color, String abbr, String units, Stroke stroke, boolean editable, String character, SeriesVariable seriesVariable ) {
        this.units = units;
        this.editable = editable;
        this.stroke = stroke;
        this.title = title;
        this.color = color;
        this.abbr = abbr;
        this.seriesVariable = seriesVariable;
        this.character = character;
        assert character != null;
    }

    public String getTitle() {
        return title;
    }

    public Color getColor() {
        return color;
    }

    public String getAbbr() {
        return abbr;
    }

    public SeriesVariable getSeriesVariable(){
        return seriesVariable;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible( boolean visible ) {
        if( this.visible != visible ) {
            this.visible = visible;
            notifyVisibilityChanged();
        }
    }

    public boolean isEditable() {
        return editable;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits( String units ) {
        if( !this.units.equals( units ) ) {
            this.units = units;
            notifyUnitsChanged();
        }
    }

    private void notifyUnitsChanged() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.unitsChanged();
        }
    }

    public String getCharacterName() {
        return character;
    }

    public ITemporalVariable getObservableTimeSeries() {
        //return seriesVariable.getSeries();//todo: remove
        return seriesVariable;
    }

    public IVariable getSimulationVariable() {
        //return seriesVariable.getVariable();
        return seriesVariable;
    }

    public static interface Listener {
        void visibilityChanged();

        void unitsChanged();
    }

    public static class Adapter implements Listener {

        public void visibilityChanged() {
        }

        public void unitsChanged() {
        }
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void removeListener( Listener listener ) {
        listeners.remove( listener );
    }

    private void notifyVisibilityChanged() {
        for( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener)listeners.get( i ) ).visibilityChanged();
        }
    }

    public Stroke getStroke() {
        return stroke;
    }
}
