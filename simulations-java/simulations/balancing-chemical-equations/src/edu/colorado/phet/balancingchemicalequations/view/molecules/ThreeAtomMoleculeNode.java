// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.view.molecules;

import edu.colorado.phet.balancingchemicalequations.model.*;
import edu.colorado.phet.balancingchemicalequations.model.Atom.C;
import edu.colorado.phet.balancingchemicalequations.model.Atom.N;
import edu.colorado.phet.balancingchemicalequations.model.Atom.O;
import edu.colorado.phet.balancingchemicalequations.model.Atom.S;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Base class for molecules with 3 atoms aligned on the horizontal axis.
 * Origin is at geometric center of bounding rectangle.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class ThreeAtomMoleculeNode extends PComposite {

    public ThreeAtomMoleculeNode( Atom leftAtom, Atom centerAtom, Atom rightAtom ) {

        // atom nodes
        AtomNode leftNode = new AtomNode( leftAtom );
        AtomNode centerNode = new AtomNode( centerAtom );
        AtomNode rightNode = new AtomNode( rightAtom );

        // rendering order
        PComposite parentNode = new PComposite();
        addChild( parentNode );
        parentNode.addChild( leftNode );
        parentNode.addChild( centerNode );
        parentNode.addChild( rightNode );

        // layout
        double x = 0;
        double y = 0;
        leftNode.setOffset( x, y );
        x = leftNode.getFullBoundsReference().getMaxX() + ( 0.25 * centerNode.getFullBoundsReference().getWidth() );
        centerNode.setOffset( x, y );
        x = centerNode.getFullBoundsReference().getMaxX() + ( 0.25 * rightNode.getFullBoundsReference().getWidth() );
        rightNode.setOffset( x, y );

        // move origin to geometric center
        parentNode.setOffset( -PNodeLayoutUtils.getOriginXOffset( parentNode ), -PNodeLayoutUtils.getOriginYOffset( parentNode ) );
    }

    public static class CO2Node extends ThreeAtomMoleculeNode {
        public CO2Node() {
            super( new O(), new C(), new O() );
        }
    }

    public static class CS2Node extends ThreeAtomMoleculeNode {
        public CS2Node() {
            super( new S(), new C(), new S() );
        }
    }

    public static class N2ONode extends ThreeAtomMoleculeNode {
        public N2ONode() {
            super( new N(), new N(), new O() );
        }
    }
}
