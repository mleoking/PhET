package edu.colorado.phet.rotation.view;

import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.rotation.RotationResources;
import edu.colorado.phet.rotation.model.RotationBody;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PDimension;

import java.awt.geom.Point2D;
import java.io.IOException;

/**
 * Author: Sam Reid
 * May 11, 2007, 12:12:50 AM
 */
public class RotationBodyNode extends PhetPNode {
    private RotationBody rotationBody;

    public interface RotationBodyEnvironment {
        void dropBody( RotationBody rotationBody );
    }

    public RotationBodyNode( final RotationBodyEnvironment model, final RotationBody rotationBody ) {
        this.rotationBody = rotationBody;
        PNode node = null;
        try {
            node = new PImage( RotationResources.loadBufferedImage( "ladybug.gif" ) );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        node.translate( -node.getFullBounds().getWidth() / 2, -node.getFullBounds().getHeight() / 2 );
        addChild( node );
        addInputEventListener( new CursorHandler() );
        addInputEventListener( new PBasicInputEventHandler() {
            public void mousePressed( PInputEvent event ) {
                rotationBody.setOffPlatform();
            }

            public void mouseDragged( PInputEvent event ) {
                PDimension d = event.getDeltaRelativeTo( RotationBodyNode.this.getParent() );
                rotationBody.translate( d.width, d.height );
            }

            public void mouseReleased( PInputEvent event ) {
                model.dropBody( rotationBody );
            }
        } );
        rotationBody.addListener( new RotationBody.Listener() {
            public void positionChanged() {
                update();
            }
        } );
        update();
    }

    private void update() {
        setOffset( 0, 0 );
        setRotation( 0 );

        Point2D center = getFullBounds().getCenter2D();
        rotateAboutPoint( rotationBody.getOrientation(), center );
        setOffset( rotationBody.getPosition() );
    }


}
