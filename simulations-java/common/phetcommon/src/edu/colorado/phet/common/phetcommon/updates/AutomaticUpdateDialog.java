package edu.colorado.phet.common.phetcommon.updates;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;

import edu.colorado.phet.balloons.BalloonsApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.preferences.*;
import edu.colorado.phet.common.phetcommon.resources.PhetVersion;

public class AutomaticUpdateDialog extends UpdateResultDialog {
    private PhetApplicationConfig config;

    public AutomaticUpdateDialog( PhetApplication application, PhetVersion newVersion ) {
        this( application.getPhetFrame(), getHTML( application, newVersion ), application.getApplicationConfig(), new ApplicationConfigManualCheckForUpdates( application.getPhetFrame(), application.getApplicationConfig() ), newVersion, application.getApplicationConfig() );
    }

    private static String getHTML( PhetApplication application, PhetVersion newVersion ) {
        return "<html>Your current version of " + application.getApplicationConfig().getName() + " is " + application.getApplicationConfig().getVersion().formatForTitleBar() + ".<br>A newer version (" + newVersion.formatForTitleBar() + ") is available.</html>";
    }

    public AutomaticUpdateDialog( final Frame parent, String html, final ITrackingInfo trackingInfo, final IManualUpdateChecker iManuallyCheckForUpdates, PhetVersion newVersion, final PhetApplicationConfig config ) {
        super( parent, "New Update Available", html );
        this.config = config;
        JPanel buttonStrip = new JPanel();
        JButton updateNowButton = new JButton( "Update Now" );
        updateNowButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                UpdateResultDialog updateResultDialog = new UpdateResultDialog( parent, "Instructions on Updating", "<html>A web browser will be opened to PhET website, where you can get the new version.<br>" +
                                                                                                                    "If the web browser fails to open, please visit this URL: <a href=\"http://phet.colorado.edu/\">http://phet.colorado.edu</a></html>" );
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
                dispose();
            }
        } );
        buttonStrip.add( askMeLater );

        JButton skipThisVersion = new JButton( "Skip this version" );
        skipThisVersion.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                dispose();
            }
        } );
        buttonStrip.add( skipThisVersion );

        JButton preferencesButton = new JButton( "Preferences..." );
        preferencesButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                new PreferencesDialog( parent, trackingInfo, iManuallyCheckForUpdates, new DefaultUpdatePreferences( config ), new DefaultTrackingPreferences( config ) ).setVisible( true );
            }
        } );
        buttonStrip.add( preferencesButton );
        addComponent( buttonStrip );
        pack();
        center();
    }

    public static void main( String[] args ) {
        BalloonsApplication.BalloonsApplicationConfig config = new BalloonsApplication.BalloonsApplicationConfig( args );
        AutomaticUpdateDialog dialog = new AutomaticUpdateDialog( null, "<html>Your current version of Glaciers is 1.01.<br>A newer version (1.02) is available.</html>", config, new ApplicationConfigManualCheckForUpdates( null, config ), new PhetVersion( "1", "2", "3", "43243" ), new BalloonsApplication.BalloonsApplicationConfig( args ) );
        dialog.setVisible( true );
        dialog.addWindowListener( new WindowAdapter() {
            public void windowClosing( WindowEvent e ) {
                System.exit( 0 );
            }
        } );
    }
}
