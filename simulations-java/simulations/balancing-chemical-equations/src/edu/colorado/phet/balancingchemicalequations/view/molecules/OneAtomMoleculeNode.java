// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.view.molecules;

import edu.colorado.phet.balancingchemicalequations.model.Atom;
import edu.colorado.phet.balancingchemicalequations.model.Atom.C;
import edu.colorado.phet.balancingchemicalequations.model.Atom.S;
import edu.colorado.phet.balancingchemicalequations.view.molecules.AtomNode.BigAtomNode;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Base class for molecules with 1 atom.
 * Origin is at geometric center of bounding rectangle.
 * <p>
 * There is technically no such thing as a single-atom molecule,
 * but this allows us to simplify the equation model.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class OneAtomMoleculeNode extends PComposite {

    public OneAtomMoleculeNode( Atom atom ) {

        // atom nodes
        AtomNode node = new BigAtomNode( atom );

        // rendering order
        PComposite parentNode = new PComposite();
        addChild( parentNode );
        parentNode.addChild( node );

        // move origin to geometric center
        parentNode.setOffset( -PNodeLayoutUtils.getOriginXOffset( parentNode ), -PNodeLayoutUtils.getOriginYOffset( parentNode ) );
    }

    public static class CNode extends OneAtomMoleculeNode {
        public CNode() {
            super( new C() );
        }
    }

    public static class SNode extends OneAtomMoleculeNode {
        public SNode() {
            super( new S() );
        }
    }
}
