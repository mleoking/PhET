// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing.components;

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;

import edu.colorado.phet.common.phetcommon.simsharing.Parameter;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.messages.SimSharingConstants;

import static edu.colorado.phet.common.phetcommon.simsharing.messages.SimSharingConstants.User.UserActions.pressed;
import static edu.colorado.phet.common.phetcommon.simsharing.messages.SimSharingConstants.User.UserComponent;

/**
 * Swing button that sends sim-sharing events.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SimSharingJButton extends JButton {

    private final UserComponent object;

    public SimSharingJButton( UserComponent object ) {
        this.object = object;
    }

    public SimSharingJButton( UserComponent object, Icon icon ) {
        super( icon );
        this.object = object;
    }

    public SimSharingJButton( UserComponent object, String text ) {
        super( text );
        this.object = object;
    }

    public SimSharingJButton( UserComponent object, Action a ) {
        super( a );
        this.object = object;
    }

    public SimSharingJButton( UserComponent object, String text, Icon icon ) {
        super( text, icon );
        this.object = object;
    }

    @Override protected void fireActionPerformed( ActionEvent event ) {
        SimSharingManager.sendUserEvent( object,
                                         pressed,
                                         Parameter.componentType( SimSharingConstants.ComponentTypes.button ) );
        super.fireActionPerformed( event );
    }
}
