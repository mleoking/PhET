package edu.colorado.phet.reids.admin.version1;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Comparator;

import javax.swing.*;

/**
 * Created by: Sam
 * Feb 20, 2008 at 10:24:14 PM
 */
public class SummaryButton extends JButton {
    private TimesheetData data;

    public SummaryButton( TimesheetData data ) {
        super( "Summarize" );
        this.data = data;
        addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                summarizeSelectedData();
            }
        } );
    }

    private void summarizeSelectedData() {
        final TimesheetData d = data.getSelectedEntriesAsDataset();
        String[] categories = d.getCategories();
        Arrays.sort( categories, new Comparator() {
            public int compare( Object o1, Object o2 ) {
                return -Double.compare( d.getTotalTimeMillis( (String) o1 ), d.getTotalTimeMillis( (String) o2 ) );
            }
        } );
        StringBuffer summary = new StringBuffer();
        long totalMS = 0;
        for ( int i = 0; i < categories.length; i++ ) {
            TimesheetData tasks = d.getEntriesForCategory( categories[i] );
            summary.append( "" + categories[i] + ": " + TimesheetApp.toString( tasks.getTotalTimeMillis() ) + "\n" );
            TimesheetData sorted = tasks.sort( new TimesheetDataEntryComparator() {
                public int compare( TimesheetDataEntry e1, TimesheetDataEntry e2 ) {
                    return -Double.compare( e1.getElapsedTimeMillis(), e2.getElapsedTimeMillis() );
                }
            } );
            for ( int k = 0; k < sorted.getNumEntries(); k++ ) {
                TimesheetDataEntry e = sorted.getEntry( k );
                summary.append( "\t" + TimesheetApp.toString( e.getElapsedTimeMillis() ) + ": " + e.getNotes() + "\n" );
                totalMS += e.getElapsedTimeMillis();
            }
        }

        summary = new StringBuffer("Total time: "+TimesheetApp.toString(totalMS)+"\n\n"+summary.toString());

        System.out.println( "summary = " + summary );

        String title = "Summary of " + d.getMinTime() + " - " + d.getMaxTime();
        String body = summary.toString();

        displayText( title, body );
    }

    public static void displayText( String title, String body ) {
        JFrame frame = new JFrame( title );
        JTextArea jt = new JTextArea( body );
        frame.setContentPane( new JScrollPane( jt ) );
        frame.pack();
        frame.setVisible( true );
    }
}
