package edu.colorado.phet.reids.admin;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import edu.colorado.phet.common.phetcommon.resources.PhetResources;
import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;
import edu.colorado.phet.reids.admin.jintellitype.JIntellitypeSupport;
import edu.colorado.phet.reids.admin.util.FileUtils;

public class TimesheetApp {
    public static Object[] columnNames = { "Start", "End", "Elapsed", "Category", "Notes", "Report" };
    private JFrame frame = new JFrame( "Timesheet App" );
    private TimesheetModel timesheetModel = new TimesheetModel();
    private ArrayList<File> recentFiles = new ArrayList<File>();
    private File currentFile;
    private String WINDOW_HEIGHT = "window.h";
    private String WINDOW_WIDTH = "window.w";
    private String WINDOW_Y = "window.y";
    private String WINDOW_X = "window.x";
    private String RECENT_FILES = "recentFiles";
    private String CURRENT_FILE = "currentFile";
    private String TARGET_HOURS = "target.hours";
    private File PREFERENCES_FILE = new File( System.getProperty( "user.home", "." ), ".timesheet/timesheet-app.properties" );
    private final JTable table;
    private SelectionModel selectionModel = new SelectionModel();
    private MutableInt targetHours = new MutableInt( 0 );
    private final StretchingModel stretchingModel = new StretchingModel();
    private final BufferedImage iconImage = new PhetResources( "timesheet" ).getImage( "x-office-calendar.png" );
    private final BufferedImage warningImage = new PhetResources( "timesheet" ).getImage( "x-office-warning.png" );
    private final BufferedImage bufferedIconImage = new BufferedImage( iconImage.getWidth(), iconImage.getHeight(), BufferedImage.TYPE_INT_RGB );
    private AffineTransform identity = new AffineTransform();

    private void updateIconImage() {
        if ( frame != null && frame.isVisible() ) {
            double fractionComplete = stretchingModel.getTimeSinceBeginningOfLastSession( timesheetModel ) / 3600.0;
            boolean timeToStretch = fractionComplete >= 1.0;

            //Clear the graphics
            Graphics2D graphics = bufferedIconImage.createGraphics();
            graphics.setPaint( Color.white );
            graphics.fillRect( 0, 0, bufferedIconImage.getWidth(), bufferedIconImage.getHeight() );

            //Draw the base icon image
            graphics.drawRenderedImage( timeToStretch ? warningImage : iconImage, identity );

            //Show the progress bar
            graphics.setPaint( Color.green );
            int height = iconImage.getHeight() / 4;
            graphics.fillRect( 0, 0, (int) ( fractionComplete * iconImage.getWidth() ), height );
            graphics.setPaint( Color.red );
            graphics.fillRect( 0, 0, 1, height );
            graphics.fillRect( iconImage.getWidth() - 1, 0, 1, height );

            //Show a red button if clocked in (like 'recording')
            int inset = 5;
            Ellipse2D.Double ellipse = new Ellipse2D.Double( inset, inset, bufferedIconImage.getWidth() - inset * 2, bufferedIconImage.getHeight() - inset * 2 );
            if ( timesheetModel.isClockedIn() ) {
                graphics.setPaint( Color.red );
                graphics.fill( ellipse );
            }

            //Show a green outline around the red button if time to stretch
            if ( fractionComplete >= 1.0 ) {
                graphics.setPaint( Color.green );
                graphics.setStroke( new BasicStroke( 1 ) );
                graphics.draw( ellipse );
            }
            graphics.dispose();
            frame.setIconImage( bufferedIconImage );
        }
    }

    public TimesheetApp() throws IOException {
        JIntellitypeSupport.init( new Runnable() {
                                      public void run() {
                                          System.out.println( "Clocking in for Work" );
                                          timesheetModel.startNewTask();
                                      }
                                  }, new Runnable() {
            public void run() {
                System.out.println( "Clocking out for Home" );
                timesheetModel.clockOut();
                try {
                    save();
                }
                catch ( IOException e1 ) {
                    JOptionPane.showMessageDialog( frame, e1.getMessage() );
                }
            }
        }
        );

        timesheetModel.addClockedInListener( new TimesheetModel.ClockedInListener() {
            public void clockedInChanged() {
                updateIconImage();
            }
        } );
        timesheetModel.addTimeListener( new TimesheetModel.TimeListener() {
            public void timeChanged() {
                updateIconImage();
            }
        } );

        final DefaultTableModel tableModel = new DefaultTableModel( new Object[][] { }, columnNames ) {
            public Class<?> getColumnClass( int columnIndex ) {
                if ( columnIndex == 0 || columnIndex == 1 ) { return Date.class; }
                if ( columnIndex == 2 ) { return Long.class; }
                if ( columnIndex == 3 ) { return String.class; }
                if ( columnIndex == 4 ) { return String.class; }
                if ( columnIndex == 5 ) { return Boolean.class; }
                return super.getColumnClass( columnIndex );
            }
        };

        timesheetModel.addItemAddedListener( new TimesheetModel.ItemAddedListener() {
            public void itemAdded( final Entry entry ) {
                tableModel.addRow( toRow( entry ) );
                final int rowCount = tableModel.getRowCount();
                entry.addTimeListener( new TimesheetModel.TimeListener() {
                    public void timeChanged() {
                        //update end and elapsed time
                        tableModel.setValueAt( entry.getEndDate(), rowCount - 1, 1 );
                        tableModel.setValueAt( entry.getElapsedSeconds(), rowCount - 1, 2 );
                    }
                } );
            }
        } );

        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventPostProcessor( new KeyEventPostProcessor() {
            public boolean postProcessKeyEvent( KeyEvent e ) {
                if ( e.isControlDown() && e.getKeyCode() == KeyEvent.VK_S ) {
                    try {
                        save();
                        return true;
                    }
                    catch ( IOException e1 ) {
                        e1.printStackTrace();
                    }
                }
                return false;
            }
        } );

        tableModel.setColumnIdentifiers( columnNames );

        table = new JTable( tableModel );
        table.getColumnModel().getColumn( 0 ).setPreferredWidth( 70 );
        table.getColumnModel().getColumn( 1 ).setPreferredWidth( 70 );
        table.getColumnModel().getColumn( 2 ).setPreferredWidth( 30 );
        table.getColumnModel().getColumn( 3 ).setPreferredWidth( 60 );
        table.getColumnModel().getColumn( 4 ).setPreferredWidth( 400 );
        table.getColumnModel().getColumn( 5 ).setPreferredWidth( 10 );
        table.setSelectionMode( ListSelectionModel.SINGLE_INTERVAL_SELECTION );
        table.setCellSelectionEnabled( false );
        table.setColumnSelectionAllowed( false );
        table.setRowSelectionAllowed( true );
        table.getSelectionModel().addListSelectionListener( new ListSelectionListener() {
            public void valueChanged( ListSelectionEvent e ) {
                selectionModel.setSelectedRows( table.getSelectedRows() );
            }
        } );
        table.setDefaultRenderer( Date.class, new DefaultTableCellRenderer() {
            protected void setValue( Object value ) {
                setText( Entry.LOAD_FORMAT.format( value ) );
            }
        } );
        table.setDefaultEditor( Date.class, new DateEditor() );
        table.setDefaultRenderer( Long.class, new DefaultTableCellRenderer() {
            protected void setValue( Object value ) {
                long v = ( (Long) value ).longValue();
                String text = Util.secondsToElapsedTimeString( v );
                super.setValue( text );
            }
        } );
        table.addKeyListener( new KeyAdapter() {
            public void keyPressed( KeyEvent e ) {
                if ( e.getKeyCode() == KeyEvent.VK_SPACE && e.isControlDown() ) {
                    System.out.println( "table.row() = " + table.getSelectedRow() + ", col = " + table.getSelectedColumn() );
                    tableModel.setValueAt( new Long( System.currentTimeMillis() ), table.getSelectedRow(), table.getSelectedColumn() );
                }
            }
        } );
        table.getModel().addTableModelListener( new TableModelListener() {
            public void tableChanged( TableModelEvent e ) {
                int row = e.getFirstRow();
                int column = e.getColumn();
                if ( row >= 0 && column >= 0 ) {
                    Object data = table.getModel().getValueAt( row, column );
                    if ( column == 0 ) { getEntry( row ).setStartTime( parseStartTime( data ) ); }
                    if ( column == 1 ) { getEntry( row ).setEndTime( parseStartTime( data ) ); }
                    if ( column == 3 ) { getEntry( row ).setCategory( data.toString() ); }
                    if ( column == 4 ) { getEntry( row ).setNotes( data.toString() ); }
                    if ( column == 5 ) { getEntry( row ).setReport( (Boolean) data ); }
                }
            }
        } );
        table.addMouseListener( new MouseAdapter() {
            @Override
            public void mouseReleased( MouseEvent e ) {
                int row = table.rowAtPoint( e.getPoint() );
                if ( e.isControlDown() ) {
                    getEntry( row ).setEndTime( Util.currentTimeSeconds() );
                }
            }
        } );
        JPanel contentPane = new JPanel();
        contentPane.setLayout( new BorderLayout() );
        final JScrollPane scrollPane = new JScrollPane( table );
        timesheetModel.addItemAddedListener( new TimesheetModel.ItemAddedListener() {
            public void itemAdded( Entry entry ) {
                SwingUtilities.invokeLater( new Runnable() {
                    public void run() {
                        scrollPane.getVerticalScrollBar().setValue( scrollPane.getVerticalScrollBar().getMaximum() );
                    }
                } );
            }
        } );
        contentPane.add( scrollPane, BorderLayout.CENTER );
        contentPane.add( new ControlPanel( timesheetModel, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                try {
                    save();
                }
                catch ( IOException e1 ) {
                    e1.printStackTrace();
                }
            }
        }, selectionModel, targetHours, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                try {
                    savePreferences();
                }
                catch ( IOException e1 ) {
                    e1.printStackTrace();
                }
            }
        }
        ), BorderLayout.SOUTH );
        frame.setContentPane( contentPane );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
//        table.setDefaultRenderer(Date.class, renderer);
        frame.addWindowListener( new WindowAdapter() {
            public void windowClosing( WindowEvent e ) {
                try {
                    exit();
                    SwingUtilities.invokeLater( new Runnable() {
                        public void run() {
                            frame.setVisible( true );
                        }
                    } );

                }
                catch ( IOException e1 ) {
                    e1.printStackTrace();
                }
            }
        } );
    }

    private void exit() throws IOException {
        savePreferences();

        //todo: save dialog, if changed
        if ( ifChangedAskToSaveOrCancel() ) {
            return;
        }
        JIntellitypeSupport.close();
        System.exit( 0 );
    }

    /**
     * return true if cancelled
     *
     * @return true if cancelled
     * @throws IOException an exception if there was a problem saving
     */
    private boolean ifChangedAskToSaveOrCancel() throws IOException {
        if ( hasUnsavedChanges() ) {
            int option = JOptionPane.showConfirmDialog( frame, "You have made unsaved changes.  Save first?" );
            if ( option == JOptionPane.OK_OPTION ) {
                save();
            }
            else if ( option == JOptionPane.CANCEL_OPTION ) {
                return true;
            }
        }
        return false;
    }

    private boolean hasUnsavedChanges() {
        return timesheetModel.isDirty();
    }

    private long parseStartTime( Object data ) {
        if ( data instanceof Date ) {
            return ( (Date) data ).getTime() / 1000;
        }
        else if ( data instanceof String ) {
            String s = data.toString();
            System.out.println( "parsing string: " + s );
            return 0;
        }
        else {
            return -1;
        }
    }

    private Entry getEntry( int row ) {
        return timesheetModel.getEntry( row );
    }

    private Object[] toRow( Entry entry ) {
        return new Object[] { entry.getStartDate(), entry.getEndDate(), entry.getElapsedSeconds(), entry.getCategory(), entry.getNotes(), entry.isReport() };
    }

    public void load() throws IOException {
        final JFileChooser jFileChooser = currentFile == null ? new JFileChooser() : new JFileChooser( currentFile.getParentFile() );
        int option = jFileChooser.showOpenDialog( frame );
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
        timesheetModel.loadCSV( currentFile );
//        addCurrentToRecent();
        frame.setTitle( "Timesheet: " + selectedFile.getName() + " [" + selectedFile.getAbsolutePath() + "]" );
        timesheetModel.setClean();
    }

    public void save() throws IOException {
        if ( table.getCellEditor() != null ) {
            table.getCellEditor().stopCellEditing();
        }
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
        int val = chooser.showSaveDialog( frame );
        if ( val == JFileChooser.APPROVE_OPTION ) {
            File file = chooser.getSelectedFile();
            if ( file.exists() ) {
                int option = JOptionPane.showConfirmDialog( frame, "File exists, overwrite?" );
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
//        addCurrentToRecent();
        currentFile.getParentFile().mkdirs();
        String s = timesheetModel.toCSV();
        FileUtils.writeString( currentFile, s );
        System.out.println( "Saved to: " + currentFile.getAbsolutePath() );
        timesheetModel.setClean();
    }

    public static void main( String[] args ) {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                try {
                    new TimesheetApp().start();
                }
                catch ( IOException e ) {
                    e.printStackTrace();
                }
            }
        } );
    }

    private void savePreferences() throws IOException {
        Properties properties = new Properties();
        properties.put( WINDOW_X, frame.getX() + "" );
        properties.put( WINDOW_Y, frame.getY() + "" );
        properties.put( WINDOW_WIDTH, frame.getWidth() + "" );
        properties.put( WINDOW_HEIGHT, frame.getHeight() + "" );

        properties.put( RECENT_FILES, getRecentFileListString() );
        properties.put( CURRENT_FILE, currentFile == null ? "null" : currentFile.getAbsolutePath() );
        properties.put( TARGET_HOURS, targetHours.getValue() + "" );

        PREFERENCES_FILE.getParentFile().mkdirs();
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

    private void loadPreferences() throws IOException {
        if ( !PREFERENCES_FILE.exists() ) {
            savePreferences();
        }
        Properties p = new Properties();
        p.load( new FileInputStream( PREFERENCES_FILE ) );
        Rectangle r = new Rectangle();
        r.x = Integer.parseInt( p.getProperty( WINDOW_X, "100" ) );
        r.y = Integer.parseInt( p.getProperty( WINDOW_Y, "100" ) );
        r.width = Integer.parseInt( p.getProperty( WINDOW_WIDTH, "800" ) );
        r.height = Integer.parseInt( p.getProperty( WINDOW_HEIGHT, "600" ) );
        targetHours.setValue( Integer.parseInt( p.getProperty( TARGET_HOURS, "176" ) ) );
        frame.setSize( r.width, r.height );
        frame.setLocation( r.x, r.y );


        String recentFiles = p.getProperty( RECENT_FILES, "" );
        System.out.println( "Loaded prefs from " + PREFERENCES_FILE.getAbsolutePath() + ", r=" + r + ", recent=" + recentFiles );
        StringTokenizer stringTokenizer = new StringTokenizer( recentFiles, "," );
        this.recentFiles.clear();
        while ( stringTokenizer.hasMoreTokens() ) {
            final File file = new File( stringTokenizer.nextToken() );
            if ( !this.recentFiles.contains( file ) ) {
                this.recentFiles.add( file );
            }
        }
//        updateMenuWithRecent();
        String currentFile = p.getProperty( CURRENT_FILE, "C:\\workingcopy\\samreid-unfuddle\\trunk\\phet-timesheet-2010.csv" );
        System.out.println( "currentFile = " + currentFile );
        if ( new File( currentFile ).exists() ) {
            load( new File( currentFile ) );
        }
    }

    private void start() throws IOException {
        frame.pack();
        new FrameSetup.CenteredWithSize( 1024, 768 ).initialize( frame );
        loadPreferences();
        frame.setVisible( true );
        updateIconImage();
    }

    public static class SelectionModel {
        private int[] selectedRows;

        public void setSelectedRows( int[] selectedRows ) {
            this.selectedRows = selectedRows;
        }

        public TimesheetModel getSelection( TimesheetModel timesheetModel ) {
            TimesheetModel selection = new TimesheetModel();
            for ( int selectedRow : selectedRows ) {
                selection.addEntry( timesheetModel.getEntry( selectedRow ) );
            }
            return selection;
        }
    }
}