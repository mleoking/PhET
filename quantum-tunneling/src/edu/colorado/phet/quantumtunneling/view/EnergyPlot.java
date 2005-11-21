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
import java.util.Observable;
import java.util.Observer;

import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
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
 * EnergyPlot
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class EnergyPlot extends XYPlot implements Observer {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private AbstractPotentialEnergy _potentialEnergy;
    private TotalEnergy  _totalEnergy;
    
    private XYSeries _totalEnergySeries;
    private XYSeries _potentialEnergySeries;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public EnergyPlot() {
        super();
        
        // Labels (localized)
        String energyLabel = SimStrings.get( "axis.energy" ) + " (" + SimStrings.get( "units.energy" ) + ")";
        String positionLabel = SimStrings.get( "axis.position" ) + " (" + SimStrings.get( "units.position" ) + ")";
        String potentialEnergyLabel = SimStrings.get( "legend.potentialEnergy" );
        String totalEnergyLabel = SimStrings.get( "legend.totalEnergy" );
        
        // Data series
        _totalEnergySeries = new XYSeries( totalEnergyLabel );
        _potentialEnergySeries = new XYSeries( potentialEnergyLabel );
        
        // Dataset
        XYSeriesCollection data = new XYSeriesCollection();
        data.addSeries( _potentialEnergySeries );
        data.addSeries( _totalEnergySeries );
        
        // Renderer
        XYItemRenderer renderer = new StandardXYItemRenderer();
        renderer.setSeriesPaint( 0, QTConstants.POTENTIAL_ENERGY_COLOR );
        renderer.setSeriesStroke( 0, QTConstants.POTENTIAL_ENERGY_STROKE );
        renderer.setSeriesPaint( 1, QTConstants.TOTAL_ENERGY_COLOR );
        renderer.setSeriesStroke( 1, QTConstants.TOTAL_ENERGY_STROKE );
        
        // X axis 
        NumberAxis xAxis = new NumberAxis( positionLabel );
        xAxis.setLabelFont( QTConstants.AXIS_LABEL_FONT );
        xAxis.setRange( QTConstants.POSITION_RANGE );
        
        // Y axis
        NumberAxis yAxis = new NumberAxis( energyLabel );
        yAxis.setLabelFont( QTConstants.AXIS_LABEL_FONT );
        yAxis.setRange( QTConstants.ENERGY_RANGE );

        setRangeAxisLocation( AxisLocation.BOTTOM_OR_LEFT );
        setBackgroundPaint( QTConstants.PLOT_BACKGROUND );
        setDataset( data );
        setRenderer( renderer );
        setDomainAxis( xAxis );
        setRangeAxis( yAxis );
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Sets the total energy model that is displayed.
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
     * Sets the potential energy model that is displayed.
     * 
     * @param potential
     */
    public void setPotentialEnergy( AbstractPotentialEnergy potential ) {
        if ( _potentialEnergy != null ) {
            _potentialEnergy.deleteObserver( this );
        }
        _potentialEnergy = potential;
        _potentialEnergy.addObserver( this );
        updatePotentialEnergy();
    }
    
    /**
     * Sets the font used for labeling the axes.
     * 
     * @param font
     */
    public void setAxesFont( Font font ) {
        getDomainAxis().setLabelFont( font );
        getRangeAxis().setLabelFont( font );
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
            updatePotentialEnergy();
        }
        else if ( observable == _totalEnergy ) {
            updateTotalEnergy();
        }
    }
    
    //----------------------------------------------------------------------------
    // Update handlers
    //----------------------------------------------------------------------------
    
    /*
     * Updates the total energy series to match the model.
     */
    private void updateTotalEnergy() {
        Range range = getDomainAxis().getRange();
        _totalEnergySeries.clear();
        _totalEnergySeries.add( range.getLowerBound(), _totalEnergy.getEnergy() );
        _totalEnergySeries.add( range.getUpperBound(), _totalEnergy.getEnergy() );
    }
    
    /*
     * Updates the potential energy series to match the model.
     */
    private void updatePotentialEnergy() {
        _potentialEnergySeries.clear();
        PotentialRegion[] regions = _potentialEnergy.getRegions();
        for ( int i = 0; i < regions.length; i++ ) {
            double start = regions[i].getStart();
            double end = regions[i].getEnd();
            double energy = regions[i].getEnergy();
            _potentialEnergySeries.add( start, energy );
            _potentialEnergySeries.add( end, energy );
        }
    }
}
