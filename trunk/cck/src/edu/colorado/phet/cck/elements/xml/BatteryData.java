/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.cck.elements.xml;

import edu.colorado.phet.cck.elements.branch.Branch;
import edu.colorado.phet.cck.elements.branch.components.Battery;
import edu.colorado.phet.cck.elements.circuit.Circuit;

/**
 * User: Sam Reid
 * Date: Nov 22, 2003
 * Time: 9:21:44 PM
 * Copyright (c) Nov 22, 2003 by Sam Reid
 */
public class BatteryData extends BranchData {
    double DX;

    public double getDX() {
        return DX;
    }

    public void setDX(double DX) {
        this.DX = DX;
    }

    public BatteryData() {
    }

    public BatteryData(Battery b) {
        super(b);
        this.DX = b.DX;
    }

    public Branch toBranch(Circuit parent) {
        Battery b = new Battery(parent, x0, y0, x1, y1, voltageDrop, DX);
        return b;
    }
}
