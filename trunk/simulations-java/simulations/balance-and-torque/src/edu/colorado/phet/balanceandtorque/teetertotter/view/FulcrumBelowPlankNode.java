// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.teetertotter.view;

import java.awt.Color;

import edu.colorado.phet.balanceandtorque.teetertotter.model.FulcrumBelowPlank;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;

/**
 * Graphic for the fulcrum, a triangle that the plank pivots about.
 *
 * @author Sam Reid
 */
public class FulcrumBelowPlankNode extends ModelObjectNode {
    public FulcrumBelowPlankNode( final ModelViewTransform mvt, final FulcrumBelowPlank fulcrum ) {
        super( mvt, fulcrum, new Color( 214, 164, 119 ) );
    }
}
