/* Copyright 2004, Sam Reid */
package edu.colorado.phet.forces1d.view;

import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphicListener;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.util.BufferedImageUtils;
import edu.colorado.phet.common.view.util.FrameSequence;
import edu.colorado.phet.forces1d.Force1DModule;
import edu.colorado.phet.forces1d.model.Force1DModel;

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
    private Force1DPanel forcePanel;
    private Force1DModule module;
    private double max = 500.0;
    private FrameSequence flippedAnimation;

    public LeanerGraphic( final Force1DPanel forcePanel, final PhetGraphic target ) throws IOException {
        super( forcePanel, (BufferedImage)null );
        this.forcePanel = forcePanel;
        this.target = target;
        this.module = forcePanel.getModule();
        animation = new FrameSequence( "images/pusher-leaning/pusher-leaning", 15 );
        BufferedImage[] flipped = new BufferedImage[animation.getNumFrames()];
        for( int i = 0; i < flipped.length; i++ ) {
            flipped[i] = BufferedImageUtils.flipX( animation.getFrame( i ) );
        }
        flippedAnimation = new FrameSequence( flipped );
        super.setImage( animation.getFrame( 0 ) );
        target.addPhetGraphicListener( new PhetGraphicListener() {
            public void phetGraphicChanged( PhetGraphic phetGraphic ) {
                update();
            }

            public void phetGraphicVisibilityChanged( PhetGraphic phetGraphic ) {
                setVisible( phetGraphic.isVisible() );
            }

        } );
//        module.getForceModel().getBlock().addListener( new Block.Listener() {
//            public void positionChanged() {
//                update();
//            }
//
//            public void propertyChanged() {
//            }
//        } );
        module.getForceModel().addListener( new Force1DModel.Listener() {
            public void appliedForceChanged() {
                update();
            }

            public void gravityChanged() {
            }
        } );
    }

    private int getFrame() {

        double appliedForce = Math.abs( module.getForceModel().getAppliedForce() );
        int index = (int)( animation.getNumFrames() * appliedForce / max );
        if( index >= animation.getNumFrames() ) {
            index = animation.getNumFrames() - 1;
        }
        return index;
    }

    private void update() {
        boolean facingRight = true;
        double app = module.getForceModel().getAppliedForce();
        if( app < 0 ) {
            facingRight = false;
        }
        int index = getFrame();
        BufferedImage frame = animation.getFrame( index );
        int x = 0;
        int y = 0;
        if( facingRight ) {
            x = target.getX() - frame.getWidth();
            y = forcePanel.getWalkwayGraphic().getFloorY() - getHeight();
        }
        else {
            x = target.getX() + target.getWidth();
            y = forcePanel.getWalkwayGraphic().getFloorY() - getHeight();
            frame = flippedAnimation.getFrame( index );
        }

        setImage( frame );
        setLocation( x, y );
    }
}
