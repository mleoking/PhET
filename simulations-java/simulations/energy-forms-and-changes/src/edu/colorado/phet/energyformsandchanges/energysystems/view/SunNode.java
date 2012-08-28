// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.view.graphics.RoundGradientPaint;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.energyformsandchanges.energysystems.model.Sun;
import edu.umd.cs.piccolo.PNode;

/**
 * Node that represents the sun, clouds, and a slider to control the level
 * of cloudiness in the view.
 *
 * @author John Blanco
 */
public class SunNode extends PositionableFadableModelElementNode {

    public SunNode( final Sun sun, final ModelViewTransform mvt ) {
        super( sun, mvt );
        final double radius = mvt.modelToViewDeltaX( Sun.RADIUS );
        PNode sunNode = new PhetPPath( new Ellipse2D.Double( -radius, -radius, radius * 2, radius * 2 ) ) {{
            setOffset( mvt.modelToViewDelta( Sun.OFFSET_TO_CENTER_OF_SUN ).toPoint2D() );
            setPaint( new RoundGradientPaint( 0, 0, Color.WHITE, new Point2D.Double( radius * 0.7, radius * 0.7 ), new Color( 255, 215, 0 ) ) );
            setStroke( new BasicStroke( 1 ) );
            setStrokePaint( Color.YELLOW );
        }};
        final LightRays lightRays = new LightRays( mvt.modelToViewDelta( Sun.OFFSET_TO_CENTER_OF_SUN ), radius, 500, 40, Color.YELLOW );
        lightRays.addRayBlockingShape( new Rectangle2D.Double( -10, 100, 20, 20 ) );
        lightRays.addRayBlockingShape( new Rectangle2D.Double( 50, -10, 20, 20 ) );
        lightRays.addRayBlockingShape( new Rectangle2D.Double( 50, 50, 20, 20 ) );
        addChild( lightRays );
        addChild( sunNode );
    }
}
