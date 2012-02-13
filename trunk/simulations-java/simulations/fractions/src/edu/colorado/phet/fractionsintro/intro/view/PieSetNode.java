// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.intro.view;

import fj.Equal;
import fj.F2;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.BucketView;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.fractionsintro.intro.model.slicemodel.PieSet;
import edu.colorado.phet.fractionsintro.intro.model.slicemodel.Slice;
import edu.umd.cs.piccolo.PNode;

import static fj.Equal.equal;
import static fj.Function.curry;

/**
 * Renders the pie set node from the given model.  Unconventional way of using piccolo, where the scene graph is recreated any time the model changes.
 * Done to support immutable model and still get some piccolo benefits.
 *
 * @author Sam Reid
 */
public class PieSetNode extends PNode {

    private final BucketView bucketView;
    private final PNode rootNode;
    private boolean debugCenter = false;

    public static <T> Equal<T> refEqual() {
        return equal( curry( new F2<T, T, Boolean>() {
            public Boolean f( final T a1, final T a2 ) {
                return a1 == a2;
            }
        } ) );
    }

    //Create a PieSetNode, have to pass in the root node since the scene graph tree is reconstructed each time and you cannot use getDeltaRelativeTo(getParent) since the node may no longer be in the tree
    public PieSetNode( final SettableProperty<PieSet> model, PNode rootNode ) {
        this.rootNode = rootNode;

        final ModelViewTransform mvt = ModelViewTransform.createSinglePointScaleInvertedYMapping( new Point(), new Point(), 1 );
        bucketView = new BucketView( model.get().bucket, mvt );

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
    }
}