package edu.colorado.phet.common.phetcommon.preferences;

import java.awt.*;
import java.io.IOException;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.resources.PhetVersion;
import edu.colorado.phet.common.phetcommon.updates.UpdateManager;

public class DefaultManualCheckForUpdates implements IManuallyCheckForUpdates {
    private PhetVersion currentVersion;
    private String humanReadableSimName;
    private Window window;
    private String projectName;

    public DefaultManualCheckForUpdates( Window window, String projectName, PhetVersion currentVersion, String humanReadableSimName ) {
        this.window = window;
        this.projectName = projectName;
        this.currentVersion = currentVersion;
        this.humanReadableSimName = humanReadableSimName;
    }

    public void checkForUpdates() {
        UpdateManager updateManager = new UpdateManager( projectName, currentVersion );
        UpdateManager.Listener listener = new UpdateManager.Listener() {
            public void discoveredRemoteVersion( PhetVersion remoteVersion ) {
            }

            public void newVersionAvailable( PhetVersion currentVersion, PhetVersion remoteVersion ) {
                String title = "New version available";
                String text = "<html>Your current version of " + humanReadableSimName + " is " + currentVersion.formatForTitleBar() + ".  A newer version (" + remoteVersion.formatForTitleBar() + ") is available.<br>" +
                              "A web browser is being opened to the PhET website, where you can get the new version.<html>";
                if ( window instanceof Frame || window == null ) {
                    new UpdateResultDialog( (Frame) window, title, text ).setVisible( true );
                }
                else if ( window instanceof Dialog ) {
                    new UpdateResultDialog( (Dialog) window, title, text ).setVisible( true );
                }
            }

            public void exceptionInUpdateCheck( IOException e ) {
                JOptionPane.showMessageDialog( null, "An error was encountered while trying to access the PhET website. \n" +
                                                     "Please try again later, or visit http://phet.colorado.edu. \n" +
                                                     "If the problem persists, please contact phethelp@colorado.edu." );
            }

            public void noNewVersionAvailable( PhetVersion currentVersion, PhetVersion remoteVersion ) {
                JOptionPane.showMessageDialog( null, "You have the current version (" + currentVersion.formatForTitleBar() + ") of " + humanReadableSimName + "." );
            }
        };
        updateManager.addListener( listener );
        updateManager.checkForUpdates();
        updateManager.removeListener( listener );
    }
}
