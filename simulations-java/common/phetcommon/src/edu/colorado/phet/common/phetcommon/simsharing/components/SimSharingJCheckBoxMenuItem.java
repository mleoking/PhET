// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing.components;

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;

import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterKeys;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserActions;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponent;

import static edu.colorado.phet.common.phetcommon.simsharing.Parameter.componentType;
import static edu.colorado.phet.common.phetcommon.simsharing.messages.ComponentTypes.checkBoxMenuItem;

/**
 * CheckBoxMenuItem used in phetcommon for transmitting data on student usage of menus, see #3144
 *
 * @author Sam Reid
 */
public class SimSharingJCheckBoxMenuItem extends JCheckBoxMenuItem {

    private final UserComponent object;

    public SimSharingJCheckBoxMenuItem( UserComponent object ) {
        this.object = object;
    }

    public SimSharingJCheckBoxMenuItem( UserComponent object, Icon icon ) {
        super( icon );
        this.object = object;
    }

    public SimSharingJCheckBoxMenuItem( UserComponent object, String text ) {
        super( text );
        this.object = object;
    }

    public SimSharingJCheckBoxMenuItem( UserComponent object, Action a ) {
        super( a );
        this.object = object;
    }

    public SimSharingJCheckBoxMenuItem( UserComponent object, String text, Icon icon ) {
        super( text, icon );
        this.object = object;
    }

    public SimSharingJCheckBoxMenuItem( UserComponent object, String text, boolean b ) {
        super( text, b );
        this.object = object;
    }

    public SimSharingJCheckBoxMenuItem( UserComponent object, String text, Icon icon, boolean b ) {
        super( text, icon, b );
        this.object = object;
    }

    @Override protected void fireActionPerformed( ActionEvent event ) {
        SimSharingManager.sendUserMessage( object,
                                           UserActions.pressed,
                                           componentType( checkBoxMenuItem ).param( ParameterKeys.isSelected, isSelected() ) );
        super.fireActionPerformed( event );
    }
}
