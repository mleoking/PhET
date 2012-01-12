// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.glaciers.charts;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.Range;
import org.jfree.data.RangeType;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import edu.colorado.phet.common.phetcommon.application.PaintImmediateDialog;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ClockListener;
import edu.colorado.phet.glaciers.GlaciersStrings;
import edu.colorado.phet.glaciers.model.Glacier;
import edu.colorado.phet.glaciers.model.GlaciersClock;
import edu.colorado.phet.glaciers.util.UnitsConverter;

/**
 * GlacierLengthVersusTimeChart displays a "Glacier Length versus Time" chart.
 * The chart updates as the glacier evolves.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GlacierLengthVersusTimeChart extends PaintImmediateDialog {
    
    private static final Range MIN_LENGTH_RANGE_METRIC = new Range( 0, 3E3 ); // meters
    private static final Range MIN_LENGTH_RANGE_ENGLISH = new Range( 
            UnitsConverter.metersToFeet( MIN_LENGTH_RANGE_METRIC.getLowerBound() ), 
            UnitsConverter.metersToFeet( MIN_LENGTH_RANGE_METRIC.getUpperBound() ) ); // feet
    
    private static final int MAX_NUMBER_OF_YEARS = 1000;
    
    private final Glacier _glacier;
    private final GlaciersClock _clock;
    private final ClockListener _clockListener;
    private final XYSeries _series;
    private final NumberAxis _rangeAxis, _domainAxis;
    private boolean _englishUnits;
    
    public GlacierLengthVersusTimeChart( Frame owner, Dimension size, Glacier glacier, GlaciersClock clock, boolean englishUnits ) {
        super( owner );
        
        setSize( size );
        setResizable( false );
        
        _englishUnits = englishUnits;
        _glacier = glacier;
        
        _clock = clock;
        _clockListener = new ClockAdapter() {
            public void simulationTimeReset( ClockEvent clockEvent ) {
                _series.clear();
            }
            public void simulationTimeChanged( ClockEvent clockEvent ) {
                addSamplePoint();
            }
        };
        _clock.addClockListener( _clockListener );
        
        // series and dataset
        _series = new XYSeries( "glacierLengthVersusTime", false /* autoSort */ );
        _series.setMaximumItemCount( MAX_NUMBER_OF_YEARS );
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries( _series );
        
        // create the chart
        String xAxisLabel = GlaciersStrings.AXIS_TIME;
        String yAxisLabel = ( englishUnits ? GlaciersStrings.AXIS_GLACIER_LENGTH_ENGLISH : GlaciersStrings.AXIS_GLACIER_LENGTH_METRIC );
        JFreeChart chart = ChartFactory.createXYLineChart(
            GlaciersStrings.TITLE_GLACIER_LENGTH_VERSUS_TIME, // title
            xAxisLabel,
            yAxisLabel,
            dataset,
            PlotOrientation.VERTICAL,
            false, // legend
            false, // tooltips
            false  // urls
        );
        
        XYPlot plot = (XYPlot) chart.getPlot();
        
        _domainAxis = (NumberAxis) plot.getDomainAxis();
        _domainAxis.setStandardTickUnits( NumberAxis.createIntegerTickUnits() );
        // x-axis (time) range will be set dynamically
        
        _rangeAxis = (NumberAxis) plot.getRangeAxis();
        _rangeAxis.setAutoRange( true );
        _rangeAxis.setRangeType( RangeType.POSITIVE );
        
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setMouseZoomable( false );
        setContentPane( chartPanel );
        
        addWindowListener( new WindowAdapter() {
            // called when the close button in the dialog's window dressing is clicked
            public void windowClosing( WindowEvent e ) {
                cleanup();
            }
            // called by JDialog.dispose
            public void windowClosed( WindowEvent e ) {
                cleanup();
            }
        });
        
        updateYAxis();
        addSamplePoint();
    }
    
    private void cleanup() {
        _clock.removeClockListener( _clockListener );
    }
    
    public void setEnglishUnits( boolean englishUnits ) {
        if ( englishUnits != _englishUnits ) {
            _englishUnits = englishUnits;
            
            // change labels and ranges
            updateYAxis();
            
            // convert existing data to new units
            int itemCount = _series.getItemCount();
            for ( int i = 0; i < itemCount; i++ ) {
                XYDataItem item = (XYDataItem) _series.getDataItem( i );
                if ( _englishUnits ) {
                    item.setY( UnitsConverter.metersToFeet( item.getY().doubleValue() ) );
                }
                else {
                    item.setY( UnitsConverter.feetToMeters( item.getY().doubleValue() ) );
                }
            }
        }
    }
    
    private void addSamplePoint() {
        final double t = _clock.getSimulationTime();
        double length = _glacier.getLength();
        if ( _englishUnits ) {
            length = UnitsConverter.metersToFeet( length );
        }
        _series.add( t, length );
        double tMin = _series.getDataItem( 0 ).getX().doubleValue();
        _domainAxis.setRange( new Range( tMin, tMin + MAX_NUMBER_OF_YEARS ) );
    }
    
    private void updateYAxis() {
        if ( _englishUnits ) {
            _rangeAxis.setLabel( GlaciersStrings.AXIS_GLACIER_LENGTH_ENGLISH );
            _rangeAxis.setAutoRangeMinimumSize( MIN_LENGTH_RANGE_ENGLISH.getUpperBound() );
        }
        else {
            _rangeAxis.setLabel( GlaciersStrings.AXIS_GLACIER_LENGTH_METRIC );
            _rangeAxis.setAutoRangeMinimumSize( MIN_LENGTH_RANGE_METRIC.getUpperBound() );
        }
    }
}
