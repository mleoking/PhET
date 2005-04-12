/* Copyright 2004, Sam Reid */
package edu.colorado.phet.movingman.plotdevice;

import edu.colorado.phet.chart.BufferedLinePlot;
import edu.colorado.phet.chart.DataSet;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.graphics.transforms.LinearTransform2D;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.movingman.plots.TimePoint;
import edu.colorado.phet.movingman.plots.TimeSeries;

import java.awt.*;
import java.util.Random;

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

    static final Random rand = new Random( 0 );

    public PlotDeviceData( final PlotDevice plotDevice, TimeSeries timeSeries, Color color, String name, Stroke stroke ) {
        this.plotDevice = plotDevice;
        this.rawData = timeSeries;
        this.color = color;
        this.name = name;
        this.stroke = stroke;
        storedData = new DataSet();

        bufferedLinePlot = new BufferedLinePlot( plotDevice.getBufferedChart(), stroke, color );
        bufferedLinePlot.setAutoRepaint( true );
        timeSeries.addObserver( new TimeSeries.Observer() {
            public void dataAdded( TimeSeries timeSeries ) {
                TimePoint timePoint = timeSeries.getLastPoint();
                storedData.addPoint( timePoint.getTime(), timePoint.getValue() );
                Rectangle r = bufferedLinePlot.lineTo( storedData.getLastPoint() );

                //todo optimize this correct code (keeping it correct)
                Rectangle sourceRect = new Rectangle( plotDevice.getBufferedChart().getSize() );
                Rectangle destRect = new Rectangle( plotDevice.getBufferedChart().getBounds() );
                LinearTransform2D linearTransform2D = new LinearTransform2D( sourceRect, destRect, false );
                Rectangle screenRect = linearTransform2D.createTransformedShape( r ).getBounds();
//                System.out.println( "linearTransform2D = " + linearTransform2D );
//                System.out.println( "r = " + r );
//                System.out.println( "screenRect = " + screenRect );

                if( r != null ) {
//                    addRectangleGraphic( r, plotDevice );

                    plotDevice.getComponent().repaint( screenRect.x, screenRect.y, screenRect.width, screenRect.height );//todo which is faster?
//                    ( (JComponent)plotDevice.getComponent() ).paintImmediately( r.x, r.y, r.width, r.height );
//                    plotDevice.getComponent().repaint();//slow, but guaranteed to clip the correct region.
                }
            }

            public void cleared( TimeSeries timeSeries ) {
                bufferedLinePlot.clear();
            }
        } );
    }

    private void addRectangleGraphicForDebugging( Rectangle screenRect, final PlotDevice plotDevice ) {
        Color color = new Color( rand.nextFloat(), rand.nextFloat(), rand.nextFloat() );
        PhetShapeGraphic phetShapeGraphic = new PhetShapeGraphic( plotDevice.getComponent(), screenRect, new BasicStroke( 1 ), color );

        ApparatusPanel ap = (ApparatusPanel)plotDevice.getComponent();
        ap.addGraphic( phetShapeGraphic, Double.POSITIVE_INFINITY );
    }

    public void chartChanged() {
        bufferedLinePlot.setBufferedChart( plotDevice.getBufferedChart() );
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


    public void reset() {
        rawData.reset();
        storedData.clear();
    }
}
