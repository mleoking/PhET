/* Copyright 2004, Sam Reid */
package edu.colorado.phet.movingman.plotdevice;

import edu.colorado.phet.chart.BufferedChart;
import edu.colorado.phet.chart.Chart;
import edu.colorado.phet.chart.Range2D;
import edu.colorado.phet.chart.controllers.BufferedChartCursor;
import edu.colorado.phet.chart.controllers.ChartCursor;
import edu.colorado.phet.chart.controllers.ChartSlider;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.phetcomponents.PhetJComponent;
import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.util.BufferedImageUtils;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.movingman.plots.TimePoint;
import edu.colorado.phet.movingman.plots.TimeSeries;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Mar 28, 2005
 * Time: 10:23:39 AM
 * Copyright (c) Mar 28, 2005 by Sam Reid
 */

public class PlotDevice extends GraphicLayerSet {
    private Color chartBackgroundColor = new Color( 250, 247, 224 );
    private String name;

    private ArrayList listeners = new ArrayList();
    private ArrayList data = new ArrayList();

    private PhetGraphic minimizeButton;
    private PhetGraphic zoomPanel;
    private ChartSlider chartSlider;
    private BufferedChartCursor cursor;

    private Chart chart;
    private BufferedChart bufferedChart;

    public PlotDevice( ApparatusPanel apparatusPanel, Range2D range, String name, String sliderImageLoc, Color foregroundColor ) {
        super( apparatusPanel );
        this.name = name;

        Dimension chartBounds = new Dimension( 600, 200 );
        chart = new Chart( apparatusPanel, range, chartBounds );
        chart.setBackground( chartBackgroundColor );
        chart.getVerticalGridlines().setMajorGridlinesColor( Color.gray );
        chart.getVerticalGridlines().setMinorGridlinesColor( Color.gray );

        chart.getHorizonalGridlines().setMajorGridlinesColor( Color.gray );
        chart.getHorizonalGridlines().setMinorGridlinesColor( Color.gray );

        chart.getXAxis().setStroke( new BasicStroke( 1, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 1, new float[]{8, 4}, 0 ) );
        chart.getHorizontalTicks().setVisible( false );
        try {
            zoomPanel = new ZoomPanel( this );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        chartSlider = new ChartSlider( apparatusPanel, chart, sliderImageLoc, foregroundColor );
        minimizeButton = createMinimizeButton();

        addGraphic( chartSlider );
        chartSlider.addListener( new ChartSlider.Listener() {
            public void valueChanged( double value ) {
                for( int i = 0; i < listeners.size(); i++ ) {
                    PlotDeviceListener plotDeviceListener = (PlotDeviceListener)listeners.get( i );
                    plotDeviceListener.sliderDragged( value );
                }
            }
        } );
        addGraphic( zoomPanel );
        addGraphic( minimizeButton );

        bufferedChart = new BufferedChart( apparatusPanel, chart );
        cursor = new BufferedChartCursor( apparatusPanel, chart, 7, bufferedChart );
        cursor.addListener( new ChartCursor.Listener() {
            public void modelValueChanged( double modelX ) {
                fireCursorDragged( modelX );
            }
        } );
        addGraphic( cursor );
        addListener( new CursorHelpItem( apparatusPanel, this ) );
        setChartSize( 600, 200 );
    }

    private void fireCursorDragged( double modelX ) {
        for( int i = 0; i < listeners.size(); i++ ) {
            PlotDeviceListener plotDeviceListener = (PlotDeviceListener)listeners.get( i );
            plotDeviceListener.cursorDragged( modelX );
        }
    }

    public void reset() {
        chartSlider.setValue( 0.0 );
        for( int i = 0; i < data.size(); i++ ) {
            PlotDeviceSeries plotDeviceSeries = (PlotDeviceSeries)data.get( i );
            plotDeviceSeries.reset();
        }
        rebuildChartBuffer();
    }

    public void setChartSize( int width, int height ) {
        chart.setChartSize( width, height );
//        GradientPaint gradientPaint=new GradientPaint( 0,0,chartBackgroundColor, width, height, Color.white);
//        chart.setBackground( gradientPaint );

        rebuildChartBuffer();
        int guessSliderThumbHeight = (int)( chartSlider.getWidth() / 2.0 );
        chartSlider.setBounds( -chartSlider.getWidth() * 2 - 2, -guessSliderThumbHeight / 2, chartSlider.getWidth(), height + guessSliderThumbHeight );

        zoomPanel.setLocation( 2, chart.getChartBounds().height - zoomPanel.getHeight() - 2 );
        minimizeButton.setLocation( chart.getChartBounds().width - minimizeButton.getWidth() - 2, 2 );

        setBoundsDirty();
        autorepaint();
    }

    private void rebuildChartBuffer() {
        if( bufferedChart != null ) {
            removeGraphic( bufferedChart );
        }
        bufferedChart = new BufferedChart( getComponent(), chart );
        bufferedChart.setLocation( chart.getLocalBounds().x, chart.getLocalBounds().y );
        cursor.setBufferedChart( bufferedChart );
        addGraphic( bufferedChart, -1 );
        for( int i = 0; i < data.size(); i++ ) {
            PlotDeviceSeries plotDeviceSeries = (PlotDeviceSeries)data.get( i );
            plotDeviceSeries.chartChanged();
        }

        setBoundsDirty();
    }

    private void setMagnitude( double magnitude ) {
        Range2D range = chart.getRange();
        Range2D newRange = new Range2D( range );
        newRange.setMaxY( magnitude );
        newRange.setMinY( -magnitude );
        chart.setRange( newRange );
    }

    public void requestTypingFocus() {
    }

    public void addListener( PlotDeviceListener listener ) {
        listeners.add( listener );
    }

    public double getSliderValue() {
        return chartSlider.getValue();
    }

    public Chart getChart() {
        return chart;
    }

    private BufferedImage testResize( BufferedImage image, double fraction ) {
        if( Toolkit.getDefaultToolkit().getScreenSize().width <= 1024 ) {
            image = BufferedImageUtils.rescaleYMaintainAspectRatio( getComponent(), image, (int)( image.getHeight() * fraction ) );
        }
        return image;
    }

    private PhetGraphic createMinimizeButton() {
        try {
            BufferedImage image = ImageLoader.loadBufferedImage( "images/x-25.gif" );
            image = testResize( image, 0.75 );
            ImageIcon icon = new ImageIcon( image );
            JButton minimize = new JButton( icon );
            minimize.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    for( int i = 0; i < listeners.size(); i++ ) {
                        PlotDeviceListener listener = (PlotDeviceListener)listeners.get( i );
                        listener.minimizePressed();
                    }
                }
            } );
            return PhetJComponent.newInstance( getComponent(), minimize );
        }
        catch( IOException e ) {
            e.printStackTrace();
            return null;
        }
    }

    public void addPlotDeviceData( final PlotDeviceSeries plotDeviceSeries ) {
        data.add( plotDeviceSeries );
        addGraphic( plotDeviceSeries );
        plotDeviceSeries.setLocation( 10, 5 );
        //todo how to decide which datasets to observe with the slider?
        plotDeviceSeries.getRawData().addObserver( new TimeSeries.Observer() {
            public void dataAdded( TimeSeries timeSeries ) {
                chartSlider.setValue( timeSeries.getLastPoint().getValue() );
            }

            public void cleared( TimeSeries timeSeries ) {
            }
        } );
    }

    public BufferedChart getBufferedChart() {
        return bufferedChart;
    }

    public void setPlaybackTime( double time ) {
        cursor.setX( time );
        //change the reading
        for( int i = 0; i < data.size(); i++ ) {
            PlotDeviceSeries plotDeviceSeries = (PlotDeviceSeries)data.get( i );
            plotDeviceSeries.setPlaybackTime( time );
        }
        if( data.size() > 0 ) {
            PlotDeviceSeries plotDeviceSeries = (PlotDeviceSeries)data.get( 0 );
            TimePoint value = plotDeviceSeries.getRawData().getValueForTime( time );
            chartSlider.setValue( value.getValue() );
        }
        for( int i = 0; i < listeners.size(); i++ ) {
            PlotDeviceListener plotDeviceListener = (PlotDeviceListener)listeners.get( i );
            plotDeviceListener.playbackTimeChanged();
        }
    }

    public Paint getBackground() {
        return chartBackgroundColor;
    }

    public class ZoomPanel extends GraphicLayerSet {
        public ZoomPanel( final PlotDevice plotDevice ) throws IOException {
            BufferedImage imPlus = ImageLoader.loadBufferedImage( "images/icons/glass-20-plus.gif" );
            imPlus = testResize( imPlus, 0.9 );
            BufferedImage imgMinus = ImageLoader.loadBufferedImage( "images/icons/glass-20-minus.gif" );
            imgMinus = testResize( imgMinus, 0.9 );
            final double smooth = 1;
            ActionListener smoothPos = new ValueChange( smooth, 0, 100 );
            ActionListener smoothNeg = new ValueChange( -smooth, 0, 100 );
            ActionListener incPos = new ValueChange( 5, 0, 100 );
            ActionListener incNeg = new ValueChange( -5, 0, 100 );

            MagButton zoomIn = new MagButton( new ImageIcon( imPlus ), smoothPos, incPos, SimStrings.get( "MMPlot.ZoomInButton" ) );
            MagButton zoomOut = new MagButton( new ImageIcon( imgMinus ), smoothNeg, incNeg, SimStrings.get( "MMPlot.ZoomOutButton" ) );

            JPanel panel = new JPanel();
            panel.setLayout( new BoxLayout( panel, BoxLayout.Y_AXIS ) );
            panel.add( zoomOut );
            panel.add( zoomIn );
            PhetGraphic g = PhetJComponent.newInstance( plotDevice.getComponent(), panel );
            addGraphic( g );
        }

    }

    public void setPaintYLines( double[] lines ) {
        double[] full = new double[lines.length * 2 + 1];
        for( int i = 0; i < lines.length; i++ ) {
            full[i] = lines[i];
            full[full.length - 1 - i] = -lines[i];
        }
        full[lines.length] = 0;

        double[] half = new double[lines.length * 2];
        for( int i = 0; i < lines.length; i++ ) {
            half[i] = lines[i];
            half[half.length - 1 - i] = -lines[i];
        }
        chart.getHorizonalGridlines().setMajorGridlines( half );
        chart.getVerticalTicks().setMajorGridlines( full );
        chart.getYAxis().setMajorGridlines( full );
    }

    public void setCursorVisible( boolean visible ) {
        cursor.setVisible( visible );
        notifyCursorListeners( visible );
    }

    private void notifyCursorListeners( boolean visible ) {
        for( int i = 0; i < listeners.size(); i++ ) {
            PlotDeviceListener plotDeviceListener = (PlotDeviceListener)listeners.get( i );
            plotDeviceListener.cursorVisibilityChanged( visible );
        }
    }

    public void setChartRange( Range2D range ) {
        chart.setRange( range );
        rebuildChartBuffer();
    }

    public void setChartRange( Rectangle2D.Double modelBounds ) {
        chart.setRange( new Range2D( modelBounds ) );
        rebuildChartBuffer();
    }

    public String getName() {
        return name;
    }

    public PlotDeviceSeries dataSeriesAt( int i ) {
        return (PlotDeviceSeries)data.get( i );
    }

    public ChartSlider getChartSlider() {
        return chartSlider;
    }

    class MagButton extends JButton {
        public MagButton( Icon icon, ActionListener hold, ActionListener click, String tooltip ) {
            super( icon );
            addMouseListener( new RepeatClicker( hold, click ) );
            setToolTipText( tooltip );
        }
    }


    class ValueChange implements ActionListener {
        double increment;
        private double min;
        private double max;

        public ValueChange( double increment, double min, double max ) {
            this.increment = increment;
            this.min = min;
            this.max = max;
        }

        public void actionPerformed( ActionEvent e ) {
            Range2D origRange = chart.getRange();
            double diffY = origRange.getMaxY();
            double newDiffY = diffY - increment;
            if( newDiffY > min && newDiffY < max ) {
                setMagnitude( newDiffY );
                setPaintYLines( getYLines( newDiffY, 5 ) );
                rebuildChartBuffer();
                notifyBufferChanged();
            }
        }

    }

    private void notifyBufferChanged() {
        for( int i = 0; i < listeners.size(); i++ ) {
            PlotDeviceListener plotDeviceListener = (PlotDeviceListener)listeners.get( i );
            plotDeviceListener.bufferChanged();
        }
    }

    private double[] getYLines( double magnitude, double dy ) {
        ArrayList values = new ArrayList();
        for( double i = dy; i < magnitude; i += dy ) {
            values.add( new Double( i ) );
        }
        if( values.size() > 5 ) {
            return getYLines( magnitude, dy * 2 );
        }
        if( values.size() <= 1 ) {
            return getYLines( magnitude, dy / 2 );
        }
        double[] d = new double[values.size()];
        for( int i = 0; i < d.length; i++ ) {
            d[i] = ( (Double)values.get( i ) ).doubleValue();
        }
        return d;
    }

    public int getNumDataSeries() {
        return data.size();
    }

    public BufferedChartCursor getCursor() {
        return cursor;
    }

    public int numPlotDeviceData() {
        return data.size();
    }

    public PlotDeviceSeries plotDeviceSeriesAt( int i ) {
        return (PlotDeviceSeries)data.get( i );
    }

}
