// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.selfdrivenparticlemodel.experiment;

import java.util.ArrayList;

import javax.swing.*;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import edu.colorado.phet.selfdrivenparticlemodel.model.ParticleModel;
import edu.colorado.phet.selfdrivenparticlemodel.view.ParticleApplication;
import edu.umd.cs.piccolo.activities.PActivity;

public class ChartExperiment {
    private ParticleApplication particleApplication;
    private XYSeriesCollection xySeriesCollection;
    private XYPlot xyPlot;
    private JFreeChart chr;
    private ChartPanel chartPanel;
    private JFrame frame;
    private XYSeries meanSeries;
    private NumberAxis domainAxis;
    private NumberAxis rangeAxis;
    private XYLineAndShapeRenderer renderer;
    private PActivity activity;
    private boolean firstTime = true;
    private int numRuns = 5;
    private int experimentTime = 3000;
    private XYSeries rawSeries;
    private ParticleModel.Adapter listener;
    private int num = 0;
    private double sum = 0.0;
    private long lastTimeStep;

    public ChartExperiment( ParticleApplication particleApplication, String seriesName,
                            String domainName, String rangeName, String title ) {
        this.particleApplication = particleApplication;

        meanSeries = new XYSeries( seriesName );
        rawSeries = new XYSeries( "Data Points" );
        xySeriesCollection = new XYSeriesCollection( rawSeries );
        xySeriesCollection.addSeries( meanSeries );
        domainAxis = new NumberAxis( domainName );
        rangeAxis = new NumberAxis( rangeName );
        renderer = new XYLineAndShapeRenderer();
        int MEAN_SERIES = 1;
        int RAW_SERIES = 0;
        renderer.setSeriesLinesVisible( RAW_SERIES, false );
        renderer.setSeriesLinesVisible( MEAN_SERIES, true );
        renderer.setSeriesShapesVisible( RAW_SERIES, true );
        renderer.setSeriesShapesVisible( MEAN_SERIES, false );

        xyPlot = new XYPlot( xySeriesCollection,
                             domainAxis,
                             rangeAxis, renderer );
        chr = new JFreeChart( title, xyPlot );
        frame = new JFrame();
        chartPanel = new ChartPanel( chr );
        frame.setContentPane( chartPanel );
        frame.setSize( 800, 800 );

        rangeAxis.setAutoRange( false );
        domainAxis.setAutoRange( false );
        rangeAxis.setRange( -0.01, 1.01 );
        domainAxis.setRange( -0.01, Math.PI * 2 + 0.01 );

        PActivity activity = new PActivity( -1, experimentTime ) {
            protected void activityStep( long elapsedTime ) {
                step( elapsedTime );
            }
        };
        this.activity = activity;
        listener = new ParticleModel.Adapter() {
            public void steppedInTime() {
                if ( passedTransient() ) {
                    sum += getOrderParameter();
                    num++;
                }
            }
        };
    }

    private boolean passedTransient() {
        long dtMS = System.currentTimeMillis() - lastTimeStep;
        if ( dtMS > experimentTime / 2 ) {
            return true;
        }
        return false;
    }

    public void start() {
        particleApplication.getParticlePanel().getRoot().addActivity( this.activity );
        frame.show();
        particleApplication.getParticleModel().addListener( listener );
    }

    private void step( long elapsedTime ) {
        this.lastTimeStep = System.currentTimeMillis();
        if ( firstTime ) {
            particleApplication.getParticleModel().setRandomness( 0.0 );
            particleApplication.getParticleModel().randomize();
            firstTime = false;
        }
        else {
            recordData( elapsedTime );
            nextExperiment( elapsedTime );
        }
    }

    private ArrayList readings = new ArrayList();

    private void recordData( long elapsedTime ) {
        double va = sum / num;
        double randomness = particleApplication.getParticleModel().getRandomness();
        readings.add( new Double( va ) );
        rawSeries.add( randomness, va );
        if ( readings.size() >= numRuns ) {
            double sum = 0;
            for ( int i = 0; i < readings.size(); i++ ) {
                Double aDouble = (Double) readings.get( i );
                sum += aDouble.doubleValue();
            }
            double avg = sum / readings.size();
            meanSeries.add( randomness, avg );
        }

        rangeAxis.configure();
        domainAxis.configure();
        sum = 0;
        num = 0;
    }

    private double getOrderParameter() {
        double va = particleApplication.getParticleModel().getOrderParameter();
        return va;
    }

    private void nextExperiment( long elapsedTime ) {
        double randomness = particleApplication.getParticleModel().getRandomness();
        double newRandomness = randomness;
        if ( readings.size() >= numRuns ) {
            newRandomness = randomness + Math.PI / 12;
            readings.clear();
        }
        if ( newRandomness > Math.PI * 2 ) {
            terminate();
        }
        else {
            particleApplication.getParticleModel().setRandomness( newRandomness );
            particleApplication.getParticleModel().randomize();
        }
    }

    private void terminate() {
        activity.terminate();
        particleApplication.getParticleModel().removeListener( listener );
    }
}
