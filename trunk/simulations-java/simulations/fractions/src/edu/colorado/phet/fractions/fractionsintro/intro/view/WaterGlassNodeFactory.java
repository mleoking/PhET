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
public class WaterGlassNodeFactory extends F<SliceNodeArgs, PNode> {

    public static final boolean debugDragRegion = false;

    //Create images for the full-sized draggable water glasses
    @Override public PNode f( final SliceNodeArgs args ) {
        final Rectangle2D a = args.slice.getShape().getBounds2D();
        final Rectangle2D bounds = new Rectangle2D.Double( a.getX(), a.getY(), 100, 200 );

        BufferedImage image = cachedWaterGlassNode( 1, args.denominator, args.slice.color, bounds.getWidth(), bounds.getHeight() );
        int c = debugDragRegion ? 100 : 0;
        final Point2D offset = new Point2D.Double( bounds.getCenterX() - image.getWidth() / 2, bounds.getCenterY() - image.getHeight() / 2 );
        PNode node = args.inContainer ? new PhetPPath( new Rectangle2D.Double( 0, 0, image.getWidth(), image.getHeight() ), new Color( c, c, c, c ) )
                                      : new PImage( image );

        node.setOffset( offset );
        return node;
    }
}