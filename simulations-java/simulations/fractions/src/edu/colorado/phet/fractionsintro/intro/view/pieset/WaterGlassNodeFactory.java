// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.intro.view.pieset;

import fj.F;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Hashtable;

import edu.colorado.phet.common.phetcommon.util.Pair;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.fractionsintro.intro.view.WaterGlassNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

import static edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils.toBufferedImage;

/**
 * Function that creates water glass nodes for PieSetNode.
 *
 * @author Sam Reid
 */
public class WaterGlassNodeFactory extends F<SliceNodeArgs, PNode> {

    //Fix performance by cashing images to avoid calling the WaterGlassNode constructor too many times
    private static final Hashtable<Pair<Integer, Integer>, BufferedImage> nodes = new Hashtable<Pair<Integer, Integer>, BufferedImage>();
    private static final boolean debugDragRegion = false;
    private final Color color;

    public WaterGlassNodeFactory( Color color ) {
        this.color = color;
    }

    @Override public PNode f( final SliceNodeArgs args ) {
        final Pair<Integer, Integer> key = new Pair<Integer, Integer>( 1, args.denominator );
        if ( !nodes.containsKey( key ) ) {
            nodes.put( key, toBufferedImage( new WaterGlassNode( 1, args.denominator, color, 560 * 0.33, 681 * 0.5 ).toImage() ) );
        }

        int c = debugDragRegion ? 100 : 0;
        final Point2D offset = new Point2D.Double( args.slice.shape().getBounds2D().getCenterX() - nodes.get( key ).getWidth() / 2, args.slice.shape().getBounds2D().getCenterY() - nodes.get( key ).getHeight() / 2 );
        PNode node = args.inContainer ? new PhetPPath( new Rectangle2D.Double( 0, 0, nodes.get( key ).getWidth(), nodes.get( key ).getHeight() ), new Color( c, c, c, c ) )
                                      : new PImage( nodes.get( key ) );

        node.setOffset( offset );
        return node;
    }
}