// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildamolecule.control;

import java.awt.geom.Rectangle2D;
import java.text.MessageFormat;

import edu.colorado.phet.buildamolecule.BuildAMoleculeStrings;
import edu.colorado.phet.buildamolecule.model.CollectionBox;
import edu.colorado.phet.buildamolecule.model.CompleteMolecule;
import edu.colorado.phet.buildamolecule.model.MoleculeList;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * Allows the collection of a single molecule
 */
public class SingleCollectionBoxNode extends CollectionBoxNode {
    private static double maxWidth;
    private static double maxHeight;

    static {
        maxWidth = 0;
        maxHeight = 0;

        // compute maximum width and height for all different molecules
        for ( CompleteMolecule molecule : MoleculeList.COLLECTION_BOX_MOLECULES ) {
            PBounds boxBounds = new SingleCollectionBoxNode( new CollectionBox( molecule, 1 ), new Function1<PNode, Rectangle2D>() {
                public Rectangle2D apply( PNode pNode ) {
                    return null;
                }
            } ).getFullBounds();

            maxWidth = Math.max( maxWidth, boxBounds.getWidth() );
            maxHeight = Math.max( maxHeight, boxBounds.getHeight() );
        }
    }

    public static double getMaxWidth() {
        return maxWidth;
    }

    public static double getMaxHeight() {
        return maxHeight;
    }

    public SingleCollectionBoxNode( final CollectionBox box, Function1<PNode, Rectangle2D> toModelBounds ) {
        super( box, 1, toModelBounds );
        assert ( box.getCapacity() == 1 );

        addHeaderNode( new PNode() {{
            final HTMLNode nameAndFormula = new HTMLNode( MessageFormat.format( BuildAMoleculeStrings.COLLECTION_SINGLE_FORMAT,
                                                                                box.getMoleculeType().getGeneralFormulaFragment(),
                                                                                box.getMoleculeType().getDisplayName() ) ) {{
                setFont( new PhetFont( 15, true ) );
            }};
            addChild( nameAndFormula );
        }} );
    }
}
