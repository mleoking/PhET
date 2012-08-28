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
 * Class that represents rays of light in the view.
 *
 * @author John Blanco
 */
public class LightRays extends PNode {

    private static Stroke RAY_STROKE = new BasicStroke( 2 );

    public LightRays( Vector2D center, double innerRadius, double outerRadius, int numRays, Color color ) {
        assert numRays > 0 && outerRadius > innerRadius; // Parameter checking.
        for ( int i = 0; i < numRays; i++ ) {
            double angle = ( Math.PI * 2 / numRays ) * i;
            Vector2D rayStartPoint = new Vector2D( innerRadius, 0 ).getRotatedInstance( angle );
            Vector2D rayEndPoint = new Vector2D( outerRadius, 0 ).getRotatedInstance( angle );
            addChild( new PhetPPath( new Line2D.Double( rayStartPoint.toPoint2D(), rayEndPoint.toPoint2D() ), RAY_STROKE, color ) );
        }
        setOffset( center.toPoint2D() );
    }
}
