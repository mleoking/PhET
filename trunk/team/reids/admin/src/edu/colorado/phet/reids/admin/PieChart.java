package edu.colorado.phet.reids.admin;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.*;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.util.SortOrder;

import edu.colorado.phet.reids.admin.util.FrameSetup;


/**
 * Created by: Sam
 * Feb 14, 2008 at 7:01:38 AM
 */
public class PieChart extends JFrame {

    public PieChart( TimesheetData timesheetData ) {
        setContentPane( new PieChartPanel( timesheetData ) );
        new FrameSetup.CenteredWithSize( 1024, 768 ).initialize( this );
    }

    private class PieChartPanel extends JPanel {
        public PieChartPanel( TimesheetData d ) {
            Date minTime = d.getMinTime();
            Date maxTime = d.getMaxTime();
            setLayout( new BorderLayout() );
            DefaultPieDataset pieDataset = new DefaultPieDataset();
            String[] cat = d.getCategories();
            for ( int i = 0; i < cat.length; i++ ) {
                String s = cat[i];
                pieDataset.setValue( s, d.getTotalTimeMillis( s ) );
            }
            pieDataset.sortByValues( SortOrder.DESCENDING );

//            pieDataset = process( pieDataset );
            SimpleDateFormat date = new SimpleDateFormat( "M/dd/yyyy" );
            JFreeChart chart = ChartFactory.createPieChart( "Work History " + date.format( minTime ) + " - " + date.format( maxTime ) + "", pieDataset, false, true, false );
            PiePlot pie = (PiePlot) chart.getPlot();
//            pie.set
            pie.setLabelFont( new Font( "Calibri", Font.BOLD, 16 ) );//todo: support for if this font is missing
            pie.setLabelGap( 0.05 );
            pie.setIgnoreZeroValues( true );
            ChartPanel chartPanel = new ChartPanel( chart );
            add( chartPanel, BorderLayout.CENTER );
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
