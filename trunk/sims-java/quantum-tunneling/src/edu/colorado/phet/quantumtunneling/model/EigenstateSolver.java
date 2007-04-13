
package edu.colorado.phet.quantumtunneling.model;

/*
 ************************************************************************
 * NOTE!
 * 
 * This code was modified for use with PhET Quantum Tunneling simulation.
 * The original copyright notice appears below.
 * The original code contained additional functionality that 
 * was needed by Quantum Tunneling, specifically the ability
 * to solve the wave function for a specified eigenstate.
 * 
 * ************************************************************************
 */

/*
 Copyright (C) 1998 Kevin E. Schmidt and Michael A. Lee

 This program is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; either version 2 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.

 We can be contacted at the addresses given in the Schroedinger.tex
 or other forms of the documentation.
 */

/**
 * EigenstateSolver can be used to calculate eigenstate energies for a potential.
 */
public class EigenstateSolver {

    private static final boolean REPORT_WARNINGS = false;
    
    private double[]v;
    private double hb, xmin, xmax;
    private int n;

    /**
     * PotentialEvaluator is the interface used to evaluate a potential energy function.
     * The EigenstateSolver uses this interface to assemble a profile of the 
     * potential energy.
     */
    public interface PotentialEvaluator {
        double evaluate( double x );
    }

    /**
     * EigenstateException is an exception that may be thrown by the EigenstateSolver.
     * This exception will always have an associated message.
     */
    public static class EigenstateException extends Exception {
        public EigenstateException( String message ) {
            super( message );
        }
    }

    /**
     * Constructor.
     *
     * @param hb        magic constant, = (hbar^2)/(2*mass)
     * @param xmin      minimum position (model coordinates)
     * @param xmax      maximum position (model coordinates)
     * @param n number  of sample points
     * @param evaluator potential energy function evaluator
     * @throws EigenstateException
     */
    public EigenstateSolver( double hb, double xmin, double xmax, int n, PotentialEvaluator evaluator ) {
        this.hb = hb;
        this.xmin = xmin;
        this.xmax = xmax;
        this.n = n;
        this.v = calculatePotential( evaluator );
    }

    /**
     * Gets the eigenstate energy for a specified node.
     * 
     * @param node
     * @throws EigenstateException when the integrator is unable to find an upper or 
     *                              lower bound to the energy -- generally because of 
     *                              underflows or overflows or because the step size is too small.
     */
    public double getEnergy( int node ) throws EigenstateException {
        return calculateEnergy( node );
    }

    /*
     * Calculates the potential.
     */
    private double[] calculatePotential( PotentialEvaluator evaluator ) {
        int i;
        double x;
        double[] v = new double[n];
        final double dx = ( xmax - xmin ) / ( n - 1 );
        for ( i = 1; i < n - 1; i++ ) {
            x = xmin + i * dx;
            v[i] = evaluator.evaluate( x );
        }
        return v;
    }
    
    /*
     * Calcuates the eigenvalue for a specified node.
     */
    private double calculateEnergy( int node ) throws EigenstateException {
        final int MAXTRY = 100;
        final double SMALL = 1.e-10;
        int k;
        Tester t = null, tu = null, tl = null;
        double en = 0.0, eu, el;
        //
        // Find upper bound
        //
        eu = hb * ( node + 1 ) * ( node + 1 ) * 10.0 / ( ( xmax - xmin ) * ( xmax - xmin ) );
        for ( k = 0; k < MAXTRY; k++ ) {
            eu *= 2.;
            tu = integrate( eu );
            if ( tu.isupper( node ) ) {
                break;
            }
        }
        if ( k == MAXTRY ) {
            throw new EigenstateException( "Upper bound not found, node=" + node );
        }
        //
        // Find lower bound
        //
        el = -hb * ( node + 1 ) * ( node + 1 ) * 10.0 / ( ( xmax - xmin ) * ( xmax - xmin ) );
        for ( k = 0; k < MAXTRY; k++ ) {
            el *= 2.;
            tl = integrate( el );
            if ( !tl.isupper( node ) ) {
                break;
            }
        }
        if ( k == MAXTRY ) {
            throw new EigenstateException( "Lower bound not found, node=" + node );
        }
        //
        // Binary chop to get close
        //
        for ( k = 0; k < MAXTRY && ( tl.node != tu.node ); k++ ) {
            en = .5 * ( eu + el );
            t = integrate( en );
            if ( t.isupper( node ) ) {
                eu = en;
                tu = t;
            }
            else {
                el = en;
                tl = t;
            }
        }
        if ( k == MAXTRY ) {
            warn( "No convergence in binary chop, node=" + node );
            return en; //XXX Fix by throwing exception?
        }
        //
        // Linearly interpolate for better convergence
        //
        for ( k = 0; ( k < MAXTRY ) && ( Math.abs( tu.alogd - tl.alogd ) > SMALL ); k++ ) {
            en = eu - ( eu - el ) * tu.alogd / ( tl.alogd - tu.alogd );
            if ( en > eu || en < el ) {
                en = 0.5 * ( eu + el );
            }
            t = integrate( en );
            if ( t.isupper( node ) ) {
                eu = en;
                tu = t;
            }
            else {
                el = en;
                tl = t;
            }
        }
        if ( k == MAXTRY ) {
            warn( "No convergence in interpolation, node=" + node );
            return en; //XXX Fix by throwing exception?
        }
        return en;
    }

    /*
     * Integrates the Schroedinger equation for a guessed value of
     * the energy and returns whether the energy is too high or low.
     */
    private Tester integrate( double e ) {
        int i, nmat, node;
        double dx, alogd, h12, u1, u2, u3, v1, v2, v3, u1f, u2f, hbi;
        hbi = 1.0 / hb;
        nmat = (int) ( n * 0.53 ); // pick match point -- do better later
        dx = ( xmax - xmin ) / ( n - 1 );
        h12 = dx * dx / 12.0;
        //
        // Initial and final boundary conditions
        //
        u1 = 0.0; // to make javac happy
        u2 = 0.0;
        u3 = dx;
        u1f = 0.0;
        u2f = dx;
        node = 0;
        v2 = 0.;
        v3 = hbi * ( v[1] - e );
        for ( i = 2; i <= nmat + 1; i++ ) {
            u1 = u2;
            u2 = u3;
            v1 = v2;
            v2 = v3;
            v3 = hbi * ( v[i] - e );
            u3 = ( u2 * ( 2. + 10. * h12 * v2 ) - u1 * ( 1. - h12 * v1 ) ) / ( 1. - h12 * v3 );
            if ( i <= nmat && ( ( u3 < 0. && u2 > 0. ) || ( u3 > 0. && u2 < 0. ) ) ) {
                node++;
            }
        }
        alogd = ( u3 - u1 ) / ( 2. * dx * u2 );
        u2 = u1f;
        u3 = u2f;
        v2 = 0.;
        v3 = hbi * ( v[n - 2] - e );
        for ( i = n - 3; i >= nmat - 1; i-- ) {
            u1 = u2;
            u2 = u3;
            v1 = v2;
            v2 = v3;
            v3 = hbi * ( v[i] - e );
            u3 = ( u2 * ( 2. + 10. * h12 * v2 ) - u1 * ( 1. - h12 * v1 ) ) / ( 1. - h12 * v3 );
            if ( i >= nmat && ( ( u3 < 0. && u2 > 0. ) || ( u3 > 0. && u2 < 0. ) ) ) {
                node++;
            }
        }
        alogd += ( u3 - u1 ) / ( 2. * dx * u2 );
        return new Tester( node, alogd );
    }

    /*
     * Prints a warning message to System.err.
     */
    private void warn( String message ) {
        if ( REPORT_WARNINGS ) {
            System.err.println( "EigenstateSolver WARNING: " + message );
        }
    }
    
    /*
     * Tests whether the energy is too high.
     */
    private static class Tester {

        public int node; // Number of nodes
        public double alogd; //Derivative

        public Tester( int node, double alogd ) {
            this.node = node;
            this.alogd = alogd;
        }

        public boolean isupper( int node ) {
            return this.node > node || ( this.node == node && alogd < 0.0 );
        }
    }
}
