// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.view.controls.valuecontrol;

import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterKeys;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponent;

import static edu.colorado.phet.common.phetcommon.simsharing.Parameter.param;
import static edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager.sendUserMessage;
import static edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterKeys.*;
import static edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterValues.*;
import static edu.colorado.phet.common.phetcommon.simsharing.messages.UserActions.*;

/**
 * Logarithmic value control that also sends simsharing event messages.
 *
 * @author Sam Reid
 */
public class SimSharingLogarithmicValueControl extends LogarithmicValueControl {
    private final UserComponent userComponent;

    public SimSharingLogarithmicValueControl( UserComponent userComponent, double min, double max, String label, String textFieldPattern, String units ) {
        super( min, max, label, textFieldPattern, units );
        this.userComponent = userComponent;
    }

    public SimSharingLogarithmicValueControl( UserComponent userComponent, double min, double max, String label, String textFieldPattern, String units, ILayoutStrategy layoutStrategy ) {
        super( min, max, label, textFieldPattern, units, layoutStrategy );
        this.userComponent = userComponent;
    }

    //For sim sharing
    //override with SimSharing*ValueControl classes to send messages at the right times
    protected void valueCorrected() {
        sendUserMessage( userComponent, invalidInput, param( correctedValue, getValue() ) );
        super.valueCorrected();
    }

    protected void sliderDragged( double modelValue ) {
        sendUserMessage( userComponent, drag, param( value, modelValue ) );
        super.sliderDragged( modelValue );
    }

    protected void focusLostInvalidInput() {
        sendUserMessage( userComponent, focusLostInvalidInput, param( correctedValue, getValue() ) );
        super.focusLostInvalidInput();
    }

    protected void userFocusLost( double value ) {
        sendUserMessage( userComponent, focusLost, param( ParameterKeys.value, value ) );
        super.userFocusLost( value );
    }

    protected void userPressedEnter( double value ) {
        sendUserMessage( userComponent, pressedKey, param( key, enter ).param( ParameterKeys.value, value ) );
        super.userPressedEnter( value );
    }

    public void downPressed( double value ) {
        sendUserMessage( userComponent, pressedKey, param( key, down ).param( ParameterKeys.value, value ) );
        super.downPressed( value );
    }

    protected void upPressed( double value ) {
        sendUserMessage( userComponent, pressedKey, param( key, up ).param( ParameterKeys.value, value ) );
        super.upPressed( value );
    }
}
