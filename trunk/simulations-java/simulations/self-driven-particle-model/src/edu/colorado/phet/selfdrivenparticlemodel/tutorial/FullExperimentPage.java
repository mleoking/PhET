// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.selfdrivenparticlemodel.tutorial;

import edu.colorado.phet.selfdrivenparticlemodel.model.ParticleModel;
import edu.colorado.phet.selfdrivenparticlemodel.tutorial.unit2.OrderParameterVsTimeChart;
import edu.colorado.phet.selfdrivenparticlemodel.tutorial.unit2.PlotBeta240;
import edu.umd.cs.piccolo.activities.PActivity;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.pswing.PSwing;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;

public class FullExperimentPage extends PlotBeta240 {
    private PButton plotButton;
    private OrderParameterVsTimeChart orderParameterVsTimeChart;
    private TutorialChartFrame tutorialChartFrame;
    private ParticleModel.Adapter listener;
    private PText clusterCountGraphic;
    private PSwing clusterCheckbox;
    private PActivity clusterCountActivity;
    private PButton randomize;
    private PButton applauseButton;

    public FullExperimentPage( BasicTutorialCanvas page ) {
        super( page );
        setText( "On this page, you can experiment with the Self-Driven Particle Model as you wish.  " +
                 "Here you can have up to 200 particles, display a cluster # indicator, and easily reset the particles.\n\n" +
                 "This page concludes the tutorial, so you are entitled to applause." );
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

        final JCheckBox jcb = new JCheckBox( "Show Cluster Count", false );
        jcb.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if( jcb.isSelected() ) {
                    getParticleModel().setComputeClusterCount( true );
                    addChild( clusterCountGraphic );
                    getBasePage().getRoot().addActivity( clusterCountActivity );
                }
                else {
                    getParticleModel().setComputeClusterCount( false );
                    removeChild( clusterCountGraphic );
                    getBasePage().getRoot().getActivityScheduler().removeActivity( clusterCountActivity );
                }
            }
        } );
        clusterCheckbox = new PSwing( page, jcb );
        clusterCountGraphic = new PText( "" );
        clusterCountGraphic.setOffset( getUniverseGraphic().getFullBounds().getX() + 4, getUniverseGraphic().getFullBounds().getY() + 4 );

        clusterCountActivity = new PActivity( -1 ) {
            protected void activityStep( long elapsedTime ) {
                super.activityStep( elapsedTime );
                clusterCountGraphic.setText( "# Clusters: " + getParticleModel().getNumClusters() );
            }
        };
        getNumberSliderPanel().setMaxNumber( 200 );
        getNumberSliderPanel().getModelSlider().setModelTicks( new double[]{0, 50, 100, 150, 200} );

        randomize = new PButton( page, "Randomize" );
        randomize.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                randomize();
            }
        } );

        applauseButton = new PButton( page, "Applause" );
        applauseButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                playApplause();
            }
        } );
    }

    void randomize() {
        getParticleModel().randomize();
    }

    private void showPlot() {
        tutorialChartFrame.setVisible( true );
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

    protected void showNextButton() {
    }

    public void init() {
        super.init();
        plotButton.setOffset( getUniverseGraphic().getFullBounds().getX(), getUniverseGraphic().getFullBounds().getMaxY() );
        addChild( plotButton );
        clusterCheckbox.setOffset( plotButton.getFullBounds().getMaxX(), plotButton.getFullBounds().getY() );
        addChild( clusterCheckbox );

        randomize.setOffset( clusterCheckbox.getFullBounds().getMaxX(), clusterCheckbox.getFullBounds().getY() );
        addChild( randomize );

        applauseButton.setOffset( randomize.getFullBounds().getMaxX(), randomize.getFullBounds().getY() );
        addChild( applauseButton );
        playApplause();
    }

    protected void showFinishText() {
    }

    protected void append( String finishText ) {
    }

    public void teardown() {
        super.teardown();
        removeChild( plotButton );
        removeChild( clusterCheckbox );
        removeChild( randomize );
        removeChild( applauseButton );
    }

    protected void advance() {
    }
}
