// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.view;

import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.moleculepolarity.common.control.MoleculeRotationHandler;
import edu.colorado.phet.moleculepolarity.common.control.RotationCursorHandler;
import edu.colorado.phet.moleculepolarity.common.model.DiatomicMolecule;

/**
 * Visual representation of a diatomic molecule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DiatomicMoleculeNode extends PhetPNode {

    private final DiatomicMolecule molecule;

    public DiatomicMoleculeNode( final DiatomicMolecule molecule ) {

        this.molecule = molecule;

        addChild( new BondNode( molecule.bond ) );
        addChild( new AtomNode( molecule.atomA ) );
        addChild( new AtomNode( molecule.atomB ) );

        addInputEventListener( new RotationCursorHandler() );
        addInputEventListener( new MoleculeRotationHandler( molecule, this ) );
    }
}