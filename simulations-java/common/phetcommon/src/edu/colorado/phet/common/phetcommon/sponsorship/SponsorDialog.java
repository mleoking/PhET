// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.common.phetcommon.sponsorship;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.MessageFormat;
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
 - i18n
 - display after KSU Credits window closes
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

    //TODO read these from phetcommon-strings
    // i18n
    private static final String SPONSORED_BY = "Sponsored by";
    private static final String SINCE_DATE = "(since {0})";

    // Constructor is private, creation and display is handled by static methods.
    private SponsorDialog( PhetApplicationConfig config, Frame parent ) {
        super( parent );
        setResizable( false );

        // properties
        Properties properties = config.getResourceLoader().getProperties( PROPERTIES_FILE_NAME );
        String imageResourceName = properties.getProperty( config.getFlavor() + ".image" );
        String url = properties.getProperty( config.getFlavor() + ".url" );
        String since = properties.getProperty( config.getFlavor() + ".since" );

        // layout components, some of which are optional
        GridPanel mainPanel = new GridPanel();
        int xMargin = 40;
        int yMargin = 20;
        mainPanel.setBorder( new CompoundBorder( new LineBorder( Color.BLACK, 1 ), new EmptyBorder( yMargin, xMargin, yMargin, xMargin ) ) );
        mainPanel.setGridX( 0 ); // vertical
        mainPanel.setAnchor( Anchor.CENTER );
        mainPanel.add( new JLabel( SPONSORED_BY ) {{
            setFont( new PhetFont( 18 ) );
        }} );
        mainPanel.add( Box.createVerticalStrut( 15 ) );
        // logo is required
        mainPanel.add( new JLabel( new ImageIcon( config.getResourceLoader().getImage( imageResourceName ) ) ) );
        // url is optional
        if ( url != null ) {
            mainPanel.add( Box.createVerticalStrut( 15 ) );
            mainPanel.add( createInteractiveHTMLPane( url, new PhetFont( 14 ) ) );
        }
        mainPanel.add( Box.createVerticalStrut( 15 ) );
        // since date is optional
        if ( since != null ) {
            mainPanel.add( new JLabel( MessageFormat.format( SINCE_DATE, since ) ) {{
                setFont( new PhetFont( 10 ) );
            }} );
        }

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
        boolean hasPropertiesFile = config.getResourceLoader().getProperties( PROPERTIES_FILE_NAME ).getProperty( config.getFlavor() + ".image" ) != null;
        boolean hasProgramArg = config.hasCommandLineArg( "-sponsorPrototype" ); //TODO delete this after prototyping
        return isFeatureEnabled && hasPropertiesFile && hasProgramArg;
    }

    private static InteractiveHTMLPane createInteractiveHTMLPane( String url, Font font ) {
        return new InteractiveHTMLPane( HTMLUtils.createStyledHTMLFromFragment( "<a href=\"http://" + url + "\" target=\"_blank\">" + url, font ) ) {{
            setOpaque( false );
        }};
    }
}
