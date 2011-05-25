// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.nodes.background;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;

/**
 * Node that (when parameterized correctly) can be used to represent the sky in a simulation.
 *
 * @author John Blanco
 * @author Sam Reid
 */
public class SkyNode extends GradientBackgroundNode {
    public SkyNode( ModelViewTransform mvt, Rectangle2D modelRect, double modelGradientHeight ) {
        super( mvt, modelRect, new Color( 208, 236, 251 ), new Color( 1, 172, 228 ), 0, modelGradientHeight );
    }
}
