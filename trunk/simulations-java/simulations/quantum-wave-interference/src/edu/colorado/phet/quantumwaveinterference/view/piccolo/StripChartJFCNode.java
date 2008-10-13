/*  */
package edu.colorado.phet.quantumwaveinterference.view.piccolo;

import edu.colorado.phet.common.jfreechartphet.piccolo.JFreeChartNode;
import edu.colorado.phet.quantumwaveinterference.QWIResources;
import edu.umd.cs.piccolo.PNode;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.text.SimpleDateFormat;

/**
 * User: Sam Reid
 * Date: Dec 20, 2005
 * Time: 1:18:43 AM
 */

public class StripChartJFCNode extends PNode {

    private XYSeries series;
    private JFreeChart jFreeChart;
    private JFreeChartNode jFreeChartNode;
    private boolean enabled = true;//debugging

    public StripChartJFCNode( int width, int height ) {
        XYSeriesCollection xyDataset = new XYSeriesCollection();
        series = new XYSeries( "0" );
        xyDataset.addSeries( series );
        for( int i = 0; i < 100; i++ ) {
            series.add( i, Math.sin( i / 100.0 * Math.PI * 2 ) );
        }
        jFreeChart = createChart( xyDataset );
        jFreeChartNode = new JFreeChartNode( jFreeChart );
        addChild( jFreeChartNode );
        jFreeChartNode.setBounds( 0, 0, width, height );
    }

    public static JFreeChart createChart( XYDataset dataset ) {

        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "",  // title
                QWIResources.getString( "x.axis" ),             // x-axis label
                QWIResources.getString( "y.axis" ),   // y-axis label
                dataset,            // data
                false,               // create legend?
                false,               // generate tooltips?
                false               // generate URLs?
        );

        XYPlot plot = (XYPlot)chart.getPlot();
        DateAxis axis = (DateAxis)plot.getDomainAxis();
        axis.setDateFormatOverride( new SimpleDateFormat( "ssss" ) );
        plot.getDomainAxis().setTickLabelsVisible( false );
        plot.getDomainAxis().setAutoRange( true );
        plot.getRangeAxis().setAutoRange( false );
        plot.getRangeAxis().setRange( -0.2, 0.2 );
        return chart;
    }

    public void addValue( double x, double y ) {
        if( enabled ) {
            //todo can we temporarily disable render, do both steps as batch?
            series.add( x, y );
            series.remove( 0 );
        }
    }
}
