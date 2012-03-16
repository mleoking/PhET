// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.rotation.view;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.rotation.RotationResources;
import edu.colorado.phet.rotation.model.RotationBody;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PAffineTransform;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Author: Sam Reid
 * May 11, 2007, 12:12:50 AM
 */
public class RotationBodyNode extends PhetPNode {
    private RotationBody rotationBody;
    private PNode centerIndicatorNode;
    private PNode imageNode;

    private static final double CENTER_NODE_WIDTH = 20;
    private double modelSizeMeters = 1;

    public interface RotationBodyEnvironment {
        void dropBody( RotationBody rotationBody );

        boolean platformContains( double x, double y );
    }

    public RotationBodyNode( final RotationBodyEnvironment model, final RotationBody rotationBody ) {
        this.rotationBody = rotationBody;
        try {
            BufferedImage newImage = RotationResources.loadBufferedImage( rotationBody.getImageName() );
//            newImage = BufferedImageUtils.rescaleXMaintainAspectRatio( newImage, 5);
            imageNode = new PImage( newImage );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        imageNode.translate( -imageNode.getFullBounds().getWidth() / 2, -imageNode.getFullBounds().getHeight() / 2 );
        addChild( imageNode );
        addInputEventListener( new CursorHandler() );
        addInputEventListener( new PBasicInputEventHandler() {
            public void mousePressed( PInputEvent event ) {
                rotationBody.setOffPlatform();
            }

            public void mouseDragged( PInputEvent event ) {
                PDimension d = event.getDeltaRelativeTo( RotationBodyNode.this.getParent() );
                Point2D origPT = rotationBody.getPosition();
                rotationBody.translate( d.width, d.height );
                if ( model.platformContains( origPT.getX(), origPT.getY() ) ) {
                    if ( rotationBody.isConstrained() && !model.platformContains( rotationBody.getX(), rotationBody.getY() ) ) {
                        rotationBody.translate( -d.width, -d.height );
                    }
                }
            }

            public void mouseReleased( PInputEvent event ) {
                model.dropBody( rotationBody );
            }
        } );
        rotationBody.addListener( new RotationBody.Adapter() {
            public void positionChanged() {
                update();
            }

            public void orientationChanged() {
                update();
            }
        } );
        centerIndicatorNode = new PhetPPath( new Ellipse2D.Double( -CENTER_NODE_WIDTH / 2, -CENTER_NODE_WIDTH / 2, CENTER_NODE_WIDTH, CENTER_NODE_WIDTH ), Color.white, new BasicStroke( 1 ), Color.black );
        addChild( centerIndicatorNode );
        update();
    }

    private void update() {
        double s = modelSizeMeters / imageNode.getWidth();
        PAffineTransform newTransform = new PAffineTransform( AffineTransform.getScaleInstance( s, -s ) );
        Point2D center = getFullBounds().getCenter2D();
        newTransform.rotate( -rotationBody.getOrientation(), center.getX(), center.getY() );
        newTransform.setOffset( rotationBody.getPosition().getX(), rotationBody.getPosition().getY() );
        if ( !newTransform.equals( getTransform() ) ) {
            setTransform( newTransform );
        }
    }

//        private void update() {
//        double s = modelSizeMeters / imageNode.getWidth();
//        setTransform( AffineTransform.getScaleInstance( s, -s ) );
//
//        Point2D center = getFullBounds().getCenter2D();
//        rotateAboutPoint( -rotationBody.getOrientation(), center );//negative angle since +y is up
//        setOffset( rotationBody.getPosition() );
//    }
}
