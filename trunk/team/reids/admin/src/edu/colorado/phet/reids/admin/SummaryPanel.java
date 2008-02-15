package edu.colorado.phet.reids.admin;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.*;

/**
 * Created by: Sam
 * Feb 13, 2008 at 10:31:55 PM
 */
public class SummaryPanel extends JPanel {
    private JTextField summary;
    private TimesheetData data;
    private TimesheetApp app;
    private final JButton saveButton = new JButton( "Save" );

    public SummaryPanel( final TimesheetData data, final TimesheetApp app ) {
        this.app = app;
        summary = new JTextField( 10 );
        this.data = data;
        data.addListener( new TimesheetData.Adapter() {
            public void timeEntryAdded( TimesheetDataEntry e ) {
                updateTimeEntry();
            }

            public void timeChanged() {
                updateTimeEntry();
            }

            public void timeEntryRemoved( TimesheetDataEntry entry ) {
                updateTimeEntry();
            }
        } );
        updateTimeEntry();

        JButton pause = new JButton( "Stop" );
        pause.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                data.pauseAll();
            }
        } );
        add( pause );

        JButton newEntry = new JButton( "New Entry" );
        newEntry.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                data.startNewEntry();
            }
        } );
        add( newEntry );
        add( summary );
        saveButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                try {
                    app.save();
                }
                catch( IOException e1 ) {
                    JOptionPane.showMessageDialog( app, e1.getMessage() );
                }
            }
        } );
        add( saveButton );
        data.addListener( new TimesheetData.Adapter() {
            public void notifyHasChangesStateChanged() {
                updateSaveButtonEnabled();
            }
        } );

        JButton piechart = new JButton( "Pie Chart" );
        piechart.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                new PieChart( app ).show();
            }
        } );
        add( piechart );
        updateSaveButtonEnabled();
    }

    private void updateSaveButtonEnabled() {
        saveButton.setEnabled( data.hasChanges() );
    }

    private void updateTimeEntry() {
        summary.setText( TimesheetApp.toString( data.getTotalElapsedTimeMillis() ) );
    }
}
