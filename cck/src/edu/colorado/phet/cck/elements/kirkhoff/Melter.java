/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.cck.elements.kirkhoff;

import edu.colorado.phet.cck.elements.branch.Branch;
import edu.colorado.phet.cck.elements.circuit.Circuit;

/**
 * User: Sam Reid
 * Date: Dec 17, 2003
 * Time: 12:02:41 PM
 * Copyright (c) Dec 17, 2003 by Sam Reid
 */
public class Melter {
    double maxCurrent = 30;

    public void doMelt(Circuit parent) {
        for (int i = 0; i < parent.numBranches(); i++) {
            Branch b = parent.branchAt(i);
            doMelt(b);
        }
    }

    private void doMelt(Branch b) {
        double i = b.getCurrent();
        if (i > maxCurrent) {
            b.setOnFire(true);
        } else {
            b.setOnFire(false);
        }
    }
}
