// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.intro.view;

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
import edu.umd.cs.piccolo.PNode;

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

    //Create a PieSetNode, have to pass in the root node since the scene graph tree is reconstructed each time and you cannot use getDeltaRelativeTo(getParent) since the node may no longer be in the tree
    public PieSetNode( final SettableProperty<PieSet> model, PNode rootNode ) {
        this.rootNode = rootNode;

        bucketView = new BucketView( model.get().sliceFactory.bucket, createSinglePointScaleInvertedYMapping( new Point(), new Point(), 1 ) );

        model.addObserver( new SimpleObserver() {
            public void update() {
                rebuildScene( model );
            }
        } );
    }

    private void rebuildScene( final SettableProperty<PieSet> model ) {
        removeAllChildren();

        addChild( bucketView.getHoleNode() );

        PieSet state = model.get();
        for ( Slice cell : state.cells ) {
            addChild( new PhetPPath( cell.shape(), new BasicStroke( state.cellFilled( cell ) ? 2 : 1 ), Color.darkGray ) );

            if ( debugCenter ) {
                addChild( new PhetPPath( new Rectangle2D.Double( cell.center().getX(), cell.center().getY(), 2, 2 ) ) );
            }
        }
        for ( final Slice slice : state.slices ) {
            addChild( new MovableSliceNode( rootNode, model, slice ) );
        }
        addChild( bucketView.getFrontNode() );

        //Show an icon label on the bucket so the user knows what is in the bucket
        PNode icon = new PNode() {{
            final int denominator = model.get().denominator;
            for ( int i = 0; i < denominator; i++ ) {
                Slice cell = model.get().sliceFactory.createPieCell( model.get().pies.length(), 0, i, denominator );
                addChild( new PhetPPath( cell.shape(), Color.white, new BasicStroke( 3 ), Color.black ) );
            }

            addChild( new MovableSliceNode( rootNode, model, model.get().sliceFactory.createPieCell( model.get().pies.length(), 0, 0, denominator ) ) {{
                setPickable( false );
                setChildrenPickable( false );
            }} );

            scale( 0.4 );
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