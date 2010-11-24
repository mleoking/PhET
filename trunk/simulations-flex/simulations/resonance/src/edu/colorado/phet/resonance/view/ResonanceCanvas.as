/**
 * Created by ${PRODUCT_NAME}.
 * User: Sam
 * Date: 11/22/10
 * Time: 8:02 AM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.resonance.view {
import flash.events.Event;
import flash.events.MouseEvent;

import mx.containers.Canvas;
import mx.controls.Button;

public class ResonanceCanvas extends Canvas {
    public function ResonanceCanvas() {
        var button: Button = new Button();
        button.label = "Resonance sim";
        button.addEventListener(MouseEvent.CLICK,buttonPressed);


        var mass1:Mass = new Mass();
        addChild( button );
    }

    public function buttonPressed(event:Event): void {
        trace("pressed")
        var b:Button = new Button();
        b.label = "new button!";
        addChild(b);
    }

    public function init(): void {
    }
}
}
