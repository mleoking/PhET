// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.travoltage;

import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * User: Sam Reid
 * Date: Jul 1, 2006
 * Time: 12:14:45 AM
 */

public class RotationHandler extends PBasicInputEventHandler {
    private LimbNode armNode;
    private double angle;

    public RotationHandler( LimbNode armNode ) {
        this.armNode = armNode;
    }

    public void mousePressed( PInputEvent event ) {
        angle = getMouseAngle( event );
    }

    public void mouseDragged( PInputEvent event ) {
        double currentAngle = getMouseAngle( event );
        armNode.rotateAboutPivot( currentAngle - angle );
        angle = currentAngle;
    }

    private double getMouseAngle( PInputEvent event ) {
        return armNode.getAngleGlobal( event.getCanvasPosition().getX(), event.getCanvasPosition().getY() );
    }

}
