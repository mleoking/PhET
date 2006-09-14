/** Sam Reid*/
package edu.colorado.phet.cck3.phetgraphics_cck.circuit.components;

import edu.colorado.phet.cck3.model.components.CircuitComponent;
import edu.colorado.phet.common_cck.view.graphics.transforms.ModelViewTransform2D;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: May 25, 2004
 * Time: 8:34:54 PM
 * Copyright (c) May 25, 2004 by Sam Reid
 */
public class SchematicBatteryGraphic extends SchematicPlatedGraphic {

    public SchematicBatteryGraphic( Component parent, CircuitComponent component, ModelViewTransform2D transform,
                                    double wireThickness ) {
        super( parent, component, transform, wireThickness, 0.395, 3, 1.75 );
    }

}
