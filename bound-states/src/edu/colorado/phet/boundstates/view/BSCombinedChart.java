/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.boundstates.view;

import java.awt.Font;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.TickUnits;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.PlotOrientation;

import edu.colorado.phet.boundstates.BSConstants;



/**
 * BSCombinedChart is a "combined chart" (in JFreeChart terminology).
 * It combines plots for "Energy" and "Wave Function",
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
    public static final int WAVE_FUNCTION_PLOT_INDEX = 1;
        
    private static final boolean CREATE_LEGEND = false;
    private static final double CHART_SPACING = 25.0;
    
    private static final Font AXIS_LABEL_FONT = new Font( BSConstants.FONT_NAME, Font.PLAIN, 20 );
    private static final Font AXIS_TICK_LABEL_FONT = new Font( BSConstants.FONT_NAME, Font.PLAIN, 14 );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private BSEnergyPlot _energyPlot;
    private BSWaveFunctionPlot _waveFunctionPlot;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     */
    public BSCombinedChart() {
        super( null, null, new CombinedDomainXYPlot(), CREATE_LEGEND );
        
        setBackgroundPaint( BSConstants.CHART_BACKGROUND );
        
        // Energy plot...
        { 
            _energyPlot = new BSEnergyPlot();
            _energyPlot.setDomainAxis( null );
            _energyPlot.getRangeAxis().setLabelFont( AXIS_LABEL_FONT );
            _energyPlot.getRangeAxis().setTickLabelFont( AXIS_TICK_LABEL_FONT );
            // Y axis tick units
            TickUnits tickUnits = new TickUnits();
            tickUnits.add( new NumberTickUnit( BSConstants.ENERGY_TICK_SPACING, BSConstants.ENERGY_TICK_FORMAT ) );
            _energyPlot.getRangeAxis().setStandardTickUnits( tickUnits );
            _energyPlot.getRangeAxis().setAutoTickUnitSelection( true );
        }
        
        // Wave Function plot...
        {
            _waveFunctionPlot = new BSWaveFunctionPlot();
            _waveFunctionPlot.setDomainAxis( null );
            _waveFunctionPlot.getRangeAxis().setLabelFont( AXIS_LABEL_FONT );
            _waveFunctionPlot.getRangeAxis().setTickLabelFont( AXIS_TICK_LABEL_FONT );
        }

        // Common X axis...
        BSPositionAxis positionAxis = new BSPositionAxis();
        {
            positionAxis.setLabelFont( AXIS_LABEL_FONT );
            positionAxis.setTickLabelFont( AXIS_TICK_LABEL_FONT );
            // Tick units
            TickUnits tickUnits = new TickUnits();
            tickUnits.add( new NumberTickUnit( BSConstants.POSITION_TICK_SPACING, BSConstants.POSITION_TICK_FORMAT ) );
            positionAxis.setStandardTickUnits( tickUnits );
            positionAxis.setAutoTickUnitSelection( true );
        }
        
        // Parent plot configuration...
        {
            CombinedDomainXYPlot plot = (CombinedDomainXYPlot) getPlot();
            
            // Misc properties
            plot.setDomainAxis( positionAxis );
            plot.setGap( CHART_SPACING );
            plot.setOrientation( PlotOrientation.VERTICAL );

            // Add the subplots, weights all the same
            final int weight = 1;
            plot.add( _energyPlot, weight );
            plot.add( _waveFunctionPlot, weight );
        }
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Gets a reference to the Energy plot.
     *
     * @return
     */
    public BSEnergyPlot getEnergyPlot() {
        return _energyPlot;
    }
    
    /**
     * Gets a reference to the Wave Function plot.
     *
     * @return
     */
    public BSWaveFunctionPlot getWaveFunctionPlot() {
        return _waveFunctionPlot;
    }
}
