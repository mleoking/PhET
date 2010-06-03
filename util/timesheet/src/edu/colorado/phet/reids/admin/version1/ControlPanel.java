package edu.colorado.phet.reids.admin.version1;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.*;

/**
 * Created by: Sam
 * Apr 10, 2008 at 10:04:54 PM
 */
public class ControlPanel extends JPanel {
    private ControlPanelEntry[] entries;

    public ControlPanel( TimesheetData data, TimesheetApp app ) {
        setLayout( new GridBagLayout() );
        GridBagConstraints constraints = new GridBagConstraints( 0, GridBagConstraints.RELATIVE, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets( 1, 0, 1, 0 ), 0, 0 );
        File[] f = app.getRecentFiles();
        entries = new ControlPanelEntry[f.length];
        for ( int i = 0; i < f.length; i++ ) {
            entries[i] = new ControlPanelEntry( f[i], app );
            add( entries[i], constraints );
        }
        JButton refresh = new JButton( "Refresh" );
        refresh.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                refreshAll();
            }
        } );
        add( refresh, constraints );
        //update once per minute, since fine-grained sub-sheets aren't yet available
        Timer timer = new Timer( 1000 * 60, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                refreshAll();
            }
        } );
        timer.start();
    }

    private void refreshAll() {
        for ( int i = 0; i < entries.length; i++ ) {
            entries[i].refresh();
        }
    }

    private long getWeekTime( TimesheetData data ) {
        long currentTime = System.currentTimeMillis();
        long lastWeek = currentTime - 7 * 24 * 60 * 60 * 1000;
        TimesheetData timesheetData = data.getEntriesAfter( new Date( lastWeek ) );
        return timesheetData.getTotalTimeMillis();
    }

    public static class LabeledTextField extends JPanel {
        private JTextField textField;

        public LabeledTextField( String label, String text ) {
            add( new JLabel( label ) );
            textField = new JTextField( text );
            textField.setEditable( false );
            add( textField );
        }

        public void setText( String s ) {
            textField.setText( s );
        }
    }

    private class ControlPanelEntry extends JPanel {
        private TimesheetData timesheetData;
        private LabeledTextField totalTimeField;
        private LabeledTextField weekTimeField;
        private LabeledTextField avgWeekTime;
        private LabeledTextField dayTime;

        public ControlPanelEntry( final File file, final TimesheetApp app ) {
            setLayout( new GridBagLayout() );
            GridBagConstraints constraints = new GridBagConstraints( 0, GridBagConstraints.RELATIVE, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets( 1, 0, 0, 0 ), 0, 0 );
            JLabel entry = new JLabel( file.getName().toLowerCase().endsWith( ".csv" ) ? file.getName().substring( 0, file.getName().length() - ".csv".length() ) : file.getName() );
            add( entry, constraints );
            JButton openButton = new JButton( "Open" );
            openButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    app.loadFileWithModifiedCheckBeforeClose( file );
                }
            } );
            add( openButton, constraints );
            try {
                timesheetData = new TimesheetData( file );

                totalTimeField = new LabeledTextField( "Total Time", TimesheetApp.toString( timesheetData.getTotalTimeMillis() ) );
                add( totalTimeField, constraints );

                weekTimeField = new LabeledTextField( "Week Time", TimesheetApp.toString( getWeekTime( timesheetData ) ) );
                add( weekTimeField, constraints );

                avgWeekTime = new LabeledTextField( "Avg Weekly", TimesheetApp.toString( getAverageWeekly( timesheetData ) ) );
                add( avgWeekTime, constraints );

                dayTime = new LabeledTextField( "Today", TimesheetApp.toString( getTimeToday( timesheetData ) ) );
                add( dayTime, constraints );

                add( new JSeparator(), constraints );
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
            setBorder( BorderFactory.createLineBorder( Color.black ) );
            app.getTimesheetData().addListener( new TimesheetData.Adapter() {
                public void timeChanged() {
                    update( app, file );
                }
            } );
            update( app, file );
        }

        private void update( TimesheetApp app, File file ) {
            if ( app.getFile() != null && app.getFile().equals( file ) ) {
                refresh( app.getTimesheetData() );
                timesheetData = new TimesheetData( app.getTimesheetData() );
            }
        }

        private long getAverageWeekly( TimesheetData timesheetData ) {
            if ( timesheetData.getNumEntries() == 0 ) {
                return 0;
            }
            else {
                long startTime = timesheetData.getMinTime().getTime() - 1;//subtract 1 so that "after" call will catch this event as well

                double timeWorked = timesheetData.getEntriesAfter( new Date( startTime ) ).getTotalTimeMillis();
                double totalTime = ( System.currentTimeMillis() - startTime );
                double numWeeks = totalTime / ( 7 * 24 * 60 * 60 * 1000 );
                return (long) ( timeWorked / numWeeks );
            }
        }

        public void refresh() {
            refresh( timesheetData );
        }

        private long getTimeToday( TimesheetData timesheetData ) {
            GregorianCalendar cal = new GregorianCalendar();
            cal.set( GregorianCalendar.HOUR_OF_DAY, 0 );
            return timesheetData.getEntriesAfter( cal.getTime() ).getTotalTimeMillis();
        }

        public void refresh( TimesheetData timesheetData ) {
            totalTimeField.setText( TimesheetApp.toString( timesheetData.getTotalTimeMillis() ) );
            weekTimeField.setText( TimesheetApp.toString( getWeekTime( timesheetData ) ) );
            avgWeekTime.setText( TimesheetApp.toString( getAverageWeekly( timesheetData ) ) );
            dayTime.setText( TimesheetApp.toString( getTimeToday( timesheetData ) ) );
        }
    }
}
