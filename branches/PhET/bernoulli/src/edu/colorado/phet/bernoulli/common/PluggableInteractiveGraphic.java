package edu.colorado.phet.bernoulli.common;

import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.graphics.InteractiveGraphic;

import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * Divides up the InteractiveGraphic interface to the Graphic and InteractionHandler, for ease of pluggability and reuse.
 */
public class PluggableInteractiveGraphic implements InteractiveGraphic, InteractionHandler {
    Graphic graphic;
    InteractionHandler interactionHandler;

    public boolean canHandleMousePress( MouseEvent event ) {
        return false;
    }

    public void mousePressed( MouseEvent event ) {
        interactionHandler.mousePressed( event );
    }

    public void mouseDragged( MouseEvent event ) {
        interactionHandler.mouseDragged( event );
    }

    public void mouseReleased( MouseEvent event ) {
        interactionHandler.mouseReleased( event );
    }

    public void mouseEntered( MouseEvent event ) {
        interactionHandler.mouseEntered( event );
    }

    public void mouseExited( MouseEvent event ) {
        interactionHandler.mouseExited( event );
    }

    public void paint( Graphics2D g ) {
        graphic.paint( g );
    }
}
