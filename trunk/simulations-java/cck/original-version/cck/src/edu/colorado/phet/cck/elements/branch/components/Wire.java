/*Copyright, University of Colorado, 2004.*/
package edu.colorado.phet.cck.elements.branch.components;

import edu.colorado.phet.cck.elements.branch.Branch;
import edu.colorado.phet.cck.elements.circuit.Circuit;
import edu.colorado.phet.cck.elements.xml.BranchData;
import edu.colorado.phet.cck.elements.xml.WireData;

/**
 * User: Sam Reid
 * Date: Sep 3, 2003
 * Time: 2:36:25 AM
 * Copyright (c) Sep 3, 2003 by Sam Reid
 */
public class Wire extends Branch implements HasResistance {

    public static double DEFAULT_RESISTANCE_SCALE = .0000001;
    private static double resistanceScale = DEFAULT_RESISTANCE_SCALE;

    public static void setResistanceScale( double resistanceScaleInput ) {
        resistanceScale = resistanceScaleInput;
    }

    public Wire( Circuit parent, double x1, double y1, double x2, double y2 ) {
        super( parent, x1, y1, x2, y2 );
    }

    public Branch copy() {
        return new Wire( parent, getX1(), getY1(), getX2(), getY2() );
    }

    public BranchData toBranchData() {
        return new WireData( this );
    }

    public double getResistance() {
        double length = getLength();
        return length * resistanceScale;
    }

    public double getVoltageDrop() {
        return Math.abs( getCurrent() * getResistance() );
    }

    public void setResistance( double resistance ) {
        throw new RuntimeException( "Not supported." );
    }

    public static double getResistanceScale() {
        return resistanceScale;
    }
}
