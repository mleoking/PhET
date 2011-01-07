// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.piccolophet.event;

import java.awt.*;

import javax.swing.*;

import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * User: Sam Reid
 * Date: Sep 30, 2005
 * Time: 7:43:35 AM
 */

public class PopupMenuHandler extends PBasicInputEventHandler {
    private Component parent;
    private JPopupMenu popupMenu;

    public PopupMenuHandler( Component parent, JPopupMenu popupMenu ) {
        this.parent = parent;
        this.popupMenu = popupMenu;
    }

    /**
     * right-click popup menu on mac is fired on mousePressed, not mouseReleased:
     * http://developer.apple.com/technotes/tn/tn2042.html
     *
     * @param event
     */
    public void mousePressed( PInputEvent event ) {
        super.mouseReleased( event );
        handlePopup( event );
    }

    public void mouseReleased( PInputEvent event ) {
        super.mouseReleased( event );
        handlePopup( event );
    }

    private void handlePopup( PInputEvent event ) {

        if ( event.isPopupTrigger() ) {
            popupMenu.show( parent, (int) event.getCanvasPosition().getX(), (int) event.getCanvasPosition().getY() );
        }
    }
}