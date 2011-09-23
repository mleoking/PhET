// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.view;

import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.moleculepolarity.common.control.MoleculeRotationHandler;
import edu.colorado.phet.moleculepolarity.common.control.RotateCursorHandler;
import edu.colorado.phet.moleculepolarity.common.model.DiatomicMolecule;

/**
 * Visual representation of a diatomic molecule.
 * Children position themselves in world coordinates, so this node's offset should be (0,0).
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DiatomicMoleculeNode extends PhetPNode {

    public DiatomicMoleculeNode( DiatomicMolecule molecule ) {

        addChild( new BondNode( molecule.bond ) ); // bond behind atoms
        addChild( new AtomNode( molecule.atomA ) );
        addChild( new AtomNode( molecule.atomB ) );

        addInputEventListener( new RotateCursorHandler() );
        addInputEventListener( new MoleculeRotationHandler( molecule, this ) );
    }
}