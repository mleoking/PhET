package edu.colorado.phet.ec3.plots;

import java.awt.*;

public class DataUnit {
    private ValueAccessor valueAccessor;
    private TimeSeries timeSeries;
    private EnergySkaterTimePlotNode plotDeviceSeries;
    private TimeSeriesPNode seriesGraphic;

    public DataUnit( TimeSeriesPNode series ) {
        this.valueAccessor = series.getValueAccessor();
        this.timeSeries = series.getTimeSeries();
        this.plotDeviceSeries = series.getPlot();
        this.seriesGraphic = series;
    }

    public void reset() {
        timeSeries.reset();
        plotDeviceSeries.reset();
        seriesGraphic.reset();
    }

    public void updatePlot( Object state, double recordTime ) {
        double value = valueAccessor.getValue( state );
        timeSeries.addPoint( value, recordTime );
    }

    public String getName() {
        return valueAccessor.getName();
    }

    public void setVisible( boolean selected ) {
        seriesGraphic.setVisible( selected );
        plotDeviceSeries.repaintAll();
    }

    public Color getColor() {
        return valueAccessor.getColor();
    }

    public String getFullName() {
        return valueAccessor.getFullName();
    }
}