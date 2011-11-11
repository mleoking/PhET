// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simeventprocessor;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import edu.colorado.phet.common.phetcommon.util.IProguardKeepClass;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;

/**
 * @author Sam Reid
 */
public class JavaPredef implements IProguardKeepClass {

    public static void plot( String title, String domainAxis, String rangeAxis, final XYSeries[] xySeries ) {
        XYPlot xyPlot = new XYPlot();
        final XYSeriesCollection dataset = new XYSeriesCollection() {{
            for ( XYSeries series : xySeries ) {
                addSeries( series );
            }
        }};
        xyPlot.setDataset( dataset );
        JFreeChart plot = ChartFactory.createScatterPlot( title, domainAxis, rangeAxis, dataset, PlotOrientation.VERTICAL, true, false, false );
        ChartFrame frame = new ChartFrame( title, plot );
        frame.setSize( 900, 600 );
        SwingUtils.centerWindowOnScreen( frame );
        frame.setVisible( true );
    }
}
