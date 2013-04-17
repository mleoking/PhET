// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.fractionsintro.intro.view;

import fj.F;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.fractions.fractionsintro.intro.view.pieset.SliceNodeArgs;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

import static edu.colorado.phet.fractions.fractionsintro.intro.view.WaterGlassNode.cachedWaterGlassNode;

/**
 * Function that creates water glass nodes for PieSetNode.
 *
 * @author Sam Reid
 */
class WaterGlassNodeFactory extends F<SliceNodeArgs, PNode> {

    private static final boolean debugDragRegion = false;

    //Create images for the full-sized draggable water glasses
    @Override public PNode f( final SliceNodeArgs args ) {

        final Rectangle2D bounds = new Rectangle2D.Double( args.slice.position.x - 50, args.slice.position.y - 100, 100, 200 );

        BufferedImage image = cachedWaterGlassNode( 1, args.denominator, args.slice.color, bounds.getWidth(), bounds.getHeight() );
        int c = debugDragRegion ? 100 : 0;

        //The bounds for the grabbable area when the beaker fluid is in the container.  Values were hard coded using empirical testing.
        final Point2D offset = new Point2D.Double( bounds.getCenterX() - image.getWidth() / 2, bounds.getCenterY() - image.getHeight() / 2 );

        //The bounds for the grabbable area when the beaker fluid is in the container.  Values were hard coded using empirical testing.
        final Rectangle2D.Double inContainerBounds = new Rectangle2D.Double( offset.getX(), image.getHeight() * 0.9, image.getWidth(), image.getHeight() );

        //Hack alert! See MovableSliceNode for a reason this must return PhetPPath when in container.
        return args.inContainer ? new PhetPPath( inContainerBounds, new Color( c, c, c, c ) )
                                : new PImage( image ) {{
            setOffset( offset );
        }};
    }
}