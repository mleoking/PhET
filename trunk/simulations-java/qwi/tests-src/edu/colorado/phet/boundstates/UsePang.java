/* Copyright 2004, Sam Reid */
package edu.colorado.phet.boundstates;

/**
 * User: Sam Reid
 * Date: Mar 8, 2006
 * Time: 11:23:49 AM
 * Copyright (c) Mar 8, 2006 by Sam Reid
 */

public class UsePang {
    public static void main( String[] args ) {
        PangShooting pangShooting = new PangShooting();
        double epsilon = 1e-6;
//        double e = 2.49999;
        double de = 0.1;

        // Find the eigenvalue via the secant search
        int ni = 10;
//        double e = pangShooting.secant( ni, epsilon, 1.0, de );
//        System.out.println( "e = " + e );
        double dEnergy = 0.01;
        for( double e0 = 2.4; e0 < 10; e0 += dEnergy ) {
            try {
                double e = new PangShooting().secant( ni, epsilon, e0, de );
                System.out.println( "e0=" + e0 + ", e = " + e );
            }
            catch( Exception e ) {
                System.out.println( "Pang failed: e0=" + e0 );
            }
        }
    }
}
