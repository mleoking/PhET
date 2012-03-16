package com.pixelzoom.util;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * GUI-based utility for converting between dates and millisonds since Epoch.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DateToMillis extends JFrame {
    
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat( "MM/dd/yyyy hh:mm:ss" );
    
    private Calendar calendar;
    private IntSpinner yearSpinner;
    private IntSpinner monthSpinner;
    private IntSpinner daySpinner;
    private IntSpinner hourSpinner;
    private IntSpinner minuteSpinner;
    private IntSpinner secondSpinner;
    private JTextField longTextField;
    private JLabel dateLabel;
    
    public DateToMillis() {
        super( "Date <--> Long" );
        
        calendar = new GregorianCalendar();
        
        ChangeListener changeListener = new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                convertToLong();
            }
        };
        yearSpinner = new IntSpinner( 2009, 1970, 2020 );
        yearSpinner.addChangeListener( changeListener );
        monthSpinner = new IntSpinner( 1, 1, 12 );
        monthSpinner.addChangeListener( changeListener );
        daySpinner = new IntSpinner( 1, 1, 31 );
        daySpinner.addChangeListener( changeListener );
        hourSpinner = new IntSpinner( 0, 0, 23 );
        hourSpinner.addChangeListener( changeListener );
        minuteSpinner = new IntSpinner( 0, 0, 59 );
        minuteSpinner.addChangeListener( changeListener );
        secondSpinner = new IntSpinner( 0, 0, 59 );
        secondSpinner.addChangeListener( changeListener );
        JPanel datePanel = new JPanel();

        // month/day/year
        datePanel.add( monthSpinner );
        datePanel.add( new JLabel( "/" ) );
        datePanel.add( daySpinner );
        datePanel.add( new JLabel( "/" ) );
        datePanel.add( yearSpinner );
        datePanel.add( new JLabel( " " ) );
        // hour:minute:second
        datePanel.add( hourSpinner );
        datePanel.add( new JLabel( ":" ) );
        datePanel.add( minuteSpinner );
        datePanel.add( new JLabel( ":" ) );
        datePanel.add( secondSpinner );
        
        longTextField = new JTextField();
        longTextField.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                convertToDate();
            }
        } );
        JPanel longPanel = new JPanel();
        longPanel.add( longTextField );
        longPanel.add( new JLabel( "ms" ) );
        
        JPanel controlPanel = new JPanel();
        controlPanel.add( datePanel );
        controlPanel.add( new JLabel( "==" ) );
        controlPanel.add( longPanel );
        
        Box box = new Box( BoxLayout.Y_AXIS );
        box.add( controlPanel );
        dateLabel = new JLabel();
        box.add( dateLabel );
        getContentPane().add( box );
        
        convertToLong();
        pack();
        setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
    }
    
    private void convertToLong() {
        int year = yearSpinner.getInt();
        int month = monthSpinner.getInt() - 1;
        int day = daySpinner.getInt();
        int hour = hourSpinner.getInt();
        int minute = minuteSpinner.getInt();
        int second = secondSpinner.getInt();
        calendar.set( year, month, day, hour, minute, second );
        long millis = calendar.getTimeInMillis();
        longTextField.setText( String.valueOf( millis ) );
        dateLabel.setText( DATE_FORMAT.format( new Date( millis ) ) );
    }
    
    private void convertToDate() {
        try {
            long millis = new Long( longTextField.getText() ).longValue();
            calendar.setTimeInMillis( millis );
            yearSpinner.setValue( calendar.get( Calendar.YEAR ) );
            monthSpinner.setValue( calendar.get( Calendar.MONTH ) + 1 );
            daySpinner.setValue( calendar.get( Calendar.DATE ) );
            hourSpinner.setValue( calendar.get( Calendar.HOUR ) );
            minuteSpinner.setValue( calendar.get( Calendar.MINUTE ) );
            secondSpinner.setValue( calendar.get( Calendar.SECOND ) );
            dateLabel.setText( DATE_FORMAT.format( new Date( millis ) ) );
        }
        catch ( NumberFormatException e ) {
           Toolkit.getDefaultToolkit().beep();
           convertToLong();
        }
    }
    
    private static class IntSpinner extends JSpinner {
        
        public IntSpinner( int value, int min, int max ) {
            super( new SpinnerNumberModel( value, min, max, 1 ) );
            setEditor( new JSpinner.NumberEditor( this, "0" ) );
        }
        
        public void setValue( int value ) {
            setValue( new Integer( value ) );
        }
        
        public int getInt() {
            return ( (Integer) getValue() ).intValue();
        }
    }
    
    public static void main( String[] args ) {
        JFrame frame = new DateToMillis();
        Dimension frameSize = frame.getSize();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation( screenSize.width / 2 - frameSize.width / 2, screenSize.height / 2 - frameSize.height / 2 );
        frame.setVisible( true );
    }
}
