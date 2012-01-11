// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing.components;

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JMenuItem;

import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserActions;

import static edu.colorado.phet.common.phetcommon.simsharing.Parameter.componentType;
import static edu.colorado.phet.common.phetcommon.simsharing.messages.ComponentTypes.menuItem;

/**
 * MenuItem used in phetcommon for transmitting data on student usage of menus, see #3144
 *
 * @author Sam Reid
 */
public class SimSharingJMenuItem extends JMenuItem {

    private final IUserComponent userComponent;

    public SimSharingJMenuItem( IUserComponent userComponent ) {
        this.userComponent = userComponent;
    }

    public SimSharingJMenuItem( IUserComponent userComponent, Icon icon ) {
        super( icon );
        this.userComponent = userComponent;
    }

    public SimSharingJMenuItem( IUserComponent userComponent, String text ) {
        super( text );
        this.userComponent = userComponent;
    }

    public SimSharingJMenuItem( IUserComponent userComponent, Action a ) {
        super( a );
        this.userComponent = userComponent;
    }

    public SimSharingJMenuItem( IUserComponent userComponent, String text, Icon icon ) {
        super( text, icon );
        this.userComponent = userComponent;
    }

    public SimSharingJMenuItem( IUserComponent userComponent, String text, int mnemonic ) {
        super( text, mnemonic );
        this.userComponent = userComponent;
    }

    @Override protected void fireActionPerformed( ActionEvent event ) {
        SimSharingManager.sendUserMessage( userComponent,
                                           UserActions.pressed,
                                           componentType( menuItem ) );
        super.fireActionPerformed( event );
    }
}
