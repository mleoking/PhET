package edu.colorado.phet.common.phetcommon.tracking;

/**
 * Tracking message sent when the user performs some action.
 * This message is analogous to Swing's ActionListener interface.
 * <p>
 * The client is responsible for choosing a name that uniquely identifies the action.
 * The action may involve an optional system response.
 * 
 * @author Sam Reid
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ActionPerformedMessage extends TrackingMessage {
    
    /* values for the constructor's "name" argument */
    
    // OK button pressed in Preferences dialog
    public static final String PREFERENCES_OK_PRESSED = "preferences-ok-pressed";
    // Cancel button pressed in Preferences dialog
    public static final String PREFERENCES_CANCEL_PRESSED = "preferences-cancel-pressed";
    // Check for update" button pressed in the Updates tab of the Preferences dialog
    public static final String CHECK_FOR_UPDATE_PRESSED = "check-for-update-pressed";
    // Help->Check for update selected from the menu bar
    public static final String HELP_CHECK_FOR_UPDATE_SELETED = "help-check-for-update-selected";
    // Details button pressed in the Tracking tab of the Preferences dialog
    public static final String TRACKING_DETAILS_PRESSED = "tracking-details-pressed";
    // sim automatically checked the web site to see if an update is available
    public static final String AUTO_CHECK_FOR_UPDATES = "auto-check-for-updates";
    // "Update Now!" button was pressed in Update dialog
    public static final String UPDATE_NOW_PRESSED = "update-now-pressed";
    // "Ask me later" button was pressed in Update dialog
    public static final String ASK_ME_LATER_PRESSED = "ask-me-later-update-pressed";
    // "Skip this update" button was pressed in Update dialog
    public static final String SKIP_UPDATE_PRESSED = "skip-update-pressed";
    // "Cancel" button was pressed at some point in the update process
    public static final String UPDATES_CANCEL_PRESSED = "updates-cancel-pressed";
    // "Try it before updating" link was selected in the Update dialog
    public static final String UPDATES_TRY_IT_PRESSED = "updates-try-it-pressed";
    // File->Exit was selected from the menu bar
    public static final String PHET_FRAME_CLOSING = "phet-frame-closing";
    // App->Quit was selected from the Mac Finder's menu bar 
    public static final String MAC_OSX_QUIT_SELECTED = "mac-osx-quit";
    // close button was pressed in the main frame's window dressing
    public static final String FILE_EXIT_SELECTED = "file-exit-selected";
    
    public ActionPerformedMessage( SessionID sessionID, String name ) {
        this( sessionID, name, null );
    }

    public ActionPerformedMessage( SessionID sessionID, String name, String systemResponse ) {
        super( sessionID, "action-performed" );
        addField( new TrackingMessageField( "name", name ) );
        addField( new TrackingMessageField( "system-response", systemResponse ) );
    }
}
