// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.view.controls.simsharing;

import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.simsharing.components.SimSharingJRadioButton;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponent;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyRadioButton;

/**
 * Radio button that uses property and signifies events with simsharing
 *
 * @author Sam Reid
 */
public class SimSharingPropertyRadioButton<T> extends PropertyRadioButton<T> {

    private final UserComponent userComponent;

    public SimSharingPropertyRadioButton( UserComponent userComponent, final String text, final SettableProperty<T> settableProperty, final T value ) {
        super( text, settableProperty, value );
        this.userComponent = userComponent;
    }

    @Override protected void doActionPerformed() {
        SimSharingJRadioButton.sendEvent( userComponent, isSelected() );
        super.doActionPerformed();
    }
}