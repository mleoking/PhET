/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.ec2.elements.spline;

import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.graphics.InteractiveGraphic;
import edu.colorado.phet.ec2.common.util.CursorHandler;
import edu.colorado.phet.ec2.common.view.creation.CreationEvent;

import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * User: Sam Reid
 * Date: Jul 28, 2003
 * Time: 12:31:30 AM
 * Copyright (c) Jul 28, 2003 by Sam Reid
 */
public class DragSplineToCreate implements InteractiveGraphic {
    SplineGraphic icon;
    CreationEvent creationEvent;
    CursorHandler cursorHandler = new CursorHandler();
    private boolean created;
    private SplineGraphic forwardTo;
    private Graphic boundary;

    public DragSplineToCreate( SplineGraphic icon, CreationEvent creationEvent, Graphic boundary ) {
        this.icon = icon;
        this.creationEvent = creationEvent;
        this.boundary = boundary;
    }

    public boolean canHandleMousePress( MouseEvent event ) {
        return icon.canHandleMousePress( event );
    }

    public void mousePressed( MouseEvent event ) {
    }

    public void mouseDragged( MouseEvent event ) {
        if( created ) {
            forwardTo.getCurveGraphic().mouseDragged( event );
        }
        else {
            created = true;
            forwardTo = (SplineGraphic)creationEvent.createElement();
            forwardTo.getCurveGraphic().mousePressed( event );
            forwardTo.getCurveGraphic().mouseDragged( event );
        }
    }

    public void mouseReleased( MouseEvent event ) {
        forwardTo.getCurveGraphic().mouseReleased( event );
        created = false;
    }

    public void mouseEntered( MouseEvent event ) {
        cursorHandler.mouseEntered( event );
    }

    public void mouseExited( MouseEvent event ) {
        cursorHandler.mouseExited( event );
    }

    public void paint( Graphics2D g ) {
        icon.paint( g );
        boundary.paint( g );
    }

}

