package edu.colorado.phet.reids.admin;

import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.*;

public class OrderedReportFrame {
    private JFrame frame = new JFrame();

    public OrderedReportFrame( TimesheetModel selection ) {
        System.out.println( "Selection time = " + selection.getTotalTimeSeconds() );
        final Hashtable<String, Long> table = new Hashtable<String, Long>();
//        long sum1 = 0;
        for ( int i = 0; i < selection.getEntryCount(); i++ ) {
            Entry e = selection.getEntry( i );
            Long time = table.get( e.getCategory() );
            if ( time == null ) { time = 0L; }
            time = time + e.getElapsedSeconds();
            table.put( e.getCategory(), time );
//            sum1 += e.getElapsedSeconds();
        }
//        System.out.println( "sum1 = " + sum1 );
//
//        long sum2=0;
//        for ( Long v : table.values() ) {
//            sum2 += v;
//        }
//        System.out.println( "sum2 = " + sum2 );

        String text = "Total time: " + Util.secondsToElapsedTimeString( selection.getTotalTimeSeconds() ) + "\n";
        text += "Number of entries: " + selection.getEntryCount() + "\n";
        text += "Number of categories: " + table.keySet().size() + "\n";
        text += "\n";

        long totalTime = 0;
        ArrayList<String> keys = new ArrayList<String>( table.keySet() );
        for ( String key : new MonthlyReportFilter().getAllCategories() ) {
            final Long sec = table.get( key ) == null ? 0L : table.get( key );

            final String timeStr = sec == null ? "" : Util.secondsToElapsedTimeDecimal( sec );
            System.out.println( key + "\t" + timeStr );
            text += key + "\t" + timeStr + "\n";
            keys.remove( key );

            totalTime += sec;
        }

        String epilogue = "*************** Unused report, these values did not get accounted for *******************\n";
        epilogue += "Didn't handle keys: " + keys + "\n";
        for ( String key : keys ) {
            Long sec = table.get( key );
            epilogue += "key = " + key + ", value = " + Util.secondsToElapsedTimeDecimal( sec ) + "\n";
        }

        epilogue += "time in the TimesheetModel: " + Util.secondsToElapsedTimeDecimal( selection.getTotalTimeSeconds() ) + "\n";
        epilogue += "time in the outputted spreadsheet (not account for unhandled keys): " + Util.secondsToElapsedTimeDecimal( totalTime ) + "\n";

        text += "\n" + epilogue;
        System.out.println( "unused = " + epilogue );


        frame.setContentPane( new JScrollPane( new JTextArea( text ) ) );
        frame.setSize( 800, 600 );
    }

    public void setVisible( boolean b ) {
        frame.setVisible( b );
    }
}
