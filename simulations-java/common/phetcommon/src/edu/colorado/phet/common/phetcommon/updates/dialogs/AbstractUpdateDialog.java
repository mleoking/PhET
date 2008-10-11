package edu.colorado.phet.common.phetcommon.updates.dialogs;

import java.awt.Frame;
import java.text.MessageFormat;

import javax.swing.JDialog;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;

/**
 * Base class for all dialogs related to the Updates feature.
 * Contains shared functionality, and localization of reusable "chucks" of HTML.
 * Handles localization that involves MessageFormat syntax (pattern replacement).
 */
public abstract class AbstractUpdateDialog extends JDialog {
    
    private static final String PATTERN_SIM_LINK = "<a href=\"{0}\">{0}</a>";
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

    protected static String getAutomaticUpdateMessageHTML( String simName, String currentVersion, String newVersion ) {
        String htmlFragment = getVersionComparisonHTMLFragment( simName, currentVersion, newVersion );
        return HTMLUtils.createStyledHTMLFromFragment( htmlFragment );
    }
    
    protected static String getAutomaticUpdateInstructionsHTML( String project, String sim, String newVersion ) {
        String htmlFragment = getUpdateInstructionsHTMLFragment( project, sim, newVersion );
        return HTMLUtils.createStyledHTMLFromFragment( htmlFragment );
    }
    
    protected static String getManualUpdateInstructionsHTML( String project, String sim, String simName, String currentVersion, String newVersion ) {
        String htmlFragment = 
            getVersionComparisonHTMLFragment( simName, currentVersion, newVersion ) + "<br><br>" + 
            getUpdateInstructionsHTMLFragment( project, sim, newVersion );
        return HTMLUtils.createStyledHTMLFromFragment( htmlFragment );
    }
    
    protected static String getUpToDateHTML( String currentVersion, String simName ) {
        Object[] args = { currentVersion, simName };
        String htmlFragment = MessageFormat.format( PATTERN_YOU_HAVE_CURRENT, args );
        return HTMLUtils.createStyledHTMLFromFragment( htmlFragment );
    }
    
    protected static String getErrorMessageHTML() {
        Object[] args = { HTMLUtils.getPhetHomeHref(), HTMLUtils.getPhetMailtoHref() };
        String htmlFragment = MessageFormat.format( PATTERN_ERROR_MESSAGE, args );
        return HTMLUtils.createStyledHTMLFromFragment( htmlFragment );
    }

    private static String getVersionComparisonHTMLFragment( String simName, String currentVersion, String newVersion ) {
        Object[] args = { simName, currentVersion, newVersion };
        return MessageFormat.format( PATTERN_VERSION_COMPARISON, args );
    }
    
    private static String getUpdateInstructionsHTMLFragment( String project, String sim, String newVersion ) {
        String url = HTMLUtils.getSimURL( project, sim );
        Object[] linkArgs = { url };
        String link = MessageFormat.format( PATTERN_SIM_LINK, linkArgs );
        Object[] args = { newVersion, link };
        return MessageFormat.format( PATTERN_INSTRUCTIONS, args );
    }
    
    // test the more complicate methods that involve MessageFormat
    public static void main( String[] args ) {
        System.out.println( getUpToDateHTML("1.0","glaciers") );
        System.out.println( getErrorMessageHTML() );
        System.out.println( getUpdateInstructionsHTMLFragment( "foo", "bar", "2.00" ) );
        System.out.println( getVersionComparisonHTMLFragment( "Glaciers", "1.00", "2.00" ) );
    }
}
