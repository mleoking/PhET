// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.view.molecules;

import edu.colorado.phet.balancingchemicalequations.model.Atom.N;

/**
 * N2 molecule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class N2Node extends TwoAtomNode {

    public N2Node() {
        super( new N(), new N() );
    }
}