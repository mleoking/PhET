/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.cck.elements.branch.components;

import edu.colorado.phet.cck.elements.branch.Branch;
import edu.colorado.phet.cck.elements.branch.BranchObserver;
import edu.colorado.phet.cck.elements.branch.ImagePortion;
import edu.colorado.phet.cck.elements.circuit.Circuit;
import edu.colorado.phet.cck.elements.junction.Junction;
import edu.colorado.phet.cck.elements.xml.BatteryData;
import edu.colorado.phet.cck.elements.xml.BranchData;
import edu.colorado.phet.coreadditions.math.PhetVector;

/**
 * User: Sam Reid
 * Date: Sep 3, 2003
 * Time: 2:36:21 AM
 * Copyright (c) Sep 3, 2003 by Sam Reid
 */
public class Battery extends Branch {
    boolean recursing = false;
    private PhetVector dirVector;
    public double DX;
    private double internalResistance = .000001;
    private double magnitude;
    private boolean enableRotation = false;

    public String toString() {
        return super.toString() + " voltage=" + getVoltageDrop() + " internal resistance=" + internalResistance;
    }

    public Branch copy() {
        return new Battery(parent, getX1(), getY1(), getX2(), getY2(), getVoltageDrop(), DX);
    }

    public BranchData toBranchData() {
        return new BatteryData(this);
    }

    public void setDirVector(PhetVector dirVector) {
        this.dirVector = dirVector;
    }

    public Battery(Circuit parent, double x1, double y1, double x2, double y2, double voltageDrop, final double DX) {
        super(parent, x1, y1, x2, y2);
//        bareComponent = isBareComponent;
        dirVector = new PhetVector(x2 - x1, y2 - y1);
        this.magnitude = dirVector.getMagnitude();
        this.DX = DX;
//        this.voltageDrop = voltageDrop;
        setVoltageDrop(voltageDrop);

//        PhetVector startLoc = getEndJunction().getVector().getAddedInstance(-DX, 0);
//        getStartJunction().setLocation(startLoc.getX(), startLoc.getY());

        addObserver(new BranchObserver() {
            public void junctionMoved(Branch branch2, Junction junction) {
                if (DX == 0)
                    return;
                if (recursing)
                    return;
                recursing = true;
                if (!enableRotation)
                {
                    recursing=false;
                    return;
                }
                if (junction == getStartJunction()) {
                    //rotate about the end junction.
//                    PhetVector endLoc=getStartJunction().getVector().getAddedInstance(dirVector);
                    PhetVector dir = getStartJunction().getVector().getSubtractedInstance(getEndJunction().getVector()).getNormalizedInstance();
                    dir = dir.getScaledInstance(magnitude);

                    PhetVector startLoc = getEndJunction().getVector().getAddedInstance(dir);
                    getStartJunction().setLocation(startLoc.getX(), startLoc.getY());
//                    PhetVector endLoc = getStartJunction().getVector().getAddedInstance(DX, 0);
//                    getEndJunction().setLocation(endLoc.getX(), endLoc.getY());
                } else if (junction == getEndJunction()) {
//                    PhetVector startLoc=getEndJunction().getVector().getAddedInstance(dirVector.getScaledInstance(-1.0));

                    PhetVector dir = getEndJunction().getVector().getSubtractedInstance(getStartJunction().getVector()).getNormalizedInstance();
                    dir = dir.getScaledInstance(magnitude);

                    PhetVector endLoc = getStartJunction().getVector().getAddedInstance(dir);
                    getEndJunction().setLocation(endLoc.getX(), endLoc.getY());



//                    PhetVector startLoc = getEndJunction().getVector().getAddedInstance(-DX, 0);
//                    getStartJunction().setLocation(startLoc.getX(), startLoc.getY());
                }
                recursing = false;//What a crazy hack.
            }

            public void currentOrVoltageChanged(Branch branch2) {
            }
        });
    }

    public void setVoltageDrop(double voltageDrop) {
        super.setVoltageDrop(voltageDrop);
        parent.fireConnectivityChanged();
    }

    public void resetDirVector() {
        this.dirVector = new PhetVector(getEndJunction().getVector().getSubtractedInstance(getStartJunction().getVector()));
//        System.out.println("dirVector = " + dirVector);
    }

    public double getInternalResistance() {
        return internalResistance;
    }

    public double getDX() {
        return DX;
    }

    public void setLength(double length) {
        this.magnitude = length;
        PhetVector dir = getEndJunction().getVector().getSubtractedInstance(getStartJunction().getVector()).getNormalizedInstance();
        dir = dir.getScaledInstance(magnitude);

        PhetVector endLoc = getStartJunction().getVector().getAddedInstance(dir);
        getEndJunction().setLocation(endLoc.getX(), endLoc.getY());
    }

//    public void setLocation(Branch location) {
//        //disable rotation
//        enableRotation = false;
//        getStartJunction().setLocation(location.getX1(), location.getY1());
//        getEndJunction().setLocation(location.getX2(), location.getY2());
//        enableRotation = true;
//    }

    public void setRotateEnabled(boolean enab) {
        this.enableRotation=enab;
    }
}
