/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.cck.elements.branch;

import edu.colorado.phet.cck.elements.circuit.Circuit;
import edu.colorado.phet.cck.elements.xml.BranchData;

/**
 * User: Sam Reid
 * Date: Dec 6, 2003
 * Time: 10:02:40 PM
 * Copyright (c) Dec 6, 2003 by Sam Reid
 */
public class BareBranch extends Branch {
    public BareBranch(Circuit parent, Branch source) {
        super(parent, source);
    }

    public BareBranch(Circuit parent, double x1, double y1, double x2, double y2) {
        super(parent, x1, y1, x2, y2);
    }

    public Branch copy() {
        throw new RuntimeException("Unsupported.");
    }

    public BranchData toBranchData() {
        throw new RuntimeException("Unsupported.");
    }

}