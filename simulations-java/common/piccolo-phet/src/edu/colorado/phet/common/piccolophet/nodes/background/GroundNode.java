// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.nodes.background;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;

/**
 * Node that (when parameterized correctly) can be used to represent grass-covered ground
 * in a simulation.
 *
 * @author John Blanco
 * @author Sam Reid
 */
public class GroundNode extends GradientBackgroundNode {
    public GroundNode( ModelViewTransform mvt, Rectangle2D modelRect, double modelGradientDepth ) {
        super( mvt, modelRect, new Color( 144, 199, 86 ), new Color( 103, 162, 87 ), 0, -modelGradientDepth );
    }
}
