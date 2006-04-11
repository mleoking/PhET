/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling.model;

import edu.colorado.phet.quantumtunneling.util.Complex;


/**
 * WaveFunctionSolution contains the wave function solution
 * for specific values of x and t.  This object is IMMUTABLE.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class WaveFunctionSolution {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private double _x;
    private double _t;
    private Complex _incidentPart;
    private Complex _reflectedPart;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public WaveFunctionSolution( double x, double t, Complex incidentPart, Complex reflectedPart ) {
        _incidentPart = incidentPart;
        _reflectedPart = reflectedPart;
    }
    
    public WaveFunctionSolution( double x, double t, Complex incidentPart ) {
        this( x, t, incidentPart, null );
    }
    
    public WaveFunctionSolution( double x, double t ) {
        this( x, t, null, null );
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Gets the position value, x.
     */
    public double getX() {
        return _x;
    }
    
    /**
     * Gets the time value, t.
     * @return
     */
    public double getT() {
        return _t;
    }
    
    /**
     * Gets the energy of the incident wave.
     * @return
     */
    public Complex getIncidentPart() {
        return _incidentPart;
    }
    
    /**
     * Gets the energy of the reflected wave.
     * @return
     */
    public Complex getReflectedPart() {
        return _reflectedPart;
    }
    
    /**
     * Gets the sum of the incident and reflected waves.
     * @return
     */
    public Complex getSum() {
        Complex sum = null;
        if ( _incidentPart == null ) {
            sum = _reflectedPart;
        }
        else if ( _reflectedPart == null ) {
            sum = _incidentPart;
        }
        else {
            sum = _incidentPart.getAdd( _reflectedPart );
        }
        return sum;
    }
}
