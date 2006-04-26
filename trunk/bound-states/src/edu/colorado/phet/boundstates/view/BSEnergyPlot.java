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
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import edu.colorado.phet.boundstates.BSConstants;
import edu.colorado.phet.boundstates.color.BSColorScheme;
import edu.colorado.phet.boundstates.model.BSAbstractPotential;
import edu.colorado.phet.boundstates.model.BSModel;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.jfreechart.FastPathRenderer;


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
    private BSModel _model;
    
    // View
    private XYSeries _potentialSeries;
    private int _potentialIndex; // well dataset index
    private double _dx;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public BSEnergyPlot() {
        super();
        
        // Labels (localized)
        String energyLabel = SimStrings.get( "axis.energy" ) + " (" + SimStrings.get( "units.energy" ) + ")";
        String potentialEnergyLabel = SimStrings.get( "legend.potentialEnergy" );
        
        int dataSetIndex = 0;
        
        // Potential series
        _potentialSeries = new XYSeries( potentialEnergyLabel, false /* autoSort */ );
        {
            _potentialIndex = dataSetIndex++;
            // Dataset
            XYSeriesCollection dataset = new XYSeriesCollection();
            dataset.addSeries( _potentialSeries );
            setDataset( _potentialIndex, dataset );
            // Renderer
            XYItemRenderer renderer = new FastPathRenderer();
            renderer.setPaint( BSConstants.COLOR_SCHEME.getPotentialEnergyColor() );
            renderer.setStroke( BSConstants.POTENTIAL_ENERGY_STROKE );
            setRenderer( _potentialIndex, renderer );
        }
        
        // X axis 
        BSPositionAxis xAxis = new BSPositionAxis();
        
        // Y axis
        NumberAxis yAxis = new NumberAxis( energyLabel );
        yAxis.setLabelFont( BSConstants.AXIS_LABEL_FONT );
        yAxis.setRange( BSConstants.ENERGY_RANGE.getLowerBound() * 1.05, BSConstants.ENERGY_RANGE.getUpperBound() * 1.05 );
        yAxis.setTickLabelPaint( BSConstants.COLOR_SCHEME.getTickColor() );
        yAxis.setTickMarkPaint( BSConstants.COLOR_SCHEME.getTickColor() );

        setRangeAxisLocation( AxisLocation.BOTTOM_OR_LEFT );
        setBackgroundPaint( BSConstants.COLOR_SCHEME.getChartColor() );
        setDomainGridlinesVisible( BSConstants.SHOW_VERTICAL_GRIDLINES );
        setRangeGridlinesVisible( BSConstants.SHOW_HORIZONTAL_GRIDLINES );
        setDomainGridlinePaint( BSConstants.COLOR_SCHEME.getGridlineColor() );
        setRangeGridlinePaint( BSConstants.COLOR_SCHEME.getGridlineColor() );
        setDomainAxis( xAxis );
        setRangeAxis( yAxis );
        
        _dx = 0.1;
    }
    
    public void cleanup() {
        if ( _model != null ) {
            _model.deleteObserver( this );
            _model = null;
        }
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public void setModel( BSModel model ) {
        if ( _model != model ) {
            if ( _model != null ) {
                _model.deleteObserver( this );
            }
            _model = model;
            _model.addObserver( this );
            updateDatasets();
        }
    }
    
    /**
     * Sets the color scheme for the plot.
     * 
     * @param scheme
     */
    public void setColorScheme( BSColorScheme scheme ) {
        // Background
        setBackgroundPaint( scheme.getChartColor() );
        // Ticks
        getDomainAxis().setTickLabelPaint( scheme.getTickColor() );
        getDomainAxis().setTickMarkPaint( scheme.getTickColor() );
        getRangeAxis().setTickLabelPaint( scheme.getTickColor() );
        getRangeAxis().setTickMarkPaint( scheme.getTickColor() );
        // Gridlines
        setDomainGridlinePaint( scheme.getGridlineColor() );
        setRangeGridlinePaint( scheme.getGridlineColor() );
        // Series
        getRenderer( _potentialIndex ).setPaint( scheme.getPotentialEnergyColor() );
    }
    
    public void setDx( double dx ) {
        if ( dx != _dx ) {
            _dx = dx;
            updateDatasets();
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
        if ( observable == _model && arg == BSModel.PROPERTY_POTENTIAL ) {
            updateDatasets();
        }
    }
    
    //----------------------------------------------------------------------------
    // Dataset updaters
    //----------------------------------------------------------------------------
    
    /*
     * Updates the datasets to match the model.
     */
    private void updateDatasets() {
        _potentialSeries.setNotify( false );
        final double minX = getDomainAxis().getLowerBound();
        final double maxX = getDomainAxis().getUpperBound();
        BSAbstractPotential potential = _model.getPotential();
        Point2D[] points = potential.getPotentialPoints( minX, maxX, _dx );
        _potentialSeries.clear();
        for ( int i = 0; i < points.length; i++ ) {
            _potentialSeries.add( points[i].getX(), points[i].getY() );
        }
        _potentialSeries.setNotify( true );
    }
}
