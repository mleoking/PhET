/* Copyright 2004, Sam Reid */
package edu.colorado.phet.quantumtunneling.srr;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.Timer;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import edu.colorado.phet.quantumtunneling.QTConstants;
import edu.colorado.phet.quantumtunneling.model.ConstantPotential;
import edu.colorado.phet.quantumtunneling.model.TotalEnergy;
import edu.colorado.phet.quantumtunneling.model.WavePacket;
import edu.colorado.phet.quantumtunneling.util.LightweightComplex;

/**
 * User: Sam Reid
 * Date: Mar 8, 2006
 * Time: 10:06:00 PM
 * Copyright (c) Mar 8, 2006 by Sam Reid
 */

public class BasicPlotter {
    private JFreeChart chart;                        
    private ChartFrame chartFrame;
    private XYSeries xySeries;

    public BasicPlotter() {
        xySeries = new XYSeries( "0", false, true );
        chart = ChartFactory.createScatterPlot( "Title", "x", "y", new XYSeriesCollection( xySeries ), PlotOrientation.VERTICAL, false, false, false );

        chartFrame = new ChartFrame( "Chart", chart );
        chartFrame.setSize( 800, 600 );
        chart.getXYPlot().getRangeAxis().setRange( -1, 1 );
        chart.getXYPlot().getDomainAxis().setRange( QTConstants.POSITION_RANGE );
        chart.getXYPlot().setRenderer( new XYLineAndShapeRenderer( true, false ) );
        chartFrame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    }

    public void setVisible( boolean visible ) {
        chartFrame.setVisible( visible );
    }

    public static void main( String[] args ) {
        runTest();
//        SwingUtilities.invokeLater( new Runnable() {
//            public void run() {
//                runTest();
//            }
//        } );
    }

    private static void runTest() {
        final BasicPlotter basicPlotter = new BasicPlotter();
        basicPlotter.setVisible( true );

        final WavePacket wavePacket = new WavePacket();
        wavePacket.setTotalEnergy( new TotalEnergy( 0.8 ) );
        wavePacket.setPotentialEnergy( new ConstantPotential( 0 ) );
        wavePacket.setEnabled( true );
        wavePacket.update( null, null );
        Timer timer = new Timer( 5, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {

                double[] positions = wavePacket.getPositionValues();
                LightweightComplex[] psi = wavePacket.getWaveFunctionValues();
                ArrayList data = new ArrayList();
                for( int j = 0; j < psi.length; j++ ) {
                    LightweightComplex lightweightComplex = psi[j];
                    data.add( new Point2D.Double( positions[j], lightweightComplex.getReal() ) );
                }

                basicPlotter.setData( (Point2D.Double[])data.toArray( new Point2D.Double[0] ) );
                wavePacket.propagate();
            }
        } );
        wavePacket.update( null,null);
        timer.start();
    }

    private void setData( Point2D.Double []objects ) {
        clearData();
        for( int i = 0; i < objects.length; i++ ) {
            Point2D.Double object = objects[i];
            xySeries.add( object.x, object.y, i == objects.length - 1 );
        }
        chartFrame.getContentPane().repaint();
    }

    private void clearData() {
        xySeries.clear();
    }
}
