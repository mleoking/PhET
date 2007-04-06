/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.charts;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.util.Observable;
import java.util.Observer;

import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.data.Range;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import edu.colorado.phet.opticaltweezers.OTConstants;
import edu.colorado.phet.opticaltweezers.OTStrings;


public class PotentialEnergyPlot extends XYPlot implements Observer {

    private static final String SERIES_KEY = "positionPotentialEnergySeries";
    private static final Color BACKGROUND_COLOR = new Color( 0, 0, 0, 0 ); // transparent
    private static final Color PLOT_COLOR = new Color( 178, 25, 205 ); // purple
    private static final Stroke PLOT_STROKE = new BasicStroke( 1f );
    private static final Color GRIDLINES_COLOR = Color.BLACK;
    private static final Font AXIS_LABEL_FONT = new Font( OTConstants.DEFAULT_FONT_NAME, Font.PLAIN, 14 );
    private static final Stroke AXIS_STROKE = new BasicStroke( 1f );
    private static final Color AXIS_COLOR = Color.BLACK;
    
    private XYSeries _series;
    private NumberAxis _xAxis, _yAxis;
    
    public PotentialEnergyPlot() {
        super();
        
        // axis labels
        String positonLabel = OTStrings.POSITION_AXIS;
        String potentialLabel = OTStrings.POTENTIAL_ENERGY_AXIS;
        
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
        _xAxis.setAxisLineStroke( AXIS_STROKE );
        _xAxis.setAxisLinePaint( AXIS_COLOR );
        
        _yAxis = new NumberAxis();
        _yAxis.setLabel( potentialLabel );
        _yAxis.setLabelFont( AXIS_LABEL_FONT );
        _yAxis.setTickLabelsVisible( false );
        _yAxis.setTickMarksVisible( false );
        _yAxis.setAxisLineStroke( AXIS_STROKE );
        _yAxis.setAxisLinePaint( AXIS_COLOR );
        
        setRangeAxisLocation( AxisLocation.BOTTOM_OR_LEFT );
        setBackgroundPaint( BACKGROUND_COLOR );
        setDomainGridlinesVisible( false );
        setRangeGridlinesVisible( false );
        setDomainGridlinePaint( GRIDLINES_COLOR );
        setRangeGridlinePaint( GRIDLINES_COLOR );
        setDomainAxis( _xAxis );
        setRangeAxis( _yAxis );
        
        _xAxis.setRange( new Range( 0, 100 ) ); //XXX
        _yAxis.setRange( new Range( 0, 100 ) ); //XXX
    }
    
    public void setPositionRange( Range range ) {
        _xAxis.setRange( range );
    }
    
    public void setPotentialRange( Range range ) {
        _yAxis.setRange( range );
    }
    
    public void clearData() {
        _series.clear();
    }
    
    public void update( Observable o, Object arg ) {
        //XXX
    }
}
