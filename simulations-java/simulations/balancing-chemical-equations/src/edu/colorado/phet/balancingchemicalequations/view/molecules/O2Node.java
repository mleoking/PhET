// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.view.molecules;

import edu.colorado.phet.balancingchemicalequations.model.Atom.O;

/**
 * O2 molecule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class O2Node extends TwoAtomNode {

    public O2Node() {
        super( new O(), new O() );
    }
}