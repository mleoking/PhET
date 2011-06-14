// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.rotation.torque;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.rotation.model.RotationPlatform;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * Author: Sam Reid
 * Aug 8, 2007, 7:30:12 PM
 */
public class RotationPlatformTorqueHandler extends PBasicInputEventHandler {
    private PNode platformNode;
    private TorqueModel rotationModel;
    private RotationPlatform rotationPlatform;
    private Point2D sourcePoint;
    private Point2D dstPoint;

    public RotationPlatformTorqueHandler( PNode platformNode, TorqueModel rotationModel, RotationPlatform rotationPlatform ) {
        this.platformNode = platformNode;
        this.rotationModel = rotationModel;
        this.rotationPlatform = rotationPlatform;
    }

    public void mousePressed( PInputEvent event ) {
        if ( rotationPlatform.containsPosition( event.getPositionRelativeTo( platformNode ) ) ) {
            sourcePoint = event.getPositionRelativeTo( platformNode );
            dstPoint = event.getPositionRelativeTo( platformNode );
            rotationModel.setAllowedAppliedForce( new Line2D.Double( sourcePoint, dstPoint ) );
        }
    }

    public void mouseDragged( PInputEvent event ) {
        if ( sourcePoint != null ) {
            dstPoint = event.getPositionRelativeTo( platformNode );
            rotationModel.setAllowedAppliedForce( new Line2D.Double( sourcePoint, dstPoint ) );
        }
    }

    public void mouseReleased( PInputEvent event ) {
        if ( sourcePoint != null ) {
            dstPoint = new Point2D.Double( sourcePoint.getX(), sourcePoint.getY() );
            rotationModel.setAllowedAppliedForce( new Line2D.Double( sourcePoint, dstPoint ) );
            sourcePoint = null;
        }
    }
}
