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
import org.jfree.chart.axis.TickUnits;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.quantumtunneling.QTConstants;
import edu.colorado.phet.quantumtunneling.enum.IRView;
import edu.colorado.phet.quantumtunneling.model.*;
import edu.colorado.phet.quantumtunneling.util.Complex;


/**
 * WaveFunctionPlot is the plot that displays the incident, 
 * reflected and transmitted waveforms.  It also manages the
 * probability density dataset, which is displayed by 
 * ProbabilityDensityPlot.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class WaveFunctionPlot extends XYPlot implements Observer {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // Indicies are determined by the order that renderers are added to this XYPlot
    private static final int INCIDENT_REAL_SERIES_INDEX = 0;  // front-most
    private static final int INCIDENT_IMAGINARY_SERIES_INDEX = 1;
    private static final int INCIDENT_MAGNITUDE_SERIES_INDEX = 2;
    private static final int REFLECTED_REAL_SERIES_INDEX = 3;
    private static final int REFLECTED_IMAGINARY_SERIES_INDEX = 4;
    private static final int REFLECTED_MAGNITUDE_SERIES_INDEX = 5;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private AbstractWave _wave;
    private double _dx;
    
    private XYSeries _incidentRealSeries;
    private XYSeries _incidentImaginarySeries;
    private XYSeries _incidentMagnitudeSeries;
    private XYSeries _reflectedRealSeries;
    private XYSeries _reflectedImaginarySeries;
    private XYSeries _reflectedMagnitudeSeries;
    private XYSeries _probabilityDensitySeries;
    
    private IRView _irView;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public WaveFunctionPlot() {
        super();
        
        // Labels
        String waveFunctionLabel = SimStrings.get( "axis.waveFunction" );
        
        // Series
        final boolean autoSort = false;
        _incidentRealSeries = new XYSeries( "incident real", autoSort );
        _incidentImaginarySeries = new XYSeries( "incident imaginary", autoSort );
        _incidentMagnitudeSeries = new XYSeries( "incident magnitude", autoSort );
        _reflectedRealSeries = new XYSeries( "reflected real", autoSort );
        _reflectedImaginarySeries = new XYSeries( "reflected imaginary", autoSort );
        _reflectedMagnitudeSeries = new XYSeries( "reflected magnitude", autoSort );
        _probabilityDensitySeries = new XYSeries( "probability density", autoSort );
        
        // Dataset
        XYSeriesCollection data = new XYSeriesCollection();
        data.addSeries( _incidentRealSeries );
        data.addSeries( _incidentImaginarySeries );
        data.addSeries( _incidentMagnitudeSeries );
        data.addSeries( _reflectedRealSeries );
        data.addSeries( _reflectedImaginarySeries );
        data.addSeries( _reflectedMagnitudeSeries );
        // do not add _probabilityDensitySeries, it is displayed by ProbabilityDensityPlot
        
        // Renderer
        XYItemRenderer renderer = new StandardXYItemRenderer();
        renderer.setSeriesPaint( INCIDENT_REAL_SERIES_INDEX, QTConstants.INCIDENT_REAL_WAVE_COLOR );
        renderer.setSeriesStroke( INCIDENT_REAL_SERIES_INDEX, QTConstants.INCIDENT_REAL_WAVE_STROKE );
        renderer.setSeriesPaint( INCIDENT_IMAGINARY_SERIES_INDEX, QTConstants.INCIDENT_IMAGINARY_WAVE_COLOR );
        renderer.setSeriesStroke( INCIDENT_IMAGINARY_SERIES_INDEX, QTConstants.INCIDENT_IMAGINARY_WAVE_STROKE );
        renderer.setSeriesPaint( INCIDENT_MAGNITUDE_SERIES_INDEX, QTConstants.INCIDENT_MAGNITUDE_WAVE_COLOR );
        renderer.setSeriesStroke( INCIDENT_MAGNITUDE_SERIES_INDEX, QTConstants.INCIDENT_MAGNITUDE_WAVE_STROKE );
        renderer.setSeriesPaint( REFLECTED_REAL_SERIES_INDEX, QTConstants.REFLECTED_REAL_WAVE_COLOR );
        renderer.setSeriesStroke( REFLECTED_REAL_SERIES_INDEX, QTConstants.REFLECTED_REAL_WAVE_STROKE );
        renderer.setSeriesPaint( REFLECTED_IMAGINARY_SERIES_INDEX, QTConstants.REFLECTED_IMAGINARY_WAVE_COLOR );
        renderer.setSeriesStroke( REFLECTED_IMAGINARY_SERIES_INDEX, QTConstants.REFLECTED_IMAGINARY_WAVE_STROKE );
        renderer.setSeriesPaint( REFLECTED_MAGNITUDE_SERIES_INDEX, QTConstants.REFLECTED_MAGNITUDE_WAVE_COLOR );
        renderer.setSeriesStroke( REFLECTED_MAGNITUDE_SERIES_INDEX, QTConstants.REFLECTED_MAGNITUDE_WAVE_STROKE );
        
        // X axis 
        PositionAxis xAxis = new PositionAxis();
        
        // Y axis
        NumberAxis yAxis = new NumberAxis( waveFunctionLabel );
        yAxis.setLabelFont( QTConstants.AXIS_LABEL_FONT );
        yAxis.setRange( QTConstants.WAVE_FUNCTION_RANGE );
        TickUnits yUnits = (TickUnits) NumberAxis.createIntegerTickUnits();
        yAxis.setStandardTickUnits( yUnits );
        yAxis.setTickLabelPaint( QTConstants.TICK_LABEL_COLOR );
        yAxis.setTickMarkPaint( QTConstants.TICK_MARK_COLOR );
        
        setRangeAxisLocation( AxisLocation.BOTTOM_OR_LEFT );
        setBackgroundPaint( QTConstants.PLOT_BACKGROUND );
        setDomainGridlinesVisible( QTConstants.SHOW_VERTICAL_GRIDLINES );
        setRangeGridlinesVisible( QTConstants.SHOW_HORIZONTAL_GRIDLINES );
        setDataset( data );
        setRenderer( renderer );
        setDomainAxis( xAxis );
        setRangeAxis( yAxis );
        
        _wave = null;
        _irView = IRView.SUM;
        _dx = 1;
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public void setDx( double dx ) {
        if ( dx <= 0 ) {
            throw new IllegalArgumentException( "dx must be > 0: " + dx );
        }
        _dx = dx;
        updateDatasets();
    }
    
    public XYSeries getProbabilityDensitySeries() {
        return _probabilityDensitySeries;
    }
    
    public void setWave( AbstractWave planeWave ) {
        if ( _wave != null ) {
            _wave.deleteObserver( this );
        }
        _wave = planeWave;
        _wave.addObserver( this );
        updateDatasets();
    }
    
    public void setRealVisible( boolean visible ) {
        getRenderer().setSeriesVisible( INCIDENT_REAL_SERIES_INDEX, new Boolean( visible ) );
        getRenderer().setSeriesVisible( REFLECTED_REAL_SERIES_INDEX, new Boolean( visible ) );
    }
    
    public void setImaginaryVisible( boolean visible ) {
        getRenderer().setSeriesVisible( INCIDENT_IMAGINARY_SERIES_INDEX, new Boolean( visible ) );
        getRenderer().setSeriesVisible( REFLECTED_IMAGINARY_SERIES_INDEX, new Boolean( visible ) );
    }
    
    public void setMagnitudeVisible( boolean visible ) {
        getRenderer().setSeriesVisible( INCIDENT_MAGNITUDE_SERIES_INDEX, new Boolean( visible ) );
        getRenderer().setSeriesVisible( REFLECTED_MAGNITUDE_SERIES_INDEX, new Boolean( visible ) );
    }
    
    public void setPhaseVisible( boolean visible ) {
        //XXX
    }
    
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
        if ( _wave != null && _wave.isInitialized() && observable == _wave ) {
            updateDatasets();
        }
    }
    
    //----------------------------------------------------------------------------
    // Updaters
    //----------------------------------------------------------------------------
    
    private void updateDatasets() {
        setSeriesNotify( false );
        if ( _wave instanceof PlaneWave ) {
            updateDataSet( (PlaneWave) _wave );
        }
        else if ( _wave instanceof WavePacket ) {
            updateDataSet( (WavePacket) _wave );
        }
        setSeriesNotify( true );
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
                        _incidentRealSeries.add( x, sum.getReal() );
                        _incidentImaginarySeries.add( x, sum.getImaginary() );
                        _incidentMagnitudeSeries.add( x, sum.getAbs() );
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
        double[] positions = wavePacket.getSolver().getPositions();
        // y coordinates...
        Complex[] psi = wavePacket.getSolver().getEnergies();
        
        final int numberOfPoints = positions.length;
        for ( int i = 0; i < numberOfPoints; i++ ) {
            final double position = positions[i];
            Complex energy = psi[i];
            _incidentRealSeries.add( position, energy.getReal() );
            _incidentImaginarySeries.add( position, energy.getImaginary() );
            _incidentMagnitudeSeries.add( position, energy.getAbs() );
            _probabilityDensitySeries.add( position, energy.getAbs() * energy.getAbs() );
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
        _probabilityDensitySeries.setNotify( notify );
    }
}
