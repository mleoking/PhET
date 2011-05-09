// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.chemistry.molecules;

import edu.colorado.phet.chemistry.model.Atom;
import edu.colorado.phet.chemistry.model.Atom.*;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Base class for molecules with N atoms aligned on the horizontal axis, for N > 0.
 * Note that here is technically no such thing as a single-atom molecule,
 * but allowing N=1 simplifies the Equation model.
 * <p/>
 * Origin is at geometric center of the node's bounding rectangle.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class HorizontalMoleculeNode extends PComposite {

    public HorizontalMoleculeNode( Atom... atoms ) {

        PComposite parentNode = new PComposite();
        addChild( parentNode );

        // add each node from left to right, overlapping consistently
        double x = 0;
        PNode previousNode = null;
        for ( Atom atom : atoms ) {
            AtomNode currentNode = new AtomNode( atom );
            parentNode.addChild( currentNode );
            if ( previousNode != null ) {
                x = previousNode.getFullBoundsReference().getMaxX() + ( 0.25 * currentNode.getFullBoundsReference().getWidth() );
            }
            currentNode.setOffset( x, 0 );
            previousNode = currentNode;
        }

        // move origin to geometric center
        parentNode.setOffset( -PNodeLayoutUtils.getOriginXOffset( parentNode ), -PNodeLayoutUtils.getOriginYOffset( parentNode ) );
    }

    public static class CNode extends HorizontalMoleculeNode {
        public CNode() {
            super( new C() );
        }
    }

    public static class Cl2Node extends HorizontalMoleculeNode {
        public Cl2Node() {
            super( new Cl(), new Cl() );
        }
    }

    public static class CONode extends HorizontalMoleculeNode {
        public CONode() {
            super( new C(), new O() );
        }
    }

    public static class CO2Node extends HorizontalMoleculeNode {
        public CO2Node() {
            super( new O(), new C(), new O() );
        }
    }

    public static class CS2Node extends HorizontalMoleculeNode {
        public CS2Node() {
            super( new S(), new C(), new S() );
        }
    }

    public static class F2Node extends HorizontalMoleculeNode {
        public F2Node() {
            super( new F(), new F() );
        }
    }

    public static class H2Node extends HorizontalMoleculeNode {
        public H2Node() {
            super( new H(), new H() );
        }
    }

    public static class N2Node extends HorizontalMoleculeNode {
        public N2Node() {
            super( new N(), new N() );
        }
    }

    public static class NONode extends HorizontalMoleculeNode {
        public NONode() {
            super( new N(), new O() );
        }
    }

    public static class N2ONode extends HorizontalMoleculeNode {
        public N2ONode() {
            super( new N(), new N(), new O() );
        }
    }

    public static class O2Node extends HorizontalMoleculeNode {
        public O2Node() {
            super( new O(), new O() );
        }
    }

    public static class SNode extends HorizontalMoleculeNode {
        public SNode() {
            super( new S() );
        }
    }
}
