// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.view;

import java.awt.Color;
import java.awt.geom.Ellipse2D;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;

/**
 * Class that represents rays of light in the view.
 *
 * @author John Blanco
 */
public class LightRays extends PNode {

    public LightRays( Vector2D center, double innerRadius, double outerRadius, int numRays, Color color ) {
        addChild( new PhetPPath( new Ellipse2D.Double( -outerRadius, -outerRadius, outerRadius * 2, outerRadius * 2 ), color ) );
        setOffset( center.toPoint2D() );
    }
}
