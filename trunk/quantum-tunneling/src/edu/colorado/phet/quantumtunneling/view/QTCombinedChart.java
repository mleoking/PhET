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
    
    /* These indicies are determined by the order in which
     * the subplots are added to the CombinedDomainXYPlot.
     */
    public static final int ENERGY_PLOT_INDEX = 0;
    public static final int WAVE_FUNCTION_PLOT_INDEX = 1;
    public static final int PROBABILIT_DENSITY_PLOT_INDEX = 2;
        
    private static final boolean CREATE_LEGEND = false;
    private static final Font AXIS_LABEL_FONT = new Font( QTConstants.FONT_NAME, Font.PLAIN, 16 );
    private static final double CHART_SPACING = 15.0;

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private AbstractPotentialEnergy _potentialEnergy;
    
    private EnergyPlot _energyPlot;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public QTCombinedChart() {
        super( null, null, new CombinedDomainXYPlot(), CREATE_LEGEND );
        
        setBackgroundPaint( QTConstants.CHART_BACKGROUND );
        
        // Labels (localized)
        String waveFunctionLabel = SimStrings.get( "axis.waveFunction" );
        String probabilityDensityLabel = SimStrings.get( "axis.probabilityDensity" );
        String positionLabel = SimStrings.get( "axis.position" ) + " (" + SimStrings.get( "units.position" ) + ")";
        
        // Energy plot...
        _energyPlot = new EnergyPlot();

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
            renderer.setSeriesPaint( 0, QTConstants.PROBABILITY_DENSITY_COLOR );
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
            plot.add( _energyPlot, weight );
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
        // Delegate to the energy plot
        _energyPlot.setTotalEnergy( totalEnergy );
    }
    
    /**
     * Sets the potential energy model that is displayed in the Energy chart.
     * 
     * @param potentialEnergy
     */
    public void setPotentialEnergy( AbstractPotentialEnergy potentialEnergy ) {
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
        PotentialRegion[] regions = _potentialEnergy.getRegions();
        for ( int i = 1; i < regions.length; i++ ) {
            double start = regions[i].getStart();
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
