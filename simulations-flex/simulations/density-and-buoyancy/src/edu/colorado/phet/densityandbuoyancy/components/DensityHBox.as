package edu.colorado.phet.densityandbuoyancy.components {
import mx.containers.HBox;

/**
 * HBox that uses the default stylings, see DensityVBox.init
 */
public class DensityHBox extends HBox {
    public function DensityHBox() {
        super();
        DensityVBox.init( this );
    }
}
}