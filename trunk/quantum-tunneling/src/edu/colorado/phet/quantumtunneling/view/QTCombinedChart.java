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
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.TickUnits;
import org.jfree.chart.plot.*;
import org.jfree.data.xy.XYSeries;

import edu.colorado.phet.quantumtunneling.QTConstants;
import edu.colorado.phet.quantumtunneling.model.AbstractPotential;
import edu.colorado.phet.quantumtunneling.model.TotalEnergy;



/**
 * QTCombinedChart is a "combined chart" (in JFreeChart terminology).
 * It combines plots for "Energy", "Wave Function" and "Probability Density",
 * and has them share a common x-axis for "Position".  This combined 
 * chart also manages vertical region markers which indicate the
 * boundaries between potential energy regions.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class QTCombinedChart extends JFreeChart implements Observer {

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
    
    private AbstractPotential _potentialEnergy;
    
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
        
        setBackgroundPaint( QTConstants.CHART_BACKGROUND );
        
        // Energy plot...
        {
            _energyPlot = new EnergyPlot();
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
            _waveFunctionPlot.getRangeAxis().setLabelFont( AXIS_LABEL_FONT );
            _waveFunctionPlot.getRangeAxis().setTickLabelFont( AXIS_TICK_LABEL_FONT );
            // Y axis tick units
            TickUnits tickUnits = new TickUnits();
            tickUnits.add( new NumberTickUnit( QTConstants.WAVE_FUNCTION_TICK_SPACING, QTConstants.WAVE_FUNCTION_TICK_FORMAT ) );
            _waveFunctionPlot.getRangeAxis().setStandardTickUnits( tickUnits );
            _waveFunctionPlot.getRangeAxis().setAutoTickUnitSelection( true );
        }

        // Probability Density plot...
        {
            XYSeries probabilityDensitySeries = _waveFunctionPlot.getProbabilityDensitySeries();
            _probabilityDensityPlot = new ProbabilityDensityPlot( probabilityDensitySeries );
            _probabilityDensityPlot.getRangeAxis().setLabelFont( AXIS_LABEL_FONT );
            _probabilityDensityPlot.getRangeAxis().setTickLabelFont( AXIS_TICK_LABEL_FONT );
            // Y axis tick units
            TickUnits tickUnits = new TickUnits();
            tickUnits.add( new NumberTickUnit( QTConstants.PROBABILITY_DENSITY_TICK_SPACING, QTConstants.PROBABILITY_DENSITY_TICK_FORMAT ) );
            _probabilityDensityPlot.getRangeAxis().setStandardTickUnits( tickUnits );
            _probabilityDensityPlot.getRangeAxis().setAutoTickUnitSelection( true );
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
     * Set the total energy model that is displayd in the Energy chart.
     * 
     * @param totalEnergy
     */
    public void setTotalEnergy( TotalEnergy totalEnergy ) {
        // Delegate to the energy plot
        _energyPlot.setTotalEnergy( totalEnergy );
    }
    
    /**
     * Sets the potential energy model that is displayed in the Energy chart.
     * 
     * @param potentialEnergy
     */
    public void setPotentialEnergy( AbstractPotential potentialEnergy ) {
        // Delegate to the energy plot...
        _energyPlot.setPotentialEnergy( potentialEnergy );
        
        // ...and observe so that we can update region markers.
        if ( _potentialEnergy != null ) {
            _potentialEnergy.deleteObserver( this );
        }
        _potentialEnergy = potentialEnergy;
        _potentialEnergy.addObserver( this );
        updateRegionMarkers();
    }
    
    //----------------------------------------------------------------------------
    // Markers
    //----------------------------------------------------------------------------
    
    /*
     * Updates the region markers to match the the model.
     */
    private void updateRegionMarkers() {
        clearRegionMarkers();
        int numberOfRegions = _potentialEnergy.getNumberOfRegions();
        for ( int i = 1; i < numberOfRegions; i++ ) {
            double start = _potentialEnergy.getStart( i );
            addRegionMarker( start );
        }
    }
    
    /*
     * Adds a region marker at the specified position.
     * A region marker is a vertical line that denotes the 
     * boundary between two regions.
     * 
     * @param x
     */
    private void addRegionMarker( double x ) {

        Marker marker = new ValueMarker( x );
        marker.setPaint( QTConstants.REGION_MARKER_COLOR );
        marker.setStroke( QTConstants.REGION_MARKER_STROKE );
        
        CombinedDomainXYPlot combinedPlot = (CombinedDomainXYPlot) getPlot();
        List subPlots = combinedPlot.getSubplots();
        for ( int i = 0; i < subPlots.size(); i ++ ) {
            XYPlot plot = (XYPlot) subPlots.get( i );
            plot.addDomainMarker( marker );
        }
    }
    
    /*
     * Clears all region markers.
     */
    private void clearRegionMarkers() {
        CombinedDomainXYPlot combinedPlot = (CombinedDomainXYPlot) getPlot();
        List subPlots = combinedPlot.getSubplots();
        for ( int i = 0; i < subPlots.size(); i ++ ) {
            XYPlot plot = (XYPlot) subPlots.get( i );
            plot.clearDomainMarkers();
        }
    }

    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------
    
    /**
     * Updates the view to match the model.
     * 
     * @param observable
     * @param arg
     */
    public void update( Observable observable, Object arg ) {
        if ( observable == _potentialEnergy ) {
            updateRegionMarkers();
        }
    }
}
