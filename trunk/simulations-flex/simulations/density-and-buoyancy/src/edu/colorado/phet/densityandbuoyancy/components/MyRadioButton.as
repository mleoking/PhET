//  Copyright 2002-2011, University of Colorado

/**
 * Created by ${PRODUCT_NAME}.
 * User: Sam
 * Date: 12/6/10
 * Time: 3:19 AM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.densityandbuoyancy.components {
import edu.colorado.phet.flexcommon.model.BooleanProperty;

import flash.events.Event;

import mx.controls.RadioButton;

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
