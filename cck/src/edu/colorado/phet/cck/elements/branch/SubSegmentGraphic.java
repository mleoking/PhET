/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.cck.elements.branch;

import edu.colorado.phet.cck.common.SegmentGraphic;
import edu.colorado.phet.common.view.graphics.InteractiveGraphic;
import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;

import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * User: Sam Reid
 * Date: Oct 27, 2003
 * Time: 1:26:43 AM
 * Copyright (c) Oct 27, 2003 by Sam Reid
 */

public class SubSegmentGraphic implements InteractiveGraphic {
    private SegmentGraphic target;
    ModelViewTransform2D transform;
    private DefaultBranchInteractionHandler interactionHandler;

    public SubSegmentGraphic( Color color, Stroke stroke, Stroke highlightStroke, Color highlightColor, ModelViewTransform2D transform, DefaultBranchInteractionHandler interactionHandler ) {
        this.interactionHandler = interactionHandler;
        target = new SegmentGraphic( transform, 0, 0, 0, 0, color, stroke, highlightStroke, highlightColor );
        this.transform = transform;
    }


    public boolean canHandleMousePress( MouseEvent event ) {
        return target.getShape().contains( event.getPoint() );
    }

    public void mousePressed( MouseEvent event ) {
        interactionHandler.mousePressed( event );
    }

    public void mouseDragged( MouseEvent event ) {
        interactionHandler.mouseDragged( event );
    }

    public void mouseMoved( MouseEvent e ) {
    }

    public void mouseReleased( MouseEvent event ) {
        interactionHandler.mouseReleased( event );
    }

    public void mouseClicked( MouseEvent e ) {
    }

    public void mouseEntered( MouseEvent event ) {
        interactionHandler.mouseEntered( event );
    }

    public void mouseExited( MouseEvent event ) {
        interactionHandler.mouseExited( event );
    }

    public void paint( Graphics2D g ) {
        target.paint( g );
    }

    public SegmentGraphic getTarget() {
        return target;
    }

    public boolean contains( int x, int y ) {
        return target.getShape().contains( x, y );
    }
}

