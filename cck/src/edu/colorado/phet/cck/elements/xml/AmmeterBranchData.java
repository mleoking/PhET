package edu.colorado.phet.cck.elements.xml;

import edu.colorado.phet.cck.elements.branch.Branch;
import edu.colorado.phet.cck.elements.branch.components.AmmeterBranch;
import edu.colorado.phet.cck.elements.circuit.Circuit;

/**
 * User: Sam Reid
 * Date: Jan 31, 2004
 * Time: 4:32:20 PM
 * Copyright (c) Jan 31, 2004 by Sam Reid
 */
public class AmmeterBranchData extends BranchData {
    public AmmeterBranchData() {
    }

    public AmmeterBranchData( AmmeterBranch ammeterBranch ) {
        super( ammeterBranch );
    }

    public Branch toBranch( Circuit parent ) {
        return new AmmeterBranch( parent, getX0(), getY0(), getX1(), getY1() );
    }
}
