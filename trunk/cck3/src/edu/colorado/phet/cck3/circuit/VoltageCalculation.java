/** Sam Reid*/
package edu.colorado.phet.cck3.circuit;

import edu.colorado.phet.cck3.circuit.components.Battery;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Oct 7, 2004
 * Time: 4:57:32 PM
 * Copyright (c) Oct 7, 2004 by Sam Reid
 */
public class VoltageCalculation {
    private Circuit circuit;
    private VoltageDifference voltageDifference;

    public VoltageCalculation( Circuit circuit ) {
        this.circuit = circuit;
        voltageDifference = new GraphTraversalVoltage( circuit );
    }

    public double getVoltage( Connection a, Connection b ) {
        if( a.equals( b ) ) {
            return 0;
        }
        else {
            Junction startJ = a.getJunction();
            Junction endJ = b.getJunction();
            double va = a.getVoltageAddon();
            double vb = -b.getVoltageAddon();//this has to be negative, because on the path VA->A->B->VB, the the VB computation is VB to B.
//            System.out.println( "va = " + va );
//            System.out.println( "vb = " + vb );
            double voltInit = ( va + vb );
//            double voltInit = ( va - vb );
//            double voltInit = ( vb - va );
//            double voltInit = -( va + vb );
            //the sign of va and vb depend on the
//            System.out.println( "voltInit = " + voltInit );

            double junctionAnswer = voltageDifference.getVoltage( new ArrayList(), startJ, endJ, 0 );
            double junctionAnswer2 = -voltageDifference.getVoltage( new ArrayList(), endJ, startJ, 0 );
//            System.out.println( "junctionAnswer = " + junctionAnswer );
//            System.out.println( "junctionAnswer2 = " + junctionAnswer2 );
            double diff = ( junctionAnswer - junctionAnswer2 );
            if( diff > .0001 && !Double.isInfinite( junctionAnswer ) && !Double.isInfinite( junctionAnswer2 ) ) {
                new RuntimeException( "Junction answers inconsistent, ans1=" + junctionAnswer + ", ans2=" + junctionAnswer2 ).printStackTrace();
            }
            double result = Double.POSITIVE_INFINITY;
            if( !Double.isInfinite( junctionAnswer ) ) {
                result = ( junctionAnswer + voltInit );
            }
            else if( !Double.isInfinite( junctionAnswer2 ) ) {
                result = ( junctionAnswer2 + voltInit );
            }
            //            return result;
//            System.out.println( "result = " + result );
            return result;
        }
    }

    private static interface VoltageDifference {
        public double getVoltage( ArrayList visited, Junction at, Junction target, double volts );
    }

    public static class GraphTraversalVoltage implements VoltageDifference {
        Circuit circuit;

        public GraphTraversalVoltage( Circuit circuit ) {
            this.circuit = circuit;
        }

        public double getVoltage( ArrayList visited, Junction at, Junction target, double volts ) {
//        System.out.println( "at = " + at + ", target=" + target );
//        System.out.println( "visited = " + visited );
            if( at == target ) {
                return volts;
            }
            Branch[] out = circuit.getAdjacentBranches( at );
            for( int i = 0; i < out.length; i++ ) {
                Branch branch = out[i];
//            System.out.println( "branch = " + branch );
                Junction opposite = branch.opposite( at );
//            System.out.println( "opposite = " + opposite );
                if( !visited.contains( branch ) ) {  //don't cross the same bridge twice.
                    double dv = 0.0;
                    if( branch instanceof Battery ) {
                        Battery batt = (Battery)branch;
                        dv = batt.getEffectiveVoltageDrop();//climb
                    }
                    else {
                        dv = -branch.getVoltageDrop();//fall
                    }
                    if( branch.getEndJunction() == opposite ) {
                        dv *= 1;
                    }
                    else {
                        dv *= -1;
                    }
//                System.out.println( "dv = " + dv );
                    ArrayList copy = new ArrayList( visited );
                    copy.add( branch );
                    double result = getVoltage( copy, opposite, target, volts + dv );
                    if( !Double.isInfinite( result ) ) {
                        return result;
                    }
                }
            }

            //no novel path to target, so voltage is infinite
            return Double.POSITIVE_INFINITY;
        }
    }

    public abstract static class Connection {
        public abstract Junction getJunction();

        public abstract double getVoltageAddon();
    }

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

        public Branch[] getBranchesToIgnore() {
            return new Branch[0];
        }

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
