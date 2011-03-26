//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.densityandbuoyancy.components {
import flash.events.MouseEvent;

import mx.controls.RadioButton;

/**
 * It's a radio button to select the mode, only used for the Density simulation.
 */
public class ModeRadioButton extends RadioButton {
    public function ModeRadioButton( label: String, selected: Boolean, listener: Function ) {
        super();
        this.selected = selected;
        groupName = "modes";
        this.label = label;
        addEventListener( MouseEvent.CLICK, listener );
    }
}
}