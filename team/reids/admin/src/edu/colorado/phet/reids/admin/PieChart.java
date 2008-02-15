package edu.colorado.phet.reids.admin;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.util.SortOrder;

import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;

/**
 * Created by: Sam
 * Feb 14, 2008 at 7:01:38 AM
 */
public class PieChart extends JFrame {
    private TimesheetApp app;

    public PieChart( TimesheetApp app ) {
        this.app = app;
        setContentPane( new PieChartPanel( app ) );
        new FrameSetup.CenteredWithSize( 800, 600 ).initialize( this );
    }

    private class PieChartPanel extends JPanel {
        public PieChartPanel( TimesheetApp app ) {
            DefaultPieDataset pieDataset = new DefaultPieDataset();
            TimesheetData d = app.getTimesheetData();
            ArrayList cat = d.getCategories();
            for ( int i = 0; i < cat.size(); i++ ) {
                String s = (String) cat.get( i );
                pieDataset.setValue( s, d.getTotalTimeMillis( s ) );
            }
            pieDataset.sortByValues( SortOrder.DESCENDING );

//            pieDataset = process( pieDataset );

            JFreeChart chart = ChartFactory.createPieChart( "Pie Chart", pieDataset, true, true, true );
            PiePlot pie = (PiePlot) chart.getPlot();
            pie.setLabelFont( new Font( "Calibri", Font.BOLD, 16 ) );//todo: support for if this font is missing
            pie.setLabelGap( 0.05 );
            pie.setIgnoreZeroValues( true );
            ChartPanel chartPanel = new ChartPanel( chart );
            add( chartPanel );
        }
    }

    private DefaultPieDataset process( DefaultPieDataset pieDataset ) {
        if ( pieDataset.getKeys().size() > 20 ) {
            DefaultPieDataset d = new DefaultPieDataset();
            List list = pieDataset.getKeys();
            for ( int i = 0; i < list.size(); i++ ) {
                if ( i < 20 ) {
                    Comparable key = (Comparable) list.get( i );
                    d.setValue( key, pieDataset.getValue( key ) );
                }
                else {

                }
            }
            System.out.println( "processed" );
            return d;
        }
        else {
            return pieDataset;
        }
    }
}
