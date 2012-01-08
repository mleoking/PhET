// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.view.controls.simsharing;

import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterKeys;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponent;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyCheckBox;

import static edu.colorado.phet.common.phetcommon.simsharing.Parameter.componentType;
import static edu.colorado.phet.common.phetcommon.simsharing.Parameter.param;
import static edu.colorado.phet.common.phetcommon.simsharing.messages.ComponentTypes.checkBox;
import static edu.colorado.phet.common.phetcommon.simsharing.messages.UserActions.pressed;

/**
 * @author Sam Reid
 */
public class SimSharingPropertyCheckBox extends PropertyCheckBox {
    private final UserComponent userComponent;

    public SimSharingPropertyCheckBox( UserComponent userComponent, String text, BooleanProperty property ) {
        super( text, property );
        this.userComponent = userComponent;
    }

    @Override protected void doActionPerformed( SettableProperty<Boolean> property ) {
        sendCheckBoxMessage( userComponent, isSelected() );
        super.doActionPerformed( property );
    }

    //For other check boxes that need to send standardized messages (e.g., as SimSharingJCheckBox should be using this).
    public static void sendCheckBoxMessage( UserComponent userComponent, boolean selected ) {
        SimSharingManager.sendUserEvent( userComponent, pressed, componentType( checkBox ), param( ParameterKeys.isSelected, selected ) );
    }
}