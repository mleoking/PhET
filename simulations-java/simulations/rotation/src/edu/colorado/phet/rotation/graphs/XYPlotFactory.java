package edu.colorado.phet.rotation.graphs;

import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * User: Sam Reid
 * Date: Jan 1, 2007
 * Time: 3:00:22 PM
 * Copyright (c) Jan 1, 2007 by Sam Reid
 */

public class XYPlotFactory {
    public XYPlot createXYPlot( String title, String rangeAxis ) {
        return createXYPlot( title, rangeAxis, Double.NaN );
    }

    public XYPlot createXYPlot( String title, String rangeAxis, double range ) {
        final XYPlot subplot1 = new XYPlot( new XYSeriesCollection( new XYSeries( title ) ), null, new NumberAxis( rangeAxis ), new StandardXYItemRenderer() );

        if( Double.isNaN( range ) ) {
        }
        else {
            NumberAxis axis = new NumberAxis();
            axis.setRange( -range, range );
            subplot1.setRangeAxis( axis );
        }

        subplot1.setRangeAxisLocation( AxisLocation.BOTTOM_OR_LEFT );
        return subplot1;
    }

}
