// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing.components;

import javax.swing.Action;
import javax.swing.JMenu;

import edu.colorado.phet.common.phetcommon.simsharing.Parameter;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingStrings.Actions;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingStrings.Objects;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingStrings.Parameters;

/**
 * Menu used in phetcommon for transmitting data on student usage of menus, see #3144
 *
 * @author Sam Reid
 */
public class SimSharingJMenu extends JMenu {
    public SimSharingJMenu() {
    }

    public SimSharingJMenu( String s ) {
        super( s );
    }

    public SimSharingJMenu( Action a ) {
        super( a );
    }

    public SimSharingJMenu( String s, boolean b ) {
        super( s, b );
    }

    @Override protected void fireMenuSelected() {
        SimSharingManager.sendEvent( Objects.MENU,
                                     Actions.PRESSED,
                                     Parameter.param( Parameters.TEXT, getText() ) );
        super.fireMenuSelected();
    }
}
