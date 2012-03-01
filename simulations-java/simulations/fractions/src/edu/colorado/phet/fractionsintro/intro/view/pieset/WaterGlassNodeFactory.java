// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.intro.view.pieset;

import fj.F;
import lombok.Data;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.fractionsintro.intro.view.WaterGlassNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

import static edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils.toBufferedImage;
import static edu.colorado.phet.fractions.util.Cache.cache;

/**
 * Function that creates water glass nodes for PieSetNode.
 *
 * @author Sam Reid
 */
public class WaterGlassNodeFactory extends F<SliceNodeArgs, PNode> {

    //Fix performance by cashing images to avoid calling the WaterGlassNode constructor too many times
    private static final boolean debugDragRegion = false;

    public static @Data class Args {
        public final int denominator;
        public final Color color;
        public final double width;
        public final double height;
    }

    private static final F<Args, BufferedImage> images = cache( new F<Args, BufferedImage>() {
        @Override public BufferedImage f( final Args args ) {
            return toBufferedImage( new WaterGlassNode( 1, args.denominator, args.color, args.width, args.height ).toImage() );
        }
    } );

    //Create images for the full-sized draggable water glasses
    @Override public PNode f( final SliceNodeArgs args ) {

        final Rectangle2D bounds = args.slice.shape().getBounds2D();
        Args a = new Args( args.denominator, args.slice.color, bounds.getWidth(), bounds.getHeight() );

        BufferedImage image = images.f( a );
        int c = debugDragRegion ? 100 : 0;
        final Point2D offset = new Point2D.Double( bounds.getCenterX() - image.getWidth() / 2, bounds.getCenterY() - image.getHeight() / 2 );
        PNode node = args.inContainer ? new PhetPPath( new Rectangle2D.Double( 0, 0, image.getWidth(), image.getHeight() ), new Color( c, c, c, c ) )
                                      : new PImage( image );

        node.setOffset( offset );
        return node;
    }
}