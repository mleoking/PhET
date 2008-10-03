package edu.colorado.phet.common.phetcommon.tracking;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;

import edu.colorado.phet.common.phetcommon.application.PhetAboutDialog;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.servicemanager.PhetServiceManager;
import edu.colorado.phet.common.phetcommon.view.HorizontalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.PhetLookAndFeel;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;

public class TrackingDialog extends JDialog {
    private Tracker tracker;

    private static final String INFO =
            "<html><head><style type=\"text/css\">body { font-size: @FONT_SIZE@; font-family: @FONT_FAMILY@ }</style></head>" +
            "<b>The Physics Education Technology project</b><br>" +
            "is made possible by grants that require us to report anonymous usage statistics.<br><br>" +
            "Please visit the PhET website for more information: <a href=http://phet.colorado.edu>http://phet.colorado.edu</a>" +
            "</html>";
    private JPanel contentPane = new JPanel();

    public TrackingDialog( Tracker tracker ) {
        super();
        contentPane.setLayout( new GridBagLayout() );
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridy = GridBagConstraints.RELATIVE;
        constraints.gridx = 0;
        constraints.gridwidth = 1;
        this.tracker = tracker;
        setTitle( "PhET Tracking Information" );
        setContentPane( contentPane );

        contentPane.add( createLogoPanel(), constraints );
        contentPane.add( Box.createRigidArea( new Dimension( 5, 5 ) ), constraints );
        contentPane.add( createInfoPanel(), constraints );
        contentPane.add( Box.createRigidArea( new Dimension( 20, 20 ) ), constraints );
        contentPane.add( createReportPanel(), constraints );
        pack();
        SwingUtils.centerWindowOnScreen( this );
    }

    private JComponent createReportPanel() {
        final JTextArea jt = new JTextArea( "" );
        if ( tracker.getTrackingInformation() != null ) {
            jt.setText( tracker.getTrackingInformation().toHumanReadable() );
        }
        jt.setBorder( BorderFactory.createTitledBorder( "Report" ) );
        tracker.addListener( new Tracker.Listener() {
            public void stateChanged( Tracker tracker, Tracker.State oldState, Tracker.State newState ) {
            }

            public void trackingInfoChanged( TrackingInfo trackingInformation ) {
                jt.setText( trackingInformation.toHumanReadable() );
            }

            public void trackingFailed( IOException trackingException ) {
                jt.setText( trackingException.toString() );
            }
        } );
        jt.setPreferredSize( new Dimension( 300, 300 ) );
        return jt;
    }

    private JComponent createInfoPanel() {
        final JLabel jLabel = new JLabel( tracker.getStatus() );
        tracker.addListener( new Tracker.Listener() {
            public void stateChanged( Tracker tracker, Tracker.State oldState, Tracker.State newState ) {
                jLabel.setText( tracker.getStatus() );
            }

            public void trackingInfoChanged( TrackingInfo trackingInformation ) {
            }

            public void trackingFailed( IOException trackingException ) {
            }
        } );
        return jLabel;
    }

    /*
    * Creates the panel that contains the logo and general copyright info.
    */
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

        HorizontalLayoutPanel logoPanel = new HorizontalLayoutPanel();
        logoPanel.setInsets( new Insets( 10, 10, 10, 10 ) ); // top,left,bottom,right
        logoPanel.add( logoLabel );
        logoPanel.add( copyrightLabel );

        return logoPanel;
    }

    public static void main( String[] args ) {
        Tracker testTracker = TrackingPanel.createTestTracker( args );
        TrackingDialog dialog = new TrackingDialog( testTracker );
        dialog.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
        dialog.setVisible( true );
        testTracker.applicationStarted();
    }

    private String getLocalizedString( String propertyName ) {
        return PhetCommonResources.getInstance().getLocalizedString( propertyName );
    }
}
