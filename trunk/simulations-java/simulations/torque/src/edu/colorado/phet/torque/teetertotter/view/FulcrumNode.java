// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.torque.teetertotter.view;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.torque.teetertotter.model.ModelElement;

/**
 * Graphic for the fulcrum, a triangle that the plank pivots about.
 *
 * @author Sam Reid
 */
public class FulcrumNode extends ModelObjectNode {
    public FulcrumNode( final ModelViewTransform mvt, final ModelElement modelObject ) {
        super( mvt, modelObject, new Color( 214, 164, 119 ) );
    }
}
