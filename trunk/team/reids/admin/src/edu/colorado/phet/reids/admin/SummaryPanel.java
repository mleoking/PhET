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
    private JButton saveButton;
    private JButton pause;
    private JButton delete;
    private JButton insert;

    public SummaryPanel( final TimesheetData data, final TimesheetApp app ) {
        this.app = app;
        summary = new JTextField( 10 );
        this.data = data;
        data.addListener( new TimesheetData.Adapter() {
            public void timeEntryAppended( TimesheetDataEntry e ) {
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

        pause = new JButton( "Stop" );
        pause.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                data.pauseAll();
            }
        } );
        data.addListener( new TimesheetData.Adapter() {
            public void timeEntryRunningChanged() {
                updateStopButtonEnabled();
            }
        } );
        updateStopButtonEnabled();
        add( pause );

        JButton newEntry = new JButton( "Start New" );
        newEntry.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                data.startNewEntry( "" );
            }
        } );
        add( newEntry );

        JButton continueCat = new JButton( "Continue Category" );
        continueCat.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                data.startNewEntry( data.getEntry( data.getNumEntries() - 1 ).getCategory() );
            }
        } );
        add( continueCat );


        add( summary );

        saveButton = new JButton( "Save" );
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
                TimesheetData data = app.getTimesheetData().getDefaultSelection();
                new PieChart( data ).show();
            }
        } );
        add( piechart );
        updateSaveButtonEnabled();

        delete = new JButton( "Delete" );
        delete.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                TimesheetDataEntry[] selected = data.getSelectedEntries();
                data.removeEntries( selected );
            }
        } );
        data.addListener( new TimesheetData.Adapter() {
            public void selectionChanged() {
                updateDeleteButtonEnabled();
            }
        } );
        updateDeleteButtonEnabled();
        add( delete );


        insert = new JButton( "Insert" );
        insert.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                data.insertNewEntry();
            }
        } );
        data.addListener( new TimesheetData.Adapter() {
            public void selectionChanged() {
                updateInsertButtonEnabled();
            }
        } );
        updateInsertButtonEnabled();
        add( insert );

        add( new WeeklyReadoutPanel( "platform-issues", data ) {
            protected TimesheetData selectData( TimesheetData data ) {
                return data.getEntriesForCategory( "platform-issues" );
            }
        } );
        add( new WeeklyReadoutPanel( "all work", data ) {
            protected TimesheetData selectData( TimesheetData data ) {
                return data;
            }
        } );
    }

    private void updateInsertButtonEnabled() {
        insert.setEnabled( data.getSelectedEntries().length > 0 );
    }

    private void updateDeleteButtonEnabled() {
        delete.setEnabled( data.getSelectedEntries().length > 0 );
    }

    private void updateStopButtonEnabled() {
        pause.setEnabled( data.isRunning() );
    }

    private void updateSaveButtonEnabled() {
        saveButton.setEnabled( data.hasChanges() );
    }

    private void updateTimeEntry() {
        summary.setText( TimesheetApp.toString( data.getTotalElapsedTimeMillis() ) );
    }
}
