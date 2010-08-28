package edu.colorado.phet.densityandbuoyancy.components {
import edu.colorado.phet.densityandbuoyancy.DensityConstants;

import mx.containers.VBox;

public class DensityVBox extends VBox {
    public function DensityVBox() {
        super();
        setStyle("backgroundColor", DensityConstants.CONTROL_PANEL_COLOR);
        setStyle("borderStyle", "solid");

        //Padding so the buttons don't touch the edge
        setStyle("paddingTop", 5);
        setStyle("paddingBottom", 5);
        setStyle("paddingLeft", 5);
        setStyle("paddingRight", 5);
    }
}
}