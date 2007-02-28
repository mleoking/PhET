package com.pixelzoom.test;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.*;
import javax.swing.border.TitledBorder;


/**
 * NativeLookAndFeel installs the native look and feel for host platform.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class NativeLookAndFeel {

    // Operating Systems
    private static final int OS_WINDOWS = 0;
    private static final int OS_MACINTOSH = 1;
    private static final int OS_LINUX = 3;
    private static final int OS_OTHER = 4;
    
    // Look and feel class names
    private static final LookAndFeel DEFAULT_LAF = UIManager.getLookAndFeel();
    private static final String WINDOWS_LAF_NAME = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
    private static final String MOTIF_LAF_NAME = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";

    /* Not intended for instantiation */
    private NativeLookAndFeel() {}
    
    /**
     * Installs the native look and feel for the host platform.
     */
    public static void install() {
        LookAndFeel laf = null;
        int os = getOS();
        try {
            switch ( os ) {
            case OS_WINDOWS:
                UIManager.setLookAndFeel( WINDOWS_LAF_NAME );
                break;
            case OS_MACINTOSH:
                // Macintosh uses its native laf by default
                UIManager.setLookAndFeel( DEFAULT_LAF );
                break;
            case OS_LINUX:
                // Use Motif laf for all Linux variants
                UIManager.setLookAndFeel( MOTIF_LAF_NAME );
                break;
            case OS_OTHER:
                // do nothing
                break;
            }
        }
        catch ( Exception e ) {
            e.printStackTrace();
        }
        System.out.println( "laf=" + UIManager.getLookAndFeel() );
    }
    
    /*
     * Determines the operating system by looking at the os.name Java property.
     * 
     * @return one of the OS_* constants
     */
    private static int getOS() {
        
        // Get the operating system name.
        String osName = System.getProperty( "os.name" ).toLowerCase();
        System.out.println( "os.name=" + osName );

        // Convert to one of the operating system constants.
        int os = OS_OTHER;
        if ( osName.startsWith( "windows" ) ) {
            os = OS_WINDOWS;
        }
        else if ( osName.startsWith( "mac" ) ) {
            os = OS_MACINTOSH;
        }
        else if ( osName.startsWith( "linux" ) ) {
            os = OS_LINUX;
        }
        
        return os;
    }
    
    /**
     * Test harness
     * 
     * @param args
     */
    public static void main( String[] args ) {
        // Set the native look and feel...
        NativeLookAndFeel.install();

        // some Swing components...
        JPanel panel = new JPanel();
        panel.setBorder( new TitledBorder( "title" ) );
        panel.add( new JLabel( "label" ) );
        panel.add( new JButton( "button" ) );
        panel.add( new JCheckBox( "checkbox" ) );
        panel.add( new JSlider() );
        panel.add( new JSpinner( new SpinnerNumberModel( 10, 0, 100, 1 ) ) );
        
        // Menubar...
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu( "File" );
        fileMenu.setMnemonic( KeyEvent.VK_F );
        menuBar.add( fileMenu );
        fileMenu.add( new JMenuItem( "New", KeyEvent.VK_N ) );
        fileMenu.addSeparator();
        fileMenu.add( new JMenuItem( "Open", KeyEvent.VK_O ) );
        fileMenu.add( new JMenuItem( "Close", KeyEvent.VK_C ) );
        fileMenu.add( new JMenuItem( "Save", KeyEvent.VK_S ) );
        fileMenu.add( new JMenuItem( "Save As..." ) );
        fileMenu.addSeparator();
        JMenuItem quit = new JMenuItem( "Quit", KeyEvent.VK_Q );
        quit.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                System.exit( 0 );
            }
        } );
        fileMenu.add( quit );
        
        // Frame...
        JFrame frame = new JFrame( "NativeLookAndFeel" );
        frame.setJMenuBar( menuBar );
        frame.getContentPane().add( panel );
        frame.pack();
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        frame.show();
    }
}
