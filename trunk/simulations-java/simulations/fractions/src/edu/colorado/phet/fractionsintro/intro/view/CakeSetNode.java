// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.intro.view;

import fj.F;
import lombok.Data;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.fractions.util.Cache;
import edu.colorado.phet.fractions.util.immutable.Vector2D;
import edu.colorado.phet.fractions.view.FNode;
import edu.colorado.phet.fractionsintro.intro.model.pieset.PieSet;
import edu.colorado.phet.fractionsintro.intro.model.pieset.Slice;
import edu.colorado.phet.fractionsintro.intro.model.pieset.factories.CakeSliceFactory;
import edu.colorado.phet.fractionsintro.intro.view.pieset.PieSetNode;
import edu.colorado.phet.fractionsintro.intro.view.pieset.SliceNodeArgs;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

import static edu.colorado.phet.fractions.FractionsResources.RESOURCES;
import static edu.colorado.phet.fractions.util.Cache.cache;

/**
 * Node for showing a set of draggable 3d cakes
 *
 * @author Sam Reid
 */
public class CakeSetNode extends PieSetNode {

    public static @Data class Arg {
        final int cell;
        final int denominator;
    }

    //Cache for performance, to avoid cropping each time
    static final Cache<Arg, BufferedImage> cakeImages = cache( new F<Arg, BufferedImage>() {
        @Override public BufferedImage f( Arg a ) {
            return cropAndTrim( RESOURCES.getImage( "cake/cake_" + a.denominator + "_" + ( a.cell + 1 ) + ".png" ) );
        }
    } );

    public CakeSetNode( SettableProperty<PieSet> cakeSet, PNode rootNode, boolean iconTextOnTheRight ) {
        super( cakeSet, rootNode, createSliceNode, CreateEmptyCellsNode, new F<PieSet, PNode>() {
            @Override public PNode f( final PieSet pieSet ) {
                return PieSetNode.createBucketIcon( pieSet, createSliceNode );
            }
        }, iconTextOnTheRight
        );
    }

    public static F<SliceNodeArgs, PNode> createSliceNode = new F<SliceNodeArgs, PNode>() {
        @Override public PNode f( final SliceNodeArgs a ) {
            return new SliceImage( a.slice.cell( a.denominator ), a );
        }
    };

    public static final F<PieSet, PNode> CreateEmptyCellsNode = new F<PieSet, PNode>() {
        @Override public PNode f( final PieSet state ) {
            return new FNode( state.cells.map( new F<Slice, Vector2D>() {
                @Override public Vector2D f( final Slice s ) {
                    return s.position;
                }
            } ).nub().map( new F<Vector2D, PNode>() {
                @Override public PNode f( final Vector2D vector2D ) {
                    return new PImage( cropAndTrim( RESOURCES.getImage( "cake/cake_grid_" + state.denominator + ".png" ) ) ) {{

                        //#3314: centerFullBoundsOnPoint gets full bounds, which invalidates paint around (0,0).  Have to move closer to its final location before calling centerBoundsOnPoint
                        setOffset( vector2D.x, vector2D.y );
                        setOffset( vector2D.x - getFullBounds().getWidth() / 2, vector2D.y - getFullBounds().getHeight() / 2 - 40 );
                    }};
                }
            } ) );
        }
    };

    //Class for slice images, to make it convenient to use in super call in constructor
    static class SliceImage extends PImage {
        SliceImage( final int cell, final SliceNodeArgs a ) {
            //Center on the slice tip because each image is padded to the amount of a full cake
            super( cakeImages.f( new Arg( cell, a.denominator ) ) );
            double fudgeY = getFullBounds().getHeight() / 4;

            //#3314: centerFullBoundsOnPoint gets full bounds, which invalidates paint around (0,0).  Have to move closer to its final location before calling centerBoundsOnPoint
            setOffset( a.slice.position.getX(), a.slice.position.getY() );
            setOffset( a.slice.position.getX() - getFullBounds().getWidth() / 2, a.slice.position.getY() - getFullBounds().getHeight() / 2 - fudgeY );
        }
    }

    //trim whitespace on the sides of the image so cakes don't overlap (or else space out the cakes more)
    //Trim the sides since there is too much alpha left over in the images and it causes them to overlap so that mouse presses are caught by adjacent cakes instead of the desired cake
    private static BufferedImage cropAndTrim( BufferedImage image ) {
        final int TRIM = 24;
        BufferedImage im = new BufferedImage( image.getWidth() - TRIM * 2, image.getHeight(), image.getType() );
        Graphics2D g2 = im.createGraphics();
        g2.drawRenderedImage( image, AffineTransform.getTranslateInstance( -TRIM, 0 ) );
        g2.dispose();
        return BufferedImageUtils.multiScaleToWidth( im, (int) ( im.getWidth() * CakeSliceFactory.CAKE_SIZE_SCALE ) );
    }
}
