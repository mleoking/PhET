/* Copyright 2004, Sam Reid */
package org.reid.particles.view;

import edu.colorado.phet.common.view.components.ModelSlider;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.piccolo.pswing.PSwing;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.util.PFixedWidthStroke;
import org.reid.particles.model.Particle;
import org.reid.particles.model.ParticleModel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;

/**
 * User: Sam Reid
 * Date: Aug 10, 2005
 * Time: 12:17:10 AM
 * Copyright (c) Aug 10, 2005 by Sam Reid
 */

public class ParticlePanel extends PhetPCanvas {
    private ParticleModel particleModel;
    private boolean showInteractionRadius = true;
    private PNode influenceLayer;
    private PNode particleLayer;
    private boolean showParticles = true;

    public ParticlePanel( final ParticleModel model ) {
        this.particleModel = model;

        PPath path = new PPath( new Rectangle2D.Double( 0, 0, model.getBoxWidth(), model.getBoxHeight() ) );
        path.setPaint( null );
        path.setStrokePaint( Color.black );
        path.setStroke( new PFixedWidthStroke( 2 ) );
        addChild( path );

        influenceLayer = new PNode();
        for( int i = 0; i < model.numParticles(); i++ ) {
            Particle p = model.particleAt( i );
            ParticleInfluenceGraphic particleGraphic = new ParticleInfluenceGraphic( this, p );

            influenceLayer.addChild( particleGraphic );
        }
        addChild( influenceLayer );

        particleLayer = new PNode();
        for( int i = 0; i < model.numParticles(); i++ ) {
            Particle p = model.particleAt( i );
            ParticleGraphic particleGraphic = new ParticleGraphic( this, p );
            particleLayer.addChild( particleGraphic );
        }
        addChild( particleLayer );


        final ModelSlider randomnessSlider = new ModelSlider( "Randomness", "radians", 0, Math.PI * 2, model.getAngleRandomness(), new DecimalFormat( "0.00" ) );
        randomnessSlider.setModelTicks( new double[]{0, Math.PI, Math.PI * 2} );
        randomnessSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                model.setAngleRandomness( randomnessSlider.getValue() );
            }
        } );

        PSwing randomnessGraphic = new PSwing( this, randomnessSlider );
        randomnessGraphic.setOffset( path.getFullBounds().getWidth(), 0 );
        addChild( randomnessGraphic );

        final ModelSlider radiusSlider = new ModelSlider( "Interaction radius", "", 0, 200, model.getRadius(), new DecimalFormat( "0.00" ) );
        radiusSlider.setModelTicks( new double[]{0, 100, 200} );
        radiusSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                model.setRadius( radiusSlider.getValue() );
            }
        } );

        PSwing radiusGraphic = new PSwing( this, radiusSlider );
        radiusGraphic.setOffset( path.getFullBounds().getWidth(), randomnessGraphic.getFullBounds().getMaxY() );
        addChild( radiusGraphic );
//        getLayer().setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF );

        final JCheckBox factorOut = new JCheckBox( "Factor Out Net Motion", model.isFactorOutNetMovement() );
        factorOut.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                model.setFactorOutNetMovement( factorOut.isSelected() );
            }
        } );
        PSwing boxSwing = new PSwing( this, factorOut );
        boxSwing.setOffset( radiusGraphic.getFullBounds().getX(), radiusGraphic.getFullBounds().getMaxY() );
        addChild( boxSwing );

        final JCheckBox showInteractionRadius = new JCheckBox( "Show Interaction Radius", isShowInteractionRadius() );
        showInteractionRadius.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                setShowInteractionRadius( showInteractionRadius.isSelected() );
            }
        } );
        PSwing interactionRadiusGraphic = new PSwing( this, showInteractionRadius );
        interactionRadiusGraphic.setOffset( boxSwing.getFullBounds().getX(), boxSwing.getFullBounds().getMaxY() );
        addChild( interactionRadiusGraphic );

        final JCheckBox showParticles= new JCheckBox( "Show Particles", isShowParticles());
        showParticles.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                setShowParticles( showParticles.isSelected() );
            }
        } );
        PSwing showParticleGraphic= new PSwing( this, showParticles );
        showParticleGraphic.setOffset( interactionRadiusGraphic.getFullBounds().getX(), interactionRadiusGraphic.getFullBounds().getMaxY() );
        addChild( showParticleGraphic);
    }

    private boolean isShowParticles() {
        return showParticles;
    }

    private void setShowInteractionRadius( boolean selected ) {
        if( showInteractionRadius != selected ) {
            this.showInteractionRadius = selected;
            if( showInteractionRadius && !getLayer().isAncestorOf( influenceLayer ) ) {
                addChild( 0, influenceLayer );
            }
            else if( !showInteractionRadius && getLayer().isAncestorOf( influenceLayer ) ) {
                removeChild( influenceLayer );
            }
        }
    }

    private void setShowParticles( boolean selected ) {
        if( showParticles != selected ) {
            this.showParticles = selected;
            if( showParticles && !getLayer().isAncestorOf( particleLayer ) ) {
                addChild( particleLayer );
            }
            else if( !showParticles && getLayer().isAncestorOf( particleLayer ) ) {
                removeChild( particleLayer );
            }
        }
    }

    private boolean isShowInteractionRadius() {
        return showInteractionRadius;
    }

    public ParticleModel getParticleModel() {
        return particleModel;
    }
}
