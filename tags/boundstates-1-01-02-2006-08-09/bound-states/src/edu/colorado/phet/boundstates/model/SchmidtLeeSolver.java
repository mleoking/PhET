
package edu.colorado.phet.boundstates.model;

/**
 * SchmidtLeeSolver calculates eigenstates and 1D Schrodinger wave functions
 * for a potential function.
 * <p>
 * This is a modified version of code written by Schmidt & Lee.
 * CHANGES:
 * <ul>  
 * <li>changed class name
 * <li>changed some method names for clarity
 * <li>changed some member data names for clarity
 * <li>added an inner exception type
 * <li>factored out constants
 * <li>added underscore prefix to member data names
 * <li>added/modified javadoc
 * <li>interface directly with BSAbstractPotential to calculate potential
 * <li>defer calculations until they are requested, instead of precalculating in constructor
 * <li>added more verbose warning messages when eigenstate solver fails
 * <li>added comment blocks
 * <li>reordered methods so that there is clear division between eigenstate solve and wave function solver
 * <li>decrease MAX_TRIES value to improve responsiveness of UI components
 * </ul>
 * <p>
 * Here is the original copyright notice:
 * <p>
 *  Copyright (C) 1998 Kevin E. Schmidt and Michael A. Lee
 *  
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *  
 *  We can be contacted at the addresses given in the Schroedinger.tex
 *  or other forms of the documentation.
 */
public class SchmidtLeeSolver {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final boolean REPORT_WARNINGS = false;
    
    private static final int MAX_TRIES = 100;
    private static final double SMALL = 1.E-10; // used in interpolator
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private double _hb;  // magic constant, = (hbar^2) / (2 * mass)
    private double _minX; // lower bound for position, in nm
    private double _maxX; // upper bound for position, in nm
    private int _numberOfPoints; // number of sample points in our "lattice"
    private double[] _potentialValues; // potential energy value along the "lattice", in eV

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param hb magic constant, = (hbar^2) / (2 * mass)
     * @param minX mininum position
     * @param maxX maximum position
     * @param numberOfPoints
     * @param potential
     */
    public SchmidtLeeSolver( double hb, double minX, double maxX, int numberOfPoints, BSAbstractPotential potential ) {
        _hb = hb;
        _minX = minX;
        _maxX = maxX;
        _numberOfPoints = numberOfPoints;
        _potentialValues = calculatePotential( potential );
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Gets the energy of the eigenstate for a specified number of nodes.
     * 
     * @param nodes
     */
    public double getEnergy( int nodes ) throws SchmidtLeeException {
        return calculateEnergy( nodes );
    }

    /**
     * Gets the wave function values for a specified eigenstate energy.
     * 
     * @param energy
     */
    public double[] getWaveFunction( final double energy ) {
        return calculateWaveFunction( energy );
    }

    //----------------------------------------------------------------------------
    // Potential Energy methods
    //----------------------------------------------------------------------------
    
    /*
     * Calculates the lattice points for a specified potential.
     * 
     * @param potential
     */
    private double[] calculatePotential( BSAbstractPotential potential ) {
        int i;
        double x, dx;
        double[] v = new double[_numberOfPoints];
        dx = ( _maxX - _minX ) / ( _numberOfPoints - 1 );
        for ( i = 1; i < _numberOfPoints - 1; i++ ) {
            x = _minX + i * dx;
            v[i] = potential.getEnergyAt( x );
        }
        return v;
    }
    
    //----------------------------------------------------------------------------
    // Eigenstate solver
    //----------------------------------------------------------------------------
    
    /*
     * Calcuates the eigenvalue for a specified number of nodes.
     * 
     * @param node
     */
    private double calculateEnergy( final int nodes ) throws SchmidtLeeException {
        
        int k;
        Tester t = null, tu = null, tl = null;
        double en = 0.0, eu, el;

        // Find upper bound...
        eu = _hb * ( nodes + 1 ) * ( nodes + 1 ) * 10.0 / ( ( _maxX - _minX ) * ( _maxX - _minX ) );
        for ( k = 0; k < MAX_TRIES; k++ ) {
            eu *= 2.;
            tu = integrate( eu );
            if ( tu.isupper( nodes ) ) {
                break;
            }
        }
        if ( k == MAX_TRIES ) {
            throw new SchmidtLeeException( "upper bound not found, nodes=" + nodes );
        }

        // Find lower bound...
        el = -_hb * ( nodes + 1 ) * ( nodes + 1 ) * 10.0 / ( ( _maxX - _minX ) * ( _maxX - _minX ) );
        for ( k = 0; k < MAX_TRIES; k++ ) {
            el *= 2.;
            tl = integrate( el );
            if ( !tl.isupper( nodes ) ) {
                break;
            }
        }
        if ( k == MAX_TRIES ) {
            throw new SchmidtLeeException( "lower bound not found, nodes=" + nodes );
        }

        // Binary chop to get close...
        for ( k = 0; k < MAX_TRIES && ( tl.node != tu.node ); k++ ) {
            en = .5 * ( eu + el );
            t = integrate( en );
            if ( t.isupper( nodes ) ) {
                eu = en;
                tu = t;
            }
            else {
                el = en;
                tl = t;
            }
        }
        if ( k == MAX_TRIES ) {
            warn( "No convergence in binary chop, nodes=" + nodes );
            return en;
        }

        // Linearly interpolate for better convergence...
        for ( k = 0; ( k < MAX_TRIES ) && ( Math.abs( tu.alogd - tl.alogd ) > SMALL ); k++ ) {
            en = eu - ( eu - el ) * tu.alogd / ( tl.alogd - tu.alogd );
            if ( en > eu || en < el ) {
                en = 0.5 * ( eu + el );
            }
            t = integrate( en );
            if ( t.isupper( nodes ) ) {
                eu = en;
                tu = t;
            }
            else {
                el = en;
                tl = t;
            }
        }
        if ( k == MAX_TRIES ) {
            warn( "No convergence in interpolation, nodes=" + nodes );
            return en;
        }

        return en;
    }

    /*
     * Integrates the Schroedinger equation for a guessed value
     * of the energy and returns whether the energy is too high or low.
     */
    private Tester integrate( double e ) {
        int i, nmat, node;
        double dx, alogd, h12, u1, u2, u3, v1, v2, v3, u1f, u2f, hbi;
        hbi = 1.0 / _hb;
        nmat = (int) ( _numberOfPoints * 0.53 ); // pick match point -- do better later
        dx = ( _maxX - _minX ) / ( _numberOfPoints - 1 );
        h12 = dx * dx / 12.0;

        // Initial and final boundary conditions...
        u1 = 0.0; // to make javac happy
        u2 = 0.0;
        u3 = dx;
        u1f = 0.0;
        u2f = dx;
        node = 0;
        v2 = 0.;
        v3 = hbi * ( _potentialValues[1] - e );
        for ( i = 2; i <= nmat + 1; i++ ) {
            u1 = u2;
            u2 = u3;
            v1 = v2;
            v2 = v3;
            v3 = hbi * ( _potentialValues[i] - e );
            u3 = ( u2 * ( 2. + 10. * h12 * v2 ) - u1 * ( 1. - h12 * v1 ) ) / ( 1. - h12 * v3 );
            if ( i <= nmat && ( ( u3 < 0. && u2 > 0. ) || ( u3 > 0. && u2 < 0. ) ) ) {
                node++;
            }
        }
        alogd = ( u3 - u1 ) / ( 2. * dx * u2 );
        u2 = u1f;
        u3 = u2f;
        v2 = 0.;
        v3 = hbi * ( _potentialValues[_numberOfPoints - 2] - e );
        for ( i = _numberOfPoints - 3; i >= nmat - 1; i-- ) {
            u1 = u2;
            u2 = u3;
            v1 = v2;
            v2 = v3;
            v3 = hbi * ( _potentialValues[i] - e );
            u3 = ( u2 * ( 2. + 10. * h12 * v2 ) - u1 * ( 1. - h12 * v1 ) ) / ( 1. - h12 * v3 );
            if ( i >= nmat && ( ( u3 < 0. && u2 > 0. ) || ( u3 > 0. && u2 < 0. ) ) ) {
                node++;
            }
        }
        alogd += ( u3 - u1 ) / ( 2. * dx * u2 );
        return new Tester( node, alogd );
    }

    /**
     * Tests whether the energy is too high.
     */
    private static class Tester {

        public int node; // Number of nodes
        public double alogd; // Derivative

        public Tester( int node, double alogd ) {
            this.node = node;
            this.alogd = alogd;
        }

        public boolean isupper( int node ) {
            return this.node > node || ( this.node == node && alogd < 0.0 );
        }
    }
    
    //----------------------------------------------------------------------------
    // Wave Function solver
    //----------------------------------------------------------------------------
    
    /*
     * Calculates the wave function for a specified eigenstate energy.
     * 
     * @param energy
     */
    private double[] calculateWaveFunction( final double energy ) {
        int i, nmat;
        double dx, h12, u1, u2, u3, v1, v2, v3, u1f, u2f, hbi, usave, uabs;
        double[] psi = new double[_numberOfPoints];
        hbi = 1.0 / _hb;
        nmat = (int) ( _numberOfPoints * 0.53 ); // pick match point -- do better later
        dx = ( _maxX - _minX ) / ( _numberOfPoints - 1 );
        h12 = dx * dx / 12.0;

        // Initial and final boundary conditions...
        u1 = 0.0; // to make javac happy
        u2 = 0.0;
        u3 = dx;
        u1f = 0.0;
        u2f = dx;
        psi[0] = u2;
        psi[1] = u3;
        psi[_numberOfPoints - 1] = u1f;
        psi[_numberOfPoints - 2] = u2f;
        v2 = 0.;
        v3 = hbi * ( _potentialValues[1] - energy );
        for ( i = 2; i <= nmat; i++ ) {
            u1 = u2;
            u2 = u3;
            v1 = v2;
            v2 = v3;
            v3 = hbi * ( _potentialValues[i] - energy );
            u3 = ( u2 * ( 2. + 10. * h12 * v2 ) - u1 * ( 1. - h12 * v1 ) ) / ( 1. - h12 * v3 );
            psi[i] = u3;
        }
        usave = u3;
        u2 = u1f;
        u3 = u2f;
        v2 = 0.;
        v3 = hbi * ( _potentialValues[_numberOfPoints - 2] - energy );
        for ( i = _numberOfPoints - 3; i >= nmat; i-- ) {
            u1 = u2;
            u2 = u3;
            v1 = v2;
            v2 = v3;
            v3 = hbi * ( _potentialValues[i] - energy );
            u3 = ( u2 * ( 2. + 10. * h12 * v2 ) - u1 * ( 1. - h12 * v1 ) ) / ( 1. - h12 * v3 );
            psi[i] = u3;
        }
        usave /= u3;
        for ( i = _numberOfPoints - 2; i >= nmat; i-- ) {
            psi[i] *= usave;
        }
        usave = 0.0;
        for ( i = 0; i < _numberOfPoints; i++ ) {
            uabs = Math.abs( psi[i] );
            usave = ( usave > uabs ) ? usave : uabs;
        }
        usave = 1.0 / usave;
        for ( i = 0; i < _numberOfPoints; i++ ) {
            psi[i] *= usave;
        }
        return psi;
    }
  
    //----------------------------------------------------------------------------
    // Exception class
    //---------------------------------------------------------------------------
    
    /**
     * SchmidtLeeException is an exception that may be thrown by the SchmidtLeeSolver.
     * This exception will always have an associated message.
     */
    public static class SchmidtLeeException extends Exception {
        public SchmidtLeeException( String message ) {
            super( message );
        }
    }
    
    //----------------------------------------------------------------------------
    // Utilities
    //---------------------------------------------------------------------------
    
    /*
     * Prints a warning message to System.err.
     */
    private void warn( String message ) {
        if ( REPORT_WARNINGS ) {
            System.err.println( "SchmidtLeeSolver WARNING: " + message );
        }
    }
}
