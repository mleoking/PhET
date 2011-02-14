// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.gravityandorbits.controlpanel;

import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyRadioButton;

/**
 * GAORadioButton provides default fonts and colors for a radio button to be used in Gravity and Orbits.
 *
 * @author Sam Reid
 */
public class GAORadioButton<T> extends PropertyRadioButton<T> {
    public GAORadioButton( String title, final Property<T> property, final T value ) {
        super( title, property, value );
        setOpaque( false );//TODO: is this a mac problem?
        setFont( GravityAndOrbitsControlPanel.CONTROL_FONT );
        setForeground( GravityAndOrbitsControlPanel.FOREGROUND );
    }
}