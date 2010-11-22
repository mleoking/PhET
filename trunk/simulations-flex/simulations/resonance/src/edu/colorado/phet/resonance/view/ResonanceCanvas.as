/**
 * Created by ${PRODUCT_NAME}.
 * User: Sam
 * Date: 11/22/10
 * Time: 8:02 AM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.resonance.view {
import mx.containers.Canvas;
import mx.controls.Button;

public class ResonanceCanvas extends Canvas {
    public function ResonanceCanvas() {
        var button: Button = new Button();
        button.label = "Hello there";
        addChild( button );
    }

    public function init(): void {
    }
}
}
