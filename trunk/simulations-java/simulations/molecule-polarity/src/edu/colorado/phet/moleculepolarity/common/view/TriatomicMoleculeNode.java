// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.view;

import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.moleculepolarity.common.model.TriatomicMolecule;

/**
 * Visual representation of a triatomic molecule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TriatomicMoleculeNode extends PhetPNode {

    private final TriatomicMolecule molecule;

    public TriatomicMoleculeNode( final TriatomicMolecule molecule ) {

        this.molecule = molecule;

        addChild( new BondNode( molecule.bondAB ) );
        addChild( new BondNode( molecule.bondBC ) );
        addChild( new AtomNode( molecule.atomA ) );
        addChild( new AtomNode( molecule.atomB ) );
        addChild( new AtomNode( molecule.atomC ) );

        addInputEventListener( new CursorHandler() ); //TODO change cursor to indicate rotation

        //TODO add molecule rotation handler, grab bonds or atomB
        //TODO add bond angle handler, grab atomA or atomC
    }
}