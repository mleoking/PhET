package edu.colorado.phet.reids.admin;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Date;

import javax.swing.*;

import edu.colorado.phet.build.FileUtils;
import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;

/**
 * Created by: Sam
 * Feb 13, 2008 at 6:46:15 PM
 */
public class TimesheetApp extends JFrame {
    private TimesheetData timesheetData;

    public TimesheetApp() {
        super( "Timesheet" );
        this.timesheetData = new TimesheetData();
        timesheetData.addEntry( new TimesheetDataEntry( new Date(), new Date(), "cck", "hello" ) );
//        for ( int i = 0; i < 10 * 7 * 4 * 12; i++ ) {
        for ( int i = 0; i < 20; i++ ) {
            timesheetData.addEntry( new TimesheetDataEntry( new Date(), new Date(), "cck", "hello2" ) );
        }
        final TimesheetDataEntry dataEntry = new TimesheetDataEntry( new Date(), null, "cck", "hello 3" );
        dataEntry.setRunning( true );
        timesheetData.addEntry( dataEntry );
        JMenu fileMenu = new JMenu( "File" );
        final JMenuItem openItem = new JMenuItem( "Open" );
        openItem.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                try {
                    load();
                }
                catch( IOException e1 ) {
                    JOptionPane.showMessageDialog( TimesheetApp.this, e1.getMessage() );
                }
            }
        } );
        fileMenu.add( openItem );

        final JMenuItem saveItem = new JMenuItem( "Save" );
        saveItem.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                try {
                    save();
                }
                catch( IOException e1 ) {
                    JOptionPane.showMessageDialog( TimesheetApp.this, e1.getMessage() );
                }
            }
        } );
        fileMenu.add( saveItem );
        fileMenu.addSeparator();
        final JMenuItem exitItem = new JMenuItem( "Exit" );
        exitItem.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                exit();
            }
        } );
        fileMenu.add( exitItem );
        addWindowListener( new WindowAdapter() {
            public void windowClosing( WindowEvent e ) {
                exit();
            }
        } );
        final JMenuBar jMenuBar = new JMenuBar();
        jMenuBar.add( fileMenu );
        setJMenuBar( jMenuBar );

        setContentPane( new ContentPane( timesheetData, this ) );
    }

    public static String toString( long timeMillis ) {
        long allSeconds = timeMillis / 1000;
        long hours = allSeconds / 3600;
        long remainingSeconds = allSeconds - hours * 3600;
        long minutes = remainingSeconds / 60;
        long seconds = remainingSeconds - minutes * 60;
        assert hours * 3600 + minutes * 60 + seconds == allSeconds;
        DecimalFormat decimalFormat = new DecimalFormat( "00" );
        return decimalFormat.format( hours ) + ":" + decimalFormat.format( minutes ) + ":" + decimalFormat.format( seconds );
    }

    private void exit() {
        //save dialog
        //save prefs
        System.exit( 0 );
    }

    public static void main( String[] args ) {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                new TimesheetApp().start();
            }
        } );
    }

    private void start() {
        new FrameSetup.CenteredWithInsets( 200, 200 ).initialize( this );
        setVisible( true );
    }

    public void load() throws IOException {
        File file = new File( "C:/Users/Sam/Desktop/save.csv" );
        String str = FileUtils.loadFileAsString( file );
        timesheetData.loadCSV( str );
    }

    public void save() throws IOException {
        //TODO: save over last save file, if exists
        File file = new File( "C:/Users/Sam/Desktop/save.csv" );
        file.getParentFile().mkdirs();
        String s = timesheetData.toCSV();
        FileUtils.writeString( file, s );
    }
}
