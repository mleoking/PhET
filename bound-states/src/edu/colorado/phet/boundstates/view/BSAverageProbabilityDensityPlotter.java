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

import edu.colorado.phet.boundstates.model.BSModel;
import edu.colorado.phet.boundstates.model.BSWaveFunctionCache;
import edu.colorado.phet.boundstates.util.Complex;
import edu.colorado.phet.boundstates.util.MutableComplex;

/**
 * BSAverageProbabilityDensityPlotter calculates the data series for the 
 * "Average Probability Density" mode of the bottom chart.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
class BSAverageProbabilityDensityPlotter extends BSAbstractBottomPlotter {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private XYSeries _probabilityDensitySeries;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param plot the plot that we're calculating data for
     */
    public BSAverageProbabilityDensityPlotter( BSBottomPlot plot ) {
        super( plot );
        _probabilityDensitySeries = plot.getProbabilityDensitySeries();
    }
    
    //----------------------------------------------------------------------------
    // BSBottomPlot.IPlotter implementation
    //----------------------------------------------------------------------------

    /**
     * Notifies the plotter that the clock time has changed.
     * This plotter does nothing -- while average probability density 
     * is technically time-dependent, there is no visible change over time.
     * 
     * @param t clock time
     */
    public void notifyTimeChanged( double t ) {}

    /**
     * Refreshes all data series.
     */
    public void refreshAllSeries() {
        updateProbabilityDensitySeries();
        updateHiliteSeries();
    }
    
    //----------------------------------------------------------------------------
    // Updaters
    //----------------------------------------------------------------------------
    
    /*
     * Updates the probability density series to display 
     * the average probability density of all selected eigenstates
     * (those eigenstates with non-zero superposition coefficients).
     * <p>
     * If eigenstates E1, E2, E3 are selected, then average probability density
     * is computed as:
     * <code>
     * ( |Psi1(x,t)|^2 + |Psi2(x,t)|^2 + |Psi3(x,t)|^2 ) / 3
     * </code>
     */
    private void updateProbabilityDensitySeries() {
        
        BSWaveFunctionCache cache = getCache();
        
        _probabilityDensitySeries.setNotify( false );
        {
            _probabilityDensitySeries.clear();
            
            final double cacheSize = cache.getSize();
            if ( cacheSize > 0 ) {
                
                final double minPosition = cache.getMinPosition();
                final double maxPosition = cache.getMaxPosition();
                
                Point2D[] positions = cache.getItem( 0 ).getPoints(); // all wave functions should share the same x values
                
                for ( int i = 0; i < positions.length; i++ ) {
                    
                    final double position = positions[i].getX();
                    if ( position >= minPosition && position <= maxPosition ) {

                        double sum = 0;
                        for ( int j = 0; j < cacheSize; j++ ) {
                            Point2D[] waveFunctions = cache.getItem( j ).getPoints();
                            final double absY = Math.abs( waveFunctions[i].getY() );
                            sum += ( absY * absY );
                        }

                        final double averageProbabilityDensity = sum / cacheSize;

                        _probabilityDensitySeries.add( position, averageProbabilityDensity );
                    }
                }
            }
        }
        _probabilityDensitySeries.setNotify( true );
    }
}
