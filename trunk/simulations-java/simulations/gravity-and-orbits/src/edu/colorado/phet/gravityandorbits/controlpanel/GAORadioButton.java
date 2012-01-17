// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.gravityandorbits.controlpanel;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.view.controls.simsharing.SimSharingPropertyRadioButton;

import static edu.colorado.phet.gravityandorbits.controlpanel.GravityAndOrbitsControlPanel.CONTROL_FONT;
import static edu.colorado.phet.gravityandorbits.controlpanel.GravityAndOrbitsControlPanel.FOREGROUND;

/**
 * GAORadioButton provides default fonts and colors for a radio button to be used in Gravity and Orbits.
 *
 * @author Sam Reid
 */
public class GAORadioButton<T> extends SimSharingPropertyRadioButton<T> {
    public GAORadioButton( IUserComponent userComponent, String title, final Property<T> property, final T value ) {
        super( userComponent, title, property, value );
        setFont( CONTROL_FONT );
        setForeground( FOREGROUND );
    }
}