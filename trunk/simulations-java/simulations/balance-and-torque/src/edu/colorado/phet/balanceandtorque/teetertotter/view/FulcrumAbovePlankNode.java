// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.teetertotter.view;

import java.awt.Color;

import edu.colorado.phet.balanceandtorque.teetertotter.model.FulcrumAbovePlank;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;

/**
 * Graphic for the type of fulcrum where the pivot point is above the plank.
 *
 * @author John Blanco
 */
public class FulcrumAbovePlankNode extends ModelObjectNode {
    public FulcrumAbovePlankNode( final ModelViewTransform mvt, final FulcrumAbovePlank fulcrumShape ) {
        super( mvt, fulcrumShape, new Color( 240, 240, 0 ) );
    }
}
