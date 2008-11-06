package edu.colorado.phet.common.phetcommon.updates.dialogs;

import java.awt.Frame;
import java.text.MessageFormat;

import javax.swing.JDialog;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;

/**
 * Base class for all dialogs related to the Updates feature.
 * Contains shared functionality, and localization of reusable "chucks" of HTML.
 * Handles localization that involves MessageFormat syntax (pattern replacement).
 */
public abstract class AbstractUpdateDialog extends JDialog {
    
    protected AbstractUpdateDialog( Frame owner, String title ) {
        super( owner, title );
    }
    
    protected void center() {
        SwingUtils.centerDialogInParent( this );
    }

    /* fragment used by multiple subclasses */
    protected static String getVersionComparisonHTML( String simName, String currentVersion, String newVersion ) {
        String pattern = PhetCommonResources.getString( "Common.updates.versionComparison" );
        Object[] args = { simName, currentVersion, newVersion };
        return MessageFormat.format( pattern, args );
    }
}
