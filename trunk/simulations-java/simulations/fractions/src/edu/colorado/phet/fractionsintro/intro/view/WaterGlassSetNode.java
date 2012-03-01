// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.intro.view;

import fj.F;
import fj.data.List;
import lombok.Data;

import java.awt.Color;
import java.awt.Image;

import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.fractionsintro.intro.model.pieset.Pie;
import edu.colorado.phet.fractionsintro.intro.model.pieset.PieSet;
import edu.colorado.phet.fractionsintro.intro.model.pieset.Slice;
import edu.colorado.phet.fractionsintro.intro.view.pieset.PieSetNode;
import edu.colorado.phet.fractionsintro.intro.view.pieset.WaterGlassNodeFactory;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

import static edu.colorado.phet.fractions.util.Cache.cache;
import static fj.Ord.doubleOrd;

/**
 * Node for showing draggable water glasses
 *
 * @author Sam Reid
 */
public class WaterGlassSetNode extends PieSetNode {
    public WaterGlassSetNode( SettableProperty<PieSet> model, PNode rootNode, Color color, double width, double height ) {
        super( model, rootNode, new WaterGlassNodeFactory(), createEmptyCellsNode( color, width, height ) );
    }

    public static final @Data class Args {
        public final int numFilled;
        public final int denominator;
        public final Color color;
        public final double width;
        public final double height;
    }

    //Improve application runtime about 20% by caching these images
    public static F<Args, Image> nodeMaker = cache( new F<Args, Image>() {
        @Override public Image f( final Args args ) {
            return new WaterGlassNode( args.numFilled, args.denominator, args.color, args.width, args.height ).toImage();
        }
    } );

    //Show the empty beakers
    public static F<PieSet, PNode> createEmptyCellsNode( final Color color, final double width, final double height ) {
        return new F<PieSet, PNode>() {
            @Override public PNode f( final PieSet state ) {
                PNode node = new PNode();

                for ( final Pie pie : state.pies ) {
                    final List<Double> centers = pie.cells.map( new F<Slice, Double>() {
                        @Override public Double f( Slice s ) {
                            return s.shape().getBounds2D().getMinY();
                        }
                    } );

                    //Read from cache like WaterGlassNodeFactory instead of creating new each time to improve performance
                    node.addChild( new PImage( nodeMaker.f( new Args( state.countFilledCells( pie ), state.denominator, color, width, height ) ) ) {{
                        setOffset( pie.cells.index( 0 ).shape().getBounds2D().getX(), centers.minimum( doubleOrd ) );
                    }} );
                }
                return node;
            }
        };
    }
}