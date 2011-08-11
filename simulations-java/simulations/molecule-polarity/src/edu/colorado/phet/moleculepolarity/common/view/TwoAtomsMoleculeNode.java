// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.view;

import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.moleculepolarity.common.model.TwoAtomsMolecule;

/**
 * Visual representation of a molecule composed for 2 atoms.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TwoAtomsMoleculeNode extends PhetPNode {

    private final TwoAtomsMolecule molecule;

    public TwoAtomsMoleculeNode( final TwoAtomsMolecule molecule ) {

        this.molecule = molecule;

        addChild( new BondNode( molecule.bond ) );
        addChild( new AtomNode( molecule.atomA ) );
        addChild( new AtomNode( molecule.atomB ) );

        addInputEventListener( new CursorHandler() ); //TODO change cursor to indicate rotation
        addInputEventListener( new MoleculeRotationHandler( molecule, this ) );
    }
}