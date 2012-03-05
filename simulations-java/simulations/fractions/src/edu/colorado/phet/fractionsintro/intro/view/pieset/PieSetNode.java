// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.intro.view.pieset;

import fj.F;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.nodes.BucketView;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.fractions.view.FNode;
import edu.colorado.phet.fractionsintro.intro.model.pieset.PieSet;
import edu.colorado.phet.fractionsintro.intro.model.pieset.Slice;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform.createSinglePointScaleInvertedYMapping;

/**
 * Renders the pie set node from the given model.  Unconventional way of using piccolo, where the scene graph is recreated any time the model changes.
 * Done to support immutable model and still get efficient reuse of piccolo.
 * <p/>
 * TODO: Rename to ShapeSetNode?
 *
 * @author Sam Reid
 */
public class PieSetNode extends FNode {

    private final BucketView bucketView;

    //Flag for showing the center of a shape.  This can help debug pie piece rotation.
    private static final boolean debugCenter = false;
    public static F<SliceNodeArgs, PNode> NodeToShape = new F<SliceNodeArgs, PNode>() {
        @Override public PNode f( final SliceNodeArgs s ) {
            return new ShapeNode( s.slice );
        }
    };

    public PieSetNode( final SettableProperty<PieSet> model, PNode rootNode ) {
        this( model, rootNode, NodeToShape, CreateEmptyCellsNode );
    }

    //Create a PieSetNode, have to pass in the root node since the scene graph tree is reconstructed each time and you cannot use getDeltaRelativeTo(getParent) since the node may no longer be in the tree
    public PieSetNode( final SettableProperty<PieSet> model, final PNode rootNode, final F<SliceNodeArgs, PNode> createSliceNode, final F<PieSet, PNode> createEmptyCellsNode ) {
        bucketView = new BucketView( model.get().sliceFactory.bucket, createSinglePointScaleInvertedYMapping( new Point(), new Point(), 1 ) );

        model.addObserver( new SimpleObserver() {
            public void update() {
                removeAllChildren();
                addChild( new PieSetContentNode( bucketView, model, createSliceNode, rootNode, createEmptyCellsNode ) );
            }
        } );
    }

    //Creates a shape for showing the empty cells
    public static final F<PieSet, PNode> CreateEmptyCellsNode = new F<PieSet, PNode>() {
        @Override public PNode f( PieSet state ) {
            PNode node = new PNode();
            for ( Slice cell : state.cells ) {
                boolean anythingInPie = state.pieContainsSliceForCell( cell );
                node.addChild( new PhetPPath( cell.getShape(), new BasicStroke( anythingInPie ? 2 : 1 ), anythingInPie ? Color.black : Color.lightGray ) );

                if ( debugCenter ) {
                    node.addChild( new PhetPPath( new Rectangle2D.Double( cell.getCenter().getX(), cell.getCenter().getY(), 2, 2 ) ) );
                }
            }
            return node;
        }
    };
}