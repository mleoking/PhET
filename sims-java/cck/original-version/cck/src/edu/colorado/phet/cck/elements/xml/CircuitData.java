/*Copyright, University of Colorado, 2004.*/
package edu.colorado.phet.cck.elements.xml;

import edu.colorado.phet.cck.elements.branch.Branch;
import edu.colorado.phet.cck.elements.circuit.Circuit;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Nov 22, 2003
 * Time: 8:48:05 PM
 * Copyright (c) Nov 22, 2003 by Sam Reid
 */
public class CircuitData {
    ArrayList branchdata = new ArrayList();

    public CircuitData() {
    }

    public ArrayList getBranchdata() {
        return branchdata;
    }

    public void setBranchdata( ArrayList branchdata ) {
        this.branchdata = branchdata;
    }

    public CircuitData( Circuit c ) {
        branchdata = new ArrayList();
        for( int i = 0; i < c.numBranches(); i++ ) {
            Branch b = c.branchAt( i );
            BranchData branchData = b.toBranchData();
            branchdata.add( branchData );
//            if (b instanceof Battery) {
//                branchdata.add(new BatteryData((Battery) b));
//            } else if (b instanceof Wire) {
//                branchdata.add(new WireData(b));
//            } else if (b instanceof Resistor)
//                branchdata.add(new ResistorData(b));
//            else if (b instanceof Bulb)
//                branchdata.add(new BulbData((Bulb) b));
//            else if (b instanceof Switch)
//                branchdata.add(new SwitchData((Switch) b));
//            else
//                throw new RuntimeException("Unknown type: " + b.getClass());
//            BranchData bd = new BranchData(b);
//            branchdata.add(bd);
        }
    }

    public Circuit toCircuit() {
        Circuit c = new Circuit();
        for( int i = 0; i < branchdata.size(); i++ ) {
            BranchData branchData = (BranchData)branchdata.get( i );
            c.addBranch( branchData.toBranch( c ) );
        }
        return c;
    }
}
