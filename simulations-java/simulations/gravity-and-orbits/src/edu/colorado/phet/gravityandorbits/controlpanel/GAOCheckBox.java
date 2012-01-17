// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.gravityandorbits.controlpanel;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.view.controls.simsharing.SimSharingPropertyCheckBox;

/**
 * GAOCheckBox provides default fonts and colors for a checkbox to be used in Gravity and Orbits.
 *
 * @author Sam Reid
 */
public class GAOCheckBox extends SimSharingPropertyCheckBox {
    public GAOCheckBox( IUserComponent userComponent, String title, final Property<Boolean> property ) {
        super( userComponent, title, property );
        setOpaque( false );
        setFont( GravityAndOrbitsControlPanel.CONTROL_FONT );
        setForeground( GravityAndOrbitsControlPanel.FOREGROUND );
    }
}