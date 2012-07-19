// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.chemicalreactions.view;

import java.awt.*;
import java.util.Arrays;

import edu.colorado.phet.chemicalreactions.model.Molecule;
import edu.colorado.phet.chemicalreactions.model.MoleculeBucket;
import edu.colorado.phet.chemistry.utils.ChemUtils;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.BucketView;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEventListener;

import static edu.colorado.phet.chemicalreactions.ChemicalReactionsConstants.MODEL_VIEW_TRANSFORM;

/**
 * Bucket node customized for chemical reactions
 */
public class MoleculeBucketNode extends BucketView {
    public MoleculeBucketNode( final MoleculeBucket bucket ) {
        super( bucket, MODEL_VIEW_TRANSFORM );

        addLabelToContainer( new ZeroOffsetNode( new PNode() {{
            final HTMLNode label = new HTMLNode( ChemUtils.toSubscript( bucket.getShape().name ),
                                                 Color.BLACK, new PhetFont( 18, true ) ) {{
                setOffset( 0, -getFullBounds().getHeight() / 2 );
            }};
            final MoleculeNode exampleMoleculeNode = new MoleculeNode( new Molecule( bucket.getShape() ) );
            MoleculeNode icon = new MoleculeNode( new Molecule( bucket.getShape() ) {{
                setPosition( MODEL_VIEW_TRANSFORM.viewToModel( new ImmutableVector2D( 0, 0 ) ) );
            }} ) {{
                setOffset( -getFullBounds().getWidth() / 3, 0 );
                scale( 0.4 );

                // don't show the mouse hand over it
                for ( PInputEventListener listener : Arrays.asList( getInputEventListeners() ) ) {
                    removeInputEventListener( listener );
                }
            }};
            addChild( label );
            addChild( icon );
        }} ) );
    }
}
