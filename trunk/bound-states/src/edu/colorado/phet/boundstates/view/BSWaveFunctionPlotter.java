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
import edu.colorado.phet.boundstates.model.BSEigenstate;
import edu.colorado.phet.boundstates.model.BSModel;
import edu.colorado.phet.boundstates.model.BSSuperpositionCoefficients;
import edu.colorado.phet.boundstates.model.BSWaveFunctionCache;
import edu.colorado.phet.boundstates.model.BSWaveFunctionCache.Item;
import edu.colorado.phet.boundstates.util.Complex;
import edu.colorado.phet.boundstates.util.MutableComplex;


/**
 * BSWaveFunctionPlotter calculates the data series for the 
 * "Wave Function" and "Probability Density" modes of the bottom chart.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
class BSWaveFunctionPlotter extends BSAbstractBottomPlotter {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private BSBottomPlot _plot;
    
    private XYSeries _realSeries;
    private XYSeries _imaginarySeries;
    private XYSeries _magnitudeSeries;
    private XYSeries _phaseSeries;
    private XYSeries _probabilityDensitySeries;
    
    // Memory optimizations
    private MutableComplex[] _psiSum; // reused by computeTimeDependentWaveFunction
    
    private double _t; // time of the most recent clock tick
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param plot the plot that we're calculating data for
     */
    public BSWaveFunctionPlotter( BSBottomPlot plot ) {
        super( plot );
        
        _plot = plot;
        
        // Series objects don't change, so get references to them now.
        _realSeries = plot.getRealSeries();
        _imaginarySeries = plot.getImaginarySeries();
        _magnitudeSeries = plot.getMagnitudeSeries();
        _phaseSeries = plot.getPhaseSeries();
        _probabilityDensitySeries = plot.getProbabilityDensitySeries();
    }
    
    //----------------------------------------------------------------------------
    // BSBottomPlot.IPlotter implementation
    //----------------------------------------------------------------------------
    
    /**
     * Notifies the plotter that the clock time has changed.
     * This plotter updates all time-dependent data series.
     * 
     * @param t clock time
     */
    public void notifyTimeChanged( final double t ) {
        _t = t;
        updateTimeDependentSeries( _t );
    }
    
    /**
     * Refreshes all data series.
     */
    public void refreshAllSeries() {
        updateTimeDependentSeries( _t );
        updateHiliteSeries();
    }
    
    //----------------------------------------------------------------------------
    // Updaters
    //----------------------------------------------------------------------------
    
    /*
     * Updates the series that display the time-dependent superposition state.
     * This is the sum of all wave functions for eigenstates.
     * 
     * @param t clock time
     */
    private void updateTimeDependentSeries( final double t ) {
        BSWaveFunctionCache cache = getCache();
        setTimeDependentSeriesNotify( false );
        clearTimeDependentSeries();
        if ( cache.getSize() > 0 ) {
            Complex[] psiSum = computeTimeDependentWaveFunction( t );
            if ( psiSum != null ) {

                final double minX = cache.getMinPosition();
                final double maxX = cache.getMaxPosition();
                Point2D[] points = cache.getItem( 0 ).getPoints(); // all wave functions should share the same x values
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
        BSWaveFunctionCache cache = getCache();
        if ( cache.getSize() > 0 ) {
            
            BSModel model = _plot.getModel();
            BSEigenstate[] eigenstates = model.getEigenstates();
            BSSuperpositionCoefficients coefficients = model.getSuperpositionCoefficients();
            assert( eigenstates.length == coefficients.getNumberOfCoefficients() );
            
            final int numberOfPoints = cache.getNumberOfPointsInEachWaveFunction();
            
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
            final int cacheSize = cache.getSize();
            for ( int i = 0; i < cacheSize; i++ ) {
                
                Item item = cache.getItem( i );

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
            final double S = cache.getSumScalingCoefficient();
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
