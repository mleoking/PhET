// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing.components;

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JRadioButton;

import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterKeys;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponent;

import static edu.colorado.phet.common.phetcommon.simsharing.Parameter.componentType;
import static edu.colorado.phet.common.phetcommon.simsharing.messages.ComponentTypes.radioButton;
import static edu.colorado.phet.common.phetcommon.simsharing.messages.UserActions.pressed;

/**
 * Swing radio button that sends sim-sharing events.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SimSharingJRadioButton extends JRadioButton {

    private final UserComponent userComponent;

    public SimSharingJRadioButton( UserComponent userComponent ) {
        this.userComponent = userComponent;
    }

    public SimSharingJRadioButton( UserComponent userComponent, Icon icon ) {
        super( icon );
        this.userComponent = userComponent;
    }

    public SimSharingJRadioButton( UserComponent userComponent, Action a ) {
        super( a );
        this.userComponent = userComponent;
    }

    public SimSharingJRadioButton( UserComponent userComponent, Icon icon, boolean selected ) {
        super( icon, selected );
        this.userComponent = userComponent;
    }

    public SimSharingJRadioButton( UserComponent userComponent, String text ) {
        super( text );
        this.userComponent = userComponent;
    }

    public SimSharingJRadioButton( UserComponent userComponent, String text, boolean selected ) {
        super( text, selected );
        this.userComponent = userComponent;
    }

    public SimSharingJRadioButton( UserComponent userComponent, String text, Icon icon ) {
        super( text, icon );
        this.userComponent = userComponent;
    }

    public SimSharingJRadioButton( UserComponent userComponent, String text, Icon icon, boolean selected ) {
        super( text, icon, selected );
        this.userComponent = userComponent;
    }

    @Override protected void fireActionPerformed( ActionEvent event ) {
        sendEvent( userComponent, isSelected() );
        super.fireActionPerformed( event );
    }

    public static void sendEvent( UserComponent userComponent, boolean isSelected ) {
        SimSharingManager.sendUserMessage( userComponent, pressed, componentType( radioButton ).param( ParameterKeys.isSelected, isSelected ) );
    }
}
