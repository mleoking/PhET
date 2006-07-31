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

import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.Range;

import edu.colorado.phet.boundstates.color.BSColorScheme;



/**
 * BSCombinedChart is a "combined chart" (in JFreeChart terminology).
 * It combines plots for "Energy" and "Wave Function / Probability Density",
 * and has them share a common x-axis for "Position".
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSCombinedChart extends JFreeChart {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    /* These indicies are determined by the order in which
     * the subplots are added to the CombinedDomainXYPlot.
     */
    public static final int ENERGY_PLOT_INDEX = 0;
    public static final int BOTTOM_PLOT_INDEX = 1;
        
    private static final boolean CREATE_LEGEND = false;
    private static final double CHART_SPACING = 25.0;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private BSEnergyPlot _energyPlot;
    private BSBottomPlot _bottomPlot;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     */
    public BSCombinedChart() {
        super( null, null, new CombinedDomainXYPlot(), CREATE_LEGEND );
        
        // Energy plot...
        { 
            _energyPlot = new BSEnergyPlot();
            _energyPlot.setDomainAxis( null );
        }
        
        // Wave Function plot...
        {
            _bottomPlot = new BSBottomPlot();
            _bottomPlot.setDomainAxis( null );
        }

        // Common X axis...
        BSPositionAxis positionAxis = new BSPositionAxis();
        
        // Parent plot configuration...
        {
            CombinedDomainXYPlot plot = (CombinedDomainXYPlot) getPlot();
            
            // Misc properties
            plot.setDomainAxis( positionAxis );
            plot.setGap( CHART_SPACING );
            plot.setOrientation( PlotOrientation.VERTICAL );

            // Add the subplots, energy plot is twice the size of wave function plot.
            plot.add( _energyPlot, 2 );
            plot.add( _bottomPlot, 1 );
        }
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Gets a reference to the Energy plot.
     *
     * @return reference to the Energy plot
     */
    public BSEnergyPlot getEnergyPlot() {
        return _energyPlot;
    }
    
    /**
     * Gets a reference to the bottom plot.
     *
     * @return reference to the bottom plot
     */
    public BSBottomPlot getBottomPlot() {
        return _bottomPlot;
    }
    
    /**
     * Gets the position range shared by all plots in this combined chart.
     * 
     * @return range of the position axis
     */
    public Range getPositionRange() {
        return ( (CombinedDomainXYPlot) getPlot() ).getDomainAxis().getRange();
    }
    
    /**
     * Sets the color scheme.
     * 
     * @param scheme
     */
    public void setColorScheme( BSColorScheme scheme ) {
        _energyPlot.setColorScheme( scheme );
        _bottomPlot.setColorScheme( scheme );
    }
}
