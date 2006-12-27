/* Copyright 2004, Sam Reid */
package edu.colorado.phet.piccolo;

import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

import javax.swing.*;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: Sep 30, 2005
 * Time: 7:43:35 AM
 * Copyright (c) Sep 30, 2005 by Sam Reid
 */

public class PopupMenuHandler extends PBasicInputEventHandler {
    private Component parent;
    private JPopupMenu popupMenu;

    public PopupMenuHandler( Component parent, JPopupMenu popupMenu ) {
        this.parent = parent;
        this.popupMenu = popupMenu;
    }

    public void mouseReleased( PInputEvent event ) {
        super.mouseReleased( event );
        if( event.isPopupTrigger() ) {
            popupMenu.show( parent, (int)event.getCanvasPosition().getX(), (int)event.getCanvasPosition().getY() );
        }
    }
}
