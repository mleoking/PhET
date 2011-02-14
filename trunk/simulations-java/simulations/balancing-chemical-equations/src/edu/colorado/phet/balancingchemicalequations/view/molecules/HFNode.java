// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.view.molecules;

import edu.colorado.phet.balancingchemicalequations.model.Atom.F;
import edu.colorado.phet.balancingchemicalequations.model.Atom.H;

/**
 * HF molecule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class HFNode extends TwoAtomNode {
    public HFNode() {
        super( new H(), new F() );
    }
}