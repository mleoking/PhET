// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.intro.view.pieset;

import fj.F;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.nodes.BucketView;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.fractionsintro.intro.model.pieset.PieSet;
import edu.colorado.phet.fractionsintro.intro.model.pieset.Slice;
import edu.colorado.phet.fractionsintro.intro.view.FractionNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.nodes.PClip;

import static edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform.createSinglePointScaleInvertedYMapping;

/**
 * Renders the pie set node from the given model.  Unconventional way of using piccolo, where the scene graph is recreated any time the model changes.
 * Done to support immutable model and still get efficient reuse of piccolo.
 *
 * @author Sam Reid
 */
public class PieSetNode extends PNode {

    private final BucketView bucketView;
    private final PNode rootNode;
    private static final boolean debugCenter = false;

    public PieSetNode( final SettableProperty<PieSet> model, PNode rootNode ) {
        this( model, rootNode, new F<SliceNodeArgs, PNode>() {
            @Override public PNode f( SliceNodeArgs s ) {
                return new ShapeNode( s.slice );
            }
        } );
    }

    //Create a PieSetNode, have to pass in the root node since the scene graph tree is reconstructed each time and you cannot use getDeltaRelativeTo(getParent) since the node may no longer be in the tree
    public PieSetNode( final SettableProperty<PieSet> model, PNode rootNode, final F<SliceNodeArgs, PNode> createSliceNode ) {
        this.rootNode = rootNode;

        bucketView = new BucketView( model.get().sliceFactory.bucket, createSinglePointScaleInvertedYMapping( new Point(), new Point(), 1 ) );

        model.addObserver( new SimpleObserver() {
            public void update() {
                rebuildScene( model, createSliceNode );
            }
        } );
    }

    private void rebuildScene( final SettableProperty<PieSet> model, final F<SliceNodeArgs, PNode> createSliceNode ) {
        removeAllChildren();

        final PNode bucketHoleNode = bucketView.getHoleNode();
        addChild( bucketHoleNode );

        final PieSet state = model.get();

        //Show graphics for the empty cells
        for ( Slice cell : state.cells ) {
            boolean anythingInPie = state.pieContainsSliceForCell( cell );
            addChild( new PhetPPath( cell.shape(), new BasicStroke( anythingInPie ? 2 : 1 ), anythingInPie ? Color.black : Color.lightGray ) );

            if ( debugCenter ) {
                addChild( new PhetPPath( new Rectangle2D.Double( cell.center().getX(), cell.center().getY(), 2, 2 ) ) );
            }
        }

        //Show graphics for the movable cells.  Put in a clip so that long bars will look like they sink through a "bottomless" bucket since they are too big at full size
        PClip movablePiecesLayer = new PClip() {{
            setStroke( null );

            //Create a clip that is far enough away that it will work for a variety of screen resolutions
            double far = 10000;

            setPathTo( new Rectangle2D.Double( -far, -far, far * 2, far + bucketHoleNode.getFullBoundsReference().getMaxY() ) );
            for ( final Slice slice : state.slices ) {
                addChild( new MovableSliceNode( createSliceNode.f( new SliceNodeArgs( slice, model.get().denominator ) ), rootNode, model, slice ) );
            }
        }};
        addChild( movablePiecesLayer );

        addChild( bucketView.getFrontNode() );

        //Show an icon label on the bucket so the user knows what is in the bucket
        PNode icon = new PNode() {{
            final int denominator = model.get().denominator;
            for ( int i = 0; i < denominator; i++ ) {
                Slice cell = model.get().sliceFactory.createPieCell( model.get().pies.length(), 0, i, denominator );
                addChild( new PhetPPath( cell.shape(), Color.white, new BasicStroke( 3 ), Color.black ) );
            }

            Slice slice = model.get().sliceFactory.createPieCell( model.get().pies.length(), 0, 0, denominator );
            PNode node = new MovableSliceNode( createSliceNode.f( new SliceNodeArgs( slice, model.get().denominator ) ), rootNode, model, slice );
            node.setPickable( false );
            node.setChildPaintInvalid( false );
            addChild( node );

            //Make as large as possible, but small enough that tall representations (like vertical bars) fit
            scale( 0.28 );
        }};

        PNode text = new FractionNode( new Property<Integer>( 1 ), new Property<Integer>( model.get().denominator ) ) {{
            scale( 0.2 );
        }};
        PNode iconAndText = new HBox( icon, text );

        addChild( new ZeroOffsetNode( iconAndText ) {{
            centerFullBoundsOnPoint( bucketView.getFrontNode().getFullBounds().getCenterX(), bucketView.getFrontNode().getFullBounds().getCenterY() + 4 );
        }} );
    }
}