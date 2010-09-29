package edu.colorado.phet.flashcommon {
// Shows a dialog which allows the user to change their
// preferences that deal with updates and privacy.
// they can also manually check updates, and access
// details about the privacy
//
// Author: Jonathan Olson

import flash.events.MouseEvent;

import org.aswing.BoxLayout;
import org.aswing.CenterLayout;
import org.aswing.Insets;
import org.aswing.JButton;
import org.aswing.JCheckBox;
import org.aswing.JPanel;
import org.aswing.JSpacer;
import org.aswing.JTextArea;
import org.aswing.SoftBoxLayout;
import org.aswing.border.EmptyBorder;
import org.aswing.border.TitledBorder;
import org.aswing.event.FrameEvent;
import org.aswing.event.InteractiveEvent;
import org.aswing.geom.IntDimension;

public class PreferencesDialog extends CommonDialog {

    // keep track of what states would be if the user clicks OK
    public var updateState: Boolean;
    public var statisticsState: Boolean;
    public var highContrastState: Boolean;

    // need references to the checkboxes
    public var updatesCheck: JCheckBox;
    public var statisticsCheck: JCheckBox;
    public var highContrastCheck: JCheckBox;

    var updatesSimButton: JButton;
    var updatesInstallationButton: JButton;
    var detailsButton: JButton;
    var okButton: JButton;
    var cancelButton: JButton;

    private static var instance: PreferencesDialog = null;

    public static function getInstance(): PreferencesDialog {
        return instance;
    }

    public function PreferencesDialog() {
        super( "preferences", CommonStrings.get( "PhETPreferences", "PhET Preferences" ) );
        instance = this;

        // load the shared object so we can pull data from it
        common.preferences.load();

        // initialize to false. this will be changed later if either should be true
        this.updateState = false;
        this.statisticsState = false;
        this.highContrastState = false;

        // SoftBoxLayout vertical, but allows different sized components
        window.getContentPane().setLayout( new SoftBoxLayout( SoftBoxLayout.Y_AXIS ) );

        // panel that will hold the updates and privacy options (everything except OK/Cancel)
        var bigPanel = new JPanel( new SoftBoxLayout( SoftBoxLayout.Y_AXIS ) );
        bigPanel.setBorder( new EmptyBorder( null, new Insets( 5, 5, 5, 5 ) ) );

        // holds the update options
        var updatesPanel = new JPanel( new SoftBoxLayout( SoftBoxLayout.Y_AXIS ) );
        updatesPanel.setName( CommonStrings.get( "Updates", "Updates" ) );
        updatesPanel.setBorder( new TitledBorder( new EmptyBorder( null, new Insets( 5, 5, 5, 5 ) ), CommonStrings.get( "Updates", "Updates" ) ) );

        // update check box
        updatesCheck = new JCheckBox( CommonStrings.get( "CheckUpdates", "Automatically check for updates" ) );
        updatesCheck.addEventListener( InteractiveEvent.STATE_CHANGED, updateToggle );

        if ( updateState != common.preferences.userAllowsUpdates() ) {
            // if updates are allowed, fill in the check box
            updatesCheck.setSelected( common.preferences.userAllowsUpdates() );
        }
        updatesPanel.append( updatesCheck );

        updatesPanel.append( new JSpacer( new IntDimension( 5, 5 ) ) );

        // update sim button
        updatesSimButton = new JButton( CommonStrings.get( "CheckSimUpdates", "Check for simulation update..." ) );
        updatesSimButton.addEventListener( MouseEvent.CLICK, updatesSimClicked );
        CommonButtons.padButtonAdd( updatesSimButton, updatesPanel );

        if ( common.fromFullInstallation() ) {
            updatesPanel.append( new JSpacer( new IntDimension( 5, 5 ) ) );

            // update installation button
            updatesInstallationButton = new JButton( CommonStrings.get( "CheckInstallationUpdates", "Check for PhET Offline Website Installer update..." ) );
            updatesInstallationButton.addEventListener( MouseEvent.CLICK, updatesInstallationClicked );
            CommonButtons.padButtonAdd( updatesInstallationButton, updatesPanel );
        }

        updatesPanel.append( new JSpacer( new IntDimension( 5, 5 ) ) );

        // holds the privacy options
        var privacyPanel = new JPanel( new SoftBoxLayout( SoftBoxLayout.Y_AXIS ) );
        privacyPanel.setName( CommonStrings.get( "Privacy", "Privacy" ) );
        privacyPanel.setBorder( new TitledBorder( new EmptyBorder( null, new Insets( 5, 5, 5, 5 ) ), CommonStrings.get( "Privacy", "Privacy" ) ) );

        // text area that displays the following string.
        // NOTE: Text area required, otherwise HTML text will not work.
        var str: String = "";

        var defaultStr: String = "<a href='{0}'>PhET</a> is made freely available through grants which ";
        defaultStr += "require us to collect a minimal amount of anonymous information to help document the amount of use ";
        defaultStr += "of PhET sims and to better serve our users' update needs.";
        str += CommonStrings.get( "PrivacyRequirement", defaultStr, ["asfunction:_level0.common.openExternalLink,http://" + FlashCommon.getMainServer() + ""] );

        var textArea = new JTextArea( str, 0, 20 );
        textArea.setHtmlText( str );
        textArea.setEditable( false );
        textArea.setCSS( FlashCommon.CENTERED_LINK_STYLE_SHEET );
        textArea.setWordWrap( true );
        textArea.setWidth( 50 );
        textArea.setBackground( common.backgroundColor );

        privacyPanel.append( textArea );

        privacyPanel.append( new JSpacer( new IntDimension( 5, 5 ) ) );

        // statistics message check-box
        statisticsCheck = new JCheckBox( CommonStrings.get( "AllowMessages", "Allow sending of information to PhET" ) );
        statisticsCheck.addEventListener( InteractiveEvent.STATE_CHANGED, statisticsToggle );
        if ( statisticsState != common.preferences.userAllowsStatistics() ) {
            // if statistics messages are	 allowed, fill in the check box
            statisticsCheck.setSelected( true );
        }
        privacyPanel.append( statisticsCheck );

        privacyPanel.append( new JSpacer( new IntDimension( 5, 5 ) ) );

        // button to show details about the privacy information
        detailsButton = new JButton( CommonStrings.get( "Details", "Details" ) + "..." );
        detailsButton.addEventListener( MouseEvent.CLICK, detailsClicked );
        CommonButtons.padButtonAdd( detailsButton, privacyPanel );

        privacyPanel.append( new JSpacer( new IntDimension( 5, 5 ) ) );

        var accessibilityPanel: JPanel = new JPanel( new SoftBoxLayout( SoftBoxLayout.Y_AXIS ) );
        //accessibilityPanel.setName( CommonStrings.get( "Accessibility", "Accessibility" ) );
        //accessibilityPanel.setBorder(new TitledBorder(new EmptyBorder(null, new Insets(5, 5, 5, 5)), CommonStrings.get("Accessibility", "Accessibility")));
        accessibilityPanel.append( new JSpacer( new IntDimension( 5, 5 ) ) );
        highContrastCheck = new JCheckBox( CommonStrings.get( "HighContrast", "High Contrast Colors" ) );
        highContrastCheck.addEventListener( InteractiveEvent.STATE_CHANGED, highContrastToggle );
        //        if( _level0.highContrast ) {
        //            highContrastCheck.click();
        //        }
        accessibilityPanel.append( highContrastCheck );
        accessibilityPanel.append( new JSpacer( new IntDimension( 5, 5 ) ) );

        bigPanel.append( updatesPanel );
        bigPanel.append( privacyPanel );
        bigPanel.append( accessibilityPanel );

        window.getContentPane().append( bigPanel );

        // holds OK and Cancel buttons
        var buttonPanel: JPanel = new JPanel( new BoxLayout() );

        okButton = new JButton( CommonStrings.get( "OK", "OK" ) );
        okButton.addEventListener( MouseEvent.CLICK, okClicked );
        CommonButtons.padButtonAdd( okButton, buttonPanel );

        cancelButton = new JButton( CommonStrings.get( "Cancel", "Cancel" ) );
        cancelButton.addEventListener( MouseEvent.CLICK, cancelClicked );
        CommonButtons.padButtonAdd( cancelButton, buttonPanel );

        window.getContentPane().append( new JSpacer( new IntDimension( 5, 5 ) ) );

        //window.getContentPane().append(buttonPanel);
        var centerPanel: JPanel = new JPanel( new CenterLayout() ); //SoftBoxLayout.X_AXIS, 0, SoftBoxLayout.CENTER
        centerPanel.append( buttonPanel );
        window.getContentPane().append( centerPanel );

        window.getContentPane().append( new JSpacer( new IntDimension( 5, 5 ) ) );

        displayMe();

        // release the preferences shared object
        common.preferences.flush();

        // TODO: remove after development:

    }

    //    public function setupTabHandler() {
    //        tabHandler.addAsWingCheckBox( updatesCheck );
    //        tabHandler.addAsWingButton( updatesSimButton );
    //		if( updatesInstallationButton ) {
    //			tabHandler.addAsWingButton( updatesInstallationButton );
    //		}
    //        tabHandler.addAsWingCheckBox( statisticsCheck );
    //		tabHandler.addAsWingButton( detailsButton );
    //        tabHandler.addAsWingCheckBox( highContrastCheck );
    //		tabHandler.addAsWingButton( okButton );
    //		tabHandler.addAsWingButton( cancelButton );
    //    }

    // called when the window is re-shown. it sets the check boxes to the
    // current values of the preferences information. this is needed to handle
    // the case when the user clicks "cancel" and the check-boxes remain different
    // from the actual preferences
    public function reCheck(): void {
        common.preferences.load();
        if ( updateState != common.preferences.userAllowsUpdates() ) {
            updatesCheck.setSelected( common.preferences.userAllowsUpdates() );
        }
        if ( statisticsState != common.preferences.userAllowsStatistics() ) {
            statisticsCheck.setSelected( common.preferences.userAllowsStatistics() );
        }
        //        if ( highContrastState != _level0.highContrast ) {
        //            highContrastCheck.doClick();
        //        }
        common.preferences.flush();
    }

    override public function closeClicked( evt: FrameEvent ): void {
        onClose();
    }

    // toggle potential update state
    public function updateToggle( evt: InteractiveEvent  ): void {
        instance.updateState = updatesCheck.isSelected();
        debug( "updateState toggled to " + instance.updateState.toString() + "\n" );
    }

    // toggle potential statistics state
    public function statisticsToggle( evt: InteractiveEvent  ): void {
        instance.statisticsState = statisticsCheck.isSelected();
        debug( "statisticsState toggled to " + instance.statisticsState.toString() + "\n" );
    }

    // toggle potential high contrast state
    public function highContrastToggle( evt: InteractiveEvent ): void {
        highContrastState = highContrastCheck.isSelected();
        debug( "highContrastState toggled to " + highContrastState.toString() + "\n" );
    }

    // manually check for sim updates
    public function updatesSimClicked( evt: MouseEvent ): void {
        common.updateHandler.manualCheckSim();
    }

    // manually check for installation updates
    public function updatesInstallationClicked( evt: MouseEvent ): void {
        common.updateHandler.manualCheckInstallation();
    }

    public function detailsClicked( evt: MouseEvent ): void {
        CommonDialog.openStatisticsDetailsDialog();
    }

    public function agreementClicked( evt: MouseEvent ): void {
        CommonDialog.openAgreementDialog();
    }

    public function cancelClicked( evt: MouseEvent ): void {
        // hide the window
        manualClose();
    }

    public function okClicked( evt: MouseEvent ): void {
        // set the potential state (updates and privacy) to the preferences
        common.preferences.setPrivacy( instance.updateState, instance.statisticsState );

        //        if ( highContrastState != _level0.highContrast ) {
        //            _level0.highContrastFunction( highContrastState );
        //        }

        // hide the window
        manualClose();
    }

    override public function manualOpen(): void {
        super.manualOpen();
        reCheck();
    }

}
}