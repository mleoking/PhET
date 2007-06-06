/* Copyright 2004, Sam Reid */
package org.reid.particles.tutorial.unit2;

import edu.umd.cs.piccolo.PNode;
import org.reid.particles.model.ParticleModel;
import org.reid.particles.tutorial.unit2.OrderParameter90;
import org.reid.particles.tutorial.unit2.OrderParameterVsTimeChart;
import org.reid.particles.tutorial.PButton;
import org.reid.particles.tutorial.TutorialChartFrame;
import org.reid.particles.tutorial.BasicTutorialCanvas;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Aug 25, 2005
 * Time: 11:27:24 PM
 * Copyright (c) Aug 25, 2005 by Sam Reid
 */

public class PlotOrderParameterVsTime extends OrderParameter90 {
    private PButton plotButton;
    private OrderParameterVsTimeChart orderParameterVsTimeChart;
    private TutorialChartFrame tutorialChartFrame;
    private ParticleModel.Adapter listener;
    public static final long MOD = 5;

    public PlotOrderParameterVsTime( BasicTutorialCanvas page ) {
        super( page );
        setText( "Let's plot this data.  Create a graph, then manipulate values while the chart is running to observe the effects on the order parameter." );
        setFinishText( "\n Notice how it takes the order parameter some time to adjust to changes in model parameters." );
        plotButton = new PButton( page, "Plot Order Parameter vs Time" );
        plotButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                showPlot();
                startDataTaking();
                advance();
            }

        } );
        orderParameterVsTimeChart = new OrderParameterVsTimeChart( (int)( 500 / MOD ) );
        tutorialChartFrame = new TutorialChartFrame( "Plot", orderParameterVsTimeChart.getChart(), getBasePage().getTutorialApplication().getTutorialFrame() );
        listener = new ParticleModel.Adapter() {
            public void steppedInTime() {
                if( getParticleModel().getTime() % MOD == 0 ) {
                    super.steppedInTime();
                    sampleData();
                }
            }
        };
    }

    Point2D.Double sampleDataValue() {
        double t = getParticleModel().getTime();
        double orderParameter = getParticleModel().getOrderParameter();
        return new Point2D.Double( t, orderParameter );
    }

    private void sampleData() {
        Point2D sample = sampleDataValue();
        orderParameterVsTimeChart.addDataPoint( sample.getX(), sample.getY() );
    }

    private void startDataTaking() {
        getParticleModel().resetTime();
        getParticleModel().addListener( listener );
    }

    private void stopTakingData() {
        getParticleModel().removeListener( listener );
    }

    protected boolean isOrderParamaterAwesome() {
        return false;
    }

    public void init() {
        super.init();
        plotButton.setOffset( getLocationBeneath( getOrderParamText() ) );
        addChild( plotButton );
    }

    public void teardown() {
        super.teardown();
        removeChild( plotButton );
        tutorialChartFrame.setVisible( false );
        stopTakingData();
    }

    public Point2D getLocationBeneath( PNode node ) {
        return new Point2D.Double( node.getFullBounds().getX(), node.getFullBounds().getMaxY() + getDy() );
    }

    private void showPlot() {
        tutorialChartFrame.setVisible( true );
    }
}
