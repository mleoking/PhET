// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.intro.view;

import fj.F;
import fj.data.List;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.fractionsintro.intro.model.pieset.Pie;
import edu.colorado.phet.fractionsintro.intro.model.pieset.PieSet;
import edu.colorado.phet.fractionsintro.intro.model.pieset.Slice;
import edu.colorado.phet.fractionsintro.intro.view.pieset.PieSetNode;
import edu.colorado.phet.fractionsintro.intro.view.pieset.SliceNodeArgs;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

import static edu.colorado.phet.fractionsintro.intro.view.WaterGlassNode.cachedWaterGlassNode;
import static fj.Ord.doubleOrd;

/**
 * Node for showing draggable water glasses
 *
 * @author Sam Reid
 */
public class WaterGlassSetNode extends PieSetNode {
    public WaterGlassSetNode( SettableProperty<PieSet> model, PNode rootNode, Color color, double width, double height, boolean iconTextOnTheRight ) {
        super( model, rootNode, new WaterGlassNodeFactory(), createEmptyCellsNode( color, width, height ), new F<PieSet, PNode>() {
            @Override public PNode f( final PieSet pieSet ) {
                return createIcon( pieSet, new WaterGlassNodeFactory() );
            }
        }, iconTextOnTheRight );
    }

    //The water glass icon has to use bucket slice since it doesn't have an empty background like the other bucket icon nodes
    public static PNode createIcon( final PieSet state, final F<SliceNodeArgs, PNode> createSliceNode ) {
        return new PNode() {{
            Slice slice = state.sliceFactory.createBucketSlice( state.denominator, 0L );

            //Create the slice.  Wrap the state in a dummy Property to facilitate reuse of MovableSliceNode code.
            //Could be improved by generalizing MovableSliceNode to not require
            final PNode node = createSliceNode.f( new SliceNodeArgs( slice, state.denominator, false ) );
            node.setPickable( false );
            node.setChildPaintInvalid( false );
            addChild( node );

            //Make as large as possible, but small enough that tall representations (like vertical bars) fit
            scale( 0.28 );
        }};
    }

    //Show the empty beakers
    public static F<PieSet, PNode> createEmptyCellsNode( final Color color, final double width, final double height ) {
        return new F<PieSet, PNode>() {
            @Override public PNode f( final PieSet state ) {
                PNode node = new PNode();

                for ( final Pie pie : state.pies ) {
                    final List<Double> centers = pie.cells.map( new F<Slice, Double>() {
                        @Override public Double f( Slice s ) {
                            return s.getShape().getBounds2D().getMinY();
                        }
                    } );

                    //Read from cache instead of creating new each time to improve performance because these graphics are expensive to make
                    node.addChild( new PImage( cachedWaterGlassNode( state.countFilledCells( pie ), state.denominator, color, width, height ) ) {{
                        setOffset( pie.cells.index( 0 ).getShape().getBounds2D().getX(), centers.minimum( doubleOrd ) );
                    }} );
                }
                return node;
            }
        };
    }
}