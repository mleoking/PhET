// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.intro.view.pieset;

import fj.F;

import java.awt.image.BufferedImage;
import java.util.Hashtable;

import edu.colorado.phet.common.phetcommon.util.Pair;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.fractionsintro.intro.view.WaterGlassNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * Function that creates water glass nodes for PieSetNode.
 *
 * @author Sam Reid
 */
public class WaterGlassNodeFactory extends F<SliceNodeArgs, PNode> {

    //Fix performance by cashing images to avoid calling the WaterGlassNode constructor too many times
    private static final Hashtable<Pair<Integer, Integer>, BufferedImage> nodes = new Hashtable<Pair<Integer, Integer>, BufferedImage>();

    @Override public PNode f( final SliceNodeArgs args ) {
        final Pair<Integer, Integer> key = new Pair<Integer, Integer>( 1, args.denominator );
        if ( !nodes.containsKey( key ) ) {
            WaterGlassNode node = new WaterGlassNode( 1, args.denominator );
            nodes.put( key, BufferedImageUtils.toBufferedImage( node.toImage() ) );
        }

        return new PImage( nodes.get( key ) ) {{
            setOffset( args.slice.shape().getBounds2D().getCenterX() - getFullBounds().getWidth() / 2, args.slice.shape().getBounds2D().getCenterY() - getFullBounds().getHeight() / 2 );
            setVisible( !args.inContainer );
        }};
    }
}