//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.densityandbuoyancy.view {
import flash.events.MouseEvent;

import mx.controls.RadioButton;

public class ModeRadioButton extends RadioButton {
    public function ModeRadioButton( label: String, selected: Boolean, listener: Function ) {
        super();
        groupName = "modes";
        this.label = label;
        addEventListener( MouseEvent.CLICK, listener );
    }
}
}