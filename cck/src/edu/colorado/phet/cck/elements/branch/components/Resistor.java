/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.cck.elements.branch.components;

import edu.colorado.phet.cck.elements.branch.Branch;
import edu.colorado.phet.cck.elements.circuit.Circuit;
import edu.colorado.phet.cck.elements.xml.BranchData;
import edu.colorado.phet.cck.elements.xml.ResistorData;

/**
 * User: Sam Reid
 * Date: Sep 3, 2003
 * Time: 2:36:14 AM
 * Copyright (c) Sep 3, 2003 by Sam Reid
 */
public class Resistor extends Branch implements HasResistance {
    double resistance;
    double volts;

    public Resistor( Circuit parent, double x1, double y1, double x2, double y2, double resistance ) {
        super( parent, x1, y1, x2, y2 );
        this.resistance = resistance;
    }

    public double getResistance() {
        return resistance;
    }

    public void setVoltage( double volts ) {
        this.volts = volts;
    }

    public void setResistance( double resistance ) {
        this.resistance = resistance;
        parent.fireConnectivityChanged();
        fireCurrentChanged();
    }

    public Branch copy() {
        return new Resistor( parent, getX1(), getY1(), getX2(), getY2(), resistance );
    }

    public BranchData toBranchData() {
        return new ResistorData( this );
    }
}
