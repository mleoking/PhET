// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.view;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.umd.cs.piccolo.PNode;

/**
 * Class that represents a ray of light in the view.  Rays of light can have
 * shapes that reduce or block the amount of light passing through.
 *
 * @author John Blanco
 */
public class LightRayNode extends PNode {
    private static final double STROKE_THICKNESS = 2;
    private final List<FadingLineNode> lines = new ArrayList<FadingLineNode>();

    public LightRayNode( Vector2D origin, Vector2D endpoint, Color color ) {
        lines.add( new FadingLineNode( origin, endpoint, color, 0.5, STROKE_THICKNESS ) );
        addChild( lines.get( 0 ) );
    }
}
