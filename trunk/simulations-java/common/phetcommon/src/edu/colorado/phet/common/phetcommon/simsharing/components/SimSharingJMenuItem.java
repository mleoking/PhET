// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing.components;

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JMenuItem;

import edu.colorado.phet.common.phetcommon.simsharing.Parameter;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingStrings.Actions;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingStrings.ParameterValues;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingStrings.Parameters;

/**
 * MenuItem used in phetcommon for transmitting data on student usage of menus, see #3144
 *
 * @author Sam Reid
 */
public class SimSharingJMenuItem extends JMenuItem {

    private final String object;

    public SimSharingJMenuItem( String object ) {
        this.object = object;
    }

    public SimSharingJMenuItem( String object, Icon icon ) {
        super( icon );
        this.object = object;
    }

    public SimSharingJMenuItem( String object, String text ) {
        super( text );
        this.object = object;
    }

    public SimSharingJMenuItem( String object, Action a ) {
        super( a );
        this.object = object;
    }

    public SimSharingJMenuItem( String object, String text, Icon icon ) {
        super( text, icon );
        this.object = object;
    }

    public SimSharingJMenuItem( String object, String text, int mnemonic ) {
        super( text, mnemonic );
        this.object = object;
    }

    @Override protected void fireActionPerformed( ActionEvent event ) {
        SimSharingManager.sendUserEvent( object,
                                         Actions.PRESSED,
                                         Parameter.param( Parameters.COMPONENT_TYPE, ParameterValues.MENU_ITEM ) );
        super.fireActionPerformed( event );
    }
}
