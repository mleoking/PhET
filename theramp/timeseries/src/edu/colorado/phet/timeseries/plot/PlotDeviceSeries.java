/* Copyright 2004, Sam Reid */
package edu.colorado.phet.timeseries.plot;

import edu.colorado.phet.chart.BufferedLinePlot;
import edu.colorado.phet.chart.DataSet;
import edu.colorado.phet.common.util.DefaultDecimalFormat;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.graphics.transforms.LinearTransform2D;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShadowTextGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.phetgraphics.ShadowHTMLGraphic;
import edu.colorado.phet.timeseries.TimePoint;
import edu.colorado.phet.timeseries.TimeSeries;

import java.awt.*;
import java.util.Random;

/**
 * User: Sam Reid
 * Date: Apr 5, 2005
 * Time: 4:28:19 AM
 * Copyright (c) Apr 5, 2005 by Sam Reid
 */
public class PlotDeviceSeries extends CompositePhetGraphic {
    private TimePlot timePlot;
    private TimeSeries rawData;
    private Color color;
    private String name;
    private Stroke stroke;

    private DataSet storedData;
    private BufferedLinePlot bufferedLinePlot;

    static final Random debuggingRandom = new Random( 0 );
//    private TimePoint lastPoint;
    private PhetShadowTextGraphic readoutGraphic;
    private DefaultDecimalFormat decimalFormat = new DefaultDecimalFormat( "0.0" );
    private ShadowHTMLGraphic unitsGraphic;
    private PhetShadowTextGraphic nameGraphic;
    private ShadowHTMLGraphic justifyMetric;
//    private PhetImageGraphic bufferedNameGraphic;

    public PlotDeviceSeries( final TimePlot timePlot, TimeSeries timeSeries,
                             Color color, final String name, Stroke stroke, Font font, String unitsString, String justifyText ) {
        super( timePlot.getComponent() );
        this.timePlot = timePlot;
        this.rawData = timeSeries;
        this.color = color;
        this.name = name;
        this.stroke = stroke;
//        this.stroke = new BasicStroke( 1 );//todo debugging plots
        storedData = new DataSet();

        bufferedLinePlot = new BufferedLinePlot( timePlot.getBufferedChart(), this.stroke, color );
        bufferedLinePlot.setAutoRepaint( true );
        timeSeries.addObserver( new TimeSeriesObserver() );
        readoutGraphic = new PhetShadowTextGraphic( timePlot.getComponent(), font, "text", color, 1, 1, Color.black );
//        unitsGraphic = new HTMLGraphic( getComponent(), font, unitsString, color );
        unitsGraphic = new ShadowHTMLGraphic( getComponent(), unitsString, font, color, 1, 1, Color.black );
        nameGraphic = new PhetShadowTextGraphic( timePlot.getComponent(), font, name + ": ", color, 1, 1, Color.black );

        justifyMetric = new ShadowHTMLGraphic( getComponent(), justifyText, font, Color.blue, 1, 1, Color.gray );
//        bufferedNameGraphic = BufferedPhetGraphic2.createBuffer( nameGraphic, new BasicGraphicsSetup(), BufferedImage.TYPE_INT_RGB, timePlotetBackground() );
//        readoutGraphic = new PhetShadowTextGraphic( timePlotetComponent(),font,
//        addGraphic( nameGraphic);
//        addGraphic( bufferedNameGraphic );
        addGraphic( nameGraphic );
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

    public void setReadoutValue( double value ) {
        updateReadoutGraphic( value );
    }

    public double getMaxTime() {
        return rawData.getLastTime();
    }

    public int getTextHeight() {
        return readoutGraphic.getHeight();
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
            Rectangle sourceRect = new Rectangle( timePlot.getBufferedChart().getSize() );
            Rectangle destRect = new Rectangle( timePlot.getBufferedChart().getBounds() );
            LinearTransform2D linearTransform2D = new LinearTransform2D( sourceRect, destRect, false );
            Rectangle screenRect = linearTransform2D.createTransformedShape( r ).getBounds();
//                System.out.println( "linearTransform2D = " + linearTransform2D );
//                System.out.println( "r = " + r );
//                System.out.println( "screenRect = " + screenRect );

            if( r != null ) {
//                    addRectangleGraphic( r, timePlot );

                timePlot.getComponent().repaint( screenRect.x, screenRect.y, screenRect.width, screenRect.height );//todo which is faster?
//                    ( (JComponent)timePlot.getComponent() ).paintImmediately( r.x, r.y, r.width, r.height );
//                    timePlot.getComponent().repaint();//slow, but guaranteed to clip the correct region.
            }
            updateReadoutGraphic( timePoint );
        }


        public void cleared( TimeSeries timeSeries ) {

            clearValue();
        }
    }

    private void updateReadoutGraphic( double v ) {

//        readoutGraphic.setLocation( nameGraphic.getX() + nameGraphic.getWidth(), nameGraphic.getY() );


        readoutGraphic.setAutorepaint( false );
//        readoutGraphic.setVisible( false );
        String value = decimalFormat.format( v );

        readoutGraphic.setText( value + " " );
        int dx = justifyMetric.getWidth() - readoutGraphic.getWidth();
        readoutGraphic.setLocation( nameGraphic.getX() + nameGraphic.getWidth() + dx, nameGraphic.getY() );

        unitsGraphic.setLocation( readoutGraphic.getX() + readoutGraphic.getWidth(), readoutGraphic.getY() );
        readoutGraphic.setAutorepaint( true );
//        readoutGraphic.setVisible( true );
        readoutGraphic.autorepaint();
    }

    private void updateReadoutGraphic( TimePoint timePoint ) {
        updateReadoutGraphic( timePoint.getValue() );
//        String value = decimalFormat.format( timePoint.getValue() );
////        readoutGraphic.setText( name + ": " + value + " " );
//        readoutGraphic.setText( value + " " );
//        unitsGraphic.setLocation( readoutGraphic.getX() + readoutGraphic.getWidth(), readoutGraphic.getY() );
    }

    private void addRectangleGraphicForDebugging( Rectangle screenRect, final TimePlot timePlot ) {
        Color color = new Color( debuggingRandom.nextFloat(), debuggingRandom.nextFloat(), debuggingRandom.nextFloat() );
        PhetShapeGraphic phetShapeGraphic = new PhetShapeGraphic( timePlot.getComponent(), screenRect, new BasicStroke( 1 ), color );

        ApparatusPanel ap = (ApparatusPanel)timePlot.getComponent();
        ap.addGraphic( phetShapeGraphic, Double.POSITIVE_INFINITY );
    }

    public void chartChanged() {
        bufferedLinePlot.setBufferedChart( timePlot.getBufferedChart() );
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
