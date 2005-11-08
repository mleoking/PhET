/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3;

import edu.colorado.phet.common.model.clock.ClockTickEvent;
import edu.colorado.phet.common.model.clock.ClockTickListener;
import edu.colorado.phet.ec3.model.Body;
import edu.colorado.phet.ec3.plots.Range2D;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Nov 6, 2005
 * Time: 8:05:15 PM
 * Copyright (c) Nov 6, 2005 by Sam Reid
 */

public class EnergyPositionPlotPanel extends PhetPCanvas {
    private JFreeChart chart;
    private XYSeriesCollection dataset;
    private EC3Module module;

    private XYSeries peSeries;
    private XYSeries keSeries;
    private XYSeries totSeries;
    private PImage image;
    private ChartRenderingInfo info = new ChartRenderingInfo();

    public EnergyPositionPlotPanel( EC3Module ec3Module ) {
        super( new Dimension( 100, 100 ) );
        this.module = ec3Module;
        ec3Module.getClock().addClockTickListener( new ClockTickListener() {
            public void clockTicked( ClockTickEvent event ) {
                update();
            }
        } );
        dataset = createDataset();
        chart = createChart( new Range2D( 0, 0, 800, 400000 ), dataset, "Title" );
//        chartPanel = new ChartPanel( chart );
        setLayout( new BorderLayout() );
//        add( chartPanel, BorderLayout.CENTER );
        peSeries = new XYSeries( "Potential" );
        keSeries = new XYSeries( "Kinetic" );
        totSeries = new XYSeries( "Total" );
//        dataset.addSeries( peSeries );
//        dataset.addSeries( keSeries );
//        dataset.addSeries( totSeries );
        JButton clear = new JButton( "Clear" );
        clear.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                reset();
            }
        } );
        add( clear, BorderLayout.SOUTH );
        chart.setAntiAlias( true );

        image = new PImage( new BufferedImage( 10, 10, BufferedImage.TYPE_INT_RGB ) );
        addScreenChild( image );
        updateImage();
        addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                updateImage();
            }

            public void componentShown( ComponentEvent e ) {
                updateImage();
            }
        } );
    }

    private void updateImage() {
        if( getWidth() > 0 && getHeight() > 0 ) {
            image.setImage( chart.createBufferedImage( getWidth(), getHeight(), info ) );
        }
        reset();
    }

    private static JFreeChart createChart( Range2D range, XYDataset dataset, String title ) {
//        JFreeChart chart = ChartFactory.createXYLineChart( "",
        JFreeChart chart = ChartFactory.createScatterPlot( "",
                                                           "Position", // x-axis label
                                                           "Energy", // y-axis label
                                                           dataset, PlotOrientation.VERTICAL, true, true, false );

        chart.setBackgroundPaint( new Color( 240, 220, 210 ) );

        XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint( Color.white );
        plot.setDomainGridlinesVisible( true );
        plot.setRangeGridlinesVisible( false );

        NumberAxis xAxis = new NumberAxis();
        xAxis.setAutoRange( true );
        xAxis.setRange( range.getMinX(), range.getMaxX() );
        xAxis.setTickLabelsVisible( false );
        xAxis.setTickMarksVisible( true );
        plot.setDomainAxis( xAxis );

        NumberAxis yAxis = new NumberAxis( title );
        yAxis.setAutoRange( true );
        yAxis.setRange( range.getMinY(), range.getMaxY() );
//        yAxis.setLabelFont( new Font( "Lucida Sans", Font.PLAIN, 11 ) );
        plot.setRangeAxis( yAxis );

        plot.setDomainCrosshairVisible( true );
        plot.setRangeCrosshairVisible( true );

        return chart;
    }

    private static XYSeriesCollection createDataset() {
        XYSeries xySeries = new XYSeries( new Integer( 0 ) );
        return new XYSeriesCollection( xySeries );
    }

    public void reset() {
        peSeries.clear();
        keSeries.clear();
        totSeries.clear();
        getScreenNode().removeAllChildren();
        addScreenChild( image );
        peDots.clear();
    }

    ArrayList peDots = new ArrayList();

    private void update() {
        if( module.getEnergyConservationModel().numBodies() > 0 ) {
            Body body = module.getEnergyConservationModel().bodyAt( 0 );
            addFadeDot( body.getX(), module.getEnergyConservationModel().getPotentialEnergy( body ), module.getEnergyLookAndFeel().getPEColor() );
            addFadeDot( body.getX(), module.getEnergyConservationModel().getTotalEnergy( body ), module.getEnergyLookAndFeel().getTotalEnergyColor() );
            addFadeDot( body.getX(), body.getKineticEnergy(), module.getEnergyLookAndFeel().getKEColor() );
        }
        for( int i = 0; i < peDots.size(); i++ ) {
            FadeDot fadeDot = (FadeDot)peDots.get( i );
            fadeDot.fade();
            if( fadeDot.isFullyFaded() ) {
                peDots.remove( i );
                getScreenNode().removeChild( fadeDot );
                i--;
            }
        }

    }

    private void addFadeDot( double x, double y, Color peColor ) {
        FadeDot path = new FadeDot( peColor, toImageLocation( x, y ) );
        addScreenChild( path );
        peDots.add( path );
    }

    static class FadeDot extends PPath {
        private Color origColor;
        private int age;
        private int dAge = 3;
        private Color fadeColor;

        public FadeDot( Color color, Point2D loc ) {
            super( new Ellipse2D.Double( -3, -3, 6, 6 ), null );
            setPaint( color );
            setOffset( loc );
            this.origColor = color;
        }

        public void fade() {
            age += dAge;
            int fadeAlpha = 255 - age;
            if( fadeAlpha < 0 ) {
                fadeAlpha = 0;
            }
            Color fadeColor = new Color( origColor.getRed(), origColor.getGreen(), origColor.getBlue(),
                                         fadeAlpha );
            this.fadeColor = fadeColor;
            setPaint( fadeColor );
        }

        public boolean isFullyFaded() {
            return fadeColor.getAlpha() <= 0;
        }
    }

    public Point2D toImageLocation( double x, double y ) {
        Rectangle2D dataArea = info.getPlotInfo().getDataArea();
        if( dataArea == null ) {
            throw new RuntimeException( "Null data area" );
        }

        double transX1 = chart.getXYPlot().getDomainAxisForDataset( 0 ).valueToJava2D( x, dataArea, chart.getXYPlot().getDomainAxisEdge() );
        double transY1 = chart.getXYPlot().getRangeAxisForDataset( 0 ).valueToJava2D( y, dataArea, chart.getXYPlot().getRangeAxisEdge() );
        Point2D.Double pt = new Point2D.Double( transX1, transY1 );
        return pt;
    }
}
