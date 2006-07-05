package edu.colorado.phet.common.view;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.clock.SwingClock;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.common.view.util.SwingUtils;
import edu.colorado.phet.piccolo.PiccoloModule;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

/**
 * This JPanel shows information about the application, with links to information about Licensing.
 * It needs to be cleaned up, generalized, parameterized and made extensible.
 *
 * @author Sam Reid
 */
public class GPLAboutPanel extends JPanel {
    private JDialog licenseDialog;
    private PhetApplication phetApplication;

    public GPLAboutPanel( final PhetApplication phetApplication ) {
        this.phetApplication = phetApplication;
        setLayout( new BorderLayout() );
        String javaVersion = SimStrings.get( "Common.HelpMenu.JavaVersion" ) + ": " + System.getProperty( "java.version" );
        final String msg = phetApplication.getTitle() + "\n\n" + phetApplication.getDescription() + "\n\n" + SimStrings.get( "Common.HelpMenu.VersionLabel" ) + ": " + phetApplication.getVersion() + "\n\n" + javaVersion + "\n";
        JTextArea info = new JTextArea() {
            protected void paintComponent( Graphics g ) {
                Graphics2D g2 = (Graphics2D)g;
                g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
                super.paintComponent( g );
            }
        };
        info.setBorder( BorderFactory.createBevelBorder( BevelBorder.LOWERED ) );
        info.setFont( new Font( "Lucida Sans", Font.BOLD, 14 ) );
        info.setText( msg );
        info.setEditable( false );
        add( info, BorderLayout.CENTER );

        try {
            JLabel icon = new JLabel( new ImageIcon( ImageLoader.loadBufferedImage( "images/Phet-Flatirons-logo-3-small.gif" ) ) );
            add( icon, BorderLayout.NORTH );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }

        JButton button = new JButton( "License" );
        button.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                showLicenseDialog();
            }
        } );
        add( button, BorderLayout.SOUTH );
    }

    private void showLicenseDialog() {
        if( licenseDialog == null ) {
            licenseDialog = new JDialog( phetApplication.getPhetFrame() );
            licenseDialog.setContentPane( createLicensePanel() );
            licenseDialog.pack();
            SwingUtils.centerWindowOnScreen( licenseDialog );
        }
        licenseDialog.show();
    }

    private static String[] read( String resourceFileName ) throws IOException {
        URL url = Thread.currentThread().getContextClassLoader().getResource( resourceFileName );
        if( url != null ) {
            BufferedReader br = new BufferedReader( new InputStreamReader( url.openStream() ) );
            ArrayList lines = new ArrayList();
            String line = br.readLine();
            while( line != null ) {
                lines.add( line );
                line = br.readLine();
            }
            return (String[])lines.toArray( new String[0] );
        }
        else {
            System.out.println( "Bad URL reported in " + resourceFileName );
            throw new RuntimeException( "Bad URL for: " + resourceFileName );
        }
    }

    private JComponent createLicensePanel() {
        StringBuffer sb = new StringBuffer();
        try {
            String[]doc = read( "gpl.txt" );
            for( int i = 0; i < doc.length; i++ ) {
                String s = doc[i];
                sb.append( s ).append( System.getProperty( "line.separator" ) );
            }
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        JPanel panel = new JPanel();
        panel.setLayout( new BorderLayout() );

        JScrollPane jScrollPane = new JScrollPane( new JTextArea( sb.toString() ) );
        jScrollPane.setPreferredSize( new Dimension( 600, 400 ) );
        panel.add( jScrollPane, BorderLayout.SOUTH );

        String text = "This program and all its components are licensed under the GNU GPL.\nThe source code is available via anonymous CVS from Sourceforge.\nPlease see http://sourceforge.net/projects/phet/";
        JTextArea north = new JTextArea( text );
        north.setFont( new Font( "Lucida Sans", Font.BOLD, 12 ) );
        north.setBorder( BorderFactory.createBevelBorder( BevelBorder.LOWERED ) );
        panel.add( north, BorderLayout.NORTH );
        north.setEditable( false );
        return panel;
    }

    /**
     * Simple tester for the GPLAboutPanel.
     *
     * @param args
     */
    public static void main( String[] args ) {
        PhetApplication ap = new PhetApplication( args, "The Moving Man", "Motion in Action", "1.00.01" );
        ap.addModule( new PiccoloModule( "module1", new SwingClock( 30, 1 ) ) );
        JDialog dialog = new JDialog();
        dialog.setContentPane( new GPLAboutPanel( ap ) );
        dialog.show();
        dialog.pack();
        SwingUtils.centerWindowOnScreen( dialog );
        dialog.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    }
}
