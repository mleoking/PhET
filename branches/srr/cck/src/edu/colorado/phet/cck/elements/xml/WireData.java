/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.cck.elements.xml;

import edu.colorado.phet.cck.elements.branch.Branch;
import edu.colorado.phet.cck.elements.branch.components.Wire;
import edu.colorado.phet.cck.elements.circuit.Circuit;

/**
 * User: Sam Reid
 * Date: Nov 22, 2003
 * Time: 9:20:06 PM
 * Copyright (c) Nov 22, 2003 by Sam Reid
 */
public class WireData extends BranchData {
    public WireData() {
    }

    public WireData(Branch b) {
        super(b);
    }

    public Branch toBranch(Circuit parent) {
        Wire w = new Wire(parent, x0, y0, x1, y1);
        return w;
    }
}
