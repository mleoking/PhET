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
    
    // Versioning the messages allows us to manage data after changing message content.
    // If the content of this message is changed, you'll need to increment the version number.
    public static final String MESSAGE_VERSION = "1";
    
    /* values for the constructor's "name" argument */
    
    // OK button pressed in Preferences dialog
    public static final String PREFERENCES_OK_PRESSED = "preferences_ok_pressed";
    // Cancel button pressed in Preferences dialog
    public static final String PREFERENCES_CANCEL_PRESSED = "preferences_cancel_pressed";
    // Check for update" button pressed in the Updates tab of the Preferences dialog
    public static final String CHECK_FOR_UPDATE_PRESSED = "check_for_update_pressed";
    // Help->Check for update selected from the menu bar
    public static final String HELP_CHECK_FOR_UPDATE_SELETED = "help_check_for_update_selected";
    // Details button pressed in the Tracking tab of the Preferences dialog
    public static final String TRACKING_DETAILS_PRESSED = "tracking_details_pressed";
    // sim automatically checked the web site to see if an update is available
    public static final String AUTO_CHECK_FOR_UPDATES = "auto_check_for_updates";
    // "Update Now!" button was pressed in Update dialog
    public static final String UPDATE_NOW_PRESSED = "update_now_pressed";
    // "Ask me later" button was pressed in Update dialog
    public static final String ASK_ME_LATER_PRESSED = "ask_me_later_update_pressed";
    // "Skip this update" button was pressed in Update dialog
    public static final String SKIP_UPDATE_PRESSED = "skip_update_pressed";
    // "Cancel" button was pressed at some point in the update process
    public static final String UPDATES_CANCEL_PRESSED = "updates_cancel_pressed";
    // "Try it before updating" link was selected in the Update dialog
    public static final String UPDATES_TRY_IT_PRESSED = "updates_try_it_pressed";
    // close button in the window dressing of the auto update dialog was pressed
    public static final String AUTO_UPDATE_DIALOG_CLOSE_BUTTON_PRESSED = "auto_update_dialog_close_button_pressed";
    // File->Exit was selected from the menu bar
    public static final String PHET_FRAME_CLOSING = "phet_frame_closing";
    // close button was pressed in the main frame's window dressing
    public static final String FILE_EXIT_SELECTED = "file_exit_selected";
    
    public ActionPerformedMessage( SessionID sessionID, String name ) {
        this( sessionID, name, null );
    }

    public ActionPerformedMessage( SessionID sessionID, String name, String systemResponse ) {
        super( sessionID, "action_performed", MESSAGE_VERSION );
        addField( new TrackingMessageField( "name", name ) );
        addField( new TrackingMessageField( "system_response", systemResponse ) );
    }
}
