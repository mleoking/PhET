// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.nodes.background;

import java.awt.Color;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;

/**
 * Node that (when parameterized correctly) can be used to represent the sky in a simulation.
 *
 * @author John Blanco
 * @author Sam Reid
 */
public class SkyNode extends GradientBackgroundNode {

    //Default colors to use, made public so that clients can easily use the default (instead of making lots of aux constructors here)
    public static Color DEFAULT_TOP_COLOR = new Color( 1, 172, 228 );
    public static Color DEFAULT_BOTTOM_COLOR = new Color( 208, 236, 251 );

    public SkyNode( ModelViewTransform mvt, Rectangle2D modelRect, double modelGradientHeight ) {
        this( mvt, modelRect, modelGradientHeight, DEFAULT_BOTTOM_COLOR, DEFAULT_TOP_COLOR );
    }

    public SkyNode( ModelViewTransform mvt, Rectangle2D modelRect, double modelGradientHeight, Color bottomColor, Color topColor ) {
        super( mvt, modelRect, bottomColor, topColor, 0, modelGradientHeight );
    }
}