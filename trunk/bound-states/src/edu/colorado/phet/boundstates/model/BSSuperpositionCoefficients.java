/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.boundstates.model;

import java.util.ArrayList;

/**
 * BSSuperpositionCoefficients is a collection of superposition coefficients.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSSuperpositionCoefficients extends BSObservable {
        
    private ArrayList _coefficients; // array of Double
   
    public BSSuperpositionCoefficients() {
        _coefficients = new ArrayList();
    }
    
    public void setNumberOfCoefficients( int numberOfCoefficients, boolean notify ) {
        if ( numberOfCoefficients < 0 ) {
            throw new IllegalArgumentException( "numberOfCoefficients must be >= 0: " + numberOfCoefficients );
        }
        
        if ( numberOfCoefficients == _coefficients.size() ) {
            // no change, do nothing
        }
        else {
            if ( numberOfCoefficients == 0 ) {
                _coefficients.clear();
            }
            else if ( getNumberOfCoefficients() == 0 || numberOfCoefficients < getNumberOfCoefficients() ) {
                /* 
                 * If we have no coefficients yet, or if the number of eigenstates has decreased,
                 * then set the first coefficient to 1 and all the others to 0.
                 */
                _coefficients.clear();
                _coefficients.add( new Double( 1 ) );
                for ( int i = 1; i < numberOfCoefficients; i++ ) {
                    _coefficients.add( new Double( 0 ) );
                }
            }
            else {
                /*
                 * If the number of eigenstates has increased,
                 * keep the existing coefficients and add new ones that are 0.
                 */
                for ( int i = getNumberOfCoefficients(); i < numberOfCoefficients; i++ ) {
                    _coefficients.add( new Double( 0 ) );
                }
            }
            
            if ( notify ) {
                notifyObservers();
            }
        }
    }
    
    /**
     * Gets the number of coefficients (possibly zero).
     * 
     * @return
     */
    public int getNumberOfCoefficients() {
        return _coefficients.size();
    }
    
    /**
     * Gets a coefficient's value.
     * 
     * @param index
     * @return
     * @throws IndexOutOfBoundsException if index is out of bounds
     */
    public double getCoefficient( int index ) {
        if ( index > _coefficients.size() - 1 ) {
            throw new IndexOutOfBoundsException( "index is out of bounds: " + index );
        }
        return ((Double)_coefficients.get( index )).doubleValue();
    }
    
    /**
     * Sets a coefficient's value.
     * 
     * @param index
     * @param value
     * @throws IndexOutOfBoundsException if index is out of bounds
     * @throws IllegalArgumentException if value isn't between 0 and 1
     */
    public void setCoefficient( int index, double value ) {
        if ( index > _coefficients.size() - 1 ) {
            throw new IndexOutOfBoundsException( "index is out of bounds: " + index );
        }
        if ( value < 0 || value > 1 ) {
            throw new IllegalArgumentException( "value must be between 0 and 1: " + value );
        }
        _coefficients.set( index, new Double( value ) );
        notifyObservers();
    }
}
