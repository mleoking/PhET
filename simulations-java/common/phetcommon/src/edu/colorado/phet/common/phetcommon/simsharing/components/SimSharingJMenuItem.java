// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing.components;

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JMenuItem;

import edu.colorado.phet.common.phetcommon.simsharing.Parameter;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingActions;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingEvents;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingObjects;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingParameters;

/**
 * MenuItem used in phetcommon for transmitting data on student usage of menus, see #3144
 *
 * @author Sam Reid
 */
public class SimSharingJMenuItem extends JMenuItem {
    public SimSharingJMenuItem() {
    }

    public SimSharingJMenuItem( Icon icon ) {
        super( icon );
    }

    public SimSharingJMenuItem( String text ) {
        super( text );
    }

    public SimSharingJMenuItem( Action a ) {
        super( a );
    }

    public SimSharingJMenuItem( String text, Icon icon ) {
        super( text, icon );
    }

    public SimSharingJMenuItem( String text, int mnemonic ) {
        super( text, mnemonic );
    }

    protected void fireActionPerformed( ActionEvent event ) {
        SimSharingEvents.sendEvent( SimSharingObjects.MENU_ITEM,
                                    SimSharingActions.SELECTED, //TODO: shouldn't this be PRESSED, it's a button?
                                    Parameter.param( SimSharingParameters.TEXT, getText() ) );
        super.fireActionPerformed( event );
    }
}
