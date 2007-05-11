package edu.colorado.phet.rotation;

import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PDimension;

import java.awt.*;
import java.awt.geom.Point2D;

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
        PText pText = new PText( "body" );
//        PNode pText = new PhetPPath( new Rectangle( -5, -5, 10, 10 ), Color.blue );
        pText.translate( -pText.getFullBounds().getWidth() / 2, -pText.getFullBounds().getHeight() / 2 );
        addChild( pText );

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

    double angle = 0;
}
