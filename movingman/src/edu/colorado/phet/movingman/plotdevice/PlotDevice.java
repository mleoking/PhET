/* Copyright 2004, Sam Reid */
package edu.colorado.phet.movingman.plotdevice;

import edu.colorado.phet.chart.BufferedLinePlot;
import edu.colorado.phet.chart.Chart;
import edu.colorado.phet.chart.DataSet;
import edu.colorado.phet.chart.Range2D;
import edu.colorado.phet.chart.controllers.ChartCursor;
import edu.colorado.phet.chart.controllers.ChartSlider;
import edu.colorado.phet.common.view.BasicGraphicsSetup;
import edu.colorado.phet.common.view.phetcomponents.PhetJComponent;
import edu.colorado.phet.common.view.phetgraphics.BufferedPhetGraphic2;
import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.movingman.plots.PlotSet;
import edu.colorado.phet.movingman.plots.TimePoint;
import edu.colorado.phet.movingman.plots.TimeSeries;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    private ArrayList data = new ArrayList();
    private Chart chart;
    private PhetGraphic minimizeButton;
    private PhetGraphic zoomPanel;
    private ChartSlider slider;
    private ChartCursor cursor;
    private TextBox textBox;
    private String name;
    private ArrayList listeners = new ArrayList();
    private PhetImageGraphic chartBuffer;

    public PlotDevice( Component component, Range2D range, String name ) {
        super( component );
        this.name = name;

        Rectangle viewBounds = new Rectangle( 0, 0, 600, 200 );
        chart = new Chart( component, range, viewBounds );
        try {
            zoomPanel = new ZoomPanel( this );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        slider = new ChartSlider( component, chart );
        cursor = new ChartCursor( component, chart, 7 );
        minimizeButton = createMinimizeButton();

        addGraphic( slider );
        addGraphic( zoomPanel );
        addGraphic( minimizeButton );
        addGraphic( cursor );
        textBox = new TextBox( component );
        setChartViewBounds( viewBounds.x, viewBounds.y, viewBounds.width, viewBounds.height );
    }

    public void reset() {
    }

    public TextBox getTextBox() {
        return textBox;
    }

    public void setChartViewBounds( int x, int y, int width, int height ) {
        setChartSize( width, height );
        setLocation( x, y );
    }

    private void setChartSize( int width, int height ) {
        chart.setChartSize( width, height );

        if( chartBuffer != null ) {
            removeGraphic( chartBuffer );
        }

        chartBuffer = BufferedPhetGraphic2.createBuffer( chart, new BasicGraphicsSetup(), BufferedImage.TYPE_INT_RGB, getComponent().getBackground() );
        chartBuffer.setLocation( chart.getLocalBounds().x, chart.getLocalBounds().y );
        addGraphic( chartBuffer, -1 );
        for( int i = 0; i < data.size(); i++ ) {
            PlotDeviceData plotDeviceData = (PlotDeviceData)data.get( i );
            plotDeviceData.chartChanged( chartBuffer.getImage(), -chartBuffer.getX(), -chartBuffer.getY() );
        }

        setBoundsDirty();
        int guessSliderThumbHeight = (int)( slider.getWidth() / 2.0 );
        slider.setBounds( -slider.getWidth() * 2 - 2, -guessSliderThumbHeight / 2, slider.getWidth(), height + guessSliderThumbHeight );

        zoomPanel.setLocation( 2, chart.getChartBounds().height - zoomPanel.getHeight() - 2 );
        minimizeButton.setLocation( chart.getChartBounds().width - minimizeButton.getWidth() - 2, 2 );

        setBoundsDirty();
        autorepaint();
    }

    public void setMagnitude( double magnitude ) {
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
    }

    public static class PlotDeviceData {
        private PlotDevice plotDevice;
        private TimeSeries timeSeries;
        private Color color;
        private String name;
        private Stroke stroke;

        private DataSet dataSet;
        private BufferedLinePlot bufferedLinePlot;

        public PlotDeviceData( final PlotDevice plotDevice, TimeSeries timeSeries, Color color, String name, Stroke stroke ) {
            this.plotDevice = plotDevice;
            this.timeSeries = timeSeries;
            this.color = color;
            this.name = name;
            this.stroke = stroke;
            dataSet = new DataSet();

            bufferedLinePlot = new BufferedLinePlot( plotDevice.chart, stroke, color, plotDevice.chartBuffer.getImage() );
            bufferedLinePlot.setAutoRepaint( true );
            timeSeries.addObserver( new TimeSeries.Observer() {
                public void dataSeriesChanged( TimeSeries timeSeries ) {
                    TimePoint timePoint = timeSeries.getLastPoint();
                    dataSet.addPoint( timePoint.getTime(), timePoint.getValue() );
                    Rectangle r = bufferedLinePlot.lineTo( dataSet.getLastPoint() );
                    if( r != null ) {
                        r.x += plotDevice.getLocation().x;
                        r.y += plotDevice.getLocation().y;
                        plotDevice.getComponent().repaint( r.x, r.y, r.width, r.height );//todo which is faster?
//                        JComponent jc = (JComponent)plotDevice.getComponent();
//                        jc.paintImmediately( r.x, r.y, r.width, r.height );
                    }
                }
            } );
        }

        public void chartChanged( BufferedImage bufferedImage, int dx, int dy ) {
            bufferedLinePlot.setBufferedImage( bufferedImage );
            bufferedLinePlot.setOffset( dx, dy );
            bufferedLinePlot.clear();
            for( int i = 0; i < dataSet.size(); i++ ) {
                bufferedLinePlot.lineTo( dataSet.pointAt( i ) );
            }
        }
    }

    public static class ZoomPanel extends GraphicLayerSet {
        public ZoomPanel( PlotDevice plotDevice ) throws IOException {
            BufferedImage im = ImageLoader.loadBufferedImage( "images/icons/glass-20-plus.gif" );
            JButton zoomIn = new JButton( new ImageIcon( im ) );
            JButton zoomOut = new JButton( new ImageIcon( ImageLoader.loadBufferedImage( "images/icons/glass-20-minus.gif" ) ) );
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

    public String getName() {
        return name;
    }
}
