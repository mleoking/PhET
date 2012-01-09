// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing.components;

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JCheckBox;

import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ComponentTypes;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterKeys;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponent;

import static edu.colorado.phet.common.phetcommon.simsharing.Parameter.componentType;
import static edu.colorado.phet.common.phetcommon.simsharing.messages.UserActions.pressed;

/**
 * Swing check box that sends sim-sharing events.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SimSharingJCheckBox extends JCheckBox {

    private final UserComponent object;

    public SimSharingJCheckBox( UserComponent object ) {
        this.object = object;
    }

    public SimSharingJCheckBox( UserComponent object, Icon icon ) {
        super( icon );
        this.object = object;
    }

    public SimSharingJCheckBox( UserComponent object, Icon icon, boolean selected ) {
        super( icon, selected );
        this.object = object;
    }

    public SimSharingJCheckBox( UserComponent object, String text ) {
        super( text );
        this.object = object;
    }

    public SimSharingJCheckBox( UserComponent object, Action a ) {
        super( a );
        this.object = object;
    }

    public SimSharingJCheckBox( UserComponent object, String text, boolean selected ) {
        super( text, selected );
        this.object = object;
    }

    public SimSharingJCheckBox( UserComponent object, String text, Icon icon ) {
        super( text, icon );
        this.object = object;
    }

    public SimSharingJCheckBox( UserComponent object, String text, Icon icon, boolean selected ) {
        super( text, icon, selected );
        this.object = object;
    }

    @Override protected void fireActionPerformed( ActionEvent event ) {
        SimSharingManager.sendUserMessage( object,
                                           pressed,
                                           componentType( ComponentTypes.checkBox ).param( ParameterKeys.isSelected, isSelected() ) );
        super.fireActionPerformed( event );
    }
}
