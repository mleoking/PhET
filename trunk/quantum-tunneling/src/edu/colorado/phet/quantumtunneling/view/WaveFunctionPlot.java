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
import edu.colorado.phet.quantumtunneling.model.TotalEnergy;
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
    
    private static final int INCIDENT_SERIES_INDEX = 0;
    private static final int REFLECTED_SERIES_INDEX = 1;
    
    private static final double X_STEP = 0.02; // x step between data points
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private PlaneWave _planeWave;
    private XYSeries _incidentSeries;
    private XYSeries _reflectedSeries;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public WaveFunctionPlot() {
        super();
        
        // Labels
        String waveFunctionLabel = SimStrings.get( "axis.waveFunction" );
        
        // Series
        _incidentSeries = new XYSeries( "incident series" );
        _reflectedSeries = new XYSeries( "reflected series" );
        
        // Dataset
        XYSeriesCollection data = new XYSeriesCollection();
        data.addSeries( _incidentSeries );
        data.addSeries( _reflectedSeries );
        
        // Renderer
        XYItemRenderer renderer = new StandardXYItemRenderer();
        renderer.setSeriesPaint( INCIDENT_SERIES_INDEX, QTConstants.INCIDENT_WAVE_COLOR );
        renderer.setSeriesStroke( INCIDENT_SERIES_INDEX, QTConstants.INCIDENT_WAVE_STROKE );
        renderer.setSeriesPaint( REFLECTED_SERIES_INDEX, QTConstants.REFLECTED_WAVE_COLOR );
        renderer.setSeriesStroke( REFLECTED_SERIES_INDEX, QTConstants.REFLECTED_WAVE_STROKE );
        
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
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public void setPlaneWave( PlaneWave planeWave ) {
        if ( _planeWave != null ) {
            _planeWave.deleteObserver( this );
        }
        _planeWave = planeWave;
        _planeWave.addObserver( this );
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
        
        _incidentSeries.clear(); 
        
        AbstractSolver solver = _planeWave.getSolver();
        if ( solver != null ) {

            AbstractPotentialSpace pe = _planeWave.getPotentialEnergy();
            final int numberOfRegions = pe.getNumberOfRegions();
            final double minX = pe.getStart( 0 );
            final double maxX = pe.getEnd( numberOfRegions - 1 ) + X_STEP;
            final double t = _planeWave.getTime();

            for ( double x = minX; x < maxX; x += X_STEP ) {
                Complex c = solver.solve( x, t );
                if ( c != null ) {
                    _incidentSeries.add( x, c.getReal() );
                }
            }
        }
    }
}
