/* Copyright 2004, Sam Reid */
package edu.colorado.phet.movingman.plotdevice;

import edu.colorado.phet.chart.BufferedLinePlot;
import edu.colorado.phet.chart.DataSet;
import edu.colorado.phet.movingman.plots.TimePoint;
import edu.colorado.phet.movingman.plots.TimeSeries;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * User: Sam Reid
 * Date: Apr 5, 2005
 * Time: 4:28:19 AM
 * Copyright (c) Apr 5, 2005 by Sam Reid
 */
public class PlotDeviceData {
    private PlotDevice plotDevice;
    private TimeSeries rawData;
    private Color color;
    private String name;
    private Stroke stroke;

    private DataSet storedData;
    private BufferedLinePlot bufferedLinePlot;

    public PlotDeviceData( final PlotDevice plotDevice, TimeSeries timeSeries, Color color, String name, Stroke stroke ) {
        this.plotDevice = plotDevice;
        this.rawData = timeSeries;
        this.color = color;
        this.name = name;
        this.stroke = stroke;
        storedData = new DataSet();

        bufferedLinePlot = new BufferedLinePlot( plotDevice.getChart(), stroke, color, plotDevice.getChartBuffer() );
        bufferedLinePlot.setAutoRepaint( true );
        timeSeries.addObserver( new TimeSeries.Observer() {
            public void dataSeriesChanged( TimeSeries timeSeries ) {
                TimePoint timePoint = timeSeries.getLastPoint();
                storedData.addPoint( timePoint.getTime(), timePoint.getValue() );
                Rectangle r = bufferedLinePlot.lineTo( storedData.getLastPoint() );
                if( r != null ) {
                    r.x += plotDevice.getChart().getLocalBounds().x + plotDevice.getLocation().x;
                    r.y += plotDevice.getChart().getLocalBounds().y + plotDevice.getLocation().y;

//                    r.x += plotDevice.getLocation().x;
//                    r.y += plotDevice.getLocation().y;
                    plotDevice.getComponent().repaint( r.x, r.y, r.width, r.height );//todo which is faster?
//                    ( (JComponent)plotDevice.getComponent() ).paintImmediately( r.x, r.y, r.width, r.height );
                }
            }
        } );
    }

    public void chartChanged( BufferedImage bufferedImage, int dx, int dy ) {
        bufferedLinePlot.setBufferedImage( bufferedImage );
        bufferedLinePlot.setOffset( dx, dy );
        bufferedLinePlot.clear();
        for( int i = 0; i < storedData.size(); i++ ) {
            bufferedLinePlot.lineTo( storedData.pointAt( i ) );
        }
    }

    public TimeSeries getRawData() {
        return rawData;
    }

    public DataSet getStoredData() {
        return storedData;
    }

    public Color getColor() {
        return color;
    }
}
