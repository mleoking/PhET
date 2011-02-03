// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.view.molecules;

import java.awt.Color;

import edu.colorado.phet.common.piccolophet.nodes.ShadedSphereNode;

/**
 * Atoms look like shaded spheres.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class AtomNode extends ShadedSphereNode {

    public AtomNode( Color color ) {
        this( 35, color );
    }

    public AtomNode( double diameter, Color color ) {
        super( diameter, color );
    }
}