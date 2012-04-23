// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.intro.view.pieset;

import fj.F;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.piccolophet.nodes.BucketView;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.fractionsintro.intro.model.pieset.PieSet;
import edu.colorado.phet.fractionsintro.intro.view.FractionNode;
import edu.colorado.phet.fractionsintro.intro.view.FractionNumberNode;
import edu.umd.cs.piccolo.PNode;

/**
 * Shows interactive graphics for one pie set
 *
 * @author Sam Reid
 */
public class PieSetContentNode extends PNode {
    public PieSetContentNode( final BucketView bucketView,
                              final SettableProperty<PieSet> model,
                              final F<SliceNodeArgs, PNode> createSliceNode,
                              final PNode rootNode,
                              final F<PieSet, PNode> createEmptyCellsNode,
                              final F<PieSet, PNode> createBucketIcon,
                              boolean iconTextOnTheRight ) {

        final PNode bucketHoleNode = bucketView.getHoleNode();
        addChild( bucketHoleNode );

        final PieSet state = model.get();

        //Show graphics for the empty cells
        addChild( createEmptyCellsNode.f( state ) );
        addChild( new MovableSliceLayer( state, createSliceNode, model, rootNode, bucketView ) );
        addChild( bucketView.getFrontNode() );

        //Show an icon label on the bucket so the user knows what is in the bucket
        PNode icon = createBucketIcon.f( model.get() );

        PNode text = new FractionNode( FractionNumberNode.NUMBER_FONT, new Property<Integer>( 1 ), new Property<Integer>( model.get().denominator ) ) {{
            scale( 0.2 );
        }};
        PNode iconAndText = iconTextOnTheRight ? new HBox( 20, icon, text ) : new HBox( 20, text, icon );

        addChild( new ZeroOffsetNode( iconAndText ) {{

            //This next line of code looks ridiculous and unnecessary, let me explain it:
            //When showing debug regions in piccolo debugger, without the first setoffset() call, the entire screen gets redrawn.
            //It is because centerFullBoundsOnPoint calls validate, and since the node is at zero before validate, (0,0) gets thrown into the dirty regions.
            setOffset( bucketView.getFrontNode().getFullBounds().getCenterX(), bucketView.getFrontNode().getFullBounds().getCenterY() + 4 );
            centerFullBoundsOnPoint( bucketView.getFrontNode().getFullBounds().getCenterX(), bucketView.getFrontNode().getFullBounds().getCenterY() + 4 );

            //Make it so the user can't grab the bucket icon
            setPickable( false );
            setChildrenPickable( false );
        }} );
    }
}