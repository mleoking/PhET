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
    
    private int _realIndex;
    private int _imaginaryIndex;
    private int _magnitudeIndex;
    private int _phaseIndex;
    private int _probabilityDensityIndex;
    private int _hiliteIndex;
    
    private BSBottomPlotMode _mode;
    
    private String _waveFunctionLabel;
    private String _probabilityDensityLabel;
    private String _averageProbabilityDensityLabel;
    
    // Cache of wave function data
    private BSWaveFunctionCache _cache;
    
    private double _t; // time of the last clock tick
    
    // Memory optimizations
    private MutableComplex[] _psiSum; // reused by computeTimeDependentWaveFunction
    
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
            _realIndex = index++;
            _realSeries = new XYSeries( "real", AUTO_SORT );
            XYSeriesCollection dataset = new XYSeriesCollection();
            dataset.addSeries( _realSeries );
            setDataset( _realIndex, dataset );
            XYItemRenderer renderer = BSRendererFactory.createCurveRenderer();
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
            XYItemRenderer renderer = BSRendererFactory.createCurveRenderer();
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
            XYItemRenderer renderer = BSRendererFactory.createCurveRenderer();
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
            XYItemRenderer renderer = BSRendererFactory.createPhaseRenderer();
            setRenderer( _phaseIndex, renderer );
        }
        
        // Probability Density
        {
            _probabilityDensityIndex = index++;
            _probabilityDensitySeries = new XYSeries( "probability density", AUTO_SORT );
            XYSeriesCollection dataset = new XYSeriesCollection();
            dataset.addSeries( _probabilityDensitySeries );
            setDataset( _probabilityDensityIndex, dataset );
            XYItemRenderer renderer = BSRendererFactory.createCurveRenderer();
            renderer.setPaint( BSConstants.COLOR_SCHEME.getMagnitudeColor() ); // use magnitude color!
            renderer.setStroke( BSConstants.PROBABILITY_DENSITY_STROKE );
            setRenderer( _probabilityDensityIndex, renderer );
        }
        
        
        // Hilited eigenstate's time-independent wave function.
        // Add this last so that it's behind everything else.
        {
            _hiliteIndex = index++;
            _hiliteSeries = new XYSeries( "hilite", AUTO_SORT );
            XYSeriesCollection dataset = new XYSeriesCollection();
            dataset.addSeries( _hiliteSeries );
            setDataset( _hiliteIndex, dataset );
            XYItemRenderer renderer = BSRendererFactory.createCurveRenderer();
            renderer.setPaint( BSConstants.COLOR_SCHEME.getEigenstateHiliteColor() );
            renderer.setStroke( BSConstants.HILITE_STROKE );
            setRenderer( _hiliteIndex, renderer );
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
        
        _cache = new BSWaveFunctionCache();
        
        setMode( BSBottomPlotMode.PROBABILITY_DENSITY );
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
            updateCache();
            updateAllSeries();
        }
    }
    
    /**
     * Sets the mode for this plot, which determines what the plot computes and displays.
     * 
     * @param mode
     */
    public void setMode( BSBottomPlotMode mode ) {
        _mode = mode;
        ValueAxis yAxis = getRangeAxis();
        if ( mode == BSBottomPlotMode.WAVE_FUNCTION ) {
            // Views
            setRealVisible( true );
            setImaginaryVisible( true );
            setMagnitudeVisible( true );
            setPhaseVisible( true );
            setProbabilityDensityVisible( false );
            // Y-axis
            yAxis.setLabel( _waveFunctionLabel );
            yAxis.setRange( BSConstants.WAVE_FUNCTION_RANGE );
            TickUnits tickUnits = new TickUnits();
            tickUnits.add( new NumberTickUnit( BSConstants.WAVE_FUNCTION_TICK_SPACING, BSConstants.WAVE_FUNCTION_TICK_FORMAT ) );
            yAxis.setStandardTickUnits( tickUnits );
            yAxis.setAutoTickUnitSelection( true );
        }
        else if ( mode == BSBottomPlotMode.PROBABILITY_DENSITY ) {
            // Views
            setRealVisible( false );
            setImaginaryVisible( false );
            setMagnitudeVisible( false );
            setPhaseVisible( false );
            setProbabilityDensityVisible( true );
            // Y-axis
            yAxis.setLabel( _probabilityDensityLabel );
            yAxis.setRange( BSConstants.PROBABILITY_DENSITY_RANGE );
            TickUnits tickUnits = new TickUnits();
            tickUnits.add( new NumberTickUnit( BSConstants.PROBABILITY_DENSITY_TICK_SPACING, BSConstants.PROBABILITY_DENSITY_TICK_FORMAT ) );
            yAxis.setStandardTickUnits( tickUnits );
            yAxis.setAutoTickUnitSelection( true );
        }
        else if ( mode == BSBottomPlotMode.PROBABILITY_DENSITY || mode == BSBottomPlotMode.AVERAGE_PROBABILITY_DENSITY ) {
            // Views
            setRealVisible( false );
            setImaginaryVisible( false );
            setMagnitudeVisible( false );
            setPhaseVisible( false );
            setProbabilityDensityVisible( true );
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
        updateAllSeries();
    }
    
    public void setRealVisible( boolean visible ) {
        getRenderer( _realIndex ).setSeriesVisible( new Boolean( visible ) );
        updateAllSeries();
    }
    
    protected boolean isRealVisible() {
        return getRenderer( _realIndex ).getSeriesVisible().booleanValue();
    }
    
    public void setImaginaryVisible( boolean visible ) {
        getRenderer( _imaginaryIndex ).setSeriesVisible( new Boolean( visible ) );
        updateAllSeries();
    }
    
    protected boolean isImaginaryVisible() {
        return getRenderer( _imaginaryIndex ).getSeriesVisible().booleanValue();
    }
    
    public void setMagnitudeVisible( boolean visible ) {
        getRenderer( _magnitudeIndex ).setSeriesVisible( new Boolean( visible ) );
        updateAllSeries();
    }
    
    protected boolean isMagnitudeVisible() {
        return getRenderer( _magnitudeIndex ).getSeriesVisible().booleanValue();
    }
    
    public void setPhaseVisible( boolean visible ) {
        getRenderer( _phaseIndex ).setSeriesVisible( new Boolean( visible ) );
        updateAllSeries();
    }
    
    protected boolean isPhaseVisible() {
        return getRenderer( _phaseIndex ).getSeriesVisible().booleanValue();
    }
    
    public void setProbabilityDensityVisible( boolean visible ) {
        getRenderer( _probabilityDensityIndex ).setSeriesVisible( new Boolean( visible ) );
        updateAllSeries();
    }
    
    protected boolean isProbabilityDensityVisible() {
        return getRenderer( _probabilityDensityIndex ).getSeriesVisible().booleanValue();
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
     * @param o
     * @param arg
     */
    public void update( Observable o, Object arg ) {
        if ( o == _model ) {
            if ( arg == BSModel.PROPERTY_HILITED_EIGENSTATE_INDEX ) {
                updateHiliteSeries();
            }
            else if ( arg == BSModel.PROPERTY_PARTICLE ) {
                // ignore, we'll be notified that the potential changed
            }
            else if ( arg == BSModel.PROPERTY_SUPERPOSITION_COEFFICIENTS_COUNT ) {
                // ignore, the cache doesn't need to change
            }
            else if ( arg == null ||
                      arg == BSModel.PROPERTY_POTENTIAL ||
                      arg == BSModel.PROPERTY_SUPERPOSITION_COEFFICIENTS_VALUES  ) { 
                updateCache();
                updateTimeDependentSeries( _t );
            }
        }
    }
    
    //----------------------------------------------------------------------------
    // Dataset updaters
    //----------------------------------------------------------------------------
    
    /*
     * Updates all data series.
     */
    private void updateAllSeries() {
        if ( _model != null ) {
            updateTimeDependentSeries( _t );
            updateHiliteSeries();
        }
    }
    
    /*
     * Updates the series for the hilited eigenstate,
     * which is a time-independent wave function solution.
     * <p>
     * How we display the hilited eigenstate's wave function is dependent
     * on what other views are visible.  If probability density is visible,
     * we display the hilite as probability density. If either real or 
     * imaginary part is visible, we display the real hililite.  
     * If magnitude or phase is the only thing visible, we display
     * magnitude.  If none of the other views is visible, we display
     * nothing.
     */
    private void updateHiliteSeries() {
        _hiliteSeries.setNotify( false );
        _hiliteSeries.clear();
        
        if ( _model != null ) {
            final int hiliteIndex = _model.getHilitedEigenstateIndex();
            if ( hiliteIndex != BSEigenstate.INDEX_UNDEFINED ) {
                
                /* 
                 * NOTE - 
                 * The cache only contains wave functions for eigenstates with non-zero
                 * superposition coefficients. It's possible for the user to hilite any of the
                 * eigenstates, so we compute their wave functions as they're hilited.  A possible
                 * optimization would be to look for the hilited eigenstate's wave function in
                 * the cache, and compute it (and cache it) if we don't find it in the cache.
                 * In practice, there is no perceptible performance difference.
                 */
                BSEigenstate[] eigenstates = _model.getEigenstates();
                BSAbstractPotential potential = _model.getPotential();
                final double minX = getDomainAxis().getLowerBound();
                final double maxX = getDomainAxis().getUpperBound();
                Point2D[] points = potential.getWaveFunctionPoints( eigenstates[hiliteIndex], minX, maxX );
                
                for ( int i = 0; i < points.length; i++ ) {
                    if ( isProbabilityDensityVisible() ) {
                        double x = points[i].getX();
                        double y = Math.pow( points[i].getY(), 2 );
                        _hiliteSeries.add( x, y );
                    }
                    else if ( isRealVisible() || isImaginaryVisible() ) {
                        double x = points[i].getX();
                        double y = points[i].getY();
                        _hiliteSeries.add( x, y );
                    }
                    else if ( isMagnitudeVisible() || isPhaseVisible() ) {
                        double x = points[i].getX();
                        double y = Math.abs( points[i].getY() );
                        _hiliteSeries.add( x, y );
                    }
                }
            }
        }
        
        _hiliteSeries.setNotify( true );
    }
    
    /*
     * Updates the cache of wave function data.
     */
    private void updateCache() {
        final double minPosition = getDomainAxis().getLowerBound();
        final double maxPosition = getDomainAxis().getUpperBound();
        _cache.update( _model, minPosition, maxPosition );
    }
    
    /*
     * Updates the series that display the time-dependent superposition state.
     * This is the sum of all wave functions for eigenstates.
     */
    private void updateTimeDependentSeries( final double t ) {
        setTimeDependentSeriesNotify( false );
        clearTimeDependentSeries();
        if ( _cache.getSize() > 0 ) {
            Complex[] psiSum = computeTimeDependentWaveFunction( t );
            if ( psiSum != null ) {

                final double minX = _cache.getMinPosition();
                final double maxX = _cache.getMaxPosition();
                Point2D[] points = _cache.getItem( 0 ).getPoints(); // all wave functions should share the same x values
                assert ( psiSum.length == points.length );

                for ( int i = 0; i < psiSum.length; i++ ) {
                    final double x = points[i].getX();
                    if ( x >= minX && x <= maxX ) {
                        Complex y = psiSum[i];
                        if ( isRealVisible() ) {
                            _realSeries.add( x, y.getReal() );
                        }
                        if ( isImaginaryVisible() ) {
                            _imaginarySeries.add( x, y.getImaginary() );
                        }
                        if ( isMagnitudeVisible() ) {
                            _magnitudeSeries.add( x, y.getAbs() );
                        }
                        if ( isPhaseVisible() ) {
                            _phaseSeries.add( x, y.getAbs() );
                            _phaseSeries.add( x, y.getPhase() );
                        }
                        if ( isProbabilityDensityVisible() ) {
                            _probabilityDensitySeries.add( x, y.getAbs() * y.getAbs() );
                        }
                    }
                }
            }
            setTimeDependentSeriesNotify( true );
        }
    }
    
    /*
     * Converts each eigenstate's time-independent wave function to a time-dependent
     * wave function, and sums all of the time-dependent wave functions according 
     * to the superposition coefficients.
     * 
     * @param t
     */
    private Complex[] computeTimeDependentWaveFunction( final double t ) {
   
        if ( _cache.getSize() > 0 ) {
            
            BSEigenstate[] eigenstates = _model.getEigenstates();
            BSSuperpositionCoefficients coefficients = _model.getSuperpositionCoefficients();
            assert( eigenstates.length == coefficients.getNumberOfCoefficients() );
            
            final int numberOfPoints = _cache.getNumberOfPointsInEachWaveFunction();
            
            // Reuse previous psiSum array if numberOfPoints hasn't changed
            if ( _psiSum == null || numberOfPoints != _psiSum.length ) {
                _psiSum = new MutableComplex[numberOfPoints];
                for ( int i = 0; i < _psiSum.length; i++ ) {
                    _psiSum[i] = new MutableComplex();
                }
            }
            else {
                for ( int i = 0; i < _psiSum.length; i++ ) {
                    _psiSum[i].setValue( 0 );
                }
            }
            
            MutableComplex y = new MutableComplex();
            MutableComplex Ft = new MutableComplex();
            
            // Iterate over the cache...
            final int cacheSize = _cache.getSize();
            for ( int i = 0; i < cacheSize; i++ ) {
                
                Item item = _cache.getItem( i );

                // Eigenstate index
                final int eigenstateIndex = item.getEigenstateIndex();

                // Data points
                Point2D[] points = item.getPoints(); // Ps(x)
                
                // F(t) = e^(-i * E * t / hbar), where E = eigenstate energy 
                final double E = eigenstates[eigenstateIndex].getEnergy();
                Ft.setValue( Complex.I );
                Ft.multiply( -1 * E * t / BSConstants.HBAR );
                Ft.exp();

                // Superposition coefficient 
                final double C = coefficients.getCoefficient( eigenstateIndex );
                
                // Normalization coefficient
                final double A = item.getNormalizationCoefficient();
                
                for ( int j = 0; j < points.length; j++ ) {
                    // Psi(x,t) = Psi(x) * F(t) * C * A
                    y.setValue( points[j].getY() );
                    y.multiply( Ft );
                    if ( C != 1 ) {
                        y.multiply( C );
                    }
                    if ( A != 1 ) {
                        y.multiply( A );
                    }
                    _psiSum[j].add( y );
                }
            }
            
            // Scale the wave function sum
            final double S = _cache.getSumScalingCoefficient();
            if ( S != 1 ) {
                for ( int j = 0; j < _psiSum.length; j++ ) {
                    _psiSum[j].multiply( S );
                }
            }
        }
        return _psiSum;
    }
    
    /*
     * Clears the data from all time-dependent series.
     */
    private void clearTimeDependentSeries() {
        _realSeries.clear();
        _imaginarySeries.clear();
        _magnitudeSeries.clear();
        _phaseSeries.clear();
        _probabilityDensitySeries.clear();
    }
    
    /*
     * Changes notification for all time-dependent series.
     * <p>
     * Call this method with false before adding a lot of points, so that
     * we don't get unnecessary updates.  When all points have been added,
     * call this method with true to notify listeners that the series 
     * have changed.
     * 
     * @param notify true or false
     */
    private void setTimeDependentSeriesNotify( boolean notify ) {
        _realSeries.setNotify( notify );
        _imaginarySeries.setNotify( notify );
        _magnitudeSeries.setNotify( notify );
        _phaseSeries.setNotify( notify );
        _probabilityDensitySeries.setNotify( notify );
    }

    //----------------------------------------------------------------------------
    // ClockListener implementation
    //----------------------------------------------------------------------------

    public void simulationTimeChanged( ClockEvent clockEvent ) {
        _t = clockEvent.getSimulationTime();
        updateTimeDependentSeries( _t );
    }

    public void simulationTimeReset( ClockEvent clockEvent ) {}
    
    public void clockTicked( ClockEvent clockEvent ) {}

    public void clockStarted( ClockEvent clockEvent ) {}

    public void clockPaused( ClockEvent clockEvent ) {}
}
