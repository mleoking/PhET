// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing.components;

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;

import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingStrings.Actions;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingStrings.ParameterValues;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingStrings.Parameters;

import static edu.colorado.phet.common.phetcommon.simsharing.Parameter.param;

/**
 * CheckBoxMenuItem used in phetcommon for transmitting data on student usage of menus, see #3144
 *
 * @author Sam Reid
 */
public class SimSharingJCheckBoxMenuItem extends JCheckBoxMenuItem {

    private final String object;

    public SimSharingJCheckBoxMenuItem( String object ) {
        this.object = object;
    }

    public SimSharingJCheckBoxMenuItem( String object, Icon icon ) {
        super( icon );
        this.object = object;
    }

    public SimSharingJCheckBoxMenuItem( String object, String text ) {
        super( text );
        this.object = object;
    }

    public SimSharingJCheckBoxMenuItem( String object, Action a ) {
        super( a );
        this.object = object;
    }

    public SimSharingJCheckBoxMenuItem( String object, String text, Icon icon ) {
        super( text, icon );
        this.object = object;
    }

    public SimSharingJCheckBoxMenuItem( String object, String text, boolean b ) {
        super( text, b );
        this.object = object;
    }

    public SimSharingJCheckBoxMenuItem( String object, String text, Icon icon, boolean b ) {
        super( text, icon, b );
        this.object = object;
    }

    @Override protected void fireActionPerformed( ActionEvent event ) {
        SimSharingManager.sendUserEvent( object,
                                         Actions.PRESSED,
                                         param( Parameters.COMPONENT_TYPE, ParameterValues.CHECK_BOX_MENU_ITEM ),
                                         param( Parameters.IS_SELECTED, isSelected() ) );
        super.fireActionPerformed( event );
    }
}
