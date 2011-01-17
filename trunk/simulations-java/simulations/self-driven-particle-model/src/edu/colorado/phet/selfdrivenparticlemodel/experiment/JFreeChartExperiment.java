// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.selfdrivenparticlemodel.experiment;

import javax.swing.*;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import edu.colorado.phet.selfdrivenparticlemodel.view.ParticleApplication;
import edu.umd.cs.piccolo.activities.PActivity;

public class JFreeChartExperiment {
    private long lastTime = 0;
    private ParticleApplication particleApplication;
    private XYSeriesCollection xySeriesCollection;
    private XYPlot xyPlot;
    private JFreeChart chr;
    private ChartPanel chartPanel;
    private JFrame frame;
    private XYSeries series;
    private NumberAxis domainAxis;
    private NumberAxis rangeAxis;
    private XYItemRenderer renderer;
    private PActivity activity;

    public JFreeChartExperiment( ParticleApplication particleApplication ) {
//        JFreeChart chart= ChartFactory.createLineChart( );
        this.particleApplication = particleApplication;

        series = new XYSeries( new Integer( 0 ) );
        xySeriesCollection = new XYSeriesCollection( series );
        domainAxis = new NumberAxis( "domain" );
        rangeAxis = new NumberAxis( "range" );
//        renderer = new XYDotRenderer();
//        renderer = new XYAreaRenderer2( );
        renderer = new StandardXYItemRenderer();
        xyPlot = new XYPlot( xySeriesCollection,
                             domainAxis,
                             rangeAxis, renderer );
        chr = new JFreeChart( "Title", xyPlot );
        frame = new JFrame();
        chartPanel = new ChartPanel( chr );
        frame.setContentPane( chartPanel );
        frame.setSize( 800, 800 );


        rangeAxis.setAutoRange( true );
        rangeAxis.setAutoRangeIncludesZero( false );
        domainAxis.setAutoRange( true );
        domainAxis.setAutoRangeIncludesZero( false );

//        particleApplication.getParticlePanel().getRoot().addActivity( new PActivity( 5000, 500 ) {
        PActivity activity = new PActivity( -1, 5000 ) {
            protected void activityStep( long elapsedTime ) {
                takeData( elapsedTime );
            }

        };
        this.activity = activity;
    }

    public void start() {
        particleApplication.getParticlePanel().getRoot().addActivity( this.activity );
        frame.show();
    }

    private void takeData( long elapsedTime ) {
        double va = particleApplication.getParticleModel().getOrderParameter();
//        series.add( elapsedTime / 1000.0, va );
        double randomness = particleApplication.getParticleModel().getRandomness();
        particleApplication.getParticleModel().randomize();
        double angleRandomness = randomness - Math.PI / 12;
        if ( angleRandomness < 0 ) {
            activity.terminate();
        }
        particleApplication.getParticleModel().setRandomness( angleRandomness );

        series.add( randomness, va );
        rangeAxis.configure();
        domainAxis.configure();
    }

}
