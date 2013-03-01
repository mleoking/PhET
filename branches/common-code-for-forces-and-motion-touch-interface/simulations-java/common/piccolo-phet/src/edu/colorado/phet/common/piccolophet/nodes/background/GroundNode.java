// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.nodes.background;

import java.awt.Color;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;

/**
 * Node that (when parametrized correctly) can be used to represent grass-covered ground
 * in a simulation.
 *
 * @author John Blanco
 * @author Sam Reid
 */
public class GroundNode extends GradientBackgroundNode {
    public GroundNode( ModelViewTransform mvt, Rectangle2D modelRect, double modelGradientDepth ) {
        this( mvt, modelRect, modelGradientDepth, new Color( 144, 199, 86 ), new Color( 103, 162, 87 ) );
    }

    public GroundNode( ModelViewTransform mvt, Rectangle2D modelRect, double modelGradientDepth, Color topColor, Color bottomColor ) {
        super( mvt, modelRect, topColor, bottomColor, 0, -modelGradientDepth );
    }
}