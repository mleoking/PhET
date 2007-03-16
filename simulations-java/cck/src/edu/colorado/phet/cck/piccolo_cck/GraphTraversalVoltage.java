package edu.colorado.phet.cck.piccolo_cck;

import edu.colorado.phet.cck.model.Circuit;
import edu.colorado.phet.cck.model.Junction;
import edu.colorado.phet.cck.model.components.Battery;
import edu.colorado.phet.cck.model.components.Branch;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Sep 27, 2006
 * Time: 9:05:21 AM
 * Copyright (c) Sep 27, 2006 by Sam Reid
 */
public class GraphTraversalVoltage implements VoltageDifference {
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
