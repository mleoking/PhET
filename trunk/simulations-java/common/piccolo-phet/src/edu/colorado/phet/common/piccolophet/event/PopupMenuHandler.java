// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.event;

import java.awt.Component;

import javax.swing.JPopupMenu;

import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentTypes;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

import static edu.colorado.phet.common.phetcommon.simsharing.messages.UserActions.popupTriggered;

public class PopupMenuHandler extends PBasicInputEventHandler {
    private final IUserComponent userComponent;
    private Component parent;
    private JPopupMenu popupMenu;

    public PopupMenuHandler( IUserComponent userComponent, Component parent, JPopupMenu popupMenu ) {
        this.userComponent = userComponent;
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