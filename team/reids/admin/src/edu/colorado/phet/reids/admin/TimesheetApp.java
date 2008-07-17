package edu.colorado.phet.reids.admin;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.imageio.ImageIO;
import javax.swing.*;

import edu.colorado.phet.reids.admin.util.FileUtils;
import edu.colorado.phet.reids.admin.util.FrameSetup;

/**
 * Created by: Sam
 * Feb 13, 2008 at 6:46:15 PM
 */
public class TimesheetApp extends JFrame {
    private TimesheetData timesheetData;
    private ArrayList recentFiles = new ArrayList();
    private File currentFile;
    private String WINDOW_HEIGHT = "window.h";
    private String WINDOW_WIDTH = "window.w";
    private String WINDOW_Y = "window.y";
    private String WINDOW_X = "window.x";
    private String RECENT_FILES = "recentFiles";
    private String CURRENT_FILE = "currentFile";
    private JMenu fileMenu = new JMenu( "File" );
    private File PREFERENCES_FILE = new File( System.getProperty( "user.home", "." ), ".timesheet/timesheet-app.properties" );

    public TimesheetApp() throws IOException {
        super( "Timesheet" );
        System.out.println( "TimesheetApp started, pref file=" + PREFERENCES_FILE.getAbsolutePath() );
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventPostProcessor( new KeyEventPostProcessor() {
            public boolean postProcessKeyEvent( KeyEvent e ) {
                if ( e.isControlDown() && e.getKeyCode() == KeyEvent.VK_S ) {
                    try {
                        save();
                        return true;
                    }
                    catch( IOException e1 ) {
                        e1.printStackTrace();
                    }
                }
                return false;
            }
        } );


        this.timesheetData = new TimesheetData();
        updateIconImage();
        timesheetData.addListener( new TimesheetData.Adapter() {
            public void timeEntryRunningChanged() {
                try {
                    updateIconImage();
                }
                catch( IOException e ) {
                    e.printStackTrace();
                }
            }
        } );

        final JMenuItem newItem = new JMenuItem( "Clear" );
        newItem.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                try {
                    if ( ifChangedAskToSaveOrCancel() ) {
                        return;
                    }
                    else {
                        timesheetData.clear();
                    }
                }
                catch( IOException e1 ) {
                    e1.printStackTrace();
                }
            }
        } );
        fileMenu.add( newItem );
        fileMenu.addSeparator();

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

        final JMenuItem saveAsItem = new JMenuItem( "Save As" );
        saveAsItem.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                try {
                    saveAs();
                }
                catch( IOException e1 ) {
                    JOptionPane.showMessageDialog( TimesheetApp.this, e1.getMessage() );
                }
            }
        } );
        fileMenu.add( saveAsItem );

        fileMenu.addSeparator();
        final JMenuItem exitItem = new JMenuItem( "Exit" );
        exitItem.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                try {
                    exit();
                }
                catch( IOException e1 ) {
                    e1.printStackTrace();
                }
            }
        } );
        fileMenu.add( exitItem );
        fileMenu.addSeparator();

        addWindowListener( new WindowAdapter() {
            public void windowClosing( WindowEvent e ) {
                try {
                    exit();
                    SwingUtilities.invokeLater( new Runnable() {
                        public void run() {
                            setVisible( true );
                        }
                    } );

                }
                catch( IOException e1 ) {
                    e1.printStackTrace();
                }
            }
        } );
        final JMenuBar jMenuBar = new JMenuBar();
        jMenuBar.add( fileMenu );
        setJMenuBar( jMenuBar );

        new FrameSetup.CenteredWithInsets( 200, 200 ).initialize( this );
        loadPreferences();
        setContentPane( new ContentPane( timesheetData, this ) );

    }

    private void updateIconImage() throws IOException {
        setIconImage( ImageIO.read( new File( "C:\\reid-not-backed-up\\phet\\svn\\trunk2\\team\\reids\\admin\\contrib\\tango\\" + ( timesheetData.isRunning() ? "x-office-running.png" : "x-office-calendar.png" ) ) ) );
    }

    public File[] getRecentFiles() {
        return (File[]) recentFiles.toArray( new File[0] );
    }

    private void loadPreferences() throws IOException {
        Properties p = new Properties();
        p.load( new FileInputStream( PREFERENCES_FILE ) );
        Rectangle r = new Rectangle();
        r.x = Integer.parseInt( p.getProperty( WINDOW_X ,"100") );
        r.y = Integer.parseInt( p.getProperty( WINDOW_Y ,"100") );
        r.width = Integer.parseInt( p.getProperty( WINDOW_WIDTH,"800" ) );
        r.height = Integer.parseInt( p.getProperty( WINDOW_HEIGHT,"600" ) );
        setSize( r.width, r.height );
        setLocation( r.x, r.y );


        String recentFiles = p.getProperty( RECENT_FILES,"" );
        System.out.println( "Loaded prefs, r=" + r + ", recent=" + recentFiles );
        StringTokenizer stringTokenizer = new StringTokenizer( recentFiles, "," );
        this.recentFiles.clear();
        while ( stringTokenizer.hasMoreTokens() ) {
            final File file = new File( stringTokenizer.nextToken() );
            if ( !this.recentFiles.contains( file ) ) {
                this.recentFiles.add( file );
            }
        }
        updateMenuWithRecent();
        String currentFile = p.getProperty( CURRENT_FILE,"" );
        if ( new File( currentFile ).exists() ) {
            load( new File( currentFile ) );
        }
    }

    public File getFile() {
        return currentFile;
    }

    public class RecentFileMenuItem extends JMenuItem {
        public RecentFileMenuItem( final String text ) {
            super( text );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    if ( new File( text ).exists() ) {
                        loadFileWithModifiedCheckBeforeClose( new File( text ) );
                    }
                }
            } );
        }

    }

    public void loadFileWithModifiedCheckBeforeClose( File file ) {
        try {
            if ( ifChangedAskToSaveOrCancel() ) {
            }
            else {
                load( file );
            }
        }
        catch( IOException e1 ) {
            e1.printStackTrace();
        }
    }

    private void updateMenuWithRecent() {
        for ( int i = 0; i < fileMenu.getPopupMenu().getComponentCount(); i++ ) {
            if ( fileMenu.getPopupMenu().getComponent( i ) instanceof RecentFileMenuItem ) {
                fileMenu.getPopupMenu().remove( i );
                i--;
            }
        }
        for ( int i = 0; i < recentFiles.size(); i++ ) {
            File file = (File) recentFiles.get( i );
            RecentFileMenuItem recentFileMenuItem = new RecentFileMenuItem( file.getAbsolutePath() );
            fileMenu.add( recentFileMenuItem, fileMenu.getComponentCount() - 1 );
        }
    }

    private void savePreferences() throws IOException {
//        System.out.println( "prefFile.getAbsolutePath() = " + prefFile.getAbsolutePath() );
        Properties properties = new Properties();
        properties.put( WINDOW_X, getX() + "" );
        properties.put( WINDOW_Y, getY() + "" );
        properties.put( WINDOW_WIDTH, getWidth() + "" );
        properties.put( WINDOW_HEIGHT, getHeight() + "" );

        properties.put( RECENT_FILES, getRecentFileListString() );
        properties.put( CURRENT_FILE, currentFile == null ? "null" : currentFile.getAbsolutePath() );

        properties.store( new FileOutputStream( PREFERENCES_FILE ), "auto-generated on " + new Date() );
        System.out.println( "Stored prefs: " + properties );
    }

    private String getRecentFileListString() {
        String s = "";
        for ( int i = 0; i < recentFiles.size(); i++ ) {
            File file = (File) recentFiles.get( i );
            s += file.getAbsolutePath();
            if ( i < recentFiles.size() - 1 ) {
                s += ",";
            }
        }
        return s;
    }

    public TimesheetData getTimesheetData() {
        return timesheetData;
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

    private void exit() throws IOException {
        //save prefs
        savePreferences();

        //todo: save dialog, if changed
        if ( ifChangedAskToSaveOrCancel() ) {
            return;
        }

        System.exit( 0 );
    }

    /**
     * return true if cancelled
     *
     * @return
     * @throws IOException
     */
    private boolean ifChangedAskToSaveOrCancel() throws IOException {
        if ( hasChanges() ) {
            int option = JOptionPane.showConfirmDialog( this, "You have made unsaved changes.  Save first?" );
            if ( option == JOptionPane.OK_OPTION ) {
                save();
            }
            else if ( option == JOptionPane.CANCEL_OPTION ) {
                return true;
            }
        }
        return false;
    }

    private boolean hasChanges() {
        return timesheetData.hasChanges();
    }

    public static void main( String[] args ) {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                try {
                    new TimesheetApp().start();
                }
                catch( IOException e ) {
                    e.printStackTrace();
                }
            }
        } );
    }

    private void start() {
        setVisible( true );
    }

    public void load() throws IOException {
        final JFileChooser jFileChooser = currentFile == null ? new JFileChooser() : new JFileChooser( currentFile.getParentFile() );
        int option = jFileChooser.showOpenDialog( this );
        if ( option == JFileChooser.APPROVE_OPTION ) {
            final File selectedFile = jFileChooser.getSelectedFile();
            load( selectedFile );
        }
        else {
            System.out.println( "TimesheetApp.load, didn't save" );
        }

    }

    private void load( File selectedFile ) throws IOException {
        currentFile = selectedFile;
        timesheetData.loadCSV( currentFile );
        addCurrentToRecent();
        setTitle( "Timesheet: " + selectedFile.getName() + " [" + selectedFile.getAbsolutePath() + "]" );
        timesheetData.clearChanges();
    }

    public void saveAs() throws IOException {
        File selected = selectSaveFile();
        if ( selected != null ) {
            save( selected );
        }
        else {

            System.out.println( "Didn't save" );
        }
    }

    public void save() throws IOException {
        //TODO: save over last save file, if exists
        File selected = currentFile;
        if ( currentFile == null ) {
            selected = selectSaveFile();
        }
        if ( selected != null ) {
            save( selected );
        }
        else {
            System.out.println( "didn't save" );
        }
    }

    private File selectSaveFile() {
        File selected = null;
        final JFileChooser chooser = currentFile == null ? new JFileChooser() : new JFileChooser( currentFile.getParentFile() );
        int val = chooser.showSaveDialog( this );
        if ( val == JFileChooser.APPROVE_OPTION ) {
            File file = chooser.getSelectedFile();
            if ( file.exists() ) {
                int option = JOptionPane.showConfirmDialog( this, "File exists, overwrite?" );
                if ( option == JOptionPane.YES_OPTION ) {
                    selected = file;
                }
            }
            else {
                selected = file;
            }
        }
        return selected;
    }

    private void save( File selected ) throws IOException {
        this.currentFile = selected;
        addCurrentToRecent();
        currentFile.getParentFile().mkdirs();
        String s = timesheetData.toCSV();
        FileUtils.writeString( currentFile, s );
        System.out.println( "Saved to: " + currentFile.getAbsolutePath() );
        timesheetData.clearChanges();
    }

    private void addCurrentToRecent() {
        if ( !recentFiles.contains( currentFile ) ) {
            recentFiles.add( currentFile );
        }
        updateMenuWithRecent();
    }
}
