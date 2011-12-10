// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.common.phetcommon.sponsorship;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Properties;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.Timer;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.view.util.GridPanel;
import edu.colorado.phet.common.phetcommon.view.util.GridPanel.Anchor;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils.InteractiveHTMLPane;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;

/*
 TODO:
 - all properties are currently required, make some optional and adapt layout
 - display dialog only if required properties are present
 - i18n
 - display after KSU Credits window closes
 - support optional font sizes/styles in properties file?
 - delete -sponsorPrototype program arg for production
 */

/**
 * Dialog that displays a simulation's sponsor. See #3166.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SponsorDialog extends JDialog {

    private static final String PROPERTIES_FILE_NAME = "sponsor.properties";
    private static final int DISPLAY_TIME = 5; // seconds

    // Constructor is private, creation and display is handled by static methods.
    private SponsorDialog( PhetApplicationConfig config, Frame parent ) {
        super( parent );
        setResizable( false );
        setTitle( "Sponsor" );

        // properties file
        Properties properties = config.getResourceLoader().getProperties( PROPERTIES_FILE_NAME );

        // components
        JLabel label = new JLabel( "This simulation is sponsored by..." ) {{
            setFont( new PhetFont( 18 ) );
        }};
        JLabel name = new JLabel( properties.getProperty( config.getFlavor() + ".name" ) ) {{
            setFont( new PhetFont( Font.BOLD, 28 ) );
        }};
        JLabel logo = new JLabel( new ImageIcon( config.getResourceLoader().getImage( properties.getProperty( config.getFlavor() + ".logo" ) ) ) );
        InteractiveHTMLPane url = createInteractiveHTMLPane( properties.getProperty( config.getFlavor() + ".url" ), new PhetFont( 14 ) );
        JLabel since = new JLabel( "since " + properties.getProperty( config.getFlavor() + ".since" ) ) {{
            setFont( new PhetFont( 14 ) );
        }};

        // layout
        GridPanel nameUrlPanel = new GridPanel();
        nameUrlPanel.setGridX( 0 ); // vertical
        nameUrlPanel.setAnchor( Anchor.WEST );
        nameUrlPanel.add( name );
        nameUrlPanel.add( Box.createVerticalStrut( 6 ) );
        nameUrlPanel.add( url );
        GridPanel logoPanel = new GridPanel();
        logoPanel.setGridY( 0 ); // horizontal
        logoPanel.setAnchor( Anchor.CENTER );
        logoPanel.add( logo );
        logoPanel.add( Box.createHorizontalStrut( 10 ) );
        logoPanel.add( nameUrlPanel );
        GridPanel mainPanel = new GridPanel();
        int xMargin = 40;
        int yMargin = 20;
        mainPanel.setBorder( new CompoundBorder( new LineBorder( Color.BLACK, 1 ), new EmptyBorder( yMargin, xMargin, yMargin, xMargin ) ) );
        mainPanel.setGridX( 0 ); // vertical
        mainPanel.setAnchor( Anchor.CENTER );
        mainPanel.add( label );
        mainPanel.add( Box.createVerticalStrut( 15 ) );
        mainPanel.add( logoPanel );
        mainPanel.add( Box.createVerticalStrut( 15 ) );
        mainPanel.add( since );

        setContentPane( mainPanel );
        pack();

        // click on the dialog to make it go away
        addMouseListener( new MouseAdapter() {
            @Override
            public void mousePressed( MouseEvent event ) {
                dispose();
            }
        } );
    }

    // Show the dialog, centered in the parent frame.
    public static JDialog show( PhetApplicationConfig config, Frame parent, boolean startDisposeTimer ) {
        final JDialog dialog = new SponsorDialog( config, parent );
        SwingUtils.centerInParent( dialog );
        dialog.setVisible( true );

        if ( startDisposeTimer ) {
            // Dispose of the dialog after DISPLAY_TIME seconds. Take care to call dispose in the Swing thread.
            Timer timer = new Timer( DISPLAY_TIME * 1000, new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    dialog.dispose();
                }
            } );
            timer.setRepeats( false );
            timer.start();
        }
        return dialog;
    }

    // Should the dialog be displayed?
    public static boolean shouldShow( PhetApplicationConfig config ) {
        boolean isFeatureEnabled = config.isSponsorFeatureEnabled();
        boolean hasPropertiesFile = config.getResourceLoader().getProperties( PROPERTIES_FILE_NAME ).getProperty( config.getFlavor() + ".name" ) != null;
        boolean hasProgramArg = config.hasCommandLineArg( "-sponsorPrototype" ); //TODO delete this after prototyping
        return isFeatureEnabled && hasPropertiesFile && hasProgramArg;
    }

    private static InteractiveHTMLPane createInteractiveHTMLPane( String url, Font font ) {
        return new InteractiveHTMLPane( HTMLUtils.createStyledHTMLFromFragment( "<a href=\"http://" + url + "\" target=\"_blank\">" + url, font ) ) {{
            setOpaque( false );
        }};
    }
}
