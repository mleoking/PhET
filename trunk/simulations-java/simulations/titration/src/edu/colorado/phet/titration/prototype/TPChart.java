
package edu.colorado.phet.titration.prototype;

import java.text.DecimalFormat;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.TickUnits;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;


public class TPChart extends JFreeChart {

    private static class TPDomainAxis extends NumberAxis {

        public TPDomainAxis() {
            super( "titrant volume (mL)" );
            setRange( TPConstants.TITRANT_VOLUME_RANGE.getMin(), TPConstants.TITRANT_VOLUME_RANGE.getMax() );
            // ticks every 5 units
            TickUnits tickUnits = new TickUnits();
            tickUnits.add( new NumberTickUnit( 5, new DecimalFormat( "0" ) ) );
            setStandardTickUnits( tickUnits );
            setAutoTickUnitSelection( true );
        }
    }

    private static class TPRangeAxis extends NumberAxis {

        public TPRangeAxis() {
            super( "pH" );
            setRange( TPConstants.PH_RANGE.getMin(), TPConstants.PH_RANGE.getMax() );
            // ticks every 1 unit
            TickUnits tickUnits = new TickUnits();
            tickUnits.add( new NumberTickUnit( 1, new DecimalFormat( "0" ) ) );
            setStandardTickUnits( tickUnits );
            setAutoTickUnitSelection( true );
        }
    }

    private static class TPPlot extends XYPlot {

        private final XYSeries series;

        public TPPlot() {
            super();
            // axes
            setDomainAxis( new TPDomainAxis() );
            setRangeAxis( new TPRangeAxis() );
            // series
            series = new XYSeries( "real", true /* autoSort */);
            // dataset
            XYSeriesCollection dataset = new XYSeriesCollection();
            dataset.addSeries( series );
            setDataset( dataset );
            // renderer
            StandardXYItemRenderer renderer = new StandardXYItemRenderer();
            renderer.setDrawSeriesLineAsPath( true );
            setRenderer( renderer );
        }
        
        public void addPoint( double x, double y ) {
            if ( Double.isNaN( y ) ) {
                System.out.println( "WARNING: x=" + x + " at y=" + y );
            }
            series.add( x, y );
        }
        
        public void setNotify( boolean b ) {
            series.setNotify( b );
        }
        
        public void clear() {
            series.clear();
        }
    }

    public TPChart() {
        super( "Titrant volume vs pH", new PhetFont( 18 ), new TPPlot(), false /* createLegend */ );
    }

    public void addPoint( double x, double y ) {
        ( ( TPPlot) getPlot() ).addPoint( x, y );
    }
    
    public void clear() {
        ( (TPPlot) getPlot() ).clear();
    }

}
