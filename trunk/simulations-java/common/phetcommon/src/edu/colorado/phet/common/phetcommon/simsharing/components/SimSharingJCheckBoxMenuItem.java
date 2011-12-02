// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing.components;

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;

import edu.colorado.phet.common.phetcommon.simsharing.SimSharingActions;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingEvents;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingObjects;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingParameters;

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
        SimSharingEvents.sendEvent( SimSharingObjects.CHECK_BOX_MENU_ITEM,
                                    SimSharingActions.TOGGLED, //TODO shouldn't this be PRESSED, to be consistent with PropertyCheckBox?
                                    param( SimSharingParameters.TEXT, getText() ),
                                    param( SimSharingParameters.IS_SELECTED, isSelected() ) );
        super.fireActionPerformed( event );
    }
}
