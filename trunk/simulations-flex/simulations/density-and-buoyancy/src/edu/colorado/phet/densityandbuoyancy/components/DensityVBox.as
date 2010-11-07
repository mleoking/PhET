package edu.colorado.phet.densityandbuoyancy.components {
import edu.colorado.phet.densityandbuoyancy.DensityConstants;

import mx.containers.Box;
import mx.containers.VBox;

public class DensityVBox extends VBox {
    public function DensityVBox() {
        super();
        init( this );
    }

    public static function init( box: Box ): void {
        box.setStyle( "backgroundColor", DensityConstants.CONTROL_PANEL_COLOR );
        box.setStyle( "borderStyle", "solid" );

        //Padding so the buttons don't touch the edge
        box.setStyle( "paddingTop", 5 );
        box.setStyle( "paddingBottom", 5 );
        box.setStyle( "paddingLeft", 5 );
        box.setStyle( "paddingRight", 5 );
    }
}
}