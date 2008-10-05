package edu.colorado.phet.common.phetcommon.preferences;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;

import edu.colorado.phet.common.phetcommon.application.PhetAboutDialog;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.servicemanager.PhetServiceManager;
import edu.colorado.phet.common.phetcommon.tracking.Tracker;
import edu.colorado.phet.common.phetcommon.tracking.TrackingInfo;
import edu.colorado.phet.common.phetcommon.view.PhetLookAndFeel;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;

public class FullTrackingInfoDialog extends JDialog {
    private Tracker tracker;

    public FullTrackingInfoDialog( Dialog owner, Tracker tracker ) {
        super( owner );
        init( tracker );
    }

    public FullTrackingInfoDialog( Frame owner, Tracker tracker ) {
        super( owner );
        init( tracker );
    }

    private void init( Tracker tracker ) {
        this.tracker = tracker;
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridy = GridBagConstraints.RELATIVE;
        constraints.gridx = 0;
        constraints.gridwidth = 1;
        getContentPane().setLayout( new GridBagLayout() );
        getContentPane().add( createLogoPanel(), constraints );
        getContentPane().add( createReportPanel(), constraints );
        pack();
    }

    private JComponent createReportPanel() {
        final JTextArea jt = new JTextArea( "" );
        if ( tracker.getTrackingInformation() != null ) {
            jt.setText( tracker.getTrackingInformation().toHumanReadable() );
        }
        tracker.addListener( new Tracker.Listener() {
            public void stateChanged( Tracker tracker, Tracker.State oldState, Tracker.State newState ) {
            }

            public void trackingInfoChanged( TrackingInfo trackingInformation ) {
                jt.setText( tracker.getTrackingInformation().toHumanReadable() );
                pack();
            }

            public void trackingFailed( IOException trackingException ) {
            }
        } );
        jt.setBorder( BorderFactory.createTitledBorder( "Report" ) );
        jt.setEditable( false );
        return jt;
    }

    private JPanel createLogoPanel() {

        BufferedImage image = PhetCommonResources.getInstance().getImage( PhetLookAndFeel.PHET_LOGO_120x50 );
        JLabel logoLabel = new JLabel( new ImageIcon( image ) );
        logoLabel.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
        logoLabel.setToolTipText( getLocalizedString( "Common.About.WebLink" ) );
        logoLabel.addMouseListener( new MouseInputAdapter() {
            public void mouseReleased( MouseEvent e ) {
                PhetServiceManager.showPhetPage();
            }
        } );

        String html = INFO;
        html = html.replaceAll( "@FONT_SIZE@", new PhetFont().getSize() + "pt" );
        html = html.replaceAll( "@FONT_FAMILY@", new PhetFont().getFamily() );
        PhetAboutDialog.HTMLPane copyrightLabel = new PhetAboutDialog.HTMLPane( html );

        VerticalLayoutPanel logoPanel = new VerticalLayoutPanel();
        logoPanel.add( logoLabel );
        logoPanel.setInsets( new Insets( 10, 10, 10, 10 ) ); // top,left,bottom,right
        logoPanel.add( copyrightLabel );

        return logoPanel;
    }

    private String getLocalizedString( String propertyName ) {
        return PhetCommonResources.getInstance().getLocalizedString( propertyName );
    }

    private static final String INFO =
            "<html><head><style type=\"text/css\">body { font-size: @FONT_SIZE@; font-family: @FONT_FAMILY@ }</style></head>" +
            "<b><a href=http://phet.colorado.edu>PhET</a></b> " +
            "is made possible by grants that require us to report anonymous usage statistics.<br>No personal or private data is sent; you can see the full report sent to PhET below.<br><br>"
            +
            "Please visit the PhET website for more information: <a href=http://phet.colorado.edu>http://phet.colorado.edu</a>" +
            "</html>";
}
