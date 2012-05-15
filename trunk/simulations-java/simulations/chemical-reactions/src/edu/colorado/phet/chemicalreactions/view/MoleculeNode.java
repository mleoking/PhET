// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.chemicalreactions.view;

import edu.colorado.phet.chemicalreactions.model.Atom;
import edu.colorado.phet.chemicalreactions.model.Molecule;
import edu.colorado.phet.chemistry.nodes.LabeledAtomNode;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.umd.cs.piccolo.PNode;

public class MoleculeNode extends PNode {
    public MoleculeNode( Molecule molecule ) {
        for ( final Atom atom : molecule.getAtoms() ) {
            addChild( new LabeledAtomNode( atom.getElement() ) {{
                atom.position.addObserver( new SimpleObserver() {
                    public void update() {
                        final ImmutableVector2D position = atom.position.get();
                        setOffset( position.getX(), position.getY() );
                    }
                } );
            }} );
        }
    }
}
