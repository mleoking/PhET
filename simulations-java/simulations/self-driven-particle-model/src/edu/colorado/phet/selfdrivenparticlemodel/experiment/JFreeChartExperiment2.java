// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.selfdrivenparticlemodel.experiment;

import edu.colorado.phet.selfdrivenparticlemodel.model.ParticleModel;
import edu.colorado.phet.selfdrivenparticlemodel.view.ParticleApplication;
import edu.umd.cs.piccolo.activities.PActivity;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.util.ArrayList;

public class JFreeChartExperiment2 {
    private long lastTime = 0;
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

    public JFreeChartExperiment2( ParticleApplication particleApplication, String seriesName,
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

//        renderer.setSeriesOutlinePaint( RAW_SERIES, null );
//        renderer.setSeriesPaint( MEAN_SERIES, Color.red );
//        renderer.setSeriesCreateEntities( RAW_SERIES, new Boolean( true ) );
//        renderer.setSeriesFillPaint( RAW_SERIES, Color.green);
//        renderer.setSeriesShapesFilled( RAW_SERIES, new Boolean( true ) );
//        renderer.setSeriesShape( RAW_SERIES, new Rectangle(0,0,15,15));
//        renderer.setSeriesPaint( RAW_SERIES, Color.blue );

//        StandardXYItemRenderer standy = new StandardXYItemRenderer();

        xyPlot = new XYPlot( xySeriesCollection,
                             domainAxis,
                             rangeAxis, renderer );
        chr = new JFreeChart( title, xyPlot );
        frame = new JFrame();
        chartPanel = new ChartPanel( chr );
        frame.setContentPane( chartPanel );
        frame.setSize( 800, 800 );

//        rangeAxis.setAutoRange( true );
//        rangeAxis.setAutoRangeIncludesZero( false );
//        domainAxis.setAutoRange( true );
//        domainAxis.setAutoRangeIncludesZero( false );

        rangeAxis.setAutoRange( false );
//        rangeAxis.setAutoRangeIncludesZero( false );
        domainAxis.setAutoRange( false );
//        domainAxis.setAutoRangeIncludesZero( false );
        rangeAxis.setRange( -0.01, 1.01 );
        domainAxis.setRange( -0.01, Math.PI * 2 + 0.01 );

//        particleApplication.getParticlePanel().getRoot().addActivity( new PActivity( 5000, 500 ) {
        PActivity activity = new PActivity( -1, experimentTime ) {
            protected void activityStep( long elapsedTime ) {
                step( elapsedTime );
            }
        };
        this.activity = activity;
        listener = new ParticleModel.Adapter() {
            public void steppedInTime() {
//                super.steppedInTime();
                if( passedTransient() ) {
                    sum += getOrderParameter();
                    num++;
                }
            }


        };
    }

    private boolean passedTransient() {
        long dtMS = System.currentTimeMillis() - lastTimeStep;
        if( dtMS > experimentTime / 2 ) {
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
        if( firstTime ) {
//            setupExperiment( elapsedTime );
            particleApplication.getParticleModel().setRandomness( 0.0 );
            particleApplication.getParticleModel().randomize();
            firstTime = false;
        }
        else {
            recordData( elapsedTime );
            nextExperiment( elapsedTime );
        }
    }

    static class Data {
        ArrayList readings = new ArrayList();
    }

    ArrayList readings = new ArrayList();

    private void recordData( long elapsedTime ) {
        double va = sum / num;
//        System.out.println( "sum = " + sum+", num="+num+", va="+va );

//        double va = getOrderParameter();
        double randomness = particleApplication.getParticleModel().getRandomness();
//        System.out.println( "recording @va = " + va + ", randomness=" + randomness );
        readings.add( new Double( va ) );
        rawSeries.add( randomness, va );
        if( readings.size() >= numRuns ) {
            double sum = 0;
            for( int i = 0; i < readings.size(); i++ ) {
                Double aDouble = (Double)readings.get( i );
                sum += aDouble.doubleValue();
            }
            double avg = sum / readings.size();
//            System.out.println( "va=" + va + ", readings = " + readings + ": avg=" + avg );
            meanSeries.add( randomness, avg );
//            readings.clear();
        }
//        else {
//            rawSeries.add( randomness, va );
//        }

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
        if( readings.size() >= numRuns ) {
            newRandomness = randomness + Math.PI / 12;
            readings.clear();
        }
        if( newRandomness > Math.PI * 2 ) {
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
