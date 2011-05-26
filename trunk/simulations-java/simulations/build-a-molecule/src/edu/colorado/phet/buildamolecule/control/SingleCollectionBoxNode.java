// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildamolecule.control;

import java.awt.geom.Rectangle2D;
import java.text.MessageFormat;

import edu.colorado.phet.buildamolecule.BuildAMoleculeStrings;
import edu.colorado.phet.buildamolecule.model.CollectionBox;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.umd.cs.piccolo.PNode;

/**
 * Allows the collection of a single molecule
 */
public class SingleCollectionBoxNode extends CollectionBoxNode {
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
