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
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.jfreechart.FastPathRenderer;


/**
 * BSWaveFunctionPlot is the Wave Function plot.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSWaveFunctionPlot extends XYPlot implements Observer {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // We provide sorted data, so turn off series autosort to improve performance.
    private static final boolean AUTO_SORT = false;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private XYSeries _realSeries;
    private XYSeries _imaginarySeries;
    private XYSeries _magnitudeSeries;
    private XYSeries _phaseSeries;
    private XYSeries _probabilityDensitySeries;
    private XYSeries _hiliteSeries;
    
    private int _realIndex;
    private int _imaginaryIndex;
    private int _magnitudeIndex;
    private int _phaseIndex;
    private int _probabilityDensityIndex;
    private int _hiliteIndex;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public BSWaveFunctionPlot() {
        super();
        
        // Labels
        String waveFunctionLabel = SimStrings.get( "axis.waveFunction" );
        
        int index = 0;
        
        // Real
        {
            _realIndex = index++;
            _realSeries = new XYSeries( "real", AUTO_SORT );
            XYSeriesCollection dataset = new XYSeriesCollection();
            dataset.addSeries( _realSeries );
            setDataset( _realIndex, dataset );
            XYItemRenderer renderer = new FastPathRenderer();
            renderer.setPaint( BSConstants.COLOR_SCHEME.getRealColor() );
            renderer.setStroke( BSConstants.REAL_STROKE );
            setRenderer( _realIndex, renderer );
        }
         
        // Imaginary
        {
            _imaginaryIndex = index++;
            _imaginarySeries = new XYSeries( "imaginary", AUTO_SORT );
            XYSeriesCollection dataset = new XYSeriesCollection();
            dataset.addSeries( _imaginarySeries );
            setDataset( _imaginaryIndex, dataset );
            XYItemRenderer renderer = new FastPathRenderer();
            renderer.setPaint( BSConstants.COLOR_SCHEME.getImaginaryColor() );
            renderer.setStroke( BSConstants.IMAGINARY_STROKE );
            setRenderer( _imaginaryIndex, renderer );
        }
        
        // Magnitude
        {
            _magnitudeIndex = index++;
            _magnitudeSeries = new XYSeries( "magnitude", AUTO_SORT );
            XYSeriesCollection dataset = new XYSeriesCollection();
            dataset.addSeries( _magnitudeSeries );
            setDataset( _magnitudeIndex, dataset );
            XYItemRenderer renderer = new FastPathRenderer();
            renderer.setPaint( BSConstants.COLOR_SCHEME.getMagnitudeColor() );
            renderer.setStroke( BSConstants.MAGNITUDE_STROKE );
            setRenderer( _magnitudeIndex, renderer );
        }
        
        // Phase
        {
            _phaseIndex = index++;
            _phaseSeries = new XYSeries( "phase", AUTO_SORT );
            XYSeriesCollection dataset = new XYSeriesCollection();
            dataset.addSeries( _phaseSeries );
            setDataset( _phaseIndex, dataset );
            XYItemRenderer renderer = new PhaseRenderer();
            setRenderer( _phaseIndex, renderer );
        }
        
        // Probability Density
        {
            _probabilityDensityIndex = index++;
            _probabilityDensitySeries = new XYSeries( "probability density", AUTO_SORT );
            XYSeriesCollection dataset = new XYSeriesCollection();
            dataset.addSeries( _probabilityDensitySeries );
            setDataset( _probabilityDensityIndex, dataset );
            XYItemRenderer renderer = new FastPathRenderer();
            renderer.setPaint( BSConstants.COLOR_SCHEME.getMagnitudeColor() ); // use magnitude color!
            renderer.setStroke( BSConstants.PROBABILITY_DENSITY_STROKE );
            setRenderer( _probabilityDensityIndex, renderer );
        }
        
        // Hilited eigenstate's time-independent wave function
        {
            _hiliteIndex = index++;
            _hiliteSeries = new XYSeries( "hilite", AUTO_SORT );
            XYSeriesCollection dataset = new XYSeriesCollection();
            dataset.addSeries( _hiliteSeries );
            setDataset( _hiliteIndex, dataset );
            XYItemRenderer renderer = new FastPathRenderer();
            renderer.setPaint( BSConstants.COLOR_SCHEME.getEigenstateHiliteColor() );
            renderer.setStroke( BSConstants.HILITE_STROKE );
            setRenderer( _hiliteIndex, renderer );
        }
        
        // X (domain) axis 
        BSPositionAxis xAxis = new BSPositionAxis();
        
        // Y (range) axis
        NumberAxis yAxis = new NumberAxis( waveFunctionLabel );
        yAxis.setLabelFont( BSConstants.AXIS_LABEL_FONT );
        yAxis.setRange( BSConstants.WAVE_FUNCTION_RANGE );
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
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------

    public void setRealVisible( boolean visible ) {
        getRenderer( _realIndex ).setSeriesVisible( new Boolean( visible ) );
    }
    
    public void setImaginaryVisible( boolean visible ) {
        getRenderer( _imaginaryIndex ).setSeriesVisible( new Boolean( visible ) );
    }
    
    public void setMagnitudeVisible( boolean visible ) {
        getRenderer( _magnitudeIndex ).setSeriesVisible( new Boolean( visible ) );
    }
    
    public void setPhaseVisible( boolean visible ) {
        getRenderer( _phaseIndex ).setSeriesVisible( new Boolean( visible ) );
    }
    
    public void setProbabilityDensityVisible( boolean visible ) {
        getRenderer( _probabilityDensityIndex ).setSeriesVisible( new Boolean( visible ) );
    }
    
    public void setHiliteVisible( boolean visible ) {
        getRenderer( _hiliteIndex ).setSeriesVisible( new Boolean( visible ) );
    }
    
    /**
     * Sets the color scheme for this plot.
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
        getRenderer( _realIndex ).setPaint( scheme.getRealColor() );
        getRenderer( _imaginaryIndex ).setPaint( scheme.getImaginaryColor() );
        getRenderer( _magnitudeIndex ).setPaint( scheme.getMagnitudeColor() );
        getRenderer( _probabilityDensityIndex ).setPaint( scheme.getMagnitudeColor() ); // use magnitude color!
        getRenderer( _hiliteIndex ).setPaint( scheme.getEigenstateHiliteColor() );
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
        updateDatasets();
    }
    
    //----------------------------------------------------------------------------
    // Updaters
    //----------------------------------------------------------------------------
    
    private void updateDatasets() {
        clearAllSeries();
        setSeriesNotify( false );
        //XXX
        setSeriesNotify( true );
    }
    
    /*
     * Clears the data from all series.
     */
    private void clearAllSeries() {
        _realSeries.clear();
        _imaginarySeries.clear();
        _magnitudeSeries.clear();
        _phaseSeries.clear();
        _probabilityDensitySeries.clear();
        _hiliteSeries.clear();
    }
    
    /*
     * Changes notification for all series.
     * <p>
     * Call this method with false before adding a lot of points, so that
     * we don't get unnecessary updates.  When all points have been added,
     * call this method with true to notify listeners that the series 
     * have changed.
     * 
     * @param notify true or false
     */
    private void setSeriesNotify( boolean notify ) {
        _realSeries.setNotify( notify );
        _imaginarySeries.setNotify( notify );
        _magnitudeSeries.setNotify( notify );
        _phaseSeries.setNotify( notify );
        _probabilityDensitySeries.setNotify( notify );
        _hiliteSeries.setNotify( notify );
    }
}
