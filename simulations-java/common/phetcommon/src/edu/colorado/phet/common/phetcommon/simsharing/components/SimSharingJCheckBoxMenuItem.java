// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing.components;

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;

import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingStrings.Actions;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingStrings.Objects;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingStrings.Parameters;

import static edu.colorado.phet.common.phetcommon.simsharing.Parameter.param;

/**
 * CheckBoxMenuItem used in phetcommon for transmitting data on student usage of menus, see #3144
 *
 * @author Sam Reid
 */
public class SimSharingJCheckBoxMenuItem extends JCheckBoxMenuItem {
    public SimSharingJCheckBoxMenuItem() {
    }

    public SimSharingJCheckBoxMenuItem( Icon icon ) {
        super( icon );
    }

    public SimSharingJCheckBoxMenuItem( String text ) {
        super( text );
    }

    public SimSharingJCheckBoxMenuItem( Action a ) {
        super( a );
    }

    public SimSharingJCheckBoxMenuItem( String text, Icon icon ) {
        super( text, icon );
    }

    public SimSharingJCheckBoxMenuItem( String text, boolean b ) {
        super( text, b );
    }

    public SimSharingJCheckBoxMenuItem( String text, Icon icon, boolean b ) {
        super( text, icon, b );
    }

    @Override protected void fireActionPerformed( ActionEvent event ) {
        SimSharingManager.sendEvent( Objects.CHECK_BOX_MENU_ITEM,
                                     Actions.TOGGLED, //TODO shouldn't this be PRESSED, to be consistent with PropertyCheckBox?
                                     param( Parameters.TEXT, getText() ),
                                     param( Parameters.IS_SELECTED, isSelected() ) );
        super.fireActionPerformed( event );
    }
}
