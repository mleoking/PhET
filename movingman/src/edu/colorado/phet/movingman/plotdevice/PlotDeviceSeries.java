/* Copyright 2004, Sam Reid */
package edu.colorado.phet.movingman.plotdevice;

import edu.colorado.phet.chart.BufferedLinePlot;
import edu.colorado.phet.chart.DataSet;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.BasicGraphicsSetup;
import edu.colorado.phet.common.view.graphics.transforms.LinearTransform2D;
import edu.colorado.phet.common.view.phetgraphics.*;
import edu.colorado.phet.movingman.plots.TimePoint;
import edu.colorado.phet.movingman.plots.TimeSeries;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.util.Random;

/**
 * User: Sam Reid
 * Date: Apr 5, 2005
 * Time: 4:28:19 AM
 * Copyright (c) Apr 5, 2005 by Sam Reid
 */
public class PlotDeviceSeries extends CompositePhetGraphic {
    private PlotDevice plotDevice;
    private TimeSeries rawData;
    private Color color;
    private String name;
    private Stroke stroke;

    private DataSet storedData;
    private BufferedLinePlot bufferedLinePlot;

    static final Random debuggingRandom = new Random( 0 );
//    private TimePoint lastPoint;
    private PhetShadowTextGraphic readoutGraphic;
    private DecimalFormat decimalFormat = new DecimalFormat( "0.0" );
    private HTMLGraphic unitsGraphic;
    private PhetShadowTextGraphic nameGraphic;
    private PhetImageGraphic bufferedNameGraphic;

    public PlotDeviceSeries( final PlotDevice plotDevice, TimeSeries timeSeries,
                             Color color, final String name, Stroke stroke, Font font, String unitsString ) {
        super( plotDevice.getComponent() );
        this.plotDevice = plotDevice;
        this.rawData = timeSeries;
        this.color = color;
        this.name = name;
        this.stroke = stroke;
//        this.stroke = new BasicStroke( 1 );//todo debugging plots
        storedData = new DataSet();

        bufferedLinePlot = new BufferedLinePlot( plotDevice.getBufferedChart(), this.stroke, color );
        bufferedLinePlot.setAutoRepaint( true );
        timeSeries.addObserver( new TimeSeriesObserver() );
        readoutGraphic = new PhetShadowTextGraphic( plotDevice.getComponent(), font, "text", color, 1, 1, Color.black );
        unitsGraphic = new HTMLGraphic( getComponent(), font, unitsString, color );
        nameGraphic = new PhetShadowTextGraphic( plotDevice.getComponent(), font, name + ": ", color, 1, 1, Color.black );
        bufferedNameGraphic = BufferedPhetGraphic2.createBuffer( nameGraphic, new BasicGraphicsSetup(), BufferedImage.TYPE_INT_RGB, plotDevice.getBackground() );
//        readoutGraphic = new PhetShadowTextGraphic( plotDevice.getComponent(),font,
//        addGraphic( nameGraphic);
        addGraphic( bufferedNameGraphic );
        readoutGraphic.setLocation( nameGraphic.getX() + nameGraphic.getWidth(), nameGraphic.getY() );
        addGraphic( readoutGraphic );
        addGraphic( unitsGraphic );
        clearValue();
    }

    private void clearValue() {
        bufferedLinePlot.clear();
        TimePoint tp = new TimePoint( 0, 0 );
        updateReadoutGraphic( tp );
    }

    public void setPlaybackTime( double time ) {
        TimePoint timePoint = rawData.getValueForTime( time );
        updateReadoutGraphic( timePoint );
    }

    private class TimeSeriesObserver implements TimeSeries.Observer {
        public void dataAdded( TimeSeries timeSeries ) {
            TimePoint timePoint = timeSeries.getLastPoint();
//            if( name.toLowerCase().equals( "velocity" ) ) {
//                double time=timePoint.getTime();
//                System.out.println( "time="+time );
////                System.out.println( "name=" + name + ": " + timePoint );
////                    System.out.println( timePoint.getTime() );
////                    if( lastPoint != null && timePoint.getTime() < lastPoint.getTime() ) {
////                        System.out.println( "Time went negative." );
////                    }
//            }
//                lastPoint = timePoint;
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
            updateReadoutGraphic( timePoint );
        }


        public void cleared( TimeSeries timeSeries ) {

            clearValue();
        }
    }

    private void updateReadoutGraphic( TimePoint timePoint ) {
        String value = decimalFormat.format( timePoint.getValue() );
//        readoutGraphic.setText( name + ": " + value + " " );
        readoutGraphic.setText( value + " " );
        unitsGraphic.setLocation( readoutGraphic.getX() + readoutGraphic.getWidth(), readoutGraphic.getY() );
    }

    private void addRectangleGraphicForDebugging( Rectangle screenRect, final PlotDevice plotDevice ) {
        Color color = new Color( debuggingRandom.nextFloat(), debuggingRandom.nextFloat(), debuggingRandom.nextFloat() );
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
