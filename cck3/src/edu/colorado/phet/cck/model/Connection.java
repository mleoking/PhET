package edu.colorado.phet.cck.model;

import edu.colorado.phet.cck.model.components.Branch;

/**
 * User: Sam Reid
 * Date: Sep 26, 2006
 * Time: 1:39:13 PM
 * Copyright (c) Sep 26, 2006 by Sam Reid
 */
public abstract class Connection {
    public abstract Junction getJunction();

    public abstract double getVoltageAddon();

    public static class JunctionConnection extends Connection {
        Junction junction;

        public JunctionConnection( Junction junction ) {
            this.junction = junction;
        }

        public boolean equals( Object o ) {
            if( o instanceof JunctionConnection ) {
                JunctionConnection jc = (JunctionConnection)o;
                if( jc.junction == junction ) {
                    return true;
                }
            }
            return false;
        }

//        public Branch[] getBranchesToIgnore() {
//            return new Branch[0];
//        }

        public Junction getJunction() {
            return junction;
        }

        public double getVoltageAddon() {
            return 0;
        }
    }

    public static class BranchConnection extends Connection {
        Branch branch;
        double dist;

        public BranchConnection( Branch branch, double dist ) {
            this.branch = branch;
            this.dist = dist;
        }

        public Junction getJunction() {
            return branch.getStartJunction();
        }

        public double getVoltageAddon() {
//            System.out.println( "Getting voltage ADDON, junction=" + getJunction() + ", branch = " + branch );
            double resistance = branch.getResistance();
            double length = branch.getLength();
            double resistivity = resistance / length; //infer a resistivity.
            double incrementalResistance = dist * resistivity;
            double current = branch.getCurrent();
            double voltage = current * incrementalResistance;//the sign is probably right
//            System.out.println( "dist=" + dist + ", resistance = " + resistance + ", incrementalRes=" + incrementalResistance + ", current=" + current + ", DV=" + voltage );
            return voltage;
        }

    }
}
