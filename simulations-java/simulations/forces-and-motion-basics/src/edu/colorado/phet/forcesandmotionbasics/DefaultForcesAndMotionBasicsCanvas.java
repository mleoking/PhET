package edu.colorado.phet.forcesandmotionbasics;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import javax.swing.Timer;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.piccolophet.nodes.background.OutsideBackgroundNode;
import edu.colorado.phet.forcesandmotionbasics.ForcesAndMotionBasicsResources.Images;
import edu.umd.cs.piccolo.nodes.PImage;

import static edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform.createSinglePointScaleInvertedYMapping;
import static edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils.multiScaleToHeight;

/**
 * @author Sam Reid
 */
public class DefaultForcesAndMotionBasicsCanvas extends AbstractForcesAndMotionBasicsCanvas {
    private boolean inited = false;

    public DefaultForcesAndMotionBasicsCanvas( final ForcesAndMotionBasicsModel model ) {

        final ModelViewTransform transform = createSinglePointScaleInvertedYMapping( new Point2D.Double( 0, 0 ), new Point2D.Double( STAGE_SIZE.getWidth() / 2, STAGE_SIZE.getHeight() * 0.8 ), 100 );
        addChild( new OutsideBackgroundNode( transform, 10, 1 ) );
        addChild( new PImage( BufferedImageUtils.multiScaleToWidth( Images.DRAKOONSONNE, 150 ) ) {{
            setOffset( STAGE_SIZE.getWidth() - getFullBounds().getWidth(), 0 );
        }} );
        final BufferedImage tile = multiScaleToHeight( Images.BRICK_TILE, 80 );
        for ( int i = -50; i <= 50; i++ ) {
            final int finalI = i;
            addChild( new PImage( tile ) {{

                model.blockPosition.addObserver( new VoidFunction1<Double>() {
                    public void apply( final Double x ) {
                        setOffset( transform.modelToView( -x, 0 ) );
                        translate( finalI * tile.getWidth(), 0 );
                    }
                } );
            }} );
        }

        addChild( new PImage( Images.MYSTERY_BOX ) {{
            model.blockPosition.addObserver( new VoidFunction1<Double>() {
                public void apply( final Double x ) {
                    final Point2D point = transform.modelToView( 0, 0 );
                    setOffset( point );
                    translate( -getFullBounds().getWidth() / 2, -getFullBounds().getHeight() );
                }
            } );
        }} );
        for ( int i = -10; i <= 10; i++ ) {
            final int finalI = i;
            addChild( new PImage( Images.CLOUD1 ) {{
                model.blockPosition.addObserver( new VoidFunction1<Double>() {
                    public void apply( final Double x ) {
                        setOffset( transform.modelToView( -x / 4 + finalI * 10, 6 ) );
                    }
                } );
            }} );
        }

        addKeyListener( new KeyAdapter() {
            @Override public void keyPressed( final KeyEvent e ) {
                if ( e.getKeyCode() == KeyEvent.VK_LEFT ) {
                    model.leftPressed.set( true );
                }
                if ( e.getKeyCode() == KeyEvent.VK_RIGHT ) {
                    model.rightPressed.set( true );
                }
            }

            @Override public void keyReleased( final KeyEvent e ) {
                if ( e.getKeyCode() == KeyEvent.VK_LEFT ) {
                    model.leftPressed.set( false );
                }
                if ( e.getKeyCode() == KeyEvent.VK_RIGHT ) {
                    model.rightPressed.set( false );
                }
            }
        } );

        new Timer( 1000, new ActionListener() {
            public void actionPerformed( final ActionEvent e ) {
                if ( isShowing() && !hasFocus() && !inited ) {
                    requestFocus();
                    inited = true;
                }
            }
        } ).start();
    }
}