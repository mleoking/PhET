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
import java.util.Observable;
import java.util.Observer;

import org.jfree.chart.axis.*;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleInsets;

import edu.colorado.phet.boundstates.BSConstants;
import edu.colorado.phet.boundstates.color.BSColorScheme;
import edu.colorado.phet.boundstates.enums.BSBottomPlotMode;
import edu.colorado.phet.boundstates.model.*;
import edu.colorado.phet.boundstates.model.BSWaveFunctionCache.Item;
import edu.colorado.phet.boundstates.util.Complex;
import edu.colorado.phet.boundstates.util.MutableComplex;
import edu.colorado.phet.common.model.clock.ClockEvent;
import edu.colorado.phet.common.model.clock.ClockListener;
import edu.colorado.phet.common.view.util.SimStrings;


/**
 * 
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
class BSWaveFunctionPlotter {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private BSBottomPlot _plot;
    
    private XYSeries _realSeries;
    private XYSeries _imaginarySeries;
    private XYSeries _magnitudeSeries;
    private XYSeries _phaseSeries;
    private XYSeries _probabilityDensitySeries;
    private XYSeries _hiliteSeries;
    
    // Cache of wave function data
    private BSWaveFunctionCache _cache;
    
    // Memory optimizations
    private MutableComplex[] _psiSum; // reused by computeTimeDependentWaveFunction
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public BSWaveFunctionPlotter( BSBottomPlot plot ) {
        _plot = plot; 
        _cache = new BSWaveFunctionCache();
        
        // Series objects don't change, so get references to them now.
        _realSeries = plot.getRealSeries();
        _imaginarySeries = plot.getImaginarySeries();
        _magnitudeSeries = plot.getMagnitudeSeries();
        _phaseSeries = plot.getPhaseSeries();
        _probabilityDensitySeries = plot.getProbabilityDensitySeries();
        _hiliteSeries = plot.getHiliteSeries();
    }
    
    //----------------------------------------------------------------------------
    // Updaters
    //----------------------------------------------------------------------------
    
//    /**
//     * Updates all data series.
//     * 
//     * @param t the current clock time
//     */
//    public void updateAllSeries( double t ) {
//        updateTimeDependentSeries( t );
//        updateHiliteSeries();
//    }
    
    /**
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
    public void updateHiliteSeries() {
        
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
    
    /**
     * Updates the cache of wave function data.
     */
    public void updateCache() {
        final double minPosition = _plot.getDomainAxis().getLowerBound();
        final double maxPosition = _plot.getDomainAxis().getUpperBound();
        _cache.update( _plot.getModel(), minPosition, maxPosition );
    }
    
    /**
     * Updates the series that display the time-dependent superposition state.
     * This is the sum of all wave functions for eigenstates.
     */
    public void updateTimeDependentSeries( final double t ) {
        setTimeDependentSeriesNotify( false );
        clearTimeDependentSeries();
        if ( _cache.getSize() > 0 ) {
            Complex[] psiSum = computeTimeDependentWaveFunction( t );
            if ( psiSum != null ) {

                final double minX = _cache.getMinPosition();
                final double maxX = _cache.getMaxPosition();
                Point2D[] points = _cache.getItem( 0 ).getPoints(); // all wave functions should share the same x values
                assert ( psiSum.length == points.length );

                for ( int i = 0; i < psiSum.length; i++ ) {
                    final double x = points[i].getX();
                    if ( x >= minX && x <= maxX ) {
                        Complex y = psiSum[i];
                        if ( _plot.isRealSeriesVisible() ) {
                            _realSeries.add( x, y.getReal() );
                        }
                        if ( _plot.isImaginarySeriesVisible() ) {
                            _imaginarySeries.add( x, y.getImaginary() );
                        }
                        if ( _plot.isMagnitudeSeriesVisible() ) {
                            _magnitudeSeries.add( x, y.getAbs() );
                        }
                        if ( _plot.isPhaseSeriesVisible() ) {
                            _phaseSeries.add( x, y.getAbs() );
                            _phaseSeries.add( x, y.getPhase() );
                        }
                        if ( _plot.isProbabilityDensitySeriesVisible() ) {
                            _probabilityDensitySeries.add( x, y.getAbs() * y.getAbs() );
                        }
                    }
                }
            }
            setTimeDependentSeriesNotify( true );
        }
    }
    
    /*
     * Converts each eigenstate's time-independent wave function to a time-dependent
     * wave function, and sums all of the time-dependent wave functions according 
     * to the superposition coefficients.
     * 
     * @param t
     */
    private Complex[] computeTimeDependentWaveFunction( final double t ) {
   
        if ( _cache.getSize() > 0 ) {
            
            BSModel model = _plot.getModel();
            BSEigenstate[] eigenstates = model.getEigenstates();
            BSSuperpositionCoefficients coefficients = model.getSuperpositionCoefficients();
            assert( eigenstates.length == coefficients.getNumberOfCoefficients() );
            
            final int numberOfPoints = _cache.getNumberOfPointsInEachWaveFunction();
            
            // Reuse previous psiSum array if numberOfPoints hasn't changed
            if ( _psiSum == null || numberOfPoints != _psiSum.length ) {
                _psiSum = new MutableComplex[numberOfPoints];
                for ( int i = 0; i < _psiSum.length; i++ ) {
                    _psiSum[i] = new MutableComplex();
                }
            }
            else {
                for ( int i = 0; i < _psiSum.length; i++ ) {
                    _psiSum[i].setValue( 0 );
                }
            }
            
            MutableComplex y = new MutableComplex();
            MutableComplex Ft = new MutableComplex();
            
            // Iterate over the cache...
            final int cacheSize = _cache.getSize();
            for ( int i = 0; i < cacheSize; i++ ) {
                
                Item item = _cache.getItem( i );

                // Eigenstate index
                final int eigenstateIndex = item.getEigenstateIndex();

                // Data points
                Point2D[] points = item.getPoints(); // Ps(x)
                
                // F(t) = e^(-i * E * t / hbar), where E = eigenstate energy 
                final double E = eigenstates[eigenstateIndex].getEnergy();
                Ft.setValue( Complex.I );
                Ft.multiply( -1 * E * t / BSConstants.HBAR );
                Ft.exp();

                // Superposition coefficient 
                final double C = coefficients.getCoefficient( eigenstateIndex );
                
                // Normalization coefficient
                final double A = item.getNormalizationCoefficient();
                
                for ( int j = 0; j < points.length; j++ ) {
                    // Psi(x,t) = Psi(x) * F(t) * C * A
                    y.setValue( points[j].getY() );
                    y.multiply( Ft );
                    if ( C != 1 ) {
                        y.multiply( C );
                    }
                    if ( A != 1 ) {
                        y.multiply( A );
                    }
                    _psiSum[j].add( y );
                }
            }
            
            // Scale the wave function sum
            final double S = _cache.getSumScalingCoefficient();
            if ( S != 1 ) {
                for ( int j = 0; j < _psiSum.length; j++ ) {
                    _psiSum[j].multiply( S );
                }
            }
        }
        return _psiSum;
    }
    
    /*
     * Clears the data from all time-dependent series.
     */
    private void clearTimeDependentSeries() {
        _realSeries.clear();
        _imaginarySeries.clear();
        _magnitudeSeries.clear();
        _phaseSeries.clear();
        _probabilityDensitySeries.clear();
    }
    
    /*
     * Changes notification for all time-dependent series.
     * <p>
     * Call this method with false before adding a lot of points, so that
     * we don't get unnecessary updates.  When all points have been added,
     * call this method with true to notify listeners that the series 
     * have changed.
     * 
     * @param notify true or false
     */
    private void setTimeDependentSeriesNotify( boolean notify ) {
        _realSeries.setNotify( notify );
        _imaginarySeries.setNotify( notify );
        _magnitudeSeries.setNotify( notify );
        _phaseSeries.setNotify( notify );
        _probabilityDensitySeries.setNotify( notify );
    }
}
