//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.densityandbuoyancy.components {
import mx.containers.HBox;

/**
 * HBox that uses the default stylings, see DensityVBox.init
 */
public class DensityHBox extends HBox {
    public function DensityHBox() {
        super();
        DensityVBox.init( this );//REVIEW very confusing at first, general style initializer shouldn't be in DensityVBox
    }
}
}