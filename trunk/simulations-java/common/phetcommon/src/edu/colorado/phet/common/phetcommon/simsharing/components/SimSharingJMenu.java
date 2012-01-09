// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing.components;

import javax.swing.Action;
import javax.swing.JMenu;

import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ComponentTypes;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponent;

import static edu.colorado.phet.common.phetcommon.simsharing.Parameter.componentType;
import static edu.colorado.phet.common.phetcommon.simsharing.messages.UserActions.pressed;

/**
 * Menu used in phetcommon for transmitting data on student usage of menus, see #3144
 *
 * @author Sam Reid
 */
public class SimSharingJMenu extends JMenu {

    private final UserComponent object;

    public SimSharingJMenu( UserComponent object ) {
        this.object = object;
    }

    public SimSharingJMenu( UserComponent object, String text ) {
        super( text );
        this.object = object;
    }

    public SimSharingJMenu( UserComponent object, Action action ) {
        super( action );
        this.object = object;
    }

    public SimSharingJMenu( UserComponent object, String text, boolean canBeTornOff ) {
        super( text, canBeTornOff );
        this.object = object;
    }

    @Override protected void fireMenuSelected() {
        SimSharingManager.sendUserMessage( object,
                                           pressed,
                                           componentType( ComponentTypes.menu ) );
        super.fireMenuSelected();
    }
}
