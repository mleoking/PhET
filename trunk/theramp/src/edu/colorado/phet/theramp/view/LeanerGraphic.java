/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.view;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphicListener;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.util.BufferedImageUtils;
import edu.colorado.phet.common.view.util.FrameSequence;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.theramp.RampModule;
import edu.colorado.phet.theramp.model.RampModel;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Dec 6, 2004
 * Time: 8:53:15 AM
 * Copyright (c) Dec 6, 2004 by Sam Reid
 */

public class LeanerGraphic extends PhetImageGraphic {
    private FrameSequence animation;
    private PhetGraphic target;
    private double max = 100.0;
    private FrameSequence flippedAnimation;
    private BufferedImage standingStill;
    private RampModule module;
    private RampPanel rampPanel;

    public LeanerGraphic( final RampPanel rampPanel, final PhetGraphic target ) throws IOException {
        super( rampPanel, (BufferedImage)null );
        this.target = target;
        this.module = rampPanel.getRampModule();
        this.rampPanel = rampPanel;
        standingStill = ImageLoader.loadBufferedImage( "images/standing-man.png" );
        animation = new FrameSequence( "images/pusher-leaner-png/pusher-leaning-2", "png", 15 );
        BufferedImage[] flipped = new BufferedImage[animation.getNumFrames()];
        for( int i = 0; i < flipped.length; i++ ) {
            flipped[i] = BufferedImageUtils.flipX( animation.getFrame( i ) );
        }
        flippedAnimation = new FrameSequence( flipped );
        super.setImage( animation.getFrame( 0 ) );
        final long startTime = System.currentTimeMillis();
        target.addPhetGraphicListener( new PhetGraphicListener() {
            public void phetGraphicChanged( PhetGraphic phetGraphic ) {
                long dt = System.currentTimeMillis() - startTime;
                if( getAppliedForce() != 0 ) {
                    update( false );
                }
                if( dt < 5000 ) {
                    update( true );
                }
            }

            public void phetGraphicVisibilityChanged( PhetGraphic phetGraphic ) {
                setVisible( phetGraphic.isVisible() );
            }

        } );
        module.getRampModel().addListener( new RampModel.Listener() {
            public void appliedForceChanged() {
                update( false );
            }
        } );
        module.getRampModel().getRamp().addObserver( new SimpleObserver() {
            public void update() {
                LeanerGraphic.this.update( true );
            }
        } );
        setIgnoreMouse( true );
        update( true );

    }

    private double getAppliedForce() {
        return module.getRampModel().getAppliedForce().getParallelComponent();
    }

    private BufferedImage getFrame( boolean facingRight ) {
        double appliedForce = Math.abs( getAppliedForce() );
        int index = (int)( animation.getNumFrames() * appliedForce / max );
        if( index >= animation.getNumFrames() ) {
            index = animation.getNumFrames() - 1;
        }
        if( getAppliedForce() == 0 ) {
            return standingStill;
        }
        if( facingRight ) {
            return animation.getFrame( index );
        }
        else {
            return flippedAnimation.getFrame( index );
        }
    }

    public void screenSizeChanged() {
        update( true );
    }

    private void update( boolean forceLocation ) {
        boolean facingRight = true;
        double app = getAppliedForce();
        if( app < 0 ) {
            facingRight = false;
        }
        BufferedImage frame = getFrame( facingRight );
        setImage( frame );

        double modelWidthObject = rampPanel.getBlockWidthModel();
        double modelWidthLeaner = rampPanel.getModelWidth( frame.getWidth() );

        double leanerX = rampPanel.getBlockGraphic().getBlock().getPosition();
        if( facingRight ) {
            leanerX = leanerX - modelWidthLeaner;
        }
        else {
            leanerX = leanerX + modelWidthObject;
        }
        System.out.println( "rampPanel.getBlockGraphic().getBlock().getPosition() = " + rampPanel.getBlockGraphic().getBlock().getPosition() );
        System.out.println( "modelWidthObject = " + modelWidthObject );
        System.out.println( "leanerX = " + leanerX );
        if( app != 0 || forceLocation ) {
            AffineTransform tx = rampPanel.getRampGraphic().createTransform( leanerX, new Dimension( frame.getWidth(), frame.getHeight() ) );
            setTransform( tx );
        }
    }
}
