package edu.colorado.phet.common.phetcommon.updates.dialogs;

import java.awt.Frame;

import javax.swing.JDialog;
import javax.swing.JEditorPane;

import edu.colorado.phet.common.phetcommon.application.PhetAboutDialog;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;

/**
 * Functionality and HTML shared by dialogs related to the update feature.
 */
public abstract class AbstractUpdateDialog extends JDialog {
    
    protected AbstractUpdateDialog( Frame owner, String title ) {
        super( owner, title );
    }
    
    protected void center() {
        SwingUtils.centerDialogInParent( this );
    }

    protected JEditorPane createHTMLPaneWithLinks( String html ) {
        html = html.replaceAll( "@FONT_SIZE@", new PhetFont().getSize() + "pt" );
        html = html.replaceAll( "@FONT_FAMILY@", new PhetFont().getFamily() );
        return new PhetAboutDialog.HTMLPane( html );
    }
    
    protected static String getAutomaticUpdateMessageHTML( String simName, String currentVersion, String newVersion ) {
        return "<html>" + PhetAboutDialog.HTML_CUSTOM_STYLE + 
            getVersionComparisonHTMLFragment( simName, currentVersion, newVersion ) + 
            "</html>";
    }
    
    protected static String getAutomaticUpdateInstructionsHTML( String project, String sim, String newVersion ) {
        return "<html>" + PhetAboutDialog.HTML_CUSTOM_STYLE + 
            getUpdateInstructionsHTMLFragment( project, sim, newVersion ) +
            "</html>";
    }
    
    protected static String getManualUpdateInstructionsHTML( String project, String sim, String simName, String currentVersion, String newVersion ) {
        return "<html>" + PhetAboutDialog.HTML_CUSTOM_STYLE + 
            getVersionComparisonHTMLFragment( simName, currentVersion, newVersion ) + "<br><br>" + 
            getUpdateInstructionsHTMLFragment( project, sim, newVersion ) +
            "</html>";
    }
    
    protected static String getUpToDateHTML( String currentVersion, String simName ) {
        return "<html>" + PhetAboutDialog.HTML_CUSTOM_STYLE + 
            "You have the most current version (" + currentVersion + ") of " + simName + "." + 
            "<html>";
    }
    
    protected static String getErrorMessageHTML() {
        return "<html>" + PhetAboutDialog.HTML_CUSTOM_STYLE +
            "An error was encountered while trying to access the PhET website.<br>" + 
            "Please try again later, or visit <a href=\"http://phet.colorado.edu\">http://phet.colorado.edu</a>.<br>" +
            "If the problem persists, please contact <a href=\"mailto:phethelp@colorado.edu\">phethelp@colorado.edu</a>." + 
            "</html>";
    }

    private static String getVersionComparisonHTMLFragment( String simName, String currentVersion, String newVersion ) {
        return "Your current version of " + simName + " is " + currentVersion + ".<br>A newer version (" + newVersion + ") is available.";
    }
    
    private static String getUpdateInstructionsHTMLFragment( String project, String sim, String newVersion ) {
        String url = getSimURL( project, sim );
        return "When you press OK, a web browser will be opened to the PhET website,<br>where you can get the new version (" + newVersion + ").<br><br>" +
               "<font size=-2>If the web browser fails to open, please visit this URL:<br>" +
               "<a href=\"" + url + "\">" + url + "</a></font>";
    }
    
    public static String getSimURL( String project, String sim ) {
        return "http://phet.colorado.edu/simulations/sim-redirect.php?project=" + project + "&sim=" + sim;
    }
}
