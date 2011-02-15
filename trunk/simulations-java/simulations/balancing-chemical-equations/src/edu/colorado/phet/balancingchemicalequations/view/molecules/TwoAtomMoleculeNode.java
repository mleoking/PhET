// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.view.molecules;

import edu.colorado.phet.balancingchemicalequations.model.*;
import edu.colorado.phet.balancingchemicalequations.model.Atom.C;
import edu.colorado.phet.balancingchemicalequations.model.Atom.Cl;
import edu.colorado.phet.balancingchemicalequations.model.Atom.F;
import edu.colorado.phet.balancingchemicalequations.model.Atom.H;
import edu.colorado.phet.balancingchemicalequations.model.Atom.N;
import edu.colorado.phet.balancingchemicalequations.model.Atom.O;
import edu.colorado.phet.balancingchemicalequations.view.molecules.AtomNode.BigAtomNode;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Base class for molecules with 2 atoms of the same size.
 * Origin is at geometric center of bounding rectangle.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class TwoAtomMoleculeNode extends PComposite {

    public TwoAtomMoleculeNode( Atom leftAtom, Atom rightAtom ) {

        // atom nodes
        AtomNode leftNode = new BigAtomNode( leftAtom );
        AtomNode rightNode = new BigAtomNode( rightAtom );

        // rendering order
        PComposite parentNode = new PComposite();
        addChild( parentNode );
        parentNode.addChild( leftNode );
        parentNode.addChild( rightNode );

        // layout
        double x = 0;
        double y = 0;
        leftNode.setOffset( x, y );
        x = leftNode.getFullBoundsReference().getMaxX() + ( 0.25 * rightNode.getFullBoundsReference().getWidth() );
        y = leftNode.getYOffset();
        rightNode.setOffset( x, y );

        // move origin to geometric center
        parentNode.setOffset( -PNodeLayoutUtils.getOriginXOffset( parentNode ), -PNodeLayoutUtils.getOriginYOffset( parentNode ) );
    }

    public static class Cl2Node extends TwoAtomMoleculeNode {
        public Cl2Node() {
            super( new Cl(), new Cl() );
        }
    }

    public static class CONode extends TwoAtomMoleculeNode {
        public CONode() {
            super( new C(), new O() );
        }
    }

    public static class F2Node extends TwoAtomMoleculeNode {
        public F2Node() {
            super( new F(), new F() );
        }
    }

    public static class H2Node extends TwoAtomMoleculeNode {
        public H2Node() {
            super( new H(), new H() );
        }
    }

    public static class HFNode extends TwoAtomMoleculeNode {
        public HFNode() {
            super( new H(), new F() );
        }
    }

    public static class N2Node extends TwoAtomMoleculeNode {
        public N2Node() {
            super( new N(), new N() );
        }
    }

    public static class NONode extends TwoAtomMoleculeNode {
        public NONode() {
            super( new N(), new O() );
        }
    }

    public static class O2Node extends TwoAtomMoleculeNode {
        public O2Node() {
            super( new O(), new O() );
        }
    }
}
