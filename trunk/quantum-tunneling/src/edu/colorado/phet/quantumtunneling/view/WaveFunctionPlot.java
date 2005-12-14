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
import edu.colorado.phet.quantumtunneling.model.AbstractPotentialSpace;
import edu.colorado.phet.quantumtunneling.model.AbstractSolver;
import edu.colorado.phet.quantumtunneling.model.PlaneWave;
import edu.colorado.phet.quantumtunneling.model.WaveFunctionSolution;
import edu.colorado.phet.quantumtunneling.util.Complex;


/**
 * WaveFunctionPlot is the plot that displays the incident, 
 * reflected and transmitted waveforms.
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
    
    private static final double X_STEP = 0.02; // x step between data points
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private PlaneWave _planeWave;
    
    private XYSeries _incidentRealSeries;
    private XYSeries _incidentImaginarySeries;
    private XYSeries _incidentMagnitudeSeries;
    private XYSeries _reflectedRealSeries;
    private XYSeries _reflectedImaginarySeries;
    private XYSeries _reflectedMagnitudeSeries;
    private XYSeries _probabilityDensitySeries;
    
    private boolean _viewSeparateEnabled;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public WaveFunctionPlot() {
        super();
        
        // Labels
        String waveFunctionLabel = SimStrings.get( "axis.waveFunction" );
        
        // Series
        _incidentRealSeries = new XYSeries( "incident real" );
        _incidentImaginarySeries = new XYSeries( "incident imaginary" );
        _incidentMagnitudeSeries = new XYSeries( "incident magnitude" );
        _reflectedRealSeries = new XYSeries( "reflected real" );
        _reflectedImaginarySeries = new XYSeries( "reflected imaginary" );
        _reflectedMagnitudeSeries = new XYSeries( "reflected magnitude" );
        _probabilityDensitySeries = new XYSeries( "probability density" );
        
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
        
        _planeWave = null;
        _viewSeparateEnabled = false;
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public XYSeries getProbabilityDensitySeries() {
        return _probabilityDensitySeries;
    }
    
    public void setPlaneWave( PlaneWave planeWave ) {
        if ( _planeWave != null ) {
            _planeWave.deleteObserver( this );
        }
        _planeWave = planeWave;
        _planeWave.addObserver( this );
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
    
    public void setViewSeparateEnabled( boolean enabled ) {
        _viewSeparateEnabled = enabled;
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
        if ( observable == _planeWave && _planeWave != null ) {
            updateDatasets();
        }
    }
    
    //----------------------------------------------------------------------------
    // Updaters
    //----------------------------------------------------------------------------
    
    private void updateDatasets() {
        
        clearAllSeries();
        
        AbstractSolver solver = _planeWave.getSolver();
        if ( solver != null ) {

            AbstractPotentialSpace pe = _planeWave.getPotentialEnergy();
            final int numberOfRegions = pe.getNumberOfRegions();
            final double minX = pe.getStart( 0 );
            final double maxX = pe.getEnd( numberOfRegions - 1 ) + X_STEP;
            final double t = _planeWave.getTime();

            for ( double x = minX; x < maxX; x += X_STEP ) {
                WaveFunctionSolution p = solver.solve( x, t );
                if ( p != null ) {
                    if ( _viewSeparateEnabled ) {
                        // Display the incident and reflected waves separately.
                        Complex incidentPart = p.getIncidentPart();
                        if ( incidentPart != null ) {
                            _incidentRealSeries.add( x, incidentPart.getReal() );
                            _incidentImaginarySeries.add( x, incidentPart.getImaginary() );
                            _incidentMagnitudeSeries.add( x, incidentPart.getAbs() );
                        }
                        
                        Complex reflectedPart = p.getReflectedPart();
                        if ( reflectedPart != null ) {
                            _reflectedRealSeries.add( x, reflectedPart.getReal() );
                            _reflectedImaginarySeries.add( x, reflectedPart.getImaginary() );
                            _reflectedMagnitudeSeries.add( x, reflectedPart.getAbs() );
                        }
                        
                        Complex sum = p.getSum();
                        if ( sum != null ) {
                            _probabilityDensitySeries.add( x, sum.getAbs() * sum.getAbs() );
                        }
                    }
                    else {
                        // Display the sum
                        Complex sum = p.getSum();
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
}
