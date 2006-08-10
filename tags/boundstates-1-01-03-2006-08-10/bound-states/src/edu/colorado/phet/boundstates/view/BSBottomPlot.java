/* Copyright 2006, University of Colorado */

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

import org.jfree.chart.axis.*;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleInsets;

import edu.colorado.phet.boundstates.BSConstants;
import edu.colorado.phet.boundstates.color.BSColorScheme;
import edu.colorado.phet.boundstates.enums.BSBottomPlotMode;
import edu.colorado.phet.boundstates.model.*;
import edu.colorado.phet.boundstates.model.BSWaveFunctionCache.Item;
import edu.colorado.phet.boundstates.util.Complex;
import edu.colorado.phet.boundstates.util.MutableComplex;
import edu.colorado.phet.common.model.clock.ClockEvent;
import edu.colorado.phet.common.model.clock.ClockListener;
import edu.colorado.phet.common.view.util.SimStrings;


/**
 * BSBottomPlot does double-duty as both the "Wave Function" and "Probability Density" plot.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSBottomPlot extends XYPlot implements Observer, ClockListener {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // We provide sorted data, so turn off series autosort to improve performance.
    private static final boolean AUTO_SORT = false;
    
    // Are ticks visible on the Y axis?
    private static final boolean Y_AXIS_TICK_LABELS_VISIBLE = false;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // Model references
    private BSModel _model;
    
    private XYSeries _realSeries;
    private XYSeries _imaginarySeries;
    private XYSeries _magnitudeSeries;
    private XYSeries _phaseSeries;
    private XYSeries _probabilityDensitySeries;
    private XYSeries _hiliteSeries;
    
    private int _realSeriesIndex;
    private int _imaginarySeriesIndex;
    private int _magnitudeSeriesIndex;
    private int _phaseSeriesIndex;
    private int _probabilityDensitySeriesIndex;
    private int _hiliteSeriesIndex;
    
    private String _waveFunctionLabel;
    private String _probabilityDensityLabel;
    private String _averageProbabilityDensityLabel;
    
    private IPlotter _plotter;
    
    //----------------------------------------------------------------------------
    // Interface for the plotter
    //----------------------------------------------------------------------------
    
    public interface IPlotter {
        /**
         * Notifies the plotter that the model has changed.
         */
        public void notifyModelChanged();
        
        /**
         * Notifies the plotter that the clock time has changed.
         * 
         * @param t clock time
         */
        public void notifyTimeChanged( final double t );
        
        /**
         * Notifies the plotter that the index of the hilited eigenstate has changed.
         */
        public void notifyHiliteChanged();
        
        /**
         * Refreshes all data series.
         */
        public void refreshAllSeries();
    }
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public BSBottomPlot() {
        super();
        
        // Labels
        _waveFunctionLabel = SimStrings.get( "axis.waveFunction" );
        _probabilityDensityLabel = SimStrings.get( "axis.probabilityDensity" );
        _averageProbabilityDensityLabel = SimStrings.get( "axis.averageProbabilityDensity" );
        
        int index = 0;
        
        // Real
        {
            _realSeriesIndex = index++;
            _realSeries = new XYSeries( "real", AUTO_SORT );
            XYSeriesCollection dataset = new XYSeriesCollection();
            dataset.addSeries( _realSeries );
            setDataset( _realSeriesIndex, dataset );
            XYItemRenderer renderer = BSRendererFactory.createCurveRenderer();
            renderer.setPaint( BSConstants.COLOR_SCHEME.getRealColor() );
            renderer.setStroke( BSConstants.REAL_STROKE );
            setRenderer( _realSeriesIndex, renderer );
        }
         
        // Imaginary
        {
            _imaginarySeriesIndex = index++;
            _imaginarySeries = new XYSeries( "imaginary", AUTO_SORT );
            XYSeriesCollection dataset = new XYSeriesCollection();
            dataset.addSeries( _imaginarySeries );
            setDataset( _imaginarySeriesIndex, dataset );
            XYItemRenderer renderer = BSRendererFactory.createCurveRenderer();
            renderer.setPaint( BSConstants.COLOR_SCHEME.getImaginaryColor() );
            renderer.setStroke( BSConstants.IMAGINARY_STROKE );
            setRenderer( _imaginarySeriesIndex, renderer );
        }
        
        // Magnitude
        {
            _magnitudeSeriesIndex = index++;
            _magnitudeSeries = new XYSeries( "magnitude", AUTO_SORT );
            XYSeriesCollection dataset = new XYSeriesCollection();
            dataset.addSeries( _magnitudeSeries );
            setDataset( _magnitudeSeriesIndex, dataset );
            XYItemRenderer renderer = BSRendererFactory.createCurveRenderer();
            renderer.setPaint( BSConstants.COLOR_SCHEME.getMagnitudeColor() );
            renderer.setStroke( BSConstants.MAGNITUDE_STROKE );
            setRenderer( _magnitudeSeriesIndex, renderer );
        }
        
        // Phase
        {
            _phaseSeriesIndex = index++;
            _phaseSeries = new XYSeries( "phase", AUTO_SORT );
            XYSeriesCollection dataset = new XYSeriesCollection();
            dataset.addSeries( _phaseSeries );
            setDataset( _phaseSeriesIndex, dataset );
            XYItemRenderer renderer = BSRendererFactory.createPhaseRenderer();
            setRenderer( _phaseSeriesIndex, renderer );
        }
        
        // Probability Density
        {
            _probabilityDensitySeriesIndex = index++;
            _probabilityDensitySeries = new XYSeries( "probability density", AUTO_SORT );
            XYSeriesCollection dataset = new XYSeriesCollection();
            dataset.addSeries( _probabilityDensitySeries );
            setDataset( _probabilityDensitySeriesIndex, dataset );
            XYItemRenderer renderer = BSRendererFactory.createCurveRenderer();
            renderer.setPaint( BSConstants.COLOR_SCHEME.getMagnitudeColor() ); // use magnitude color!
            renderer.setStroke( BSConstants.PROBABILITY_DENSITY_STROKE );
            setRenderer( _probabilityDensitySeriesIndex, renderer );
        }
        
        // Hilited eigenstate's time-independent wave function.
        // Add this last so that it's behind everything else.
        {
            _hiliteSeriesIndex = index++;
            _hiliteSeries = new XYSeries( "hilite", AUTO_SORT );
            XYSeriesCollection dataset = new XYSeriesCollection();
            dataset.addSeries( _hiliteSeries );
            setDataset( _hiliteSeriesIndex, dataset );
            XYItemRenderer renderer = BSRendererFactory.createCurveRenderer();
            renderer.setPaint( BSConstants.COLOR_SCHEME.getEigenstateHiliteColor() );
            renderer.setStroke( BSConstants.HILITE_STROKE );
            setRenderer( _hiliteSeriesIndex, renderer );
        }
        
        // X (domain) axis 
        BSPositionAxis xAxis = new BSPositionAxis();
        
        // Y (range) axis
        NumberAxis yAxis = new NumberAxis( _waveFunctionLabel );
        {
            yAxis.setRange( BSConstants.WAVE_FUNCTION_RANGE );
            yAxis.setLabelFont( BSConstants.AXIS_LABEL_FONT );
            yAxis.setTickLabelFont( BSConstants.AXIS_TICK_LABEL_FONT );
            yAxis.setTickLabelPaint( BSConstants.COLOR_SCHEME.getTickColor() );
            yAxis.setTickMarkPaint( BSConstants.COLOR_SCHEME.getTickColor() );
            yAxis.setTickMarksVisible( true );
            yAxis.setTickLabelsVisible( Y_AXIS_TICK_LABELS_VISIBLE );
            if ( !Y_AXIS_TICK_LABELS_VISIBLE ) {
                yAxis.setLabelInsets( new RectangleInsets( 0,0,0,35 ) );
            }
        }

        setRangeAxisLocation( AxisLocation.BOTTOM_OR_LEFT );
        setBackgroundPaint( BSConstants.COLOR_SCHEME.getChartColor() );
        setDomainGridlinesVisible( BSConstants.SHOW_VERTICAL_GRIDLINES );
        setRangeGridlinesVisible( BSConstants.SHOW_HORIZONTAL_GRIDLINES );
        setDomainGridlinePaint( BSConstants.COLOR_SCHEME.getGridlineColor() );
        setRangeGridlinePaint( BSConstants.COLOR_SCHEME.getGridlineColor() );
        setDomainAxis( xAxis );
        setRangeAxis( yAxis );
        
        setMode( BSBottomPlotMode.PROBABILITY_DENSITY );
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------

    /**
     * Sets the model that this plot displays.
     * 
     * @param model
     */
    public void setModel( BSModel model ) {
        if ( _model != model ) {
            if ( _model != null ) {
                _model.deleteObserver( this );
            }
            _model = model;
            _model.addObserver( this );
            _plotter.notifyModelChanged();
        }
    }
    
    /**
     * Gets the model that this plot displays.
     * 
     * @return BSModel
     */
    public BSModel getModel() {
        return _model;
    }
    
    /**
     * Sets the mode for this plot.
     * The mode determines what series are displayed and 
     * which plotter is used to compute the data.
     * 
     * @param mode
     */
    public void setMode( BSBottomPlotMode mode ) {
        
        clearAllSeries();
        
        ValueAxis yAxis = getRangeAxis();
        
        if ( mode == BSBottomPlotMode.WAVE_FUNCTION ) {
            // Plotter
            _plotter = new BSWaveFunctionPlotter( this );
            // Views
            setRealSeriesVisible( true );
            setImaginarySeriesVisible( true );
            setMagnitudeSeriesVisible( true );
            setPhaseSeriesVisible( true );
            setProbabilityDensitySeriesVisible( false );
            // Y-axis
            yAxis.setLabel( _waveFunctionLabel );
            yAxis.setRange( BSConstants.WAVE_FUNCTION_RANGE );
            TickUnits tickUnits = new TickUnits();
            tickUnits.add( new NumberTickUnit( BSConstants.WAVE_FUNCTION_TICK_SPACING, BSConstants.WAVE_FUNCTION_TICK_FORMAT ) );
            yAxis.setStandardTickUnits( tickUnits );
            yAxis.setAutoTickUnitSelection( true );
        }
        else if ( mode == BSBottomPlotMode.PROBABILITY_DENSITY ) {
            // Plotter
            _plotter = new BSWaveFunctionPlotter( this );
            // Views
            setRealSeriesVisible( false );
            setImaginarySeriesVisible( false );
            setMagnitudeSeriesVisible( false );
            setPhaseSeriesVisible( false );
            setProbabilityDensitySeriesVisible( true );
            // Y-axis
            yAxis.setLabel( _probabilityDensityLabel );
            yAxis.setRange( BSConstants.PROBABILITY_DENSITY_RANGE );
            TickUnits tickUnits = new TickUnits();
            tickUnits.add( new NumberTickUnit( BSConstants.PROBABILITY_DENSITY_TICK_SPACING, BSConstants.PROBABILITY_DENSITY_TICK_FORMAT ) );
            yAxis.setStandardTickUnits( tickUnits );
            yAxis.setAutoTickUnitSelection( true );
        }
        else if ( mode == BSBottomPlotMode.PROBABILITY_DENSITY || mode == BSBottomPlotMode.AVERAGE_PROBABILITY_DENSITY ) {
            // Plotter
            _plotter = new BSAverageProbabilityDensityPlotter( this );
            // Views
            setRealSeriesVisible( false );
            setImaginarySeriesVisible( false );
            setMagnitudeSeriesVisible( false );
            setPhaseSeriesVisible( false );
            setProbabilityDensitySeriesVisible( true );
            // Y-axis
            yAxis.setLabel( _averageProbabilityDensityLabel );
            yAxis.setRange( BSConstants.AVERAGE_PROBABILITY_DENSITY_RANGE );
            TickUnits tickUnits = new TickUnits();
            tickUnits.add( new NumberTickUnit( BSConstants.AVERAGE_PROBABILITY_DENSITY_TICK_SPACING, BSConstants.AVERAGE_PROBABILITY_DENSITY_TICK_FORMAT ) );
            yAxis.setStandardTickUnits( tickUnits );
            yAxis.setAutoTickUnitSelection( true );
        }
        else {
            throw new UnsupportedOperationException( "unsupported mode: " + mode );
        }
        
        if ( _model != null ) {
            _plotter.notifyModelChanged();
        }
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
        getRenderer( _realSeriesIndex ).setPaint( scheme.getRealColor() );
        getRenderer( _imaginarySeriesIndex ).setPaint( scheme.getImaginaryColor() );
        getRenderer( _magnitudeSeriesIndex ).setPaint( scheme.getMagnitudeColor() );
        getRenderer( _probabilityDensitySeriesIndex ).setPaint( scheme.getMagnitudeColor() ); // use magnitude color!
        getRenderer( _hiliteSeriesIndex ).setPaint( scheme.getEigenstateHiliteColor() );
    }
    
    /**
     * Clears all series.
     */
    public void clearAllSeries() {
        _realSeries.clear();
        _imaginarySeries.clear();
        _magnitudeSeries.clear();
        _phaseSeries.clear();
        _probabilityDensitySeries.clear();
        _hiliteSeries.clear();
    }
    
    //----------------------------------------------------------------------------
    // Series references
    //----------------------------------------------------------------------------
    
    public XYSeries getRealSeries() {
        return _realSeries;
    }
    
    public XYSeries getImaginarySeries() {
        return _imaginarySeries;
    }
    
    public XYSeries getMagnitudeSeries() {
        return _magnitudeSeries;
    }
    
    public XYSeries getPhaseSeries() {
        return _phaseSeries;
    }
    
    public XYSeries getProbabilityDensitySeries() {
        return _probabilityDensitySeries;
    }
    
    public XYSeries getHiliteSeries() {
        return _hiliteSeries;
    }

    //----------------------------------------------------------------------------
    // Series visibility
    //----------------------------------------------------------------------------

    private void setSeriesVisible( int seriesIndex, boolean visible ) {
        getRenderer( seriesIndex ).setSeriesVisible( new Boolean( visible ) );
        _plotter.refreshAllSeries();
    }
    
    private boolean isSeriesVisible( int seriesIndex ) {
        return getRenderer( seriesIndex ).getSeriesVisible().booleanValue();
    }
    
    public void setRealSeriesVisible( boolean visible ) {
        setSeriesVisible( _realSeriesIndex, visible );
    }
    
    public boolean isRealSeriesVisible() {
        return isSeriesVisible( _realSeriesIndex );
    }
    
    public void setImaginarySeriesVisible( boolean visible ) {
        setSeriesVisible( _imaginarySeriesIndex, visible );
    }
    
    public boolean isImaginarySeriesVisible() {
        return isSeriesVisible( _imaginarySeriesIndex );
    }
    
    public void setMagnitudeSeriesVisible( boolean visible ) {
        setSeriesVisible( _magnitudeSeriesIndex, visible );
    }
    
    public boolean isMagnitudeSeriesVisible() {
        return isSeriesVisible( _magnitudeSeriesIndex );
    }
    
    public void setPhaseSeriesVisible( boolean visible ) {
        setSeriesVisible( _phaseSeriesIndex, visible );
    }
    
    public boolean isPhaseSeriesVisible() {
        return isSeriesVisible( _phaseSeriesIndex );
    }
    
    public void setProbabilityDensitySeriesVisible( boolean visible ) {
        setSeriesVisible( _probabilityDensitySeriesIndex, visible );
    }
    
    public boolean isProbabilityDensitySeriesVisible() {
        return isSeriesVisible( _probabilityDensitySeriesIndex );
    }
    
    public void setHiliteSeriesVisible( boolean visible ) {
        setSeriesVisible( _hiliteSeriesIndex, visible );
    }
    
    public boolean isHiliteSeriesVisible() {
        return isSeriesVisible( _hiliteSeriesIndex );
    }
    
    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------
    
    /**
     * Updates the view to match the model.
     * 
     * @param o
     * @param arg
     */
    public void update( Observable o, Object arg ) {
        if ( o == _model ) {
            if ( arg == BSModel.PROPERTY_HILITED_EIGENSTATE_INDEX ) {
                _plotter.notifyHiliteChanged();
            }
            else if ( arg == BSModel.PROPERTY_PARTICLE ) {
                // ignore, we'll be notified that the potential changed
            }
            else if ( arg == BSModel.PROPERTY_SUPERPOSITION_COEFFICIENTS_COUNT ) {
                // ignore, the plotter doesn't need to change
            }
            else if ( arg == null ||
                      arg == BSModel.PROPERTY_POTENTIAL ||
                      arg == BSModel.PROPERTY_SUPERPOSITION_COEFFICIENTS_VALUES  ) { 
                _plotter.notifyModelChanged();
            }
        }
    }
    
    //----------------------------------------------------------------------------
    // ClockListener implementation
    //----------------------------------------------------------------------------

    public void simulationTimeChanged( ClockEvent clockEvent ) {
        final double t = clockEvent.getSimulationTime();
        _plotter.notifyTimeChanged( t );
    }

    public void simulationTimeReset( ClockEvent clockEvent ) {}
    
    public void clockTicked( ClockEvent clockEvent ) {}

    public void clockStarted( ClockEvent clockEvent ) {}

    public void clockPaused( ClockEvent clockEvent ) {}
}
