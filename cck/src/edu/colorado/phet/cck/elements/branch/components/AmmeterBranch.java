/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.cck.elements.branch.components;

import edu.colorado.phet.cck.elements.branch.Branch;
import edu.colorado.phet.cck.elements.circuit.Circuit;
import edu.colorado.phet.cck.elements.xml.AmmeterBranchData;
import edu.colorado.phet.cck.elements.xml.BranchData;

/**
 * User: Sam Reid
 * Date: Sep 3, 2003
 * Time: 2:36:14 AM
 * Copyright (c) Sep 3, 2003 by Sam Reid
 */
public class AmmeterBranch extends Branch implements HasResistance {
    private double resistance;

    public AmmeterBranch( Circuit parent, double x1, double y1, double x2, double y2 ) {
        super( parent, x1, y1, x2, y2 );//resistance is internal resistance here.
        this.resistance = resistance;
    }

    public Branch copy() {
        return new AmmeterBranch( parent, getX1(), getY1(), getX2(), getY2() );
    }

    public BranchData toBranchData() {
        return new AmmeterBranchData( this );
    }

    public double getResistance() {
        return new Wire( parent, getX1(), getY1(), getX2(), getY2() ).getResistance();
    }

    public void setResistance( double resistance ) {
        this.resistance = resistance;
    }

}
