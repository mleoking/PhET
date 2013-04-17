// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.view;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.bendinglight.model.LightRay;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;

import static java.lang.Math.sqrt;

/**
 * Piccolo node for drawing a single light ray.
 *
 * @author Sam Reid
 */
public class LightRayNode extends PNode {
    private final LightRay lightRay;
    private Point2D viewStart;
    private Point2D viewEnd;

    public LightRayNode( final ModelViewTransform transform, final LightRay lightRay ) {
        this.lightRay = lightRay;
        Color color = lightRay.getColor();
        PhetPPath path = new PhetPPath( new BasicStroke( (float) transform.modelToViewDeltaX( lightRay.getRayWidth() ) ),
                                        new Color( color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, (float) sqrt( lightRay.getPowerFraction() ) ) ) {{
            //Update the view coordinates for the start and end of this ray
            viewStart = transform.modelToView( lightRay.tip.toPoint2D() );
            viewEnd = transform.modelToView( lightRay.tail.toPoint2D() );

            //Update the PPath
            setPathTo( getLine() );
        }};
        //Add the PPath
        addChild( path );

        //User cannot interact with the light ray directly
        setPickable( false );
        setChildrenPickable( false );
    }

    //Get the line traversed by this light ray in view coordinates, for usage with the Bresenham algorithm in the WhiteLightNode
    public Line2D.Double getLine() {
        return new Line2D.Double( viewStart, viewEnd );
    }

    public Color getColor() {
        return lightRay.getColor();
    }

    public LightRay getLightRay() {
        return lightRay;
    }
}
