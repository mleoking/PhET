// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.view.molecules;

import edu.colorado.phet.balancingchemicalequations.model.Atom.H;

/**
 * H2 molecule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class H2Node extends TwoAtomNode {
    public H2Node() {
        super( new H(), new H() );
    }
}
