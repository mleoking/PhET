package edu.colorado.phet.rotation.torque;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.io.IOException;

import edu.colorado.phet.common.phetcommon.math.AbstractVector2D;
import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
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
public class BrakeNode extends PNode {
    private RotationPlatform rotationPlatform;
    private TorqueModel torqueModel;
    private PNode im;
    private double BRAKE_SCALE = 5;

    public BrakeNode( final RotationPlatform rotationPlatform, final TorqueModel torqueModel ) {
        this.rotationPlatform = rotationPlatform;
        this.torqueModel = torqueModel;
        rotationPlatform.addListener( new RotationPlatform.Adapter() {
            public void radiusChanged() {
                updateTransform();
            }
        } );
        torqueModel.addListener( new TorqueModel.Adapter() {
            public void brakePressureChanged() {
                updateTransform();
            }
        } );

        try {
            im = createImageWithHandle();
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        double imageScale = RotationPlayAreaNode.SCALE * 0.6;
        im.transformBy( AffineTransform.getScaleInstance( -imageScale, imageScale ) );

        im.rotate( Math.PI / 2 );
//        im.translate( -im.getFullBounds().getWidth() / imageScale * 2.0, 0 );
        im.translate( -im.getFullBounds().getWidth() / imageScale, 0 );

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
                    Vector2D.Double dragVector = new Vector2D.Double( pressPoint, dragPoint );
                    Vector2D.Double centerVector = new Vector2D.Double( getFullBounds().getCenter2D(), rotationPlatform.getCenter() );
                    double appliedBrake = dragVector.dot( centerVector ) / 2;
                    if ( appliedBrake < 0 ) {
                        appliedBrake = 0;
                    }
//                    torqueModel.setBrakePressure( Math.min( appliedBrake, FullTorqueControlPanel.MAX_BRAKE ) );
                    appliedBrake = appliedBrake * BRAKE_SCALE;
                    torqueModel.setBrakePressure( MathUtil.clamp( FullTorqueControlPanel.MIN_BRAKE, appliedBrake, FullTorqueControlPanel.MAX_BRAKE ) );
                }
            }

            public void mouseReleased( PInputEvent event ) {
                pressPoint = null;
                torqueModel.setBrakePressure( 0.0 );
            }

            private Point2D getPoint( PInputEvent event ) {
                return event.getPositionRelativeTo( BrakeNode.this.getParent() );
            }
        } );

        updateTransform();
    }

    private PNode createImageWithHandle() throws IOException {
        final PImage image = new PImage( RotationResources.loadBufferedImage( "brake-pad.gif" ) );
        return image;
    }

    private void updateTransform() {
        double angle = -Math.PI / 4;
//        boolean awayFromPlatform = torqueModel.getBrakePressure() == 0;
        final double farAway = 0.00;
        final double close = -0.09;
        Function.LinearFunction linearFunction = new Function.LinearFunction( 0, 1, farAway, close );
        double x = linearFunction.evaluate( torqueModel.getBrakePressure() );
        x = MathUtil.clamp( close, x, farAway );

        if ( torqueModel.getBrakePressure() == 0 ) {
            x = 0.05;
        }

        AbstractVector2D vec = Vector2D.Double.parseAngleAndMagnitude( rotationPlatform.getRadius() + x, angle );
        setOffset( vec.getDestination( rotationPlatform.getCenter() ) );
        setRotation( angle );
    }
}
