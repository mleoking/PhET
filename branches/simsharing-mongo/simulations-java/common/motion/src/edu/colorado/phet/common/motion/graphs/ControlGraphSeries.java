// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.motion.graphs;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.text.DecimalFormat;
import java.util.ArrayList;

import edu.colorado.phet.common.jfreechartphet.piccolo.dynamic.BufferedSeriesView;
import edu.colorado.phet.common.motion.model.ITemporalVariable;
import edu.colorado.phet.common.phetcommon.util.DefaultDecimalFormat;

/**
 * Author: Sam Reid
 * Jul 13, 2007, 2:50:27 PM
 */
public class ControlGraphSeries {
    private String title;
    private Color color;
    private String abbr;
    private ITemporalVariable temporalVariable;
    private String units;
    private boolean visible = true;
    private Stroke stroke;
    private boolean editable;
    private String character;

    private ArrayList<Listener> listeners = new ArrayList<Listener>();
    private DefaultDecimalFormat decimalFormat = new DefaultDecimalFormat( "0.00" );

    public ControlGraphSeries( ITemporalVariable temporalVariable ) {
        this( "", Color.BLACK, "x", "meters", new BasicStroke(), "", temporalVariable );
    }

    public ControlGraphSeries( String title, Color color, String abbr, String units,
                               String character, ITemporalVariable temporalVariable ) {
        this( title, color, abbr, units, BufferedSeriesView.DEFAULT_STROKE, character, temporalVariable );
    }

    public ControlGraphSeries( String title, Color color, String abbr, String units, Stroke stroke, String character, ITemporalVariable temporalVariable ) {
        this( title, color, abbr, units, stroke, character, temporalVariable, false );
    }

    public ControlGraphSeries( String title, Color color, String abbr, String units, Stroke stroke, String character, ITemporalVariable defaultTemporalVariable, boolean editable ) {
        this.units = units;
        this.editable = editable;
        this.stroke = stroke;
        this.title = title;
        this.color = color;
        this.abbr = abbr;
        this.temporalVariable = defaultTemporalVariable;
        this.character = character;
        if ( color == null ) {
            new RuntimeException( "null color" ).printStackTrace();
        }
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

    public boolean isVisible() {
        return visible;
    }

    public DefaultDecimalFormat getDecimalFormat() {
        return decimalFormat;
    }

    public void setDecimalFormat( DefaultDecimalFormat decimalFormat ) {
        this.decimalFormat = decimalFormat;
    }

    public void setVisible( boolean visible ) {
        if ( this.visible != visible ) {
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
        if ( ( this.units == null && units != null ) || !this.units.equals( units ) ) {
            this.units = units;
            notifyUnitsChanged();
        }
    }

    private void notifyUnitsChanged() {
        for ( Listener listener : listeners ) {
            listener.unitsChanged();
        }
    }

    public String getCharacterName() {
        return character;
    }

    public ITemporalVariable getTemporalVariable() {
        //return seriesVariable.getSeries();//todo: remove
        return temporalVariable;
    }

    public DecimalFormat getNumberFormat() {
        return decimalFormat;
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
        for ( Listener listener : listeners ) {
            listener.visibilityChanged();
        }
    }

    public Stroke getStroke() {
        return stroke;
    }
}
