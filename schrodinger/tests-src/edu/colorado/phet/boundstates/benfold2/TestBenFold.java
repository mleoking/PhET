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

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Mar 8, 2006
 * Time: 3:56:49 PM
 * Copyright (c) Mar 8, 2006 by Sam Reid
 */

public class TestBenFold {
    Schrodinger schrodinger = new Schrodinger( new Quadratic( 0.25 ) );
    double intRange = 20;
    int steps = 1000;

    public TestBenFold() {
    }

    static class Solution {
        double value;
        double goodness;

        public Solution( double value, double goodness ) {
            this.value = value;
            this.goodness = goodness;
        }

        public double getValue() {
            return value;
        }

        public double getGoodness() {
            return goodness;
        }

        public String toString() {
            return "value=" + value + ", goodness=" + goodness;
        }
    }

    public Solution solve( double min, double max ) {
        ShootingMethod solver = new ShootingMethod( schrodinger, min, max, intRange, steps, min );
        solver.run();

        double value = solver.getEstimate();
        double goodness = Math.abs( solver.tryEnergy( value ) );
        return new Solution( value, goodness );
    }

    public void plot( double energy ) {
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
            sum += Math.abs( soln[i] );
        }
        for( int i = 0; i < soln.length; i++ ) {
            soln[i] /= sum;
        }
    }

    public static void main( String[] args ) {
        new TestBenFold().start();
    }

    private double[]getEnergies( double min, double windowSize, int numToGet, int maxIterations ) {
        ArrayList energies = new ArrayList();
        double epsilon = 0.0001;
        double max = min + windowSize;
        double goodnessThreshold = 6E4;
        int iteration = 0;
        while( energies.size() < numToGet && iteration < maxIterations ) {
            Solution e1 = solve( min, max );

            if( e1.getGoodness() < goodnessThreshold ) {
                System.out.println( "e1 = " + e1 );
                energies.add( new Double( e1.getValue() ) );
                min = e1.getValue() + epsilon;
                windowSize /= 2;
            }
            else {
                min = max;
                windowSize *= 2;
            }
            max = min + windowSize;
            iteration++;
        }
        double[]values = new double[energies.size()];
        for( int i = 0; i < energies.size(); i++ ) {
            values[i] = ( (Number)energies.get( i ) ).doubleValue();
        }
        return values;
    }

    private void start() {
        double[] energies = getEnergies( 0, 0.2, 10, 1000 );
        for( int i = 0; i < energies.length; i++ ) {
            double energy = energies[i];
            System.out.println( "energy = " + energy );
        }
        for( int i = 0; i < energies.length; i++ ) {
            double energy = energies[i];
            plot( energy );
        }
    }

    public void energyChanged( double d ) {
    }

    public void searchFinished() {
    }
}
