/* Copyright 2006, University of Colorado */

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
import java.util.Iterator;

/**
 * BSSuperpositionCoefficients is a collection of superposition coefficients.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSSuperpositionCoefficients extends BSObservable {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    public static final int INDEX_UNDEFINED = -1;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private ArrayList _coefficients; // array of Double
   
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor, creates an empty set of coefficients.
     */
    public BSSuperpositionCoefficients() {
        _coefficients = new ArrayList();
    }
    
    /**
     * Copy constructor.
     * 
     * @param sc
     */
    public BSSuperpositionCoefficients( BSSuperpositionCoefficients sc ) {
        this();
        _coefficients.clear();
        Iterator i = sc._coefficients.iterator();
        while( i.hasNext() ) {
            _coefficients.add( i.next() );
        }
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Gets the coefficients values.
     * 
     * @return array of coefficient values
     */
    public double[] getCoefficients() {
        double[] values = new double[ getNumberOfCoefficients() ];
        for ( int i = 0; i < values.length; i++ ) {
            values[i] = getCoefficient( i );
        }
        return values;
    }
    
    /**
     * Sets the coefficient values.
     * 
     * @param values
     */
    public void setCoefficients( double[] values ) {
        setNotifyEnabled( false );
        setNumberOfCoefficients( values.length );
        for ( int i = 0; i < values.length; i++ ) {
            setCoefficient( i, values[i] );
        }
        setNotifyEnabled( true );
    }
    
    /**
     * Sets the number of coefficients.
     * <p>
     * If there are no coefficients, the first one is set to 1 and all others to zero.
     * <p>
     * If the number of coefficients is increasing, the new ones are set to zero.
     * <p>
     * If the number of coefficients is decreasing, then we need to examine the 
     * coefficient values. If any non-zero coefficients will be lost, then 
     * the first coefficient is set to 1 and all others to zero.
     * If only zero-valued coefficients will be lost, then they can
     * simply be deleted without changing the values of any of the
     * non-zero coefficients.
     * 
     * @param numberOfCoefficients
     */
    public void setNumberOfCoefficients( int numberOfCoefficients ) {
        if ( numberOfCoefficients < 0 ) {
            throw new IllegalArgumentException( "numberOfCoefficients must be >= 0: " + numberOfCoefficients );
        }
        
        final int currentNumberOfCoefficients = getNumberOfCoefficients();
        boolean countChanged = false;
        boolean valuesChanged = false;
        
        if ( numberOfCoefficients == currentNumberOfCoefficients ) {
            // no change, do nothing
        }
        else {
            if ( numberOfCoefficients == 0 ) {
                _coefficients.clear();
                countChanged = true;
                valuesChanged = true;
            }
            else if ( currentNumberOfCoefficients == 0 ) {
                // If we have no coefficients yet, then set the first coefficient to 1 and all the others to 0.
                _coefficients.clear();
                _coefficients.add( new Double( 1 ) );
                for ( int i = 1; i < numberOfCoefficients; i++ ) {
                    _coefficients.add( new Double( 0 ) );
                }
                countChanged = true;
                valuesChanged = true;
            }
            else if ( numberOfCoefficients > currentNumberOfCoefficients ) {
                // If the number of eigenstates is increasing, add new ones with zero values.
                for ( int i = getNumberOfCoefficients(); i < numberOfCoefficients; i++ ) {
                    _coefficients.add( new Double( 0 ) );
                }
                countChanged = true;
                valuesChanged = false;
            }
            else  { 
                /* 
                 * The number of coefficients is decreasing.
                 * If we lose any non-zero coefficients, renormalize the remaining coefficients.
                 * If the remaining coefficients are all zero, then set the lowest one to 1.
                 */
                boolean needToNormalize = false;
                for ( int i = currentNumberOfCoefficients - 1; i >= numberOfCoefficients; i-- ) {
                    double coefficient = ((Double)_coefficients.get( i ) ).doubleValue();
                    if ( coefficient != 0 ) {
                        needToNormalize = true;
                    }
                    _coefficients.remove( i );
                }
                
                if ( needToNormalize ) {
                    if ( getSum() == 0 ) {
                        if ( numberOfCoefficients > 0 ) {
                            _coefficients.set( 0, new Double( 1 ) );
                        }
                    }
                    else {
                        normalize();
                    }
                }
                
                countChanged = true;
                valuesChanged = needToNormalize;
            }
            
            Object arg = null;
            if ( valuesChanged ) {
                // value change takes precedence over count change
                arg = BSModel.PROPERTY_SUPERPOSITION_COEFFICIENTS_VALUES;
            }
            else if ( countChanged ) {
                arg = BSModel.PROPERTY_SUPERPOSITION_COEFFICIENTS_COUNT;
            }
            notifyObservers( arg );
        }
    }
    
    /**
     * Gets the number of coefficients (possibly zero).
     * 
     * @return int
     */
    public int getNumberOfCoefficients() {
        return _coefficients.size();
    }
    
    /**
     * Gets the number of non-zero coefficients.
     * 
     * @return int
     */
    public int getNumberOfNonZeroCoefficients() {
        int count = 0;
        Iterator i = _coefficients.iterator();
        while( i.hasNext() ) {
            Double coefficient = (Double)i.next();
            if ( coefficient.doubleValue() != 0 ) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * Gets a coefficient's value.
     * 
     * @param index
     * @return the value, between 0 and 1 inclusive
     * @throws IndexOutOfBoundsException if index is out of bounds
     */
    public double getCoefficient( int index ) {
        if ( index > _coefficients.size() - 1 ) {
            throw new IndexOutOfBoundsException( "index is out of bounds: " + index );
        }
        return ((Double)_coefficients.get( index )).doubleValue();
    }
    
    /**
     * Sets a coefficient's value and notifies observers.
     * 
     * @param index
     * @param value a value between 0 and 1 inclusive
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
        notifyObservers( BSModel.PROPERTY_SUPERPOSITION_COEFFICIENTS_VALUES );
    }
    
    /**
     * Sets one coefficient to 1, all others to zero.
     * Observers are notified.
     * 
     * @param index
     */
    public void setOneCoefficient( int index ) {
        if ( index > _coefficients.size() - 1 ) {
            throw new IndexOutOfBoundsException( "index is out of bounds: " + index );
        }
        boolean notifyEnabled = isNotifyEnabled();
        setNotifyEnabled( false );
        setAllZero();
        setCoefficient( index, 1 );
        setNotifyEnabled( notifyEnabled );
        notifyObservers( BSModel.PROPERTY_SUPERPOSITION_COEFFICIENTS_VALUES );
    }
    
    /**
     * Normalizes the coefficients.
     * <p>
     * Normalized means that c1^2 + c2^2 + ... + cn^2 = 1.
     * If normalization was required, observers are notified.
     */
    public void normalize() {
        final double sumOfSquares = getSumOfSquares();
        if ( sumOfSquares != 0 ) {
            boolean notifyEnabled = isNotifyEnabled();
            setNotifyEnabled( false );
            final int n = getNumberOfCoefficients();
            for ( int i = 0; i < n; i++ ) {
                double coefficient = getCoefficient( i );
                double normalizedValue = Math.sqrt( ( coefficient * coefficient ) / sumOfSquares );
                setCoefficient( i, normalizedValue );
            }
            setNotifyEnabled( notifyEnabled );
            notifyObservers( BSModel.PROPERTY_SUPERPOSITION_COEFFICIENTS_VALUES );
        }
    }
   
    /**
     * Determines whether the coefficients are normalized.
     * See the normalize method for the definition of normalized.
     * 
     * @return true or false
     */
    public boolean isNormalized() {
        return isNormalized( 0 /* normalizationError */ );
    }
    
    /**
     * This version of isNormalized allows you to specify
     * how close is good enough to be considered normalized.
     * This method is useful when the view only allows the user
     * to enter values with a limited precision.  For example, 
     * the user may only be able to enter 0.54, when the actual
     * coefficient should be 0.543.
     * 
     * @return true or false
     */
    public boolean isNormalized( double normalizationError ) {
        final double sumOfSquares = getSumOfSquares();
        final double difference = Math.abs( 1 - sumOfSquares );
        return ( difference <= normalizationError );
    }
    
    /**
     * Gets the sum of all the coefficients.
     * 
     * @return double
     */
    public double getSum() {
        double total = 0;
        final int n = getNumberOfCoefficients();
        for ( int i = 0; i < n; i++ ) {
            total += getCoefficient( i );
        }
        return total;
    }
    
    /*
     * Gets the sum of squares of all the coefficients
     * (c1^2 + c2^2 + ... + cn^2).
     * 
     * @return
     */
    private double getSumOfSquares() {
        double total = 0;
        final int n = getNumberOfCoefficients();
        for ( int i = 0; i < n; i++ ) {
            double coefficient = getCoefficient( i );
            total += ( coefficient * coefficient );
        }
        return total;
    }
    
    /**
     * Determines whether this set of coefficients represents a superposition state.
     * We are in a superposition state if we have at least 2 non-zero coefficients.
     * 
     * @return true or false
     */
    public boolean isSuperpositionState() {
        return ( getNumberOfNonZeroCoefficients() > 1 );
    }
    
    /**
     * Sets all coefficients to zero and notifies observers.
     */
    public void setAllZero() {
        boolean notifyEnabled = isNotifyEnabled();
        setNotifyEnabled( false );
        final int n = getNumberOfCoefficients();
        for ( int i = 0; i < n; i++ ) {
            setCoefficient( i, 0 );
        }
        setNotifyEnabled( notifyEnabled );
        notifyObservers( BSModel.PROPERTY_SUPERPOSITION_COEFFICIENTS_VALUES );
    }
    
    /**
     * Gets the index of the lowest selected eigenstate.
     * 
     * @return the index
     */
    public int getLowestNonZeroCoefficientIndex() {
        final int numberOfCoefficients = getNumberOfCoefficients();
        int index = INDEX_UNDEFINED;
        for ( int i = 0; i < numberOfCoefficients; i++ ) {
            double coefficient = getCoefficient( i );
            if ( coefficient != 0 ) {
                index = i;
                break;
            }
        }
        return index;
    }
    
    /**
     * Sets all the coefficient values in a band of eigenstates.
     * 
     * @param bandIndex band index, starts at zero
     * @param bandSize number of coefficients in the band
     * @param value coefficient value
     */
    public void setBandCoefficients( int bandIndex, int bandSize, double value ) {
        setNotifyEnabled( false );
        {
            final int numberOfCoefficients = getNumberOfCoefficients();
            
            // Clear all
            for ( int i = 0; i < numberOfCoefficients; i++ ) {
                setCoefficient( i, 0 );
            }
            
            // Select eigenstates in band
            if ( bandIndex != INDEX_UNDEFINED ) {
                final int firstIndexInBand = bandIndex * bandSize;
                for ( int i = firstIndexInBand; i < ( firstIndexInBand + bandSize ) && i < numberOfCoefficients; i++ ) {
                    setCoefficient( i, value );
                }
            }
        }
        setNotifyEnabled( true );
    }
}
