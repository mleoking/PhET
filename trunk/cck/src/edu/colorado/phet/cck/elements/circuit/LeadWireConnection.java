/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.cck.elements.circuit;

import edu.colorado.phet.cck.elements.branch.AbstractBranchGraphic;
import edu.colorado.phet.cck.elements.branch.Branch;
import edu.colorado.phet.cck.elements.branch.components.Wire;
import edu.colorado.phet.cck.elements.junction.Junction;

/**
 * User: Sam Reid
 * Date: Oct 26, 2003
 * Time: 10:32:18 PM
 * Copyright (c) Oct 26, 2003 by Sam Reid
 */
public class LeadWireConnection {
    private AbstractBranchGraphic defaultCompositeBranchGraphic;
    private boolean startVertex;
    private double distFromVertex;

    public LeadWireConnection( AbstractBranchGraphic defaultCompositeBranchGraphic, boolean startVertex, double distFromVertex ) {
        this.defaultCompositeBranchGraphic = defaultCompositeBranchGraphic;
        this.startVertex = startVertex;
        this.distFromVertex = distFromVertex;
    }

    public double getDistFromVertex() {
        return distFromVertex;
    }

    public String toString() {
        return "branch=" + defaultCompositeBranchGraphic.getBranch().toString() + ", isStart=" + startVertex;
    }

    public AbstractBranchGraphic getDefaultCompositeBranchGraphic() {
        return defaultCompositeBranchGraphic;
    }

    public boolean isStartVertex() {
        return startVertex;
    }

    public Junction getEquivalentJunction() {
        if( startVertex ) {
            return defaultCompositeBranchGraphic.getBranch().getStartJunction();
        }
        else {
            return defaultCompositeBranchGraphic.getBranch().getEndJunction();
        }
    }

    public double getVoltageDropToVertex() {
        if( defaultCompositeBranchGraphic.getBranch() instanceof Wire ) {
            Wire w = (Wire)defaultCompositeBranchGraphic.getBranch();
            double r = getResistanceToVertex();
            double i = w.getCurrent();
            if( !startVertex && w.getCurrent() > 0 ) {
                return -Math.abs( i * r );
            }
            else if( startVertex && w.getCurrent() < 0 ) {
                return -Math.abs( i * r );
            }
            else {
                return Math.abs( i * r );
            }
            //if going from self to start vertex is with current, return negative (voltage drop)
//            if (startVertex&&w.getCurrent()<=0){
//                return -incrementalVoltage;
//            }
//            else if (!startVertex&&w.getCurrent()>0){
//                return -incrementalVoltage;
//            }
//            return incrementalVoltage;
        }
        return 0;
    }

    public Branch getBranch() {
        return defaultCompositeBranchGraphic.getBranch();
    }

    public double getResistanceToVertex() {
        if( defaultCompositeBranchGraphic.getBranch() instanceof Wire ) {
            Wire w = (Wire)defaultCompositeBranchGraphic.getBranch();
            double resistance = w.getResistance();
            double length = w.getLength();
            double dr = ( distFromVertex / length ) * resistance;
            return dr;
        }
//        throw new RuntimeException("Non-wire.");
        return 0;
    }

}
