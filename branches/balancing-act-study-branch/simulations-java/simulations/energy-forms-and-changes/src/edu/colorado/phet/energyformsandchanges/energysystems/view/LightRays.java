// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;

/**
 * Class that represents rays of light emitting from a light source.
 *
 * @author John Blanco
 */
public class LightRays extends PNode {

    private static final boolean SHOW_RAY_BLOCKING_SHAPES = false; // For debug.

    private final List<LightRayNode> lightRayNodes = new ArrayList<LightRayNode>();

    public LightRays( final Vector2D center, final double innerRadius, final double outerRadius, final int numRays, final Color color ) {
        this( center, innerRadius, outerRadius, numRays, 0, color );
    }

    public LightRays( final Vector2D center, final double innerRadius, final double outerRadius, final int numRaysForFullCircle, final double darkConeSpanAngle, final Color color ) {

        assert numRaysForFullCircle > 0 && outerRadius > innerRadius && darkConeSpanAngle < Math.PI * 2; // Parameter checking.

        // Create and add the rays.
        for ( int i = 0; i < numRaysForFullCircle; i++ ) {
            double angle = ( Math.PI * 2 / numRaysForFullCircle ) * i;
            final Vector2D rayStartPoint = center.plus( new Vector2D( innerRadius, 0 ).getRotatedInstance( angle ) );
            Vector2D rayEndPoint = center.plus( new Vector2D( outerRadius, 0 ).getRotatedInstance( angle ) );
            if ( angle <= Math.PI / 2 - darkConeSpanAngle / 2 || angle >= Math.PI / 2 + darkConeSpanAngle / 2 ) {
                // Ray is not in the "dark cone", so add it.
                final LightRayNode lightRayNode = new LightRayNode( rayStartPoint, rayEndPoint, color );
                lightRayNodes.add( lightRayNode );
                addChild( lightRayNode );
            }
        }
    }

    public void addLightAbsorbingShape( LightAbsorbingShape lightAbsorbingShape ) {
        for ( LightRayNode lightRayNode : lightRayNodes ) {
            lightRayNode.addLightAbsorbingShape( lightAbsorbingShape );
        }

        // For debug: Show the ray-blocking shapes. Note that these don't get removed if shape gets removed.
        if ( SHOW_RAY_BLOCKING_SHAPES ) {
            addChild( new PhetPPath( lightAbsorbingShape.shape, Color.PINK, new BasicStroke( 2 ), Color.GREEN ) );
        }
    }

    public void removeLightAbsorbingShape( LightAbsorbingShape lightAbsorbingShape ) {
        for ( LightRayNode lightRayNode : lightRayNodes ) {
            lightRayNode.removeLightAbsorbingShape( lightAbsorbingShape );
        }
    }
}
