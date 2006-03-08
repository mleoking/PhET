/* Copyright 2004, Sam Reid */
package edu.colorado.phet.boundstates.benfold2;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * User: Sam Reid
 * Date: Mar 8, 2006
 * Time: 3:56:49 PM
 * Copyright (c) Mar 8, 2006 by Sam Reid
 */

public class TestBenFold implements EnergyListener {

    public TestBenFold() {
    }

    public synchronized void solve( double min, double max ) {
        Schrodinger eqn = new ImprovedSchrodinger();
        double intRange = 20;

        int steps = 2000;
        ShootingMethod solveThread = new ShootingMethod( this, eqn, min, max, intRange, steps );
        solveThread.run();

        double []soln = new double[steps];
        double step = intRange / steps;
        double minX = 0;
        //	Get function
        System.out.println( "solveThread.getEstimate() = " + solveThread.getEstimate() );
        eqn.setEnergy( solveThread.getEstimate() );
        eqn.firstValue = 1.0;
        eqn.solve( minX, step, soln );

        XYSeries series = new XYSeries( "psi", false, true );
        XYDataset dataset = new XYSeriesCollection( series );
        JFreeChart plot = ChartFactory.createScatterPlot( "wavefunction", "x", "psi", dataset, PlotOrientation.VERTICAL, false, false, false );
        plot.getXYPlot().setRenderer( new XYLineAndShapeRenderer( true, false ) );
        ChartFrame chartFrame = new ChartFrame( "Shooting Method", plot );
        chartFrame.setSize( 800, 600 );
        for( int i = 0; i < soln.length; i ++ ) {
            series.add( i, soln[i] );
        }
        chartFrame.setVisible( true );
        for( int i = 0; i < soln.length; i++ ) {
            System.out.println( "soln[i] = " + soln[i] );

        }
    }

    public static void main( String[] args ) {
        new TestBenFold().start();
    }

    private void start() {
        solve( -10, 10 );
    }

    public void energyChanged( double d ) {
//        System.out.println( "d = " + d );
    }

    public void searchFinished() {
    }
}
