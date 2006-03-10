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

import java.awt.geom.Point2D;
import java.util.Observable;
import java.util.Observer;

import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import edu.colorado.phet.boundstates.BSConstants;
import edu.colorado.phet.boundstates.model.BSAbstractPotential;
import edu.colorado.phet.boundstates.model.BSSquareWells;
import edu.colorado.phet.common.view.util.SimStrings;


/**
 * BSEnergyPlot is the plot that displays total and potential energy.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSEnergyPlot extends XYPlot implements Observer {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    // Model references
    private BSAbstractPotential _well;
    
    // View
    private XYSeries _totalEnergySeries;
    private XYSeries _wellSeries;
    private int _totalEnergyIndex; // total energy dataset index
    private int _wellIndex; // well dataset index
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public BSEnergyPlot() {
        super();
        
        // Labels (localized)
        String energyLabel = SimStrings.get( "axis.energy" ) + " (" + SimStrings.get( "units.energy" ) + ")";
        String potentialEnergyLabel = SimStrings.get( "legend.potentialEnergy" );
        String totalEnergyLabel = SimStrings.get( "legend.totalEnergy" );
        
        int dataSetIndex = 0;
        
        // Well series
        _wellSeries = new XYSeries( potentialEnergyLabel, false /* autoSort */ );
        {
            _wellIndex = dataSetIndex++;
            // Dataset
            XYSeriesCollection dataset = new XYSeriesCollection();
            dataset.addSeries( _wellSeries );
            setDataset( _wellIndex, dataset );
            // Renderer
            XYItemRenderer renderer = new StandardXYItemRenderer();
            renderer.setPaint( BSConstants.POTENTIAL_ENERGY_COLOR );
            renderer.setStroke( BSConstants.POTENTIAL_ENERGY_STROKE );
            setRenderer( _wellIndex, renderer );
        }
        
        // Total Energy series
        _totalEnergySeries = new XYSeries( totalEnergyLabel, false /* autoSort */);
        {
            _totalEnergyIndex = dataSetIndex++;
            // Dataset
            XYSeriesCollection dataset = new XYSeriesCollection();
            dataset.addSeries( _totalEnergySeries );
            setDataset( _totalEnergyIndex, dataset );
            // Plane Wave renderer
            //XXX - total energy will need a custom renderer, point in series per eigenstate
            XYItemRenderer renderer = new StandardXYItemRenderer();
            renderer.setPaint( BSConstants.EIGENSTATE_UNSELECTED_COLOR );
            renderer.setStroke( BSConstants.EIGENSTATE_UNSELECTED_STROKE );
            setRenderer( _totalEnergyIndex, renderer );
        }
        
        // X axis 
        BSPositionAxis xAxis = new BSPositionAxis();
        
        // Y axis
        NumberAxis yAxis = new NumberAxis( energyLabel );
        yAxis.setLabelFont( BSConstants.AXIS_LABEL_FONT );
        yAxis.setRange( BSConstants.ENERGY_RANGE );
        yAxis.setTickLabelPaint( BSConstants.TICK_LABEL_COLOR );
        yAxis.setTickMarkPaint( BSConstants.TICK_MARK_COLOR );

        setRangeAxisLocation( AxisLocation.BOTTOM_OR_LEFT );
        setBackgroundPaint( BSConstants.PLOT_BACKGROUND );
        setDomainGridlinesVisible( BSConstants.SHOW_VERTICAL_GRIDLINES );
        setRangeGridlinesVisible( BSConstants.SHOW_HORIZONTAL_GRIDLINES );
        setDomainGridlinePaint( BSConstants.GRIDLINES_COLOR );
        setRangeGridlinePaint( BSConstants.GRIDLINES_COLOR );
        setDomainAxis( xAxis );
        setRangeAxis( yAxis ); 
    }
    
    public void cleanup() {
        if ( _well != null ) {
            _well.deleteObserver( this );
            _well = null;
        }
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public void setWell( BSAbstractPotential well ) {
        _well = well;
        _well.addObserver( this );
        update();
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
        if ( observable == _well ) {
            updateWell();
        }
    }
    
    //----------------------------------------------------------------------------
    // Update handlers
    //----------------------------------------------------------------------------

    /**
     * Updates everything.
     */
    public void update() {
        updateTotalEnergy();
        updateWell();
    }
    
    /*
     * Updates the total energy series to match the model.
     */
    private void updateTotalEnergy() {
        //XXX
    }
    
    /*
     * Updates the potential energy series to match the model.
     */
    private void updateWell() {
        final double minX = getDomainAxis().getLowerBound();
        final double maxX = getDomainAxis().getUpperBound();
        final double dx = 0.1; //XXX calculate based on plot bounds and pixels per sample
        Point2D[] points = _well.getPoints( minX, maxX, dx );
        _wellSeries.clear();
        for ( int i = 0; i < points.length; i++ ) {
            _wellSeries.add( points[i].getX(), points[i].getY() );
        }
    }
}
