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

import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * BSWaveFunctionCache is a cache of wave function solutions for all 
 * eigenstates that have non-zero superposition coefficients.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSWaveFunctionCache {

    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------
    
    /**
     * Item is an immutable item in the cache.
     */
    public static class Item {
        
        private int _eigenstateIndex;
        private double _normalizationCoefficient;
        private Point2D[] _points;
        
        public Item( 
                final int eigenstateIndex,
                final double normalizationCoefficient,
                Point2D[] points )
        {
            _eigenstateIndex = eigenstateIndex;
            _normalizationCoefficient = normalizationCoefficient;
            _points = points;
        }
        
        /**
         * Gets the index of the eigenstate that this item corresponds to.
         * @return the eigenstate index, 0...n-1
         */
        public int getEigenstateIndex() {
            return _eigenstateIndex;
        }
        
        /**
         * Gets the coefficient used to normalize the time-independent
         * wave function when we're in a superposition state.
         * 
         * @return normalization coefficient
         */
        public double getNormalizationCoefficient() {
            return _normalizationCoefficient;
        }
        
        /**
         * Gets the points that approximate the time-independent wave function
         * for the eigenstate. 
         * 
         * @return points
         */
        public Point2D[] getPoints() {
            return _points;
        }
    }
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private ArrayList _items; // contents of the cache, array of CacheItem
    private double _minPosition, _maxPosition; // position range used to compute the cache
    private double _sumScalingCoefficient; // coefficient used to scale the summed wave functions

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructs an empty cache.
     */
    public BSWaveFunctionCache() {
        _items = new ArrayList();
        _minPosition = _maxPosition = 0;
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Gets lower bound of the position range that was used to compute the cache contents.
     * 
     * @return double
     */
    public double getMinPosition() {
        return _minPosition;
    }
    
    /**
     * Gets upper bound of the position range that was used to compute the cache contents.
     * 
     * @return double
     */
    public double getMaxPosition() {
        return _maxPosition;
    }
    
    /**
     * Gets the coefficient used to scale the summed wave functions.
     * @return double
     */
    public double getSumScalingCoefficient() {
        return _sumScalingCoefficient;
    }
    
    /**
     * Gets the number of items in the cache.
     * 
     * @return int
     */
    public int getSize() {
        return _items.size();
    }
    
    /**
     * Gets an item from the cache.
     * 
     * @param index
     * @return the cache item at index
     */
    public Item getItem( int index ) {
        return (Item)_items.get( index );
    }
    
    /**
     * Gets the number of points in each wave function solution.
     * This will be the same for each item in the cache, so just
     * look at the first one.
     */
    public int getNumberOfPointsInEachWaveFunction() {
        int numberOfPoints = 0;
        if ( _items.size() > 0 ) {
            numberOfPoints = getItem( 0 ).getPoints().length;
        }
        return numberOfPoints;
    }
    
    //----------------------------------------------------------------------------
    // Cache updater
    //----------------------------------------------------------------------------
    
    /**
     * Updates the cache based on the current state of the model.
     * An item is added for each eigenstate that has a non-zero superposition coefficient.
     * Wave function approximations are computed for the specified position range.
     * The previous contents of the cache are flushed.
     * 
     * @param model
     * @param minPosition
     * @param maxPosition
     */
    public void update( BSModel model, final double minPosition, final double maxPosition ) {
        
//        System.out.println( "BSWaveFunctionCache.update minPosition=" + minPosition + " maxPosition=" + maxPosition );//XXX
        
        // Flush the cache.
        _items.clear();
        
        // Remember the position range used to compute the cache contents.
        _minPosition = minPosition;
        _maxPosition = maxPosition;
        
        // Get references from the model.
        BSAbstractPotential potential = model.getPotential();
        BSEigenstate[] eigenstates = model.getEigenstates();
        BSSuperpositionCoefficients superpositionCoefficients = model.getSuperpositionCoefficients();
        final boolean isSuperpositionState = superpositionCoefficients.isSuperpositionState();
        
        // Iterate over the superposition coefficients...
        final int numberOfCoefficients = superpositionCoefficients.getNumberOfCoefficients();
        double[] sumY = null;
        for ( int i = 0; i < numberOfCoefficients; i++ ) {
            
            // Add an item to the cache for each non-zero superposition coefficient
            final double superpositionCoefficient = superpositionCoefficients.getCoefficient( i );
            if ( superpositionCoefficient != 0 ) {
                
                // Compute the points that approximate the time-independent wave function
                Point2D[] points = potential.getWaveFunctionPoints( eigenstates[i], minPosition, maxPosition );
                
                // Compute the coefficient used to normalize the time-independent wave function.
                double normalizationCoefficient = 1;
                if ( isSuperpositionState ) {
                    normalizationCoefficient = potential.getNormalizationCoefficient( points, i );
                }
                
                // Add an item to the cache
                Item item = new Item( i, normalizationCoefficient, points );
                _items.add( item );
                
                // Add this wave function's amplitudes to the sum 
                // (normalized and multiplied by its superposition coefficient)
                if ( isSuperpositionState ) {
                    if ( sumY == null ) {
                        sumY = new double[points.length]; // each wave function has the same number of points
                    }
                    for ( int j = 0; j < points.length; j++ ) {
                        sumY[j] += ( normalizationCoefficient * superpositionCoefficient * points[j].getY() );
                    }
                }
            }
        }
        
        // Calculate the scaling coefficient for the sum of the wave functions.
        if ( !isSuperpositionState ) {
            _sumScalingCoefficient = 1;
        }
        else {
            double maxY = 0;
            for ( int i = 0; i < sumY.length; i++ ) {
                double absY = Math.abs( sumY[i] );
                if ( absY > maxY ) {
                    maxY = absY;
                }
            }
            _sumScalingCoefficient = 1 / maxY;
        }
    }
}
