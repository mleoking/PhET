// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.control;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.lwjglphet.utils.GLSimSharingPropertyRadioButton;
import edu.colorado.phet.platetectonics.PlateTectonicsResources.Strings;
import edu.colorado.phet.platetectonics.PlateTectonicsSimSharing.UserComponents;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Allows the user to select between automatic and manual mode
 */
public class PlayModePanel extends PNode {
    public PlayModePanel( final Property<Boolean> isAutoMode ) {
        PSwing autoRadioButton = new PSwing( new GLSimSharingPropertyRadioButton<Boolean>( UserComponents.automaticMode, Strings.AUTOMATIC_MODE, isAutoMode, true ) );
        PSwing manualRadioButton = new PSwing( new GLSimSharingPropertyRadioButton<Boolean>( UserComponents.manualMode, Strings.MANUAL_MODE, isAutoMode, false ) );

        addChild( autoRadioButton );
        addChild( manualRadioButton );

        manualRadioButton.setOffset( autoRadioButton.getFullBounds().getMaxX() + 10, 0 );
    }
}
