package edu.colorado.phet.circuitconstructionkit.model;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Oct 7, 2004
 * Time: 4:57:32 PM
 */
public class VoltageCalculation {
    private VoltageDifference voltageDifference;

    public VoltageCalculation( Circuit circuit ) {
        voltageDifference = new GraphTraversalVoltage( circuit );
    }

    public double getVoltage( Connection a, Connection b ) {
        if ( a.equals( b ) ) {
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
            if ( diff > .0001 && !Double.isInfinite( junctionAnswer ) && !Double.isInfinite( junctionAnswer2 ) ) {
                new RuntimeException( "Junction answers inconsistent, ans1=" + junctionAnswer + ", ans2=" + junctionAnswer2 ).printStackTrace();
            }
            double result = Double.POSITIVE_INFINITY;
            if ( !Double.isInfinite( junctionAnswer ) ) {
                result = ( junctionAnswer + voltInit );
            }
            else if ( !Double.isInfinite( junctionAnswer2 ) ) {
                result = ( junctionAnswer2 + voltInit );
            }
            //            return result;
//            System.out.println( "result = " + result );
            return result;
        }
    }

}
