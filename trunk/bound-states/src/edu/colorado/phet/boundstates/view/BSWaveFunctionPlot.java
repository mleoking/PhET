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
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import edu.colorado.phet.boundstates.BSConstants;
import edu.colorado.phet.common.view.util.SimStrings;


/**
 * BSWaveFunctionPlot is Wabe Function plot.
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
    private XYSeries _particleSeries;
    
    private int _incidentRealIndex;
    private int _incidentImaginaryIndex;
    private int _incidentMagnitudeIndex;
    private int _reflectedRealIndex;
    private int _reflectedImaginaryIndex;
    private int _reflectedMagnitudeIndex;
    private int _phaseIndex;
    private int _probabilityDensityIndex;
    private int _particleSeriesIndex;
    
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
            _incidentRealIndex = index++;
            _realSeries = new XYSeries( "real", AUTO_SORT );
            XYSeriesCollection dataset = new XYSeriesCollection();
            dataset.addSeries( _realSeries );
            setDataset( _incidentRealIndex, dataset );
            XYItemRenderer renderer = new StandardXYItemRenderer();
            renderer.setPaint( BSConstants.REAL_WAVE_COLOR );
            renderer.setStroke( BSConstants.INCIDENT_REAL_WAVE_STROKE );
            setRenderer( _incidentRealIndex, renderer );
        }
         
        // Imaginary
        {
            _incidentImaginaryIndex = index++;
            _imaginarySeries = new XYSeries( "imaginary", AUTO_SORT );
            XYSeriesCollection dataset = new XYSeriesCollection();
            dataset.addSeries( _imaginarySeries );
            setDataset( _incidentImaginaryIndex, dataset );
            XYItemRenderer renderer = new StandardXYItemRenderer();
            renderer.setPaint( BSConstants.IMAGINARY_WAVE_COLOR );
            renderer.setStroke( BSConstants.INCIDENT_IMAGINARY_WAVE_STROKE );
            setRenderer( _incidentImaginaryIndex, renderer );
        }
        
        // Magnitude
        {
            _incidentMagnitudeIndex = index++;
            _magnitudeSeries = new XYSeries( "magnitude", AUTO_SORT );
            XYSeriesCollection dataset = new XYSeriesCollection();
            dataset.addSeries( _magnitudeSeries );
            setDataset( _incidentMagnitudeIndex, dataset );
            XYItemRenderer renderer = new StandardXYItemRenderer();
            renderer.setPaint( BSConstants.MAGNITUDE_WAVE_COLOR );
            renderer.setStroke( BSConstants.INCIDENT_MAGNITUDE_WAVE_STROKE );
            setRenderer( _incidentMagnitudeIndex, renderer );
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
            XYItemRenderer renderer = new PhaseRenderer();
            setRenderer( _probabilityDensityIndex, renderer );
        }
        
        // Classical Particle
        {
            _particleSeriesIndex = index++;
            _particleSeries = new XYSeries( "classical particle", AUTO_SORT );
            XYSeriesCollection dataset = new XYSeriesCollection();
            dataset.addSeries( _particleSeries );
            setDataset( _particleSeriesIndex, dataset );
            XYItemRenderer renderer = new PhaseRenderer();
            setRenderer( _particleSeriesIndex, renderer );
        }
        
        // X (domain) axis 
        BSPositionAxis xAxis = new BSPositionAxis();

        
        // Y (range) axis
        NumberAxis yAxis = new NumberAxis( waveFunctionLabel );
        yAxis.setLabelFont( BSConstants.AXIS_LABEL_FONT );
        yAxis.setRange( BSConstants.WAVE_FUNCTION_RANGE );
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
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------

    public void setRealVisible( boolean visible ) {
        getRenderer( _incidentRealIndex ).setSeriesVisible( new Boolean( visible ) );
        getRenderer( _reflectedRealIndex ).setSeriesVisible( new Boolean( visible ) );
    }
    
    public void setImaginaryVisible( boolean visible ) {
        getRenderer( _incidentImaginaryIndex ).setSeriesVisible( new Boolean( visible ) );
        getRenderer( _reflectedImaginaryIndex ).setSeriesVisible( new Boolean( visible ) );
    }
    
    public void setMagnitudeVisible( boolean visible ) {
        getRenderer( _incidentMagnitudeIndex ).setSeriesVisible( new Boolean( visible ) );
        getRenderer( _reflectedMagnitudeIndex ).setSeriesVisible( new Boolean( visible ) );
    }
    
    public void setPhaseVisible( boolean visible ) {
        getRenderer( _phaseIndex ).setSeriesVisible( new Boolean( visible ) );
    }
    
    public void setProbabilityDensityVisible( boolean visible ) {
        getRenderer( _probabilityDensityIndex ).setSeriesVisible( new Boolean( visible ) );
    }
    
    public void setClassicalParticlVisible( boolean visible ) {
        getRenderer( _particleSeriesIndex ).setSeriesVisible( new Boolean( visible ) );
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
        //XXX update data items
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
        _particleSeries.clear();
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
        _particleSeries.setNotify( notify );
    }
}
