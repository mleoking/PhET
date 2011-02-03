// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.view.molecules;

import java.awt.Color;

import edu.colorado.phet.common.piccolophet.nodes.ShadedSphereNode;

/**
 * Atoms look like shaded spheres.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class AtomNode extends ShadedSphereNode {

    public AtomNode( double diameter, Color color ) {
        super( diameter, color );
    }

    public static class BigAtomNode extends AtomNode {
        public BigAtomNode( Color color ) {
            super( 22, color );
        }
    }

    public static class SmallAtomNode extends AtomNode {
        public SmallAtomNode( Color color ) {
            super( 14, color );
        }
    }
}