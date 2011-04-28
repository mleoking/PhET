// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildamolecule.control;

import java.text.MessageFormat;

import edu.colorado.phet.buildamolecule.BuildAMoleculeStrings;
import edu.colorado.phet.buildamolecule.model.CollectionBox;
import edu.colorado.phet.buildamolecule.view.BuildAMoleculeCanvas;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;

/**
 * Allows the collection of a single molecule
 */
public class SingleCollectionBoxNode extends CollectionBoxNode {
    public SingleCollectionBoxNode( final BuildAMoleculeCanvas canvas, final CollectionBox box ) {
        super( canvas, box, new HTMLNode( MessageFormat.format( BuildAMoleculeStrings.COLLECTION_SINGLE_FORMAT,
                                                                box.getMoleculeType().getMoleculeStructure().getStructuralFormulaFragment(),
                                                                box.getMoleculeType().getCommonName() ) ) {{
            setFont( new PhetFont( 16, true ) );
        }} );
        assert ( box.getCapacity() == 1 );
    }
}
