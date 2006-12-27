/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.ec2.common;

import edu.colorado.phet.common.view.graphics.InteractiveGraphic;

import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * I use this to add a interaction function to the apparatus panel even when the graphic gets painted in a buffer.
 */
public class GraphicControllerAdapter implements InteractiveGraphic {
    InteractiveGraphic controller;

    public GraphicControllerAdapter( InteractiveGraphic controller ) {
        this.controller = controller;
    }

    public boolean canHandleMousePress( MouseEvent event ) {
        return controller.canHandleMousePress( event );
//        return false;
    }

    public void mousePressed( MouseEvent event ) {
        controller.mousePressed( event );
    }

    public void mouseDragged( MouseEvent event ) {
        controller.mouseDragged( event );
    }

    public void mouseReleased( MouseEvent event ) {
        controller.mouseReleased( event );
    }

    public void mouseEntered( MouseEvent event ) {
        controller.mouseEntered( event );
    }

    public void mouseExited( MouseEvent event ) {
        controller.mouseExited( event );
    }

    public void paint( Graphics2D g ) {
    }
}
