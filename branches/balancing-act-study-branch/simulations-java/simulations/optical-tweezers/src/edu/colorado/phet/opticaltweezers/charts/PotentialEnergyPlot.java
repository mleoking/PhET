// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.opticaltweezers.charts;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;

import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.data.Range;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.opticaltweezers.OTResources;

/**
 * PotentialEnergyPlot is the plot for the potential energy chart.
 * The data is normalized.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PotentialEnergyPlot extends XYPlot {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final String SERIES_KEY = "potentialEnergy";
    private static final Color BACKGROUND_COLOR = new Color( 0, 0, 0, 0 ); // transparent
    private static final Color PLOT_COLOR = new Color( 178, 25, 205 ); // purple
    private static final Stroke PLOT_STROKE = new BasicStroke( 2f );
    private static final Font AXIS_LABEL_FONT = new PhetFont( Font.PLAIN, 14 );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private XYSeries _series;
    private NumberAxis _xAxis, _yAxis;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     */
    public PotentialEnergyPlot() {
        super();
        
        // axis labels
        String positonLabel = OTResources.getString( "axis.position" );
        String potentialLabel = OTResources.getString( "axis.potentialEnergy" );
        
        // Series & dataset
        _series = new XYSeries( SERIES_KEY, false /* autoSort */);
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries( _series );
        setDataset( dataset );
        
        // Renderer
        StandardXYItemRenderer renderer = new StandardXYItemRenderer();
        renderer.setDrawSeriesLineAsPath( true );
        renderer.setPaint( PLOT_COLOR );
        renderer.setStroke( PLOT_STROKE );
        setRenderer( renderer );
        
        _xAxis = new NumberAxis();
        _xAxis.setLabel( positonLabel );
        _xAxis.setLabelFont( AXIS_LABEL_FONT );
        _xAxis.setTickLabelsVisible( false );
        _xAxis.setTickMarksVisible( false );
        setDomainAxis( _xAxis );
        
        _yAxis = new NumberAxis();
        _yAxis.setLabel( potentialLabel );
        _yAxis.setLabelFont( AXIS_LABEL_FONT );
        _yAxis.setTickLabelsVisible( false );
        _yAxis.setTickMarksVisible( false );
        setRangeAxis( _yAxis );
        
        setRangeAxisLocation( AxisLocation.BOTTOM_OR_LEFT );
        setBackgroundPaint( BACKGROUND_COLOR );
        setDomainGridlinesVisible( false );
        setRangeGridlinesVisible( false );
        
        // Defaults
        setPositionRange( 0, 1 );
        setPotentialEnergyRange( 0, 1 );
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    /**
     * Sets the position (x axis) range.
     * 
     * @param minPosition
     * @param maxPosition
     */
    public void setPositionRange( double minPosition, double maxPosition ) {
        _xAxis.setRange( minPosition, maxPosition );
    }
    
    /**
     * Gets the position (x axis) range.
     * 
     * @return Range
     */
    public Range getPositionRange() {
        return _xAxis.getRange();
    }
    
    /**
     * Sets the potential energy (y axis) range.
     * 
     * @param minPotentialEnergy
     * @param maxPotentialEnergy
     */
    public void setPotentialEnergyRange( double minPotentialEnergy, double maxPotentialEnergy ) {
        _yAxis.setRange( minPotentialEnergy, maxPotentialEnergy );
    }
    
    /**
     * Gets the potential energy (y axis) range.
     * 
     * @return Range
     */
    public Range getPotentialEnergyRange() {
        return _yAxis.getRange();
    }
    
    //----------------------------------------------------------------------------
    // Data management
    //----------------------------------------------------------------------------
    
    /**
     * Adds a data point.
     * 
     * @param position
     * @param potentialEnergy
     */
    public void addData( double position, double potentialEnergy ) {
        _series.add( position, potentialEnergy );
    }
    
    /**
     * Clears all data.
     */
    public void clear() {
        _series.clear();
    }
}
