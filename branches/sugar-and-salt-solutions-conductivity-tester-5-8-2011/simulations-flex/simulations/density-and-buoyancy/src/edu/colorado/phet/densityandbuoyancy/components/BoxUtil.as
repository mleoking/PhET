//  Copyright 2002-2011, University of Colorado

package edu.colorado.phet.densityandbuoyancy.components {
import edu.colorado.phet.densityandbuoyancy.DensityAndBuoyancyConstants;

import mx.containers.Box;

public class BoxUtil {
    //Helper function to set the style for a Box used in both horizontal and vertical boxes
    public static function setStyle( box: Box ): void {
        box.setStyle( "backgroundColor", DensityAndBuoyancyConstants.CONTROL_PANEL_COLOR );
        box.setStyle( "borderStyle", "solid" );

        box.setStyle( "cornerRadius", 5 );

        //Padding so the buttons don't touch the edge
        box.setStyle( "paddingTop", 5 );
        box.setStyle( "paddingBottom", 5 );
        box.setStyle( "paddingLeft", 5 );
        box.setStyle( "paddingRight", 5 );
    }
}
}
