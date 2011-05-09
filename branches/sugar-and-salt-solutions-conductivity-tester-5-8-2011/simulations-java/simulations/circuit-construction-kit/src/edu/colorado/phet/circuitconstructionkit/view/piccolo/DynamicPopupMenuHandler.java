// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.circuitconstructionkit.view.piccolo;

import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

import javax.swing.*;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: Sep 30, 2005
 * Time: 7:43:35 AM
 */

public class DynamicPopupMenuHandler extends PBasicInputEventHandler {
    private Component parent;
    private JPopupMenuFactory popupMenuFactory;

    public DynamicPopupMenuHandler(Component parent, JPopupMenuFactory popupMenuFactory) {
        this.parent = parent;
        this.popupMenuFactory = popupMenuFactory;
    }

    public void mousePressed(PInputEvent event) {
        super.mousePressed(event);
        handlePopup(event);
    }

    public void mouseReleased(PInputEvent event) {
        super.mouseReleased(event);
        handlePopup(event);
    }

    private void handlePopup(PInputEvent event) {
        if (event.isPopupTrigger()) {
            JPopupMenu popup = popupMenuFactory.createPopupMenu();
            if (popup != null) {
                popup.show(parent, (int) event.getCanvasPosition().getX(), (int) event.getCanvasPosition().getY());
            }
        }
    }

    public static interface JPopupMenuFactory {
        JPopupMenu createPopupMenu();
    }
}
