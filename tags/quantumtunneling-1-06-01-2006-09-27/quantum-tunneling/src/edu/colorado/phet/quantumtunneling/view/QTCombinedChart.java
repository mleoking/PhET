/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling.view;

import java.awt.Font;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.TickUnits;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;

import edu.colorado.phet.quantumtunneling.QTConstants;
import edu.colorado.phet.quantumtunneling.color.QTColorScheme;
import edu.colorado.phet.quantumtunneling.model.AbstractPotential;



/**
 * QTCombinedChart is a "combined chart" (in JFreeChart terminology).
 * It combines plots for "Energy", "Wave Function" and "Probability Density",
 * and has them share a common x-axis for "Position".
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class QTCombinedChart extends JFreeChart {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    /* These indicies are determined by the order in which
     * the subplots are added to the CombinedDomainXYPlot.
     */
    public static final int ENERGY_PLOT_INDEX = 0;
    public static final int WAVE_FUNCTION_PLOT_INDEX = 1;
    public static final int PROBABILITY_DENSITY_PLOT_INDEX = 2;
        
    private static final boolean CREATE_LEGEND = false;
    private static final double CHART_SPACING = 25.0;
    
    private static final Font AXIS_LABEL_FONT = new Font( QTConstants.FONT_NAME, Font.PLAIN, 20 );
    private static final Font AXIS_TICK_LABEL_FONT = new Font( QTConstants.FONT_NAME, Font.PLAIN, 14 );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private EnergyPlot _energyPlot;
    private WaveFunctionPlot _waveFunctionPlot;
    private ProbabilityDensityPlot _probabilityDensityPlot;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     */
    public QTCombinedChart() {
        super( null, null, new CombinedDomainXYPlot(), CREATE_LEGEND );
        
        // Energy plot...
        { 
            _energyPlot = new EnergyPlot();
            _energyPlot.setDomainAxis( null );
            _energyPlot.getRangeAxis().setLabelFont( AXIS_LABEL_FONT );
            _energyPlot.getRangeAxis().setTickLabelFont( AXIS_TICK_LABEL_FONT );
            // Y axis tick units
            TickUnits tickUnits = new TickUnits();
            tickUnits.add( new NumberTickUnit( QTConstants.ENERGY_TICK_SPACING, QTConstants.ENERGY_TICK_FORMAT ) );
            _energyPlot.getRangeAxis().setStandardTickUnits( tickUnits );
            _energyPlot.getRangeAxis().setAutoTickUnitSelection( true );
        }
        
        // Wave Function plot...
        {
            _waveFunctionPlot = new WaveFunctionPlot();
            _waveFunctionPlot.setDomainAxis( null );
            _waveFunctionPlot.getRangeAxis().setLabelFont( AXIS_LABEL_FONT );
            _waveFunctionPlot.getRangeAxis().setTickLabelFont( AXIS_TICK_LABEL_FONT );
        }

        // Probability Density plot...
        {
            // Get the series from the WaveFunctionPlot...
            XYSeries probabilityDensitySeries = _waveFunctionPlot.getProbabilityDensitySeries();
            //...and display it using the ProbabilityDensityPlot
            _probabilityDensityPlot = new ProbabilityDensityPlot( probabilityDensitySeries );
            _probabilityDensityPlot.setDomainAxis( null );
            _probabilityDensityPlot.getRangeAxis().setLabelFont( AXIS_LABEL_FONT );
            _probabilityDensityPlot.getRangeAxis().setTickLabelFont( AXIS_TICK_LABEL_FONT );
        }

        // Common X axis...
        PositionAxis positionAxis = new PositionAxis();
        {
            positionAxis.setLabelFont( AXIS_LABEL_FONT );
            positionAxis.setTickLabelFont( AXIS_TICK_LABEL_FONT );
            // Tick units
            TickUnits tickUnits = new TickUnits();
            tickUnits.add( new NumberTickUnit( QTConstants.POSITION_TICK_SPACING, QTConstants.POSITION_TICK_FORMAT ) );
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
            plot.add( _probabilityDensityPlot, weight );
        }
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Sets the color scheme.
     * 
     * @param scheme
     */
    public void setColorScheme( QTColorScheme scheme ) {
        _energyPlot.setColorScheme( scheme );
        _waveFunctionPlot.setColorScheme( scheme );
        _probabilityDensityPlot.setColorScheme( scheme );
    }
    
    /**
     * Gets a reference to the Energy plot.
     *
     * @return
     */
    public EnergyPlot getEnergyPlot() {
        return _energyPlot;
    }
    
    /**
     * Gets a reference to the Wave Function plot.
     *
     * @return
     */
    public WaveFunctionPlot getWaveFunctionPlot() {
        return _waveFunctionPlot;
    }
    
    /**
     * Gets a reference to the Probability Density plot.
     *
     * @return
     */
    public ProbabilityDensityPlot getProbabilityDensityPlot() {
        return _probabilityDensityPlot;
    }
    
    /**
     * Gets the chart's CombinedDomainXYPlot.
     * 
     * @return
     */
    public CombinedDomainXYPlot getCombinedDomainXYPlot() {
        return (CombinedDomainXYPlot) getPlot();
    }
}
