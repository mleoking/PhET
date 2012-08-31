// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.geom.Line2D;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;

/**
 * Class that represents a ray of light in the view.  Rays of light can have
 * shapes that reduce or block the amount of light passing through.
 *
 * @author John Blanco
 */
public class LightRayNode extends PNode {
    private static final Stroke STROKE = new BasicStroke( 2 );

    public LightRayNode( Vector2D origin, Vector2D endpoint, Color color ) {
        addChild( new PhetPPath( new Line2D.Double( origin.toPoint2D(), endpoint.toPoint2D() ), STROKE, color ) );
    }
}
