/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.cck.elements.xml;

import edu.colorado.phet.cck.elements.branch.BareBranch;
import edu.colorado.phet.cck.elements.branch.Branch;
import edu.colorado.phet.cck.elements.branch.components.HasResistance;
import edu.colorado.phet.cck.elements.circuit.Circuit;

/**
 * User: Sam Reid
 * Date: Nov 22, 2003
 * Time: 8:48:22 PM
 * Copyright (c) Nov 22, 2003 by Sam Reid
 */
public class BranchData {
    double x0;
    double y0;
    double x1;
    double y1;
    double voltageDrop;
    double resistance;

    public BranchData() {
    }

    public BranchData(Branch b) {
        this.x0 = b.getX1();
        this.x1 = b.getX2();
        this.y0 = b.getY1();
        this.y1 = b.getY2();
        this.voltageDrop = b.getVoltageDrop();
        if (b instanceof HasResistance)
            this.resistance = ((HasResistance) b).getResistance();
    }

    public double getVoltageDrop() {
        return voltageDrop;
    }

    public void setVoltageDrop(double voltageDrop) {
        this.voltageDrop = voltageDrop;
    }

    public double getResistance() {
        return resistance;
    }

    public void setResistance(double resistance) {
        this.resistance = resistance;
    }

    public double getX0() {
        return x0;
    }

    public void setX0(double x0) {
        this.x0 = x0;
    }

    public double getY0() {
        return y0;
    }

    public void setY0(double y0) {
        this.y0 = y0;
    }

    public double getX1() {
        return x1;
    }

    public void setX1(double x1) {
        this.x1 = x1;
    }

    public double getY1() {
        return y1;
    }

    public void setY1(double y1) {
        this.y1 = y1;
    }

    public Branch toBranch(Circuit parent) {
        return new BareBranch(parent, x0, y0, x1, y1);
    }
}
