/* Copyright 2004, Sam Reid */
package edu.colorado.phet.selfdrivenparticlemodel.view;

import edu.colorado.phet.common.phetcommon.view.HorizontalLayoutPanel;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.selfdrivenparticlemodel.experiment.JFreeChartExperiment2;
import edu.colorado.phet.selfdrivenparticlemodel.model.Particle;
import edu.colorado.phet.selfdrivenparticlemodel.model.ParticleModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PActivity;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.pswing.PSwing;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * User: Sam Reid, test comment
 * Date: Aug 10, 2005
 * Time: 12:17:10 AM
 * Copyright (c) Aug 10, 2005 by Sam Reid
 */

public class ParticlePanel extends PhetPCanvas {
    private ParticleModel particleModel;
    private ParticleApplication particleApplication;
    private boolean showInteractionRadius = true;
    private PNode influenceLayer;
    private PNode particleLayer;
    private boolean showParticles = true;
    private ArrayList influenceGraphics = new ArrayList();
    private ArrayList particleGraphics = new ArrayList();

    public ParticlePanel( final ParticleModel model, final ParticleApplication particleApplication ) {
        this.particleModel = model;
        this.particleApplication = particleApplication;

        UniverseGraphic universeGraphic = new UniverseGraphic( particleModel );
        addScreenChild( universeGraphic );

        influenceLayer = new PNode();

        addScreenChild( influenceLayer );

        particleLayer = new PNode();
        for( int i = 0; i < model.numParticles(); i++ ) {
            Particle p = model.particleAt( i );
            addParticleGraphic( p );
        }
        addScreenChild( particleLayer );

        for( int i = 0; i < model.numParticles(); i++ ) {
            Particle p = model.particleAt( i );
            addGraphic( p );
        }

        final RandomnessSlider randomnessSlider = new RandomnessSlider( particleApplication.getParticleModel() );
        PSwing randomnessGraphic = new PSwing( this, randomnessSlider );
        randomnessGraphic.setOffset( universeGraphic.getFullBounds().getWidth(), 0 );
        addScreenChild( randomnessGraphic );

        InteractionRadiusControl interactionRadiusControl = new InteractionRadiusControl( model );
        PSwing radiusGraphic = new PSwing( this, interactionRadiusControl );
        radiusGraphic.setOffset( universeGraphic.getFullBounds().getWidth(), randomnessGraphic.getFullBounds().getMaxY() );
        addScreenChild( radiusGraphic );
//        getLayer().setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF );

        final JCheckBox factorOut = new JCheckBox( "Factor Out Net Motion", model.isFactorOutNetMovement() );
        factorOut.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                model.setFactorOutNetMovement( factorOut.isSelected() );
            }
        } );
        PSwing boxSwing = new PSwing( this, factorOut );
        boxSwing.setOffset( radiusGraphic.getFullBounds().getX(), radiusGraphic.getFullBounds().getMaxY() );
        addScreenChild( boxSwing );

        final JCheckBox showInteractionRadius = new JCheckBox( "Show Interaction Radius", isShowInteractionRadius() );
        showInteractionRadius.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                setShowInteractionRadius( showInteractionRadius.isSelected() );
            }
        } );
        PSwing interactionRadiusGraphic = new PSwing( this, showInteractionRadius );
        interactionRadiusGraphic.setOffset( boxSwing.getFullBounds().getX(), boxSwing.getFullBounds().getMaxY() );
        addScreenChild( interactionRadiusGraphic );

        final JCheckBox showParticles = new JCheckBox( "Show Particles", isShowParticles() );
        showParticles.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                setShowParticles( showParticles.isSelected() );
            }
        } );
        PSwing showParticleGraphic = new PSwing( this, showParticles );
        showParticleGraphic.setOffset( interactionRadiusGraphic.getFullBounds().getX(), interactionRadiusGraphic.getFullBounds().getMaxY() );
        addScreenChild( showParticleGraphic );

        HorizontalLayoutPanel speedPanel = new HorizontalLayoutPanel();
        speedPanel.add( new JLabel( "Speed" ) );

        final JSpinner speedSpinner = new JSpinner( new SpinnerNumberModel( model.getSpeed(), 0, 10, 0.1 ) );
        speedSpinner.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                model.setSpeed( ( (Number)speedSpinner.getValue() ).doubleValue() );
            }
        } );
        speedPanel.add( speedSpinner );
        PSwing speedGraphic = new PSwing( this, speedPanel );
        speedGraphic.setOffset( showParticleGraphic.getFullBounds().getX(), showParticleGraphic.getFullBounds().getMaxY() );
        addScreenChild( speedGraphic );

        NumberSliderPanel numberSliderPanel = new NumberSliderPanel( particleApplication );
        PSwing numGraphic = new PSwing( this, numberSliderPanel );
        numGraphic.setOffset( speedGraphic.getFullBounds().getX(), speedGraphic.getFullBounds().getMaxY() );
        addScreenChild( numGraphic );


        final PText orderParamText = new PText();
        final DecimalFormat decimalFormat = new DecimalFormat( "0.00" );
        getRoot().addActivity( new PActivity( -1 ) {
            protected void activityStep( long elapsedTime ) {
                super.activityStep( elapsedTime );
                orderParamText.setText( "va= " + decimalFormat.format( model.getOrderParameter() ) );
            }
        } );
        addScreenChild( orderParamText );
//

        final PText clusterCountText = new PText();
        getRoot().addActivity( new PActivity( -1 ) {
            protected void activityStep( long elapsedTime ) {
                super.activityStep( elapsedTime );
                clusterCountText.setText( "# Clusters= " + ( model.getNumClusters() ) );
//                System.out.println( System.currentTimeMillis() + ", numClusters=" + model.getNumClusters() );
            }
        } );
        addScreenChild( clusterCountText );
        clusterCountText.setOffset( 0, orderParamText.getFullBounds().getMaxY() + 20 );

        JButton but = new JButton( "Experiment" );
        but.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
//                new JFreeChartExperiment( particleApplication ).start();
                new JFreeChartExperiment2( particleApplication, "", "Randomness", "Order Parameter", "Order Parameter vs. Randomness" ).start();
            }
        } );
        PSwing ps = new PSwing( this, but );
        ps.setOffset( numGraphic.getFullBounds().x, numGraphic.getFullBounds().getMaxY() );
        addScreenChild( ps );
    }

    public void addGraphic( Particle p ) {
        addRadiusGraphic( p );
        addParticleGraphic( p );
    }

    private void addRadiusGraphic( Particle p ) {
        ParticleInfluenceGraphic particleGraphic = new ParticleInfluenceGraphic( getParticleModel(), p );
        influenceLayer.addChild( particleGraphic );
        influenceGraphics.add( particleGraphic );
    }

    private void addParticleGraphic( Particle p ) {
//            ParticleGraphic particleGraphic = new ParticleGraphic( this, p );
//            ParticleGraphicWithTail particleGraphic = new ParticleGraphicWithTail( this, p ,new double[]{8,6,3,2,1});
//            ParticleGraphicWithTail particleGraphic = new ParticleGraphicWithTail( this, p ,new double[]{8,7,6,5,4,3,2});
        ParticleGraphicWithTail particleGraphic = new ParticleGraphicWithTail( p, new double[]{8, 7, 6, 5, 4}, 1 );
//            ParticleGraphicWithTail particleGraphic = new ParticleGraphicWithTail( this, p ,new double[]{8,8,7,7,6,6,5,5},2);
        particleLayer.addChild( particleGraphic );
        particleGraphics.add( particleGraphic );
    }

    private boolean isShowParticles() {
        return showParticles;
    }

    private void setShowInteractionRadius( boolean selected ) {
        if( showInteractionRadius != selected ) {
            this.showInteractionRadius = selected;
            if( showInteractionRadius && !getLayer().isAncestorOf( influenceLayer ) ) {
                addScreenChild( 0, influenceLayer );
            }
            else if( !showInteractionRadius && getLayer().isAncestorOf( influenceLayer ) ) {
                removeScreenChild( influenceLayer );
            }
        }
    }

    private void setShowParticles( boolean selected ) {
        if( showParticles != selected ) {
            this.showParticles = selected;
            if( showParticles && !getLayer().isAncestorOf( particleLayer ) ) {
                addScreenChild( particleLayer );
            }
            else if( !showParticles && getLayer().isAncestorOf( particleLayer ) ) {
                removeScreenChild( particleLayer );
            }
        }
    }

    private boolean isShowInteractionRadius() {
        return showInteractionRadius;
    }

    public ParticleModel getParticleModel() {
        return particleModel;
    }

    public void removeParticle( Particle particle ) {
        for( int i = 0; i < influenceGraphics.size(); i++ ) {
            ParticleInfluenceGraphic particleInfluenceGraphic = (ParticleInfluenceGraphic)influenceGraphics.get( i );
            if( particleInfluenceGraphic.getParticle() == particle ) {
                influenceGraphics.remove( particleInfluenceGraphic );
                influenceLayer.removeChild( particleInfluenceGraphic );
                i--;
            }
        }
        for( int i = 0; i < particleGraphics.size(); i++ ) {
            ParticleGraphicWithTail particleGraphicWithTail = (ParticleGraphicWithTail)particleGraphics.get( i );
            if( particleGraphicWithTail.getParticle() == particle ) {
                particleGraphics.remove( particleGraphicWithTail );
                particleLayer.removeChild( particleGraphicWithTail );
                i--;
            }
        }
    }
}
