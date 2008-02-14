package edu.colorado.phet.reids.admin;

import java.awt.*;
import java.awt.event.*;
import java.text.ParseException;
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
        final TimeField startTimeGetter = new TimeField() {
            public Date getTime() {
                return entry.getStartTime();
            }

            public void setTime( Date time ) {
                entry.setStartTime( time );
            }

            public boolean isEndTime() {
                return false;
            }
        };
        startTimeField = new TimeTextField( entry, startTimeGetter );


        add( startTimeField );
        final TimeField endTimeField = new TimeField() {
            public Date getTime() {
                return entry.getEndTime();
            }

            public void setTime( Date time ) {
                entry.setEndTime( time );
            }

            public boolean isEndTime() {
                return true;
            }
        };
        this.endTimeField = new TimeTextField( entry, endTimeField );
        add( this.endTimeField );
        elapsedTimeField = new JTextField( "???", 10 );
        entry.addListener( new TimesheetDataEntry.Adapter() {
            public void timeChanged() {
                updateElapsedTimeField();
            }
        } );
        add( elapsedTimeField );
        final JTextField categoryField = new JTextField( entry.getCategory(), 10 );
        categoryField.addKeyListener( new KeyAdapter() {

        } );
        entry.addListener( new TimesheetDataEntry.Adapter() {
            public void categoryChanged() {
                if ( !categoryField.isFocusOwner() ) {
                    categoryField.setText( entry.getCategory() );
                }
            }
        } );
        categoryField.addKeyListener( new KeyAdapter() {
            public void keyReleased( KeyEvent e ) {
                if ( e.getKeyCode() == KeyEvent.VK_ENTER ) {
                    categoryField.setText( entry.getCategory() );
                }
                else {
                    entry.setCategory( categoryField.getText() );
                }
            }
        } );
        add( categoryField );
        final JTextField notesField = new JTextField( entry.getNotes(), 30 );
        entry.addListener( new TimesheetDataEntry.Adapter() {
            public void notesChanged() {
                if ( !notesField.isFocusOwner() ) {
                    notesField.setText( entry.getNotes() );
                }
            }
        } );
        notesField.addKeyListener( new KeyAdapter() {
            public void keyReleased( KeyEvent e ) {
                if ( e.getKeyCode() == KeyEvent.VK_ENTER ) {
                    notesField.setText( entry.getNotes() );
                }
                else {
                    entry.setNotes( notesField.getText() );
                }
            }
        } );

        add( notesField );

        startTimeField.getTextField().addKeyListener( new MyKeyListener( startTimeField.getTextField(), startTimeGetter ) );
        this.endTimeField.getTextField().addKeyListener( new MyKeyListener( this.endTimeField.getTextField(), endTimeField ) );

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
            field = new JTextField( 12 );
            add( field );
            entry.addListener( new TimesheetDataEntry.Listener() {
                public void timeChanged() {
                    update();
                }

                public void runningChanged() {
                    updateRunningChanged();
                }

                public void categoryChanged() {
                }

                public void notesChanged() {
                }
            } );
            update();
            updateRunningChanged();
            field.addFocusListener( new FocusListener() {
                public void focusGained( FocusEvent e ) {
                }

                public void focusLost( FocusEvent e ) {
                    update();
                }
            } );
            field.addKeyListener( new KeyListener() {
                public void keyTyped( KeyEvent e ) {
                }

                public void keyPressed( KeyEvent e ) {
                }

                public void keyReleased( KeyEvent e ) {
                    if ( e.getKeyCode() == KeyEvent.VK_ENTER ) {
                        forceUpdate();
                    }
                }
            } );
        }

        public JTextField getTextField() {
            return field;
        }

        private void updateRunningChanged() {
            setBorder( entry.isRunning() && timeField.isEndTime() ? BorderFactory.createLineBorder( Color.red ) : null );
        }

        private void update() {
            if ( !field.isFocusOwner() ) {//don't change text while user is editing
                forceUpdate();
            }
        }

        private void forceUpdate() {
            field.setText( timeField.getTime() == null ? "null" : TimesheetDataEntry.DEFAULT_DATE_FORMAT.format( timeField.getTime() ) );
        }

        public String getText() {
            return field.getText();
        }
    }

    public static interface TimeField {
        Date getTime();

        void setTime( Date time );

        boolean isEndTime();
    }

    private class MyKeyListener implements KeyListener {
        private JTextField textField;
        private TimeField timeField;

        public MyKeyListener( JTextField textField, TimeField timeField ) {
            this.textField = textField;
            this.timeField = timeField;
        }

        public void keyTyped( KeyEvent e ) {
        }

        public void keyPressed( KeyEvent e ) {
        }

        public void keyReleased( KeyEvent e ) {
            try {
                Date d = TimesheetDataEntry.DEFAULT_DATE_FORMAT.parse( textField.getText() );
                timeField.setTime( d );
            }
            catch( ParseException e1 ) {
                e1.printStackTrace();
            }
        }
    }
}
