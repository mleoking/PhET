// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.intro.view.pieset;

import fj.F;

import java.awt.BasicStroke;
import java.awt.Color;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.piccolophet.nodes.BucketView;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.fractionsintro.intro.model.pieset.PieSet;
import edu.colorado.phet.fractionsintro.intro.model.pieset.Slice;
import edu.colorado.phet.fractionsintro.intro.view.FractionNode;
import edu.colorado.phet.fractionsintro.intro.view.FractionNumberNode;
import edu.umd.cs.piccolo.PNode;

/**
 * Shows interactive graphics for one pie set
 *
 * @author Sam Reid
 */
public class PieSetContentNode extends PNode {
    public PieSetContentNode( final BucketView bucketView, final SettableProperty<PieSet> model, final F<SliceNodeArgs, PNode> createSliceNode, final PNode rootNode, F<PieSet, PNode> createEmptyCellsNode ) {

        final PNode bucketHoleNode = bucketView.getHoleNode();
        addChild( bucketHoleNode );

        final PieSet state = model.get();

        //Show graphics for the empty cells
        addChild( createEmptyCellsNode.f( state ) );
        addChild( new MovablePiecesLayer( state, createSliceNode, model, rootNode, bucketHoleNode.getFullBoundsReference().getMaxY() ) );

        addChild( bucketView.getFrontNode() );

        //Show an icon label on the bucket so the user knows what is in the bucket
        PNode icon = new PNode() {{
            final int denominator = model.get().denominator;
            for ( int i = 0; i < denominator; i++ ) {
                Slice cell = model.get().sliceFactory.createPieCell( model.get().pies.length(), 0, i, denominator );
                addChild( new PhetPPath( cell.shape(), Color.white, new BasicStroke( 3 ), Color.black ) );
            }

            Slice slice = model.get().sliceFactory.createPieCell( model.get().pies.length(), 0, 0, denominator );
            PNode node = new MovableSliceNode( createSliceNode.f( new SliceNodeArgs( slice, model.get().denominator, false ) ), rootNode, model, slice );
            node.setPickable( false );
            node.setChildPaintInvalid( false );
            addChild( node );

            //Make as large as possible, but small enough that tall representations (like vertical bars) fit
            scale( 0.28 );
        }};

        PNode text = new FractionNode( FractionNumberNode.NUMBER_FONT, new Property<Integer>( 1 ), new Property<Integer>( model.get().denominator ) ) {{
            scale( 0.2 );
        }};
        PNode iconAndText = new HBox( 20, icon, text );

        addChild( new ZeroOffsetNode( iconAndText ) {{
            centerFullBoundsOnPoint( bucketView.getFrontNode().getFullBounds().getCenterX(), bucketView.getFrontNode().getFullBounds().getCenterY() + 4 );

            //Make it so the user can't grab the bucket icon
            setPickable( false );
            setChildrenPickable( false );
        }} );
    }
}
