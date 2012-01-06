// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.view.controls.simsharing;

import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingConstants;
import edu.colorado.phet.common.phetcommon.simsharing.components.SimSharingJRadioButton;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyRadioButton;

/**
 * Radio button that uses property and signifies events with simsharing
 *
 * @author Sam Reid
 */
public class SimSharingPropertyRadioButton<T> extends PropertyRadioButton<T> {

    private final SimSharingConstants.User.UserComponent userComponent;

    public SimSharingPropertyRadioButton( SimSharingConstants.User.UserComponent userComponent, final String text, final SettableProperty<T> settableProperty, final T value ) {
        super( text, settableProperty, value );
        this.userComponent = userComponent;
    }

    @Override protected void buttonPressed() {
        super.buttonPressed();
        SimSharingJRadioButton.sendEvent( userComponent );
    }
}