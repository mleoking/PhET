/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3;

import edu.colorado.phet.common.model.clock.ClockAdapter;
import edu.colorado.phet.common.model.clock.ClockEvent;
import edu.colorado.phet.ec3.model.Body;
import edu.colorado.phet.ec3.plots.Range2D;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.JFreeChart;
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
import java.awt.geom.Line2D;
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
    private ArrayList peDots = new ArrayList();
    private XYSeriesCollection dataset;
    private EC3Module module;

    private XYSeries peSeries;
    private XYSeries keSeries;
    private XYSeries totSeries;
    private PImage image;
    private ChartRenderingInfo info = new ChartRenderingInfo();

    private PPath verticalBar = new PPath( new Line2D.Double( 0, 0, 0, 500 ) );
    private static final int COUNT_MOD = 10;

    public EnergyPositionPlotPanel( EC3Module ec3Module ) {
        super( new Dimension( 100, 100 ) );
        this.module = ec3Module;
        ec3Module.getClock().addClockListener( new ClockAdapter() {
            public void clockTicked( ClockEvent event ) {
                update();
            }
        } );
        dataset = createDataset();
//        chart = createChart( new Range2D( -50, -25000, 1250, 400000 * 1.25 ), dataset, "Energy vs. Position" );
        chart = createChart( new Range2D( -2, -7000 / 10.0, 17, 7000 ), dataset, "Energy vs. Position" );
        setLayout( new BorderLayout() );
        peSeries = new XYSeries( "Potential" );
        keSeries = new XYSeries( "Kinetic" );
        totSeries = new XYSeries( "Total" );
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
        verticalBar.setStroke( new BasicStroke( 1, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 1, new float[]{10, 3}, 0 ) );
        verticalBar.setStrokePaint( Color.black );
        addScreenChild( verticalBar );
    }

    private void updateImage() {
        if( getWidth() > 0 && getHeight() > 0 ) {
            image.setImage( chart.createBufferedImage( getWidth(), getHeight(), info ) );
        }
        reset();
    }

    private static JFreeChart createChart( Range2D range, XYDataset dataset, String title ) {
        JFreeChart chart = ChartFactory.createScatterPlot( title,
                                                           "Position", // x-axis label
                                                           "Energy", // y-axis label
                                                           dataset, PlotOrientation.VERTICAL, true, true, false );
        chart.setBackgroundPaint( new Color( 240, 220, 210 ) );

        XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint( Color.white );
        plot.getDomainAxis().setRange( range.getMinX(), range.getMaxX() );
        plot.getRangeAxis().setRange( range.getMinY(), range.getMaxY() );
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
        addScreenChild( verticalBar );
        peDots.clear();
    }

    int count = 0;

    private void update() {
        count++;
        if( !isActive() ) {
            return;
        }
        if( module.getEnergyConservationModel().numBodies() > 0 ) {
            Body body = module.getEnergyConservationModel().bodyAt( 0 );
            double x = toImageLocation( body.getX(), 0 ).getX();
            verticalBar.setPathTo( new Line2D.Double( x, 0, x, getHeight() ) );

            double potentialEnergy = module.getEnergyConservationModel().getPotentialEnergy( body );
//            double potentialEnergy = body.getAttachPoint().getY()*1000/2.0;
//            addFadeDot( body.getX(), potentialEnergy, module.getEnergyLookAndFeel().getPEColor() );
//            addFadeDot( body.getAttachPoint().getX(), body.getAttachPoint().getY() * 500, Color.black );
            addFadeDot( body.getX(), module.getEnergyConservationModel().getThermalEnergy(), module.getEnergyLookAndFeel().getThermalEnergyColor() );
            addFadeDot( body.getX(), potentialEnergy, module.getEnergyLookAndFeel().getPEColor() );
            addFadeDot( body.getX(), module.getEnergyConservationModel().getTotalEnergy( body ), module.getEnergyLookAndFeel().getTotalEnergyColor() );
            addFadeDot( body.getX(), body.getKineticEnergy(), module.getEnergyLookAndFeel().getKEColor() );

//            module.getEnergyConservationModel().getTotalEnergy( body )
        }
        if( count % COUNT_MOD == 0 ) {
            fadeDots();
        }

    }

    private void fadeDots() {
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

    private boolean isActive() {
        return SwingUtilities.getWindowAncestor( this ) != null && SwingUtilities.getWindowAncestor( this ).isVisible();
    }

    private void addFadeDot( double x, double y, Color peColor ) {
        FadeDot path = new FadeDot( peColor, toImageLocation( x, y ) );
        addScreenChild( path );
        peDots.add( path );
    }

    static class FadeDot extends PPath {
        private Color origColor;
        private double age;
        private double dAge = 1.3 * COUNT_MOD;
        private Color fadeColor;

        public FadeDot( Color color, Point2D loc ) {
            super( new Ellipse2D.Double( -3, -3, 6, 6 ), null );
            setPaint( color );
            setOffset( loc );
            this.origColor = color;
        }

        public void fade() {
            age += dAge;
            int fadeAlpha = (int)( 255 - age );
//            if (age>){
//                fadeAlpha=128;
//            }
//            if( age > 200 ) {
//                fadeAlpha = 0;
//            }

//
            if( fadeAlpha < 0 ) {
                fadeAlpha = 0;
            }
            Color fadeColor = new Color( origColor.getRed(), origColor.getGreen(), origColor.getBlue(),
                                         fadeAlpha );
            if( !fadeColor.equals( this.fadeColor ) ) {
                setPaint( fadeColor );
                this.fadeColor = fadeColor;
            }


        }

        public boolean isFullyFaded() {
            return fadeColor.getAlpha() <= 0;
        }
    }

    public Point2D toImageLocation( double x, double y ) {
//        info.getChartArea();
//        System.err.println( "Working here..?" );
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
