// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.rotation.torque;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.rotation.RotationResources;
import edu.colorado.phet.rotation.model.RotationPlatform;
import edu.colorado.phet.rotation.view.RotationPlayAreaNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * Author: Sam Reid
 * Aug 21, 2007, 3:40:17 AM
 */
public class PusherBrakeNode extends PNode {
    private RotationPlatform rotationPlatform;
    private TorqueModel torqueModel;
    private PhetPPath block;
    private PImage im = new PImage();

    public PusherBrakeNode( final RotationPlatform rotationPlatform, final TorqueModel torqueModel ) {
        this.rotationPlatform = rotationPlatform;
        this.torqueModel = torqueModel;
        rotationPlatform.addListener( new RotationPlatform.Adapter() {
            public void radiusChanged() {
                updateTransform();
            }
        } );
        torqueModel.addListener( new TorqueModel.Adapter() {
            public void brakePressureChanged() {
                updateImage();
                updateTransform();
            }
        } );
//        Color blockColor = new Color( 207, 187, 108 );
        Color blockColor = new Color( 20, 18, 10 );
//        block = new PhetPPath( new Rectangle2D.Double( 0, 0, 0.5, 0.5 ), blockColor, new BasicStroke( (float) ( 1 * RotationPlayAreaNode.SCALE ) ), Color.darkGray );
        block = new PhetPPath( new RoundRectangle2D.Double( 0, 0, 0.5, 0.5, 0.1, 0.1 ), blockColor, new BasicStroke( (float) ( 1 * RotationPlayAreaNode.SCALE ) ), Color.darkGray );
        block.translate( 0, -block.getFullBounds().getHeight() / 2.0 );
        addChild( block );

        updateImage();

        double imageScale = RotationPlayAreaNode.SCALE * 0.8;
        im.scale( imageScale );
        im.translate( block.getFullBounds().getWidth() / imageScale * 0.9, -im.getFullBounds().getHeight() / imageScale / 2.0 * 0.7 );

        addChild( im );
        addInputEventListener( new CursorHandler() );
        addInputEventListener( new PBasicInputEventHandler() {
            Point2D pressPoint = null;

            public void mousePressed( PInputEvent event ) {
                pressPoint = getPoint( event );
            }

            public void mouseDragged( PInputEvent event ) {
                if ( pressPoint != null ) {
                    Point2D dragPoint = getPoint( event );
                    Vector2D dragVector = new Vector2D( pressPoint, dragPoint );
                    Vector2D centerVector = new Vector2D( getFullBounds().getCenter2D(), rotationPlatform.getCenter() );
                    double appliedBrake = dragVector.dot( centerVector ) / 2;
                    if ( appliedBrake < 0 ) {
                        appliedBrake = 0;
                    }
//                    torqueModel.setBrakePressure( Math.min( appliedBrake, FullTorqueControlPanel.MAX_BRAKE ) );
                    torqueModel.setBrakePressure( MathUtil.clamp( FullTorqueControlPanel.MIN_BRAKE, appliedBrake, FullTorqueControlPanel.MAX_BRAKE ) );
                }
            }

            public void mouseReleased( PInputEvent event ) {
                pressPoint = null;
                torqueModel.setBrakePressure( 0.0 );
            }

            private Point2D getPoint( PInputEvent event ) {
                return event.getPositionRelativeTo( PusherBrakeNode.this.getParent() );
            }
        } );

        updateTransform();
        updateImage();
    }

    private void updateImage() {
        double numImages = 15;
//        int image = (int) ( ( torqueModel.getBrakeForceMagnitude() / ( FullTorqueControlPanel.MAX_BRAKE - FullTorqueControlPanel.MIN_BRAKE ) ) * ( numImages - 1.0 ) + 1.0 );
        int image = (int) ( ( torqueModel.getBrakePressure() / ( FullTorqueControlPanel.MAX_BRAKE - FullTorqueControlPanel.MIN_BRAKE ) ) * ( numImages - 1.0 ) + 1.0 );
        if ( image == 0 ) {//todo: resolve this workaround
            image = 1;
        }
//        System.out.println( "torqueModel.getBRakeForce= " + torqueModel.getBrakeForceMagnitude() + ", image=" + image );
        String imageString = "pusher-leaning_00" + ( ( "" + image ).length() == 1 ? "0" : "" ) + image + ".gif";
        try {
            BufferedImage i = RotationResources.loadBufferedImage( "animations/" + imageString );
            i = BufferedImageUtils.flipX( i );
            i = BufferedImageUtils.flipY( i );
            im.setImage( i );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    private void updateTransform() {
        double angle = -Math.PI / 4;
//        boolean awayFromPlatform = torqueModel.getBrakeForceMagnitude() == 0;
        boolean awayFromPlatform = torqueModel.getBrakePressure() == 0;

        ImmutableVector2D vec = Vector2D.parseAngleAndMagnitude( rotationPlatform.getRadius() + ( awayFromPlatform ? 0.08 : 0.00 ), angle );
        setOffset( vec.getDestination( rotationPlatform.getCenter() ) );
        setRotation( angle );
    }
}