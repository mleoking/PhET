// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing.components;

import javax.swing.Action;
import javax.swing.JMenu;

import edu.colorado.phet.common.phetcommon.simsharing.Parameter;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingStrings.Actions;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingStrings.ParameterValues;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingStrings.Parameters;

/**
 * Menu used in phetcommon for transmitting data on student usage of menus, see #3144
 *
 * @author Sam Reid
 */
public class SimSharingJMenu extends JMenu {

    private final String object;

    public SimSharingJMenu( String object ) {
        this.object = object;
    }

    public SimSharingJMenu( String object, String text ) {
        super( text );
        this.object = object;
    }

    public SimSharingJMenu( String object, Action action ) {
        super( action );
        this.object = object;
    }

    public SimSharingJMenu( String object, String text, boolean canBeTornOff ) {
        super( text, canBeTornOff );
        this.object = object;
    }

    @Override protected void fireMenuSelected() {
        SimSharingManager.sendUserEvent( object,
                                         Actions.PRESSED,
                                         Parameter.param( Parameters.COMPONENT_TYPE, ParameterValues.MENU ) );
        super.fireMenuSelected();
    }
}
