// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing.components;

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JRadioButton;

import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;

import static edu.colorado.phet.common.phetcommon.simsharing.Parameter.componentType;
import static edu.colorado.phet.common.phetcommon.simsharing.SimSharingConstants.ComponentTypes.radioButton;
import static edu.colorado.phet.common.phetcommon.simsharing.SimSharingConstants.User.UserActions.pressed;
import static edu.colorado.phet.common.phetcommon.simsharing.SimSharingConstants.User.UserComponent;

/**
 * Swing radio button that sends sim-sharing events.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SimSharingJRadioButton extends JRadioButton {

    private final UserComponent object;

    public SimSharingJRadioButton( UserComponent object ) {
        this.object = object;
    }

    public SimSharingJRadioButton( UserComponent object, Icon icon ) {
        super( icon );
        this.object = object;
    }

    public SimSharingJRadioButton( UserComponent object, Action a ) {
        super( a );
        this.object = object;
    }

    public SimSharingJRadioButton( UserComponent object, Icon icon, boolean selected ) {
        super( icon, selected );
        this.object = object;
    }

    public SimSharingJRadioButton( UserComponent object, String text ) {
        super( text );
        this.object = object;
    }

    public SimSharingJRadioButton( UserComponent object, String text, boolean selected ) {
        super( text, selected );
        this.object = object;
    }

    public SimSharingJRadioButton( UserComponent object, String text, Icon icon ) {
        super( text, icon );
        this.object = object;
    }

    public SimSharingJRadioButton( UserComponent object, String text, Icon icon, boolean selected ) {
        super( text, icon, selected );
        this.object = object;
    }

    @Override protected void fireActionPerformed( ActionEvent event ) {
        SimSharingManager.sendUserEvent( object,
                                         pressed,
                                         componentType( radioButton ) );
        super.fireActionPerformed( event );
    }
}
