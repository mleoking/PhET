//  Copyright 2002-2011, University of Colorado

package edu.colorado.phet.densityandbuoyancy.components {
import edu.colorado.phet.flexcommon.model.BooleanProperty;

import flash.events.Event;

import mx.controls.RadioButton;

/**
 * RadioButton that is wired up to the specified BooleanProperty.
 */
public class MyRadioButton extends RadioButton {
    private static var count: Number = 0;

    public function MyRadioButton( label: String, _selected: BooleanProperty ) {
        this.groupName =
        this.label = label;
        selected = _selected.value;
        addEventListener( Event.CHANGE, function(): void {
            _selected.value = true;
        } );
        _selected.addListener( function(): void {
            selected = _selected.value;
        } );
        count = count + 1;
        groupName = String( count );//we handle button group ourselves with booleanproperties
    }
}
}
