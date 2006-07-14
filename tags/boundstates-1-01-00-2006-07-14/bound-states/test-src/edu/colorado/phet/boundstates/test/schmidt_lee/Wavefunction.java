package edu.colorado.phet.boundstates.test.schmidt_lee;

import java.text.ParseException;
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
 * A one dimensional Schroedinger Wave Function class
 */
public class Wavefunction {

    private double[] psi, v;
    private double energy;
    private double xmin, xmax, hb;
    private int n, node;

    /**
     * The constructor calculates the potential, wave function, and the energy.
     *
     * @throws java.text.ParseException ParseException occurs when the parser is
     *                                  unable to evaluate the potential because of syntax errors.
     * @throws BoundException           BoundException occurs when the integrator
     *                                  is unable to find an upper or lower bound to the energy -- generally
     *                                  because of underflows or overflows or because the step size is too small.
     */
    public Wavefunction( double hb, double xmin, double xmax, int n, int node,
                         PotentialFunction parser ) throws ParseException, BoundException {

        this.hb = hb;
        this.xmin = xmin;
        this.xmax = xmax;
        this.n = n;
        this.node = node;
        this.v = new double[n];

        v = calv( parser );
        energy = cale();
        psi = calpsi();
    }

    /**
     * Return the energy
     */
    public double getE() {
        return energy;
    }

    /**
     * return an array of wave function values
     */
    public double[] getPsi() {
        double[] psi = new double[n];
        System.arraycopy( this.psi, 0, psi, 0, n );
        return psi;
    }

    /**
     * return an array of potential values
     */
    public double[] getV() {
        double[] v = new double[n];
        System.arraycopy( this.v, 0, v, 0, n );
        return v;
    }

    /**
     * routine to calcuate the eigenvalue
     */
    private double cale() throws BoundException {
        final int MAXTRY = 100;
        final double SMALL = 1.e-10;
        int k;
        Tester t = null, tu = null, tl = null;
        double en = 0.0, eu, el;
//
// Find upper bound
//
        eu = hb * ( node + 1 ) * ( node + 1 ) * 10.0 / ( ( xmax - xmin ) * ( xmax - xmin ) );
        for( k = 0; k < MAXTRY; k++ ) {
            eu *= 2.;
            tu = integ( eu );
            if( tu.isupper( node ) ) {
                break;
            }
        }
        if( k == MAXTRY ) {
//         System.out.println("Upper bound not found");
            throw new BoundException( "Upper bound not found, node=" + node );
        }
//
// Find lower bound
//
        el = -hb * ( node + 1 ) * ( node + 1 ) * 10.0 / ( ( xmax - xmin ) * ( xmax - xmin ) );
        for( k = 0; k < MAXTRY; k++ ) {
            el *= 2.;
            tl = integ( el );
            if( !tl.isupper( node ) ) {
                break;
            }
        }
        if( k == MAXTRY ) {
//         System.out.println("Lower bound not found");
            throw new BoundException( "Lower bound not found, node=" + node );
        }
//
// Binary chop to get close
//
        for( k = 0; k < MAXTRY && ( tl.node != tu.node ); k++ ) {
            en = .5 * ( eu + el );
            t = integ( en );
            if( t.isupper( node ) ) {
                eu = en;
                tu = t;
            }
            else {
                el = en;
                tl = t;
            }
        }
        if( k == MAXTRY ) {
         System.err.println("No convergence in binary chop, node=" + node);
            return en; // Fix by throwing exception
        }
//
// Linearly interpolate for better convergence
//
        for( k = 0; ( k < MAXTRY ) && ( Math.abs( tu.alogd - tl.alogd ) > SMALL ); k++ ) {
            en = eu - ( eu - el ) * tu.alogd / ( tl.alogd - tu.alogd );
            if( en > eu || en < el ) {
                en = 0.5 * ( eu + el );
            }
            t = integ( en );
            if( t.isupper( node ) ) {
                eu = en;
                tu = t;
            }
            else {
                el = en;
                tl = t;
            }
        }
        if( k == MAXTRY ) {
         System.err.println("No convergence in interpolation, node=" + node );
            return en; // Fix by throwing exception
        }
        return en;
    }

    /**
     * routine to integrate the Schroedinger equation for a guessed value
     * of the energy and return whether the energy is too high or low.
     */
    private Tester integ( double e ) {
        int i, nmat, node;
        double dx, alogd, h12, u1, u2, u3, v1, v2, v3, u1f, u2f, hbi;
        hbi = 1.0 / hb;
        nmat = (int)( n * 0.53 ); // pick match point -- do better later
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
        for( i = 2; i <= nmat + 1; i++ ) {
            u1 = u2;
            u2 = u3;
            v1 = v2;
            v2 = v3;
            v3 = hbi * ( v[i] - e );
            u3 = ( u2 * ( 2. + 10. * h12 * v2 ) - u1 * ( 1. - h12 * v1 ) ) / ( 1. - h12 * v3 );
            if( i <= nmat && ( ( u3 < 0. && u2 > 0. ) || ( u3 > 0. && u2 < 0. ) ) ) {
                node++;
            }
        }
        alogd = ( u3 - u1 ) / ( 2. * dx * u2 );
        u2 = u1f;
        u3 = u2f;
        v2 = 0.;
        v3 = hbi * ( v[n - 2] - e );
        for( i = n - 3; i >= nmat - 1; i-- ) {
            u1 = u2;
            u2 = u3;
            v1 = v2;
            v2 = v3;
            v3 = hbi * ( v[i] - e );
            u3 = ( u2 * ( 2. + 10. * h12 * v2 ) - u1 * ( 1. - h12 * v1 ) ) / ( 1. - h12 * v3 );
            if( i >= nmat && ( ( u3 < 0. && u2 > 0. ) || ( u3 > 0. && u2 < 0. ) ) ) {
                node++;
            }
        }
        alogd += ( u3 - u1 ) / ( 2. * dx * u2 );
        return new Tester( node, alogd );
    }

    /**
     * routine to calculate the potential.
     */
    private double[] calv( PotentialFunction parser ) throws ParseException {
        int i;
        double x, dx;
        double[] v = new double[n];
//      try {
//         parser.lines();

        dx = ( xmax - xmin ) / ( n - 1 );
        for( i = 1; i < n - 1; i++ ) {
            x = xmin + i * dx;
//            parser.evaluate(x);
//            parser.jjtree.rootNode().interpret();
            v[i] = parser.evaluate( x );
        }
        //     } catch (ParseException e) {
        //        System.out.println("Seqn: Errors during parse");
        //        e.printStackTrace();
        //     }
        return v;
    }

    /**
     * routine to calculate the wave function once the energy is known.
     */
    private double[] calpsi() {
        int i, nmat;
        double dx, h12, u1, u2, u3, v1, v2, v3, u1f, u2f, hbi, usave, uabs;
        double[] psi = new double[n];
        hbi = 1.0 / hb;
        nmat = (int)( n * 0.53 ); // pick match point -- do better later
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
        psi[0] = u2;
        psi[1] = u3;
        psi[n - 1] = u1f;
        psi[n - 2] = u2f;
        v2 = 0.;
        v3 = hbi * ( v[1] - energy );
        for( i = 2; i <= nmat; i++ ) {
            u1 = u2;
            u2 = u3;
            v1 = v2;
            v2 = v3;
            v3 = hbi * ( v[i] - energy );
            u3 = ( u2 * ( 2. + 10. * h12 * v2 ) - u1 * ( 1. - h12 * v1 ) ) / ( 1. - h12 * v3 );
            psi[i] = u3;
        }
        usave = u3;
        u2 = u1f;
        u3 = u2f;
        v2 = 0.;
        v3 = hbi * ( v[n - 2] - energy );
        for( i = n - 3; i >= nmat; i-- ) {
            u1 = u2;
            u2 = u3;
            v1 = v2;
            v2 = v3;
            v3 = hbi * ( v[i] - energy );
            u3 = ( u2 * ( 2. + 10. * h12 * v2 ) - u1 * ( 1. - h12 * v1 ) ) / ( 1. - h12 * v3 );
            psi[i] = u3;
        }
        usave /= u3;
        for( i = n - 2; i >= nmat; i-- ) {
            psi[i] *= usave;
        }
        usave = 0.0;
        for( i = 0; i < n; i++ ) {
            uabs = Math.abs( psi[i] );
            usave = ( usave > uabs ) ? usave : uabs;
        }
        usave = 1.0 / usave;
        for( i = 0; i < n; i++ ) {
            psi[i] *= usave;
        }
        return psi;
    }

//
// Inner class to test whether the energy is too high
//

    /**
     * Inner class to test whether the energy is too high
     */
    class Tester {
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

