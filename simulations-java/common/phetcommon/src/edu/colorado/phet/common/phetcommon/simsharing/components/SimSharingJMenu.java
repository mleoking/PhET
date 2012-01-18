// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing.components;

import javax.swing.Action;
import javax.swing.JMenu;

import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentTypes;

import static edu.colorado.phet.common.phetcommon.simsharing.messages.UserActions.pressed;

/**
 * Menu used in phetcommon for transmitting data on student usage of menus, see #3144
 *
 * @author Sam Reid
 */
public class SimSharingJMenu extends JMenu {

    private final IUserComponent userComponent;

    public SimSharingJMenu( IUserComponent userComponent ) {
        this.userComponent = userComponent;
    }

    public SimSharingJMenu( IUserComponent userComponent, String text ) {
        super( text );
        this.userComponent = userComponent;
    }

    public SimSharingJMenu( IUserComponent userComponent, Action action ) {
        super( action );
        this.userComponent = userComponent;
    }

    public SimSharingJMenu( IUserComponent userComponent, String text, boolean canBeTornOff ) {
        super( text, canBeTornOff );
        this.userComponent = userComponent;
    }

    @Override protected void fireMenuSelected() {
        SimSharingManager.sendUserMessage( userComponent, UserComponentTypes.menu, pressed );
        super.fireMenuSelected();
    }
}
