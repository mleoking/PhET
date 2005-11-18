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
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.Range;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.quantumtunneling.QTConstants;
import edu.colorado.phet.quantumtunneling.model.AbstractPotentialEnergy;
import edu.colorado.phet.quantumtunneling.model.PotentialRegion;
import edu.colorado.phet.quantumtunneling.model.TotalEnergy;



/**
 * QTCombinedChart
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class QTCombinedChart extends JFreeChart implements Observer {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final boolean CREATE_LEGEND = false;
    private static final Font AXIS_LABEL_FONT = new Font( QTConstants.FONT_NAME, Font.PLAIN, 16 );
    private static final double CHART_SPACING = 15.0;

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private AbstractPotentialEnergy _potential;
    private TotalEnergy  _totalEnergy;
    
    private XYSeries _totalEnergySeries;
    private XYSeries _potentialEnergySeries;
    private XYSeries _probabilityDensitySeries;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public QTCombinedChart() {
        super( null, null, new CombinedDomainXYPlot(), CREATE_LEGEND );
        
        setBackgroundPaint( QTConstants.CHART_BACKGROUND );
        
        // Labels (localized)
        String energyLabel = SimStrings.get( "axis.energy" );
        String waveFunctionLabel = SimStrings.get( "axis.waveFunction" );
        String probabilityDensityLabel = SimStrings.get( "axis.probabilityDensity" );
        String positionLabel = SimStrings.get( "axis.position" );
        String totalEnergyLabel = SimStrings.get( "legend.totalEnergy" );
        String potentialEnergyLabel = SimStrings.get( "legend.potentialEnergy" );
        
        // Data series
        _totalEnergySeries = new XYSeries( totalEnergyLabel );
        _potentialEnergySeries = new XYSeries( potentialEnergyLabel );
        _probabilityDensitySeries = new XYSeries( probabilityDensityLabel );
        
        // Energy plot...
        XYPlot energyPlot = null;
        {
            XYSeriesCollection data = new XYSeriesCollection();
            XYItemRenderer renderer = new StandardXYItemRenderer();
            data.addSeries( _potentialEnergySeries );
            data.addSeries( _totalEnergySeries );
            renderer.setSeriesPaint( 0, QTConstants.POTENTIAL_ENERGY_PAINT );
            renderer.setSeriesStroke( 0, QTConstants.POTENTIAL_ENERGY_STROKE );
            renderer.setSeriesPaint( 1, QTConstants.TOTAL_ENERGY_PAINT );
            renderer.setSeriesStroke( 1, QTConstants.TOTAL_ENERGY_STROKE );
            NumberAxis yAxis = new NumberAxis( energyLabel );
            yAxis.setLabelFont( AXIS_LABEL_FONT );
            yAxis.setRange( QTConstants.ENERGY_RANGE );
            energyPlot = new XYPlot( data, null, yAxis, renderer );
            energyPlot.setRangeAxisLocation( AxisLocation.BOTTOM_OR_LEFT );
            energyPlot.setBackgroundPaint( QTConstants.PLOT_BACKGROUND );
        }

        // Wave Function plot...
        XYPlot waveFunctionPlot = null;
        {
            XYSeriesCollection data = new XYSeriesCollection();
            XYItemRenderer renderer = new StandardXYItemRenderer();
            NumberAxis yAxis = new NumberAxis( waveFunctionLabel );
            yAxis.setLabelFont( AXIS_LABEL_FONT );
            yAxis.setRange( QTConstants.WAVE_FUNCTION_RANGE );
            waveFunctionPlot = new XYPlot( data, null, yAxis, renderer );
            waveFunctionPlot.setRangeAxisLocation( AxisLocation.BOTTOM_OR_LEFT );
            waveFunctionPlot.setBackgroundPaint( QTConstants.PLOT_BACKGROUND );
        }

        // Probability Density plot...
        XYPlot probabilityDensityPlot = null;
        {
            XYSeriesCollection data = new XYSeriesCollection();
            XYItemRenderer renderer = new StandardXYItemRenderer();
            renderer.setSeriesPaint( 0, QTConstants.PROBABILITY_DENSITY_PAINT );
            renderer.setSeriesStroke( 0, QTConstants.PROBABILITY_DENSITY_STROKE );
            NumberAxis yAxis = new NumberAxis( probabilityDensityLabel );
            yAxis.setLabelFont( AXIS_LABEL_FONT );
            yAxis.setRange( QTConstants.PROBABILITY_DENSITY_RANGE );
            probabilityDensityPlot = new XYPlot( data, null, yAxis, renderer );
            probabilityDensityPlot.setRangeAxisLocation( AxisLocation.BOTTOM_OR_LEFT );
            probabilityDensityPlot.setBackgroundPaint( QTConstants.PLOT_BACKGROUND );
        }
        
        // Parent plot configuration...
        {
            CombinedDomainXYPlot plot = (CombinedDomainXYPlot) getPlot();
            
            // Common x axis...
            NumberAxis positionAxis = new NumberAxis( positionLabel );
            positionAxis.setLabelFont( AXIS_LABEL_FONT );
            positionAxis.setRange( QTConstants.POSITION_RANGE );
            plot.setDomainAxis( positionAxis );
            
            // Misc properties
            plot.setGap( CHART_SPACING );
            plot.setOrientation( PlotOrientation.VERTICAL );

            // Add the subplots, weights all the same
            final int weight = 1;
            plot.add( energyPlot, weight );
            plot.add( waveFunctionPlot, weight );
            plot.add( probabilityDensityPlot, weight );
        }
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Set the total energy model that is displayd in the Energy chart.
     * 
     * @param totalEnergy
     */
    public void setTotalEnergy( TotalEnergy totalEnergy ) {
        if ( _totalEnergy != null ) {
            _totalEnergy.deleteObserver( this );
        }
        _totalEnergy = totalEnergy;
        _totalEnergy.addObserver( this );
        updateTotalEnergy();
    }
    
    /**
     * Sets the potential energy model that is displayed 
     * in the Energy chart.
     * 
     * @param potential
     */
    public void setPotential( AbstractPotentialEnergy potential ) {
        if ( _potential != null ) {
            _potential.deleteObserver( this );
        }
        _potential = potential;
        _potential.addObserver( this );
        updatePotential();
    }
    
    //----------------------------------------------------------------------------
    // Markers
    //----------------------------------------------------------------------------
    
    /*
     * Adds a region marker at the specified position.
     * A region marker is a vertical line that denotes the 
     * boundary between two regions.
     * 
     * @param x
     */
    private void addRegionMarker( double x ) {

        Marker marker = new ValueMarker( x );
        marker.setPaint( QTConstants.REGION_MARKER_PAINT );
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
        if ( observable == _potential ) {
            updatePotential();
        }
    }
    
    //----------------------------------------------------------------------------
    // Update handlers
    //----------------------------------------------------------------------------
    
    /*
     * Updates the total energy series to match the model.
     */
    private void updateTotalEnergy() {
        CombinedDomainXYPlot combinedPlot = (CombinedDomainXYPlot) getPlot();
        Range range = combinedPlot.getDomainAxis().getRange();
        _totalEnergySeries.clear();
        _totalEnergySeries.add( range.getLowerBound(), _totalEnergy.getEnergy() );
        _totalEnergySeries.add( range.getUpperBound(), _totalEnergy.getEnergy() );
    }
    
    /*
     * Updates the potential energy series to match the model.
     */
    private void updatePotential() {
        _potentialEnergySeries.clear();
        clearRegionMarkers();
        PotentialRegion[] regions = _potential.getRegions();
        for ( int i = 0; i < regions.length; i++ ) {
            double start = regions[i].getStart();
            double end = regions[i].getEnd();
            double energy = regions[i].getEnergy();
            _potentialEnergySeries.add( start, energy );
            _potentialEnergySeries.add( end, energy );
            if ( i > 0 ) {
                addRegionMarker( start );
            }
        }
    }
}
