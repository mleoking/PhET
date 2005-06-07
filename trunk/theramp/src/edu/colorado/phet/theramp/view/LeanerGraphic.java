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
    private RampWorld rampWorld;
    private double max = 3000.0;
    private FrameSequence flippedAnimation;
    private BufferedImage standingStill;
    private RampModule module;
    private RampPanel rampPanel;
    private double modelLocation;

    public LeanerGraphic( final RampPanel rampPanel, final PhetGraphic target, RampWorld rampWorld ) throws IOException {
        super( rampPanel, (BufferedImage)null );
        this.target = target;
        this.rampWorld = rampWorld;
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
                    update();
                }
                if( dt < 5000 ) {
                    update();
                }
            }

            public void phetGraphicVisibilityChanged( PhetGraphic phetGraphic ) {
                setVisible( phetGraphic.isVisible() );
            }

        } );
        module.getRampModel().addListener( new RampModel.Listener() {
            public void appliedForceChanged() {
                update();
            }

            public void zeroPointChanged() {
            }

            public void stepFinished() {
            }
        } );
        module.getRampModel().getRamp().addObserver( new SimpleObserver() {
            public void update() {
                LeanerGraphic.this.updateTransform();
            }
        } );
        setIgnoreMouse( true );
        update();

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
        updateTransform();
    }

    private void update() {
        syncWithBlock();
        updateTransform();
    }

    private void syncWithBlock() {
        boolean facingRight = true;
        double app = getAppliedForce();
        if( app < 0 ) {
            facingRight = false;
        }
        BufferedImage frame = getFrame( facingRight );
        setImage( frame );

        double modelWidthObject = rampWorld.getBlockWidthModel();
        double modelWidthLeaner = rampWorld.getModelWidth( frame.getWidth() );

        double leanerX = 0;
        if( facingRight ) {
            leanerX = getBlockLocation() - ( modelWidthLeaner + modelWidthObject ) / 2;
        }
        else {
            leanerX = getBlockLocation() + ( modelWidthLeaner + modelWidthObject ) / 2;
        }
//        System.out.println( "rampPanel.getBlockGraphic().getBlock().getPosition() = " + rampPanel.getBlockGraphic().getBlock().getPosition() );
//        System.out.println( "modelWidthObject = " + modelWidthObject );
//        System.out.println( "leanerX = " + leanerX );
        if( app == 0 ) {
            //stay where you just were.
        }
        else {
            this.modelLocation = leanerX;
        }
    }

    private double getBlockLocation() {
        return rampWorld.getBlockGraphic().getBlock().getPositionInSurface();
    }

    private void updateTransform() {
//        AffineTransform tx = rampPanel.getRampGraphic().createTransform( modelLocation, new Dimension( getFrame().getWidth(), getFrame().getHeight() ) );
        AffineTransform tx = rampWorld.getBlockGraphic().getCurrentSurfaceGraphic().createTransform( modelLocation, new Dimension( getFrame().getWidth(), getFrame().getHeight() ) );
        setTransform( tx );
    }

    private BufferedImage getFrame() {
        return getImage();
    }
}
