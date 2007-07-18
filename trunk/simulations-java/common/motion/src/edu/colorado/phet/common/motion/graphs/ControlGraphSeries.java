package edu.colorado.phet.common.motion.graphs;

import edu.colorado.phet.common.jfreechartphet.piccolo.dynamic.BufferedSeriesView;
import edu.colorado.phet.common.motion.model.ISimulationVariable;
import edu.colorado.phet.common.motion.model.ITimeSeries;

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
    private String units;
    private ISimulationVariable simulationVariable;
    private ITimeSeries observableTimeSeries;
    private boolean visible = true;
    private Stroke stroke;
    private boolean editable;

    private ArrayList listeners = new ArrayList();


    public ControlGraphSeries( String title, Color color, String abbr, String units, ISimulationVariable simulationVariable, ITimeSeries observableTimeSeries ) {
        this( title, color, abbr, units, simulationVariable, observableTimeSeries, BufferedSeriesView.DEFAULT_STROKE );
    }

    public ControlGraphSeries( String title, Color color, String abbr, String units, ISimulationVariable simulationVariable, ITimeSeries observableTimeSeries, Stroke stroke ) {
        this( title, color, abbr, units, simulationVariable, observableTimeSeries, stroke, false );
    }

    public ControlGraphSeries( String title, Color color, String abbr, String units, ISimulationVariable simulationVariable, ITimeSeries observableTimeSeries, Stroke stroke, boolean editable ) {
        this.units = units;
        this.editable = editable;
        this.stroke = stroke;
        this.title = title;
        this.color = color;
        this.abbr = abbr;
        this.simulationVariable = simulationVariable;
        this.observableTimeSeries = observableTimeSeries;
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

    public ISimulationVariable getSimulationVariable() {
        return simulationVariable;
    }

    public ITimeSeries getObservableTimeSeries() {
        return observableTimeSeries;
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

    public static interface Listener {
        void visibilityChanged();
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
