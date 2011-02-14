// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.view.molecules;

import edu.colorado.phet.balancingchemicalequations.model.Atom;
import edu.colorado.phet.common.piccolophet.nodes.ShadedSphereNode;

/**
 * Atoms look like shaded spheres.
 * Origin is at geometric center of bounding rectangle.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class AtomNode extends ShadedSphereNode {

    public AtomNode( double diameter, Atom atom ) {
        super( diameter, atom.getColor() );
    }

    public static class BigAtomNode extends AtomNode {
        public BigAtomNode( Atom atom ) {
            super( 22, atom );
        }
    }

    public static class SmallAtomNode extends AtomNode {
        public SmallAtomNode( Atom atom ) {
            super( 14, atom );
        }
    }
}