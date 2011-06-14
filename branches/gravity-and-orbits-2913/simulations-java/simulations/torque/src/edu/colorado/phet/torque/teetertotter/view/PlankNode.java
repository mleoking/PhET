// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.torque.teetertotter.view;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.torque.teetertotter.model.ModelObject;

/**
 * Graphic for the fulcrum, a triangle that the plank pivots about.
 *
 * @author Sam Reid
 */
public class PlankNode extends ModelObjectNode {
    public PlankNode( final ModelViewTransform mvt, final ModelObject plank ) {
        super( mvt, plank, new Color( 243, 203, 127 ) );
    }
}
