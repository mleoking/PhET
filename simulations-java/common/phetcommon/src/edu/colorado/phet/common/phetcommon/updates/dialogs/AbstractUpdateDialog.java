package edu.colorado.phet.common.phetcommon.updates.dialogs;

import java.awt.Frame;
import java.text.MessageFormat;

import javax.swing.JDialog;
import javax.swing.JEditorPane;

import edu.colorado.phet.common.phetcommon.application.PhetAboutDialog;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;

/**
 * Functionality and HTML shared by dialogs related to the update feature.
 * This class also handles complex string translations that involve argument replacement.
 */
public abstract class AbstractUpdateDialog extends JDialog {
    
    public static final String PHET_HOME_URL = "http://phet.colorado.edu";
    private static final String PHET_EMAIL = "phethelp@colorado.edu";
    
    private static final String PATTERN_YOU_HAVE_CURRENT = PhetCommonResources.getString( "Common.updates.youHaveCurrent" );
    private static final String PATTERN_ERROR_MESSAGE = PhetCommonResources.getString( "Common.updates.errorMessage" );
    private static final String PATTERN_INSTRUCTIONS = PhetCommonResources.getString( "Common.updates.instructions" );
    private static final String PATTERN_VERSION_COMPARISON = PhetCommonResources.getString( "Common.updates.versionComparison" );

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
        Object[] args = { currentVersion, simName };
        String s = MessageFormat.format( PATTERN_YOU_HAVE_CURRENT, args );
        return "<html>" + PhetAboutDialog.HTML_CUSTOM_STYLE + s + "</hmtl>";
    }
    
    protected static String getErrorMessageHTML() {
        Object[] args = { PHET_HOME_URL, PHET_EMAIL };
        String s = MessageFormat.format( PATTERN_ERROR_MESSAGE, args );
        return "<html>" + PhetAboutDialog.HTML_CUSTOM_STYLE + s + "</hmtl>";
    }

    private static String getVersionComparisonHTMLFragment( String simName, String currentVersion, String newVersion ) {
        Object[] args = { simName, currentVersion, newVersion };
        return MessageFormat.format( PATTERN_VERSION_COMPARISON, args );
    }
    
    private static String getUpdateInstructionsHTMLFragment( String project, String sim, String newVersion ) {
        String url = getSimURL( project, sim );
        Object[] args = { newVersion, url };
        return MessageFormat.format( PATTERN_INSTRUCTIONS, args );
    }
    
    public static String getSimURL( String project, String sim ) {
        return PHET_HOME_URL + "/simulations/sim-redirect.php?project=" + project + "&sim=" + sim;
    }
    
    // test the more complicate methods that involve MessageFormat
    public static void main( String[] args ) {
        System.out.println( getUpToDateHTML("1.0","glaciers") );
        System.out.println( getErrorMessageHTML() );
        System.out.println( getUpdateInstructionsHTMLFragment( "foo", "bar", "2.00" ) );
        System.out.println( getVersionComparisonHTMLFragment( "Glaciers", "1.00", "2.00" ) );
    }
}
