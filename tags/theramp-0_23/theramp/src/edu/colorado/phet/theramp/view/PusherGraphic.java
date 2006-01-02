/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.view;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.util.BufferedImageUtils;
import edu.colorado.phet.common.view.util.FrameSequence;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.theramp.RampModule;
import edu.colorado.phet.theramp.common.PhetAudioClip;
import edu.colorado.phet.theramp.model.RampPhysicalModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

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

public class PusherGraphic extends PImage {
    private FrameSequence animation;
    private PNode target;
    private RampWorld rampWorld;
    private double max = 3000.0;
    private FrameSequence flippedAnimation;
    private BufferedImage standingStill;
    private RampModule module;
    private RampPanel rampPanel;
    private double modelLocation;
    private ModelElement runOver;
    private double lastDX;
    private boolean crushed = false;
    private long crushTime = 0;
    private BufferedImage crushedManImage;
    private static final long CRUSH_TIME = 1000;
    private double lastAppliedForce = 0;
    private PhetAudioClip slapSound;
//    private URL url0;
//    private URL url1;

    public PusherGraphic( final RampPanel rampPanel, final PNode target, RampWorld rampWorld ) throws IOException {
        super();
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
        module.getRampPhysicalModel().addListener( new RampPhysicalModel.Adapter() {
            public void appliedForceChanged() {
                update();
            }

        } );
        module.getRampPhysicalModel().getRamp().addObserver( new SimpleObserver() {
            public void update() {
                PusherGraphic.this.updateTransform();
            }
        } );
        setPickable( false );
        setChildrenPickable( false );
        update();

        runOver = new ModelElement() {
            public void stepInTime( double dt ) {
                double dx = getBlockDx();
                if( getAppliedForce() == 0 && lastAppliedForce == 0 && signDiffers( dx, lastDX ) && !crushed ) {
                    crushMan();
                }
                else if( crushed && timeSinceCrush() > 1000 ) {
                    standUp();
                }
                lastDX = dx;
                lastAppliedForce = getAppliedForce();
            }
        };
        module.getModel().addModelElement( runOver );
        lastDX = getBlockDx();

        modelLocation = getSurfaceGraphic().getSurface().getLength() / 2.0;
        slapSound = new PhetAudioClip( "audio/slapooh.wav" );
    }


    private long timeSinceCrush() {
        return System.currentTimeMillis() - crushTime;
    }

    private void standUp() {
        if( getAppliedForce() == 0 ) {
            setImage( standingStill );
        }
        crushed = false;
    }

    private Image getCrushedManImage() {
        if( crushedManImage == null ) {
            try {
                crushedManImage = ImageLoader.loadBufferedImage( "images/crushedman.png" );
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
        }
        return crushedManImage;
    }

    private void crushMan() {
        System.out.println( "CRUSH_MAN " );
        crushed = true;
        crushTime = System.currentTimeMillis();
        setImage( getCrushedManImage() );
        //http://www.gratisnette.com/bruitages/hommes/cris/
        slapSound.play();
//        AudioSourceDataLinePlayer.playNoBlock( url0 );
//        JSAudioPlayer.playNoBlock( url1 );

    }

    private boolean signDiffers( double a, double b ) {
        return ( a > 0 && b < 0 ) || ( a < 0 && b > 0 );
    }

    private double getBlockDx() {
        return modelLocation - getBlockLocation();
    }

    private double getAppliedForce() {
        return module.getRampPhysicalModel().getAppliedForce().getParallelComponent();
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
        if( frame != getImage() ) {
            if( app == 0 && crushed && timeSinceCrush() < CRUSH_TIME ) {}//stay crushed
            else {
                setImage( frame );
            }
        }

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
        return rampWorld.getBlockGraphic().getBlock().getPosition();
    }

    private void updateTransform() {
        double positionInSurface = getPositionInSurface();
        AffineTransform tx = getSurfaceGraphic().createTransform( positionInSurface, new Dimension( getFrame().getWidth( null ), getFrame().getHeight( null ) ) );
        if( !getTransform().equals( tx ) ) {
            setTransform( tx );//!!working
        }
    }

    private double getPositionInSurface() {
        if( getSurfaceGraphic() == rampWorld.getGroundGraphic() ) {
            return modelLocation;
        }
        else {
            return modelLocation - rampWorld.getGroundGraphic().getSurface().getLength();
        }
    }

    private SurfaceGraphic getSurfaceGraphic() {
        return rampWorld.getSurfaceGraphic( modelLocation );
//        return rampWorld.getBlockGraphic().getCurrentSurfaceGraphic();
    }

    private Image getFrame() {
        return getImage();
    }
}
