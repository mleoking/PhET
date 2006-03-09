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

public class TestBenFold {
    Schrodinger schrodinger = new Schrodinger( new Quadratic( 0.25 ) );
    double intRange = 20;
    int steps = 2000;

    public TestBenFold() {
    }

    public double solve( double min, double max ) {
        ShootingMethod solver = new ShootingMethod( schrodinger, min, max, intRange, steps );
        solver.run();

//        System.out.println( "solveThread.getEstimate() = " + solver.getEstimate() );
        double goodness = solver.tryEnergy( solver.getEstimate() );
//        System.out.println( "goodness = " + goodness );

        return solver.getEstimate();
    }

    public void plot( double energy ) {
        //        eqn.firstValue = 5.0;
        schrodinger.setEnergy( energy );
        double []soln = new double[steps];
        double step = intRange / steps;
        schrodinger.solve( -intRange / 2, step, soln );

        XYSeries series = new XYSeries( "psi", false, true );
        XYDataset dataset = new XYSeriesCollection( series );
        JFreeChart plot = ChartFactory.createScatterPlot( "wavefunction", "x", "psi", dataset, PlotOrientation.VERTICAL, false, false, false );
        plot.getXYPlot().setRenderer( new XYLineAndShapeRenderer( true, false ) );
        ChartFrame chartFrame = new ChartFrame( "Shooting Method", plot );
        chartFrame.setSize( 800, 600 );
        normalize( soln );
        for( int i = 0; i < soln.length; i ++ ) {
            series.add( i, soln[i] );
        }
        chartFrame.setVisible( true );
    }

    private void normalize( double[] soln ) {
        double sum = 0;
        for( int i = 0; i < soln.length; i++ ) {
            sum += soln[i];
        }
        for( int i = 0; i < soln.length; i++ ) {
            soln[i] /= sum;
        }
    }

    public static void main( String[] args ) {
        new TestBenFold().start();
    }

    private void start() {
//        solve( -1, 0 );
        double e1 = solve( 0, 5 );
        plot( e1 );
        double e2 = solve( 0, 1 );
        plot( e2 );
    }

    public void energyChanged( double d ) {
    }

    public void searchFinished() {
    }
}
