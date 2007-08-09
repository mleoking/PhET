package edu.colorado.phet.rotation.torque;

import edu.colorado.phet.rotation.model.RotationPlatform;
import edu.colorado.phet.rotation.view.RotationPlatformNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

/**
 * Author: Sam Reid
 * Aug 8, 2007, 7:30:12 PM
 */
public class RotationPlatformTorqueHandler extends PBasicInputEventHandler {
    private RotationPlatformNode platformNode;
    private TorqueModel rotationModel;
    private RotationPlatform rotationPlatform;
    private Point2D sourcePoint;
    private Point2D dstPoint;

    public RotationPlatformTorqueHandler( RotationPlatformNode platformNode, TorqueModel rotationModel, RotationPlatform rotationPlatform ) {
        this.platformNode = platformNode;
        this.rotationModel = rotationModel;
        this.rotationPlatform = rotationPlatform;
    }

    public void mousePressed( PInputEvent event ) {
        super.mousePressed( event );
        sourcePoint = event.getPositionRelativeTo( platformNode );
        dstPoint = event.getPositionRelativeTo( platformNode );
        rotationModel.setAppliedForce( new Line2D.Double( sourcePoint, dstPoint ) );
    }

    public void mouseDragged( PInputEvent event ) {
        dstPoint = event.getPositionRelativeTo( platformNode );
        rotationModel.setAppliedForce( new Line2D.Double( sourcePoint, dstPoint ) );
    }

    public void mouseReleased( PInputEvent event ) {
    }
}
