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

import java.util.Observable;
import java.util.Observer;

import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.jfreechart.FastPathRenderer;
import edu.colorado.phet.quantumtunneling.QTConstants;
import edu.colorado.phet.quantumtunneling.color.QTColorScheme;
import edu.colorado.phet.quantumtunneling.enums.IRView;
import edu.colorado.phet.quantumtunneling.model.*;
import edu.colorado.phet.quantumtunneling.util.Complex;
import edu.colorado.phet.quantumtunneling.util.LightweightComplex;


/**
 * WaveFunctionPlot is the plot that displays the incident, 
 * reflected and transmitted waveforms.  It also manages the
 * probability density dataset, which is displayed by 
 * ProbabilityDensityPlot.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class WaveFunctionPlot extends QTXYPlot implements Observer {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // We provide sorted data, so turn off series autosort to improve performance.
    private static final boolean AUTO_SORT = false;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private AbstractWave _wave;
    private double _dx;
    
    /**
     * NOTE: 
     * The "incident" series do double duty as "sum" series.
     * The "reflected" series are used only when the incident and reflected 
     * components are being viewed separately.
     */
    private XYSeries _incidentRealSeries;
    private XYSeries _incidentImaginarySeries;
    private XYSeries _incidentMagnitudeSeries;
    private XYSeries _reflectedRealSeries;
    private XYSeries _reflectedImaginarySeries;
    private XYSeries _reflectedMagnitudeSeries;
    private XYSeries _phaseSeries;
    private XYSeries _probabilityDensitySeries;
    
    private int _incidentRealIndex;
    private int _incidentImaginaryIndex;
    private int _incidentMagnitudeIndex;
    private int _reflectedRealIndex;
    private int _reflectedImaginaryIndex;
    private int _reflectedMagnitudeIndex;
    private int _phaseIndex;
    
    private IRView _irView;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    XYItemRenderer _incidentRealRenderer;
    
    public WaveFunctionPlot() {
        super();
        
        // Labels
        String waveFunctionLabel = SimStrings.get( "axis.waveFunction" );
        
        int index = 0;
        
        // Incident Real
        {
            _incidentRealIndex = index++;
            _incidentRealSeries = new XYSeries( "incident real", AUTO_SORT );
            XYSeriesCollection dataset = new XYSeriesCollection();
            dataset.addSeries( _incidentRealSeries );
            setDataset( _incidentRealIndex, dataset );
            XYItemRenderer renderer = new FastPathRenderer();
            _incidentRealRenderer = renderer;
            renderer.setPaint( QTConstants.COLOR_SCHEME.getRealColor() );
            renderer.setStroke( QTConstants.REAL_STROKE );
            setRenderer( _incidentRealIndex, renderer );
        }
         
        // Incident Imaginary
        {
            _incidentImaginaryIndex = index++;
            _incidentImaginarySeries = new XYSeries( "incident imaginary", AUTO_SORT );
            XYSeriesCollection dataset = new XYSeriesCollection();
            dataset.addSeries( _incidentImaginarySeries );
            setDataset( _incidentImaginaryIndex, dataset );
            XYItemRenderer renderer = new FastPathRenderer();
            renderer.setPaint( QTConstants.COLOR_SCHEME.getImaginaryColor() );
            renderer.setStroke( QTConstants.IMAGINARY_STROKE );
            setRenderer( _incidentImaginaryIndex, renderer );
        }
        
        // Incident Magnitude
        {
            _incidentMagnitudeIndex = index++;
            _incidentMagnitudeSeries = new XYSeries( "incident magnitude", AUTO_SORT );
            XYSeriesCollection dataset = new XYSeriesCollection();
            dataset.addSeries( _incidentMagnitudeSeries );
            setDataset( _incidentMagnitudeIndex, dataset );
            XYItemRenderer renderer = new FastPathRenderer();
            renderer.setPaint( QTConstants.COLOR_SCHEME.getMagnitudeColor() );
            renderer.setStroke( QTConstants.MAGNITUDE_STROKE );
            setRenderer( _incidentMagnitudeIndex, renderer );
        }
        
        // Reflected Real
        {
            _reflectedRealIndex = index++;
            _reflectedRealSeries = new XYSeries( "reflected real", AUTO_SORT );
            XYSeriesCollection dataset = new XYSeriesCollection();
            dataset.addSeries( _reflectedRealSeries );
            setDataset( _reflectedRealIndex, dataset );
            XYItemRenderer renderer = new FastPathRenderer();
            renderer.setPaint( QTConstants.COLOR_SCHEME.getRealColor() );
            renderer.setStroke( QTConstants.REAL_STROKE );
            setRenderer( _reflectedRealIndex, renderer );
        }
            
        // Reflected Imaginary
        {
            _reflectedImaginaryIndex = index++;
            _reflectedImaginarySeries = new XYSeries( "reflected imaginary", AUTO_SORT );
            XYSeriesCollection dataset = new XYSeriesCollection();
            dataset.addSeries( _reflectedImaginarySeries );
            setDataset( _reflectedImaginaryIndex, dataset );
            XYItemRenderer renderer = new FastPathRenderer();
            renderer.setPaint( QTConstants.COLOR_SCHEME.getImaginaryColor() );
            renderer.setStroke( QTConstants.IMAGINARY_STROKE );
            setRenderer( _reflectedImaginaryIndex, renderer );
        }
        
        // Reflected Magnitude
        {
            _reflectedMagnitudeIndex = index++;
            _reflectedMagnitudeSeries = new XYSeries( "reflected magnitude", AUTO_SORT );
            XYSeriesCollection dataset = new XYSeriesCollection();
            dataset.addSeries( _reflectedMagnitudeSeries );
            setDataset( _reflectedMagnitudeIndex, dataset );
            XYItemRenderer renderer = new FastPathRenderer();
            renderer.setPaint( QTConstants.COLOR_SCHEME.getMagnitudeColor() );
            renderer.setStroke( QTConstants.MAGNITUDE_STROKE );
            setRenderer( _reflectedMagnitudeIndex, renderer );
        }
        
        // Phase (sum only)
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
            _probabilityDensitySeries = new XYSeries( "probability density", AUTO_SORT );
            // display of this series is handled by ProbabilityDensityPlot
        }
        
        // X (domain) axis 
        {
            PositionAxis xAxis = new PositionAxis();
            setDomainAxis( xAxis );
            setDomainGridlinesVisible( QTConstants.SHOW_VERTICAL_GRIDLINES );
            setDomainGridlinePaint( QTConstants.COLOR_SCHEME.getGridlineColor() );
        }
        
        // Y (range) axis
        {
            NumberAxis yAxis = new NumberAxis( waveFunctionLabel );
            setRangeAxis( yAxis );
            setRangeAxisLocation( AxisLocation.BOTTOM_OR_LEFT );
            setRangeGridlinesVisible( QTConstants.SHOW_HORIZONTAL_GRIDLINES );
            setRangeGridlinePaint( QTConstants.COLOR_SCHEME.getGridlineColor() );
            yAxis.setLabelFont( QTConstants.AXIS_LABEL_FONT );
            yAxis.setRange( QTConstants.DEFAULT_WAVE_FUNCTION_RANGE );
            yAxis.setTickLabelPaint( QTConstants.COLOR_SCHEME.getTickColor() );
            yAxis.setTickMarkPaint( QTConstants.COLOR_SCHEME.getTickColor() );
        }
        
        setBackgroundPaint( QTConstants.COLOR_SCHEME.getChartColor() );
        
        _wave = null;
        _irView = IRView.SUM;
        _dx = 1;
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
   
    /**
     * Sets the color scheme for this plot.
     * 
     * @param scheme
     */ 
    public void setColorScheme( QTColorScheme scheme ) {
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
        getRenderer( _incidentRealIndex ).setPaint( scheme.getRealColor() );
        getRenderer( _incidentImaginaryIndex ).setPaint( scheme.getImaginaryColor() );
        getRenderer( _incidentMagnitudeIndex ).setPaint( scheme.getMagnitudeColor() );
        getRenderer( _reflectedRealIndex ).setPaint( scheme.getRealColor() );
        getRenderer( _reflectedImaginaryIndex ).setPaint( scheme.getImaginaryColor() );
        getRenderer( _reflectedMagnitudeIndex ).setPaint( scheme.getMagnitudeColor() );
    }
    
    /**
     * Sets dx, the position delta between sample points in model coordinates.
     * 
     * @param dx
     */
    public void setDx( double dx ) {
        if ( dx <= 0 ) {
            throw new IllegalArgumentException( "dx must be > 0: " + dx );
        }
        _dx = dx;
        updateDatasets();
    }
    
    /**
     * Gets the probability density series.
     * This series is managed here, but displayed by ProbabilityDensityPlot.
     * 
     * @return
     */
    public XYSeries getProbabilityDensitySeries() {
        return _probabilityDensitySeries;
    }
    
    /**
     * Sets the wave that the plot will display.
     * 
     * @param wave
     */
    public void setWave( AbstractWave wave ) {
        if ( _wave != null ) {
            _wave.deleteObserver( this );
        }
        _wave = wave;
        _wave.addObserver( this );
        updateDatasets();
    }
    
    /**
     * Sets visibility of the "real" data series.
     * 
     * @param visible
     */
    public void setRealVisible( boolean visible ) {
        getRenderer( _incidentRealIndex ).setSeriesVisible( new Boolean( visible ) );
        getRenderer( _reflectedRealIndex ).setSeriesVisible( new Boolean( visible ) );
    }
    
    /**
     * Sets visibility of the "imaginary" data series.
     * 
     * @param visible
     */
    public void setImaginaryVisible( boolean visible ) {
        getRenderer( _incidentImaginaryIndex ).setSeriesVisible( new Boolean( visible ) );
        getRenderer( _reflectedImaginaryIndex ).setSeriesVisible( new Boolean( visible ) );
    }
    
    /**
     * Sets visibility of the "magnitude" data series.
     * 
     * @param visible
     */
    public void setMagnitudeVisible( boolean visible ) {
        getRenderer( _incidentMagnitudeIndex ).setSeriesVisible( new Boolean( visible ) );
        getRenderer( _reflectedMagnitudeIndex ).setSeriesVisible( new Boolean( visible ) );
    }
    
    /**
     * Sets visibility of the "phase" data series.
     * 
     * @param visible
     */
    public void setPhaseVisible( boolean visible ) {
        getRenderer( _phaseIndex ).setSeriesVisible( new Boolean( visible ) );
    }
    
    /**
     * Determines whether incident and reflected wave components are
     * viewed separately or as a sum.
     * 
     * @param irView
     */
    public void setIRView( IRView irView ) {
        _irView = irView;
        updateDatasets();
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
        if ( _wave != null && observable == _wave ) {
            updateDatasets();
        }
    }
    
    //----------------------------------------------------------------------------
    // Updaters
    //----------------------------------------------------------------------------
    
    /*
     * Calls the proper update method based on the type of wave.
     * Unfortunately the update process is quite different for 
     * plane wave and wave packets.
     */
    private void updateDatasets() {
        if ( _wave != null && _wave.isInitialized() ) {
            setSeriesNotify( false );
            if ( _wave instanceof PlaneWave ) {
                updateDataSet( (PlaneWave) _wave );
            }
            else if ( _wave instanceof WavePacket ) {
                updateDataSet( (WavePacket) _wave );
            }
            setSeriesNotify( true );
        }
    }
    
    /*
     * Updates the data sets using a PlaneWave.
     * <p>
     * The wave function solution for a PlaneWave is not dependent
     * on any previous steps, so we can ask for the solution for
     * any (x,t) pair.  The solution also provides separate 
     * incident and reflected components.
     * 
     * @param PlaneWave
     */
    private void updateDataSet( PlaneWave planeWave ) {
        
        clearAllSeries();
        
        AbstractPotential pe = planeWave.getPotentialEnergy();
        final int numberOfRegions = pe.getNumberOfRegions();
        final double minX = pe.getStart( 0 );
        final double maxX = pe.getEnd( numberOfRegions - 1 ) + _dx;

        for ( double x = minX; x < maxX; x += _dx ) {
            WaveFunctionSolution solution = planeWave.solveWaveFunction( x );
            if ( solution != null ) {
                if ( _irView == IRView.SEPARATE ) {
                    // Display the incident and reflected waves separately.
                    Complex incidentPart = solution.getIncidentPart();
                    if ( incidentPart != null ) {
                        _incidentRealSeries.add( x, incidentPart.getReal() );
                        _incidentImaginarySeries.add( x, incidentPart.getImaginary() );
                        _incidentMagnitudeSeries.add( x, incidentPart.getAbs() );
                    }

                    Complex reflectedPart = solution.getReflectedPart();
                    if ( reflectedPart != null ) {
                        _reflectedRealSeries.add( x, reflectedPart.getReal() );
                        _reflectedImaginarySeries.add( x, reflectedPart.getImaginary() );
                        _reflectedMagnitudeSeries.add( x, reflectedPart.getAbs() );
                    }

                    Complex sum = solution.getSum();
                    if ( sum != null ) {
                        _probabilityDensitySeries.add( x, sum.getAbs() * sum.getAbs() );
                    }
                }
                else {
                    // Display the sum
                    Complex sum = solution.getSum();
                    if ( sum != null ) {
                        // Use the incident series to display the sum...
                        _incidentRealSeries.add( x, sum.getReal() );
                        _incidentImaginarySeries.add( x, sum.getImaginary() );
                        _incidentMagnitudeSeries.add( x, sum.getAbs() );
                        _phaseSeries.add( x, sum.getAbs() );
                        _phaseSeries.add( x, sum.getPhase() );
                        _probabilityDensitySeries.add( x, sum.getAbs() * sum.getAbs() );
                    }
                }
            }
        }
    }
    
    /*
     * Updates the data sets using a WavePacket.
     * <p>
     * The wave function solution for a WavePacket is dependent on
     * previous steps -- it uses a propogation algorithm.  We can't
     * ask for the solution for specific (x,t) pairs.  We can only
     * ask for the complete collection of sample points that were 
     * calculated by the propogation algorithm.
     * <p>
     * The solution also does NOT provide separate incident and reflected 
     * components. We are provided with the sum only, so the reflected
     * data series will be empty.
     * 
     * @param wavePacket
     */
    private void updateDataSet( WavePacket wavePacket ) {
        
        clearAllSeries();
        
        // x coordinates...
        double[] positions = wavePacket.getPositionValues();
        // y coordinates...
        LightweightComplex[] psi = wavePacket.getWaveFunctionValues();
        
        final int numberOfPoints = positions.length;
        for ( int i = 0; i < numberOfPoints; i++ ) {
            final double position = positions[i];
            // Add only points that are in the visible position range...
            if ( QTConstants.POSITION_RANGE.contains( position ) ) {
                LightweightComplex energy = psi[i];
                _incidentRealSeries.add( position, energy.getReal() );
                _incidentImaginarySeries.add( position, energy.getImaginary() );
                _incidentMagnitudeSeries.add( position, energy.getAbs() );
                _phaseSeries.add( position, energy.getAbs() );
                _phaseSeries.add( position, energy.getPhase() );
                _probabilityDensitySeries.add( position, energy.getAbs() * energy.getAbs() );
            }
        }
    }
    
    /*
     * Clears the data from all series.
     */
    private void clearAllSeries() {
        _incidentRealSeries.clear();
        _incidentImaginarySeries.clear();
        _incidentMagnitudeSeries.clear();
        _reflectedRealSeries.clear();
        _reflectedImaginarySeries.clear();
        _reflectedMagnitudeSeries.clear();
        _phaseSeries.clear();
        _probabilityDensitySeries.clear();
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
        _incidentRealSeries.setNotify( notify );
        _incidentImaginarySeries.setNotify( notify );
        _incidentMagnitudeSeries.setNotify( notify );
        _reflectedRealSeries.setNotify( notify );
        _reflectedImaginarySeries.setNotify( notify );
        _reflectedMagnitudeSeries.setNotify( notify );
        _phaseSeries.setNotify( notify );
        _probabilityDensitySeries.setNotify( notify );
    }
}
