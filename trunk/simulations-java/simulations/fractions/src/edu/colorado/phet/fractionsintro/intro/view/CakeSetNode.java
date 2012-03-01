// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.intro.view;

import fj.F;
import lombok.Data;

import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.fractions.util.Cache;
import edu.colorado.phet.fractionsintro.intro.model.pieset.PieSet;
import edu.colorado.phet.fractionsintro.intro.view.pieset.PieSetNode;
import edu.colorado.phet.fractionsintro.intro.view.pieset.SliceNodeArgs;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

import static edu.colorado.phet.fractions.FractionsResources.RESOURCES;
import static edu.colorado.phet.fractions.util.Cache.cache;
import static edu.colorado.phet.fractionsintro.intro.view.CakeNode.cropSides;

/**
 * Node for showing a set of draggable 3d cakes
 *
 * @author Sam Reid
 */
public class CakeSetNode extends PieSetNode {

    static @Data class Arg {
        final int cell;
        final int denominator;
    }

    //Cache for performance, to avoid cropping each time
    static final Cache<Arg, BufferedImage> cakeImages = cache( new F<Arg, BufferedImage>() {
        @Override public BufferedImage f( Arg a ) {
            return cropSides( RESOURCES.getImage( "cake/cake_" + a.denominator + "_" + ( a.cell + 1 ) + ".png" ) );
        }
    } );

    public CakeSetNode( SettableProperty<PieSet> cakeSet, PNode rootNode ) {
        super( cakeSet, rootNode, new F<SliceNodeArgs, PNode>() {
            @Override public PNode f( final SliceNodeArgs a ) {
                int cell = a.slice.cell( a.denominator );
                return new PImage( cakeImages.f( new Arg( cell, a.denominator ) ) ) {{
                    //Center on the slice tip because each image is padded to the amount of a full cake
                    double fudgeY = getFullBounds().getHeight() / 4;
                    setOffset( a.slice.position.getX() - getFullBounds().getWidth() / 2, a.slice.position.getY() - getFullBounds().getHeight() / 2 - fudgeY );
                }};
            }
        }, CreateEmptyCellsNode );
    }
}
