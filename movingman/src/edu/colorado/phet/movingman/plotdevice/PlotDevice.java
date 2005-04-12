/* Copyright 2004, Sam Reid */
package edu.colorado.phet.movingman.plotdevice;

import edu.colorado.phet.chart.BufferedChart;
import edu.colorado.phet.chart.Chart;
import edu.colorado.phet.chart.Range2D;
import edu.colorado.phet.chart.controllers.BufferedChartCursor;
import edu.colorado.phet.chart.controllers.ChartCursor;
import edu.colorado.phet.chart.controllers.ChartSlider;
import edu.colorado.phet.common.view.phetcomponents.PhetJComponent;
import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.movingman.plots.PlotSet;
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
    private ChartSlider slider;
    private BufferedChartCursor cursor;

    private Chart chart;
    private BufferedChart bufferedChart;

    public PlotDevice( Component component, Range2D range, String name ) {
        super( component );
        this.name = name;

        Dimension chartBounds = new Dimension( 600, 200 );
        chart = new Chart( component, range, chartBounds );
        chart.setBackground( chartBackgroundColor );
        chart.getXAxis().setStroke( new BasicStroke( 1, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 1, new float[]{8, 4}, 0 ) );
        chart.getHorizontalTicks().setVisible( false );
        try {
            zoomPanel = new ZoomPanel( this );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        slider = new ChartSlider( component, chart );

        minimizeButton = createMinimizeButton();

        addGraphic( slider );
        slider.addListener( new ChartSlider.Listener() {
            public void valueChanged( double value ) {
                for( int i = 0; i < listeners.size(); i++ ) {
                    PlotDeviceListener plotDeviceListener = (PlotDeviceListener)listeners.get( i );
                    plotDeviceListener.sliderDragged( value );
                }
            }
        } );
        addGraphic( zoomPanel );
        addGraphic( minimizeButton );

        bufferedChart = new BufferedChart( component, chart );
        cursor = new BufferedChartCursor( component, chart, 7, bufferedChart );
        cursor.addListener( new ChartCursor.Listener() {
            public void modelValueChanged( double modelX ) {
                fireCursorMoved( modelX );
            }
        } );
        addGraphic( cursor );
        setChartSize( 600, 200 );
    }

    private void fireCursorMoved( double modelX ) {
        for( int i = 0; i < listeners.size(); i++ ) {
            PlotDeviceListener plotDeviceListener = (PlotDeviceListener)listeners.get( i );
            plotDeviceListener.cursorMoved( modelX );
        }
    }

    public void reset() {
        for( int i = 0; i < data.size(); i++ ) {
            PlotDeviceData plotDeviceData = (PlotDeviceData)data.get( i );
            plotDeviceData.reset();
        }
        rebuildChartBuffer();
    }

    public void setChartSize( int width, int height ) {
        chart.setChartSize( width, height );
//        GradientPaint gradientPaint=new GradientPaint( 0,0,chartBackgroundColor, width, height, Color.white);
//        chart.setBackground( gradientPaint );

        rebuildChartBuffer();
        int guessSliderThumbHeight = (int)( slider.getWidth() / 2.0 );
        slider.setBounds( -slider.getWidth() * 2 - 2, -guessSliderThumbHeight / 2, slider.getWidth(), height + guessSliderThumbHeight );

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
            PlotDeviceData plotDeviceData = (PlotDeviceData)data.get( i );
            plotDeviceData.chartChanged();
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
        return slider.getValue();
    }

    public Chart getChart() {
        return chart;
    }

    private PhetGraphic createMinimizeButton() {
        try {
            ImageIcon icon = new ImageIcon( ImageLoader.loadBufferedImage( "images/x-25.gif" ) );
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

    public void addPlotDeviceData( final PlotDeviceData plotDeviceData ) {
        data.add( plotDeviceData );
        //todo how to decide which datasets to observe with the slider?
        plotDeviceData.getRawData().addObserver( new TimeSeries.Observer() {
            public void dataAdded( TimeSeries timeSeries ) {
                slider.setValue( timeSeries.getLastPoint().getValue() );
            }

            public void cleared( TimeSeries timeSeries ) {
            }
        } );
    }

    public BufferedChart getBufferedChart() {
        return bufferedChart;
    }

    public void setCursorLocation( double time ) {
        cursor.setX( time );
    }

    public class ZoomPanel extends GraphicLayerSet {
        public ZoomPanel( final PlotDevice plotDevice ) throws IOException {
            BufferedImage imPlus = ImageLoader.loadBufferedImage( "images/icons/glass-20-plus.gif" );
            BufferedImage imgMinus = ImageLoader.loadBufferedImage( "images/icons/glass-20-minus.gif" );

            final double smooth = 1;
            ActionListener smoothPos = new Increment( smooth );
            ActionListener smoothNeg = new Decrement( smooth );
            ActionListener incPos = new Increment( 5 );
            ActionListener incNeg = new Decrement( 5 );

            MagButton zoomIn = new MagButton( new ImageIcon( imPlus ), smoothPos, incPos, SimStrings.get( "MMPlot.ZoomInButton" ) );
            MagButton zoomOut = new MagButton( new ImageIcon( imgMinus ), smoothNeg, incNeg, SimStrings.get( "MMPlot.ZoomOutButton" ) );

//            JButton zoomIn = new JButton( new ImageIcon( im ) );
//            zoomIn.addActionListener( new ActionListener() {
//                public void actionPerformed( ActionEvent e ) {
//                    plotDevice.zoomIn();
//                }
//            } );
//
//            JButton zoomOut = new JButton( icon );
//            zoomOut.addActionListener( new ActionListener() {
//                public void actionPerformed( ActionEvent e ) {
//                    plotDevice.zoomOut();
//                }
//            } );
            JPanel panel = new JPanel();
            panel.setLayout( new BoxLayout( panel, BoxLayout.Y_AXIS ) );
            panel.add( zoomOut );
            panel.add( zoomIn );
            PhetGraphic g = PhetJComponent.newInstance( plotDevice.getComponent(), panel );
            addGraphic( g );
        }

    }


    private void zoomOut() {

    }

    private void zoomIn() {
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

    public class TextBox extends GraphicLayerSet {
        private JTextField textField;
        private PhetGraphic pg;

        public TextBox( Component component ) {
            super( component );
            textField = new JTextField( 8 );
            pg = PhetJComponent.newInstance( component, textField );
            addGraphic( pg );
        }

        public boolean isChangedByUser() {
            return false;
        }

        public String getText() {
            return null;
        }

        public void clearChangedByUser() {

        }

        public void addKeyListener( PlotSet.TextHandler textHandler ) {
        }
    }

    public void setCursorVisible( boolean visible ) {
        cursor.setVisible( visible );
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

    public PlotDeviceData dataSeriesAt( int i ) {
        return (PlotDeviceData)data.get( i );
    }

    public ChartSlider getSlider() {
        return slider;
    }


    class MagButton extends JButton {
        public MagButton( Icon icon, ActionListener hold, ActionListener click, String tooltip ) {
            super( icon );
            addMouseListener( new RepeatClicker( hold, click ) );
            setToolTipText( tooltip );
        }
    }

    class Increment implements ActionListener {
        double increment;

        public Increment( double increment ) {
            this.increment = increment;
        }

        public void actionPerformed( ActionEvent e ) {
            Range2D origRange = chart.getRange();
            double diffY = origRange.getMaxY();
            double newDiffY = diffY - increment;
            if( newDiffY > 0 ) {
                setMagnitude( newDiffY );
                setPaintYLines( getYLines( newDiffY, 5 ) );
                rebuildChartBuffer();
                notifyBufferChanged();
            }
        }

    }

    class Decrement implements ActionListener {
        double increment;

        public Decrement( double increment ) {
            this.increment = increment;
        }

        public void actionPerformed( ActionEvent e ) {
            Range2D origRange = chart.getRange();
            double diffY = origRange.getMaxY();
            double newDiffY = diffY + increment;
            int MAX = 100;
            if( newDiffY < MAX ) {
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
}
