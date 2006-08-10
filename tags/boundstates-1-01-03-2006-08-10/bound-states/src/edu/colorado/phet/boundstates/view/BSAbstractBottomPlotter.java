/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.boundstates.view;

import java.awt.geom.Point2D;

import org.jfree.data.xy.XYSeries;

import edu.colorado.phet.boundstates.BSConstants;
import edu.colorado.phet.boundstates.model.*;
import edu.colorado.phet.boundstates.model.BSWaveFunctionCache.Item;
import edu.colorado.phet.boundstates.util.Complex;
import edu.colorado.phet.boundstates.util.MutableComplex;


/**
 * BSAbstractBottomPlotter
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
abstract class BSAbstractBottomPlotter implements BSBottomPlot.IPlotter {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private BSBottomPlot _plot;
    private BSWaveFunctionCache _cache;  // Cache of wave function data for each selected eigenstate
    private XYSeries _hiliteSeries;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param plot the plot that we're calculating data for
     */
    public BSAbstractBottomPlotter( BSBottomPlot plot ) {
        _plot = plot; 
        _cache = new BSWaveFunctionCache();
        _hiliteSeries = plot.getHiliteSeries();
    }
    
    //----------------------------------------------------------------------------
    // BSBottomPlot.IPlotter default implementation
    //----------------------------------------------------------------------------
    
    /**
     * Notifies the plotter that the model has changed.
     * The default implemention updates the cache and refreshes all series.
     */
    public void notifyModelChanged() {
        updateCache();
        refreshAllSeries();
    }
    
    /**
     * Notifies the plotter that the index of the hilited eigenstate has changed.
     * The default implementation updates the data series for the hilited eigenstate.
     */
    public void notifyHiliteChanged() {
        updateHiliteSeries();
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /*
     * Gets a reference to the wave function cache.
     * For use by subclasses.
     */
    protected BSWaveFunctionCache getCache() {
        return _cache;
    }
    
    //----------------------------------------------------------------------------
    // Updaters
    //----------------------------------------------------------------------------
    
    /*
     * Updates the cache of wave function data.
     */
    private void updateCache() {
        final double minPosition = _plot.getDomainAxis().getLowerBound();
        final double maxPosition = _plot.getDomainAxis().getUpperBound();
        _cache.update( _plot.getModel(), minPosition, maxPosition );
    }
    
    /*
     * Updates the series for the hilited eigenstate,
     * which is a time-independent wave function solution.
     * <p>
     * How we display the hilited eigenstate's wave function is dependent
     * on what other views are visible.  If probability density is visible,
     * we display the hilite as probability density. If either real or 
     * imaginary part is visible, we display the real hililite.  
     * If magnitude or phase is the only thing visible, we display
     * magnitude.  If none of the other views is visible, we display
     * nothing.
     */
    protected void updateHiliteSeries() {
        
        _hiliteSeries.setNotify( false );
        _hiliteSeries.clear();
        
        BSModel model = _plot.getModel();
        if ( model != null ) {
            final int hiliteIndex = model.getHilitedEigenstateIndex();
            if ( hiliteIndex != BSEigenstate.INDEX_UNDEFINED ) {
                
                /* 
                 * NOTE - 
                 * The cache only contains wave functions for eigenstates with non-zero
                 * superposition coefficients. It's possible for the user to hilite any of the
                 * eigenstates, so we compute their wave functions as they're hilited.  A possible
                 * optimization would be to look for the hilited eigenstate's wave function in
                 * the cache, and compute it (and cache it) if we don't find it in the cache.
                 * In practice, there is no perceptible performance difference.
                 */
                BSEigenstate[] eigenstates = model.getEigenstates();
                BSAbstractPotential potential = model.getPotential();
                final double minX = _plot.getDomainAxis().getLowerBound();
                final double maxX = _plot.getDomainAxis().getUpperBound();
                Point2D[] points = potential.getWaveFunctionPoints( eigenstates[hiliteIndex], minX, maxX );
                
                for ( int i = 0; i < points.length; i++ ) {
                    if ( _plot.isProbabilityDensitySeriesVisible() ) {
                        double x = points[i].getX();
                        double y = Math.pow( points[i].getY(), 2 );
                        _hiliteSeries.add( x, y );
                    }
                    else if ( _plot.isRealSeriesVisible() || _plot.isImaginarySeriesVisible() ) {
                        double x = points[i].getX();
                        double y = points[i].getY();
                        _hiliteSeries.add( x, y );
                    }
                    else if ( _plot.isMagnitudeSeriesVisible() || _plot.isPhaseSeriesVisible() ) {
                        double x = points[i].getX();
                        double y = Math.abs( points[i].getY() );
                        _hiliteSeries.add( x, y );
                    }
                }
            }
        }
        
        _hiliteSeries.setNotify( true );
    }
}
