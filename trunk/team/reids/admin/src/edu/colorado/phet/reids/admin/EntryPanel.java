package edu.colorado.phet.reids.admin;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Date;

import javax.swing.*;

/**
 * Created by: Sam
 * Feb 13, 2008 at 10:32:31 PM
 */
public class EntryPanel extends JPanel {
    private TimesheetDataEntry entry;
    private JTextField elapsedTimeField;
    private TimeTextField startTimeField;
    private TimeTextField endTimeField;

    public EntryPanel( final TimesheetData data, final TimesheetDataEntry entry ) {
        this.entry = entry;
        startTimeField = new TimeTextField( entry, new TimeField() {
            public Date getTime() {
                return entry.getStartTime();
            }

            public void setTime( Date time ) {
                entry.setStartTime( time );
            }

            public boolean isEndTime() {
                return false;
            }
        } );
        startTimeField.getTextField().addMouseListener( new MouseAdapter() {
            public void mousePressed( MouseEvent e ) {
                entry.setStartTime( new Date() );
                data.stopAllEntries();
                entry.setRunning( true );
                entry.setEndTime( new Date() );
            }
        } );

        add( startTimeField );
        endTimeField = new TimeTextField( entry, new TimeField() {
            public Date getTime() {
                return entry.getEndTime();
            }

            public void setTime( Date time ) {
                entry.setEndTime( time );
            }

            public boolean isEndTime() {
                return true;
            }
        } );
        add( endTimeField );
        elapsedTimeField = new JTextField( "???", 10 );
        entry.addListener( new TimesheetDataEntry.Listener() {
            public void changed() {
                updateElapsedTimeField();
            }

            public void runningChanged() {
            }
        } );
        add( elapsedTimeField );
        add( new JTextField( entry.getCategory(), 10 ) );
        add( new JTextField( entry.getNotes(), 30 ) );
        updateElapsedTimeField();
    }

    private void updateElapsedTimeField() {
        if ( entry.isTimeEntrySet() ) {
            elapsedTimeField.setText( TimesheetApp.toString( entry.getElapsedTimeMillis() ) );
        }
        else {
            elapsedTimeField.setText( "???" );
        }
    }

    public TimesheetDataEntry getTimesheetDataEntry() {
        return entry;
    }

    public static class TimeTextField extends JPanel {
        private JTextField field;
        private TimesheetDataEntry entry;
        private TimeField timeField;

        public TimeTextField( final TimesheetDataEntry entry, final TimeField timeGetter ) {
            this.entry = entry;
            this.timeField = timeGetter;
            field = new JTextField( 20 );
            add( field );
            entry.addListener( new TimesheetDataEntry.Listener() {
                public void changed() {
                    update();
                }

                public void runningChanged() {
                    updateRunningChanged();
                }
            } );
            update();
            updateRunningChanged();
        }

        public JTextField getTextField() {
            return field;
        }

        private void updateRunningChanged() {
            setBorder( entry.isRunning() && timeField.isEndTime() ? BorderFactory.createLineBorder( Color.red ) : null );
        }

        private void update() {
            field.setText( timeField.getTime() + "" );
        }

    }

    public static interface TimeField {
        Date getTime();

        void setTime( Date time );

        boolean isEndTime();
    }
}
