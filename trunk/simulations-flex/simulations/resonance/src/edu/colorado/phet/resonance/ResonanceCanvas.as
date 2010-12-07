/**
 * Created by ${PRODUCT_NAME}.
 * User: Sam
 * Date: 12/7/10
 * Time: 9:57 AM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.resonance {
import mx.containers.Canvas;

public class ResonanceCanvas extends Canvas {
    public function ResonanceCanvas() {
    }

    public function init(): void {
        addChild( new Resonance() );
    }
}
}
