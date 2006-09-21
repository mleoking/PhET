//package edu.colorado.phet.waveinterference.test;
//
///**
// * User: Sam Reid
// * Date: Aug 23, 2006
// * Time: 7:38:21 PM
// * Copyright (c) Aug 23, 2006 by Sam Reid
// */
//
//public class SphericalHarmonic {
//    public static void main( String[] args ) {
//        int L = 0;
//        int M = 0;
//        double value = getSphericalHarmonic( L, M );
//    }
//
//    private static Complex getSphericalHarmonic( int L, int m, double theta, double phi ) {
//        double coeff = Math.sqrt( ( 2 * L + 1 ) * factorial( L - m ) / ( 4 * Math.PI * factorial( L + m ) ) );
//        return coeff * legendre( L, m, Math.cos( theta ) ) * Complex.expImag( m * phi );
//    }
//}
