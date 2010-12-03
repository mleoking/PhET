/**
 * Created by ${PRODUCT_NAME}.
 * User: Sam
 * Date: 12/3/10
 * Time: 12:43 PM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.densityandbuoyancy.components {
import mx.controls.HSlider;

public class MyHSlider extends HSlider {
    public function MyHSlider() {
    }

    public function doCommitProperties(): void {
        commitProperties();//Until this is called, the getThumbCount() == 1, but getThumbAt(0) is null
    }
}
}
