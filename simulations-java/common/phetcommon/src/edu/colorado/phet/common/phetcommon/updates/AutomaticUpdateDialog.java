package edu.colorado.phet.common.phetcommon.updates;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.PhetAboutDialog;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.preferences.*;
import edu.colorado.phet.common.phetcommon.resources.PhetVersion;

public class AutomaticUpdateDialog extends UpdateResultDialog {
    private PhetApplicationConfig config;

    public AutomaticUpdateDialog( PhetApplication application, PhetVersion newVersion ) {
        this( application.getPhetFrame(), getHTML( application, newVersion ), application.getApplicationConfig(), new ApplicationConfigManualCheckForUpdates( application.getPhetFrame(), application.getApplicationConfig() ), newVersion, application.getApplicationConfig(), new DefaultUpdateTimer(), new DefaultVersionSkipper() );
    }

    private static String getHTML( PhetApplication application, PhetVersion newVersion ) {
        return "<html>" + PhetAboutDialog.HTML_CUSTOM_STYLE + "Your current version of " + application.getApplicationConfig().getName() + " is " + application.getApplicationConfig().getVersion().formatForTitleBar() + ".<br>A newer version (" + newVersion.formatForTitleBar() + ") is available.</html>";
    }

    public AutomaticUpdateDialog( final Frame parent, String html, final ITrackingInfo trackingInfo, final IManualUpdateChecker iManuallyCheckForUpdates, final PhetVersion newVersion, final PhetApplicationConfig config, final IUpdateTimer updateTimer, final IVersionSkipper versionSkipper ) {
        super( parent, "New Update Available", html );
        this.config = config;
        JPanel buttonStrip = new JPanel();
        JButton updateNowButton = new JButton( "Update Now" );
        updateNowButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                UpdateResultDialog updateResultDialog = new UpdateResultDialog(
                        parent, "Instructions on Updating",
                        "<html>" +
                        PhetAboutDialog.HTML_CUSTOM_STYLE + getUpdateInstructions( newVersion ) +
                        "</html>" );
                updateResultDialog.addOKButton();
                updateResultDialog.addListener( new UpdateResultDialog.Listener() {
                    public void dialogFinished() {
                        OpenWebPageToNewVersion.openWebPageToNewVersion();
                    }
                } );

                updateResultDialog.pack();
                AutomaticUpdateDialog.this.dispose();
                updateResultDialog.setVisible( true );
            }
        } );
        buttonStrip.add( updateNowButton );

        JButton askMeLater = new JButton( "Ask me later" );
        askMeLater.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                updateTimer.setLastAskMeLaterTime( System.currentTimeMillis() );
                dispose();
            }
        } );
        buttonStrip.add( askMeLater );

        JButton skipThisVersion = new JButton( "Skip this version" );
        skipThisVersion.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                versionSkipper.skipThisVersion( config.getProjectName(), config.getFlavor(), newVersion );
                dispose();
            }
        } );
        buttonStrip.add( skipThisVersion );

        String htmlText =
                "<html>" +
                "<font size=\"2\"><u>" +
                "Edit preferences...</u></font></html>";

        JLabel preferences = new JLabel( htmlText );
        preferences.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
        preferences.addMouseListener( new MouseAdapter() {
            public void mousePressed( MouseEvent e ) {
                dispose();
                new PreferencesDialog( parent, trackingInfo, iManuallyCheckForUpdates, new DefaultUpdatePreferences( config ), new DefaultTrackingPreferences( config ) ).setVisible( true );
            }
        } );
        preferences.setForeground( Color.blue );
        buttonStrip.add( preferences );

        addComponent( buttonStrip );

        pack();
        center();
    }

    public static String getUpdateInstructions( PhetVersion newVersion ) {
        return "When you press OK, a web browser will be opened to PhET website, where you can get the new version (" + newVersion.formatForTitleBar() + ").<br><br>" +
               "<font size=-2>If the web browser fails to open, please visit this URL: <a href=\"http://phet.colorado.edu/\">http://phet.colorado.edu</a></font>";
    }

//    public static void main( String[] args ) {
//        BalloonsApplication.BalloonsApplicationConfig config = new BalloonsApplication.BalloonsApplicationConfig( args );
//        AutomaticUpdateDialog dialog = new AutomaticUpdateDialog( null, "<html>Your current version of Glaciers is 1.01.<br>A newer version (1.02) is available.</html>", config, new ApplicationConfigManualCheckForUpdates( null, config ), new PhetVersion( "1", "2", "3", "43243" ), new BalloonsApplication.BalloonsApplicationConfig( args ), new DefaultUpdateTimer(), new DefaultVersionSkipper() );
//        dialog.setVisible( true );
//        dialog.addWindowListener( new WindowAdapter() {
//            public void windowClosing( WindowEvent e ) {
//                System.exit( 0 );
//            }
//        } );
//    }
}
