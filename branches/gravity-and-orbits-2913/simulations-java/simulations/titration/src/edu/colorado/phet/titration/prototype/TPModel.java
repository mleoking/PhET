// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.titration.prototype;

import java.util.ArrayList;
import java.util.Collections;

import edu.colorado.phet.common.phetcommon.math.Complex;

/*
 * Titration prototype model, based on Kelly Lancaster's Octave code
 * and Jonathan Olson's polynomial root-finding implementation.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TPModel {

    private static final double Kw = TPConstants.Kw;

    private int rootsIterations;
    
    public TPModel( int rootsIterations ) {
        this.rootsIterations = rootsIterations;
    }
    
    public void setRootsIterations( int rootsIterations ) {
        this.rootsIterations = rootsIterations;
    }
    
    public int getRootsIterations() {
        return rootsIterations;
    }

    /**
     * Strong base titrated with a strong acid.
     * Octave implementation is in file strongbase.m
     */
    public double strongBase( double Ca, double Cb, double Va, double Vb ) {
        double a = -( Va + Vb );
        double b = Ca * Va - Cb * Vb;
        double c = Kw * ( Va + Vb );
        double H2 = ( -b - Math.sqrt( b * b - 4 * a * c ) ) / ( 2 * a );
        double pH2 = -Math.log10( H2 );
        return pH2;
    }

    /**
     * Weak base titrated with a strong acid.
     * Octave implementation is in file weakbase.m
     */
    public double weakBase( double Ca, double Cb, double Va, double Vb, double Kb ) {
        double a = -( Va + Vb );
        double b = Ca * Va - Cb * Vb - ( Kw / Kb ) * ( Va + Vb );
        double c = Kw * ( Va + Vb ) + ( Ca * Va * Kw ) / Kb;
        double d = ( ( Kw * Kw ) / Kb ) * ( Va + Vb );
        double[] coefficients = new double[]{a, b, c, d}; // high to low
        double pH = pH( coefficients );
        return pH;
    }

    /**
     * Strong acid titrated with a strong base.
     * Octave implementation is in file strongacid.m
     */
    public double strongAcid( double Ca, double Cb, double Va, double Vb ) {
        double a = Va + Vb;
        double b = Cb * Vb - Ca * Va;
        double c = -Kw * ( Va + Vb );
        double H1 = ( -b + Math.sqrt( b * b - 4 * a * c ) ) / ( 2 * a );
        double pH1 = -Math.log10( H1 );
        return pH1;
    }

    /**
     * Weak acid titrated with a strong base.
     * Octave implementation is in file weakacid.m
     */
    public double weakAcid( double Ca, double Cb, double Va, double Vb, double Ka ) {
        double a = Va + Vb;
        double b = Cb * Vb + Va * Ka + Vb * Ka;
        double c = Cb * Vb * Ka - Ca * Va * Ka - Va * Kw - Vb * Kw;
        double d = -Kw * Ka * ( Va + Vb );
        double[] coefficients = new double[]{a, b, c, d}; // high to low
        double pH = pH( coefficients );
        return pH;
    }

    /**
     * Diprotic acid titrated with a strong base.
     * Octave implementation is in file diproticacid.m
     */
    public double diproticAcid( double Ca, double Cb, double Va, double Vb, double Ka1, double Ka2 ) {
        double a = Va + Vb;
        double b = Cb * Vb + Va * Ka1 + Vb * Ka1;
        double c = Cb * Vb * Ka1 - Ca * Va * Ka1 + Va * Ka1 * Ka2 + Vb * Ka1 * Ka2 - Va * Kw - Vb * Kw;
        double d = Cb * Vb * Ka1 * Ka2 - 2 * Ca * Va * Ka1 * Ka2 - Va * Ka1 * Kw - Vb * Ka1 * Kw;
        double e = -( Ka1 * Ka2 * Kw ) * ( Va + Vb );
        double[] coefficients = new double[]{a, b, c, d, e}; // high to low
        return pH( coefficients );
    }

    /**
     * Triprotic acid titrated with a strong base.
     * Octave implementation is in file triproticacid.m
     */
    public double triproticAcid( double Ca, double Cb, double Va, double Vb, double Ka1, double Ka2, double Ka3 ) {
        double a = Va + Vb;
        double b = Cb * Vb + Va * Ka1 + Vb * Ka1;
        double c = -Ca * Va * Ka1 + Cb * Vb * Ka1 + Va * Ka1 * Ka2 + Vb * Ka1 * Ka2 - Va * Kw - Vb * Kw;
        double d = -2 * Ca * Va * Ka1 * Ka2 + Cb * Vb * Ka1 * Ka2 + Va * Ka1 * Ka2 * Ka3 + Vb * Ka1 * Ka2 * Ka3 - Va * Ka1 * Kw - Vb * Ka1 * Kw;
        double e = -3 * Ca * Va * Ka1 * Ka2 * Ka3 + Cb * Vb * Ka1 * Ka2 * Ka3 - Va * Ka1 * Ka2 * Kw - Vb * Ka1 * Ka2 * Kw;
        double f = -Va * Ka1 * Ka2 * Ka3 * Kw - Vb * Ka1 * Ka2 * Ka3 * Kw;
        double[] coefficients = new double[]{a, b, c, d, e, f}; // high to low
        return pH( coefficients );
    }

    /*
     * Given an array of complex roots, return a sorted array of the real values.
     */
    private static Double[] sortReal( Complex[] roots ) {
        ArrayList<Double> list = new ArrayList<Double>();
        for ( int i = 0; i < roots.length; i++ ) {
            list.add( new Double( roots[i].getReal() ) );
        }
        Collections.sort( list );
        return (Double[]) list.toArray( new Double[list.size()] );
    }

    /*
     * Given an array of coefficients (high to low) for an Nth order polynomial,
     * find the roots, sort them, and then calculate the pH based on the Nth root. 
     */
    private double pH( double[] coefficients ) {
        Complex[] roots = roots( coefficients );
        Double[] rootsSorted = sortReal( roots );
        double H = rootsSorted[coefficients.length - 2];
        double pH = -Math.log10( H );
        if ( Double.isNaN( pH ) ) {
            System.out.println( "WARNING: pH = " + pH );
            debugPrintCoefficients( coefficients );
            debugPrintRealRoots( roots );
        }
        return pH;
    }

    private Complex[] roots( double[] coefficients ) {
        Complex[] polynomial = new Complex[coefficients.length];
        for ( int i = 0; i < coefficients.length; i++ ) {
            polynomial[i] = new Complex( coefficients[i], 0 );
        }
        Complex[] roots = LaguerreRoots.findRoots( polynomial, rootsIterations );
        return roots;
    }

    private static void debugPrintCoefficients( double[] coefficients ) {
        System.out.print( "coefficients:" );
        for ( int i = 0; i < coefficients.length; i++ ) {
            System.out.print( " " + coefficients[i] );
        }
        System.out.println();
    }

    private static void debugPrintRealRoots( Complex[] roots ) {
        System.out.print( "roots:" );
        for ( int i = 0; i < roots.length; i++ ) {
            System.out.print( " " + roots[i].getReal() );
        }
        System.out.println();
    }
}
