/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.coreadditions;

import edu.colorado.phet.common.model.ModelElement;

/**
 * User: Sam Reid
 * Date: Jun 23, 2003
 * Time: 8:39:22 PM
 * Copyright (c) Jun 23, 2003 by Sam Reid
 */
public class ClockTimer extends ModelElement {
    double total = 0;

    public void stepInTime(double dt) {
        total += dt;
    }

    public double getTime() {
        return total;
    }
}
