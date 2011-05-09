// PreferencesDialog.as
//
// Shows a dialog which allows the user to change their
// preferences that deal with updates and privacy.
// they can also manually check updates, and access
// details about the privacy
//
// Author: Jonathan Olson

import org.aswing.ASTextFormat;
import org.aswing.ASWingUtils;
import org.aswing.BoxLayout;
import org.aswing.CenterLayout;
import org.aswing.Insets;
import org.aswing.JButton;
import org.aswing.JCheckBox;
import org.aswing.JFrame;
import org.aswing.JPanel;
import org.aswing.JSpacer;
import org.aswing.JTextArea;
import org.aswing.SoftBoxLayout
import org.aswing.util.Delegate;
import org.aswing.border.EmptyBorder;
import org.aswing.border.TitledBorder;

import edu.colorado.phet.flashcommon.*;

class edu.colorado.phet.flashcommon.PreferencesDialog extends edu.colorado.phet.flashcommon.CommonDialog {
	
	// keep track of what states would be if the user clicks OK
	public var updateState : Boolean;
	public var statisticsState : Boolean;
    public var highContrastState : Boolean;

    // need references to the checkboxes
    public var updatesCheck : JCheckBox;
    public var statisticsCheck : JCheckBox;
    public var highContrastCheck:JCheckBox;

    var updatesSimButton : JButton;
    var updatesInstallationButton : JButton;
    var detailsButton : JButton;
    var okButton : JButton;
    var cancelButton : JButton;


    public function PreferencesDialog() {
        super( "preferences", _level0.common.strings.get( "PhETPreferences", "PhET Preferences" ) );
		
		// load the shared object so we can pull data from it
		common.preferences.load();
		
		// initialize to false. this will be changed later if either should be true
		this.updateState = false;
		this.statisticsState = false;
        this.highContrastState = false;
		
		// SoftBoxLayout vertical, but allows different sized components
		window.getContentPane().setLayout(new SoftBoxLayout(SoftBoxLayout.Y_AXIS));
		
		// panel that will hold the updates and privacy options (everything except OK/Cancel)
		var bigPanel = new JPanel(new SoftBoxLayout(SoftBoxLayout.Y_AXIS));
		bigPanel.setBorder(new EmptyBorder(null, new Insets(5, 5, 5, 5)));
		
		// holds the update options
		var updatesPanel = new JPanel(new SoftBoxLayout(SoftBoxLayout.Y_AXIS));
		updatesPanel.setName(common.strings.get("Updates", "Updates"));
		updatesPanel.setBorder(new TitledBorder(new EmptyBorder(null, new Insets(5, 5, 5, 5)), common.strings.get("Updates", "Updates")));
		
		// update check box
		updatesCheck = new JCheckBox(common.strings.get("CheckUpdates", "Automatically check for updates"));
		updatesCheck.addEventListener(JCheckBox.ON_CLICKED, Delegate.create(this, updateToggle));
		
		if(updateState != common.preferences.userAllowsUpdates()) {
			// if updates are allowed, fill in the check box
			updatesCheck.click();
		}
		updatesPanel.append(updatesCheck);
		
		updatesPanel.append(new JSpacer(5, 5));
		
		// update sim button
		updatesSimButton = new JButton(common.strings.get("CheckSimUpdates", "Check for simulation update..."));
		updatesSimButton.addEventListener(JButton.ON_RELEASE, Delegate.create(this, updatesSimClicked));
		CommonButtons.padButtonAdd(updatesSimButton, updatesPanel);
		
		if(common.fromFullInstallation()) {
			updatesPanel.append(new JSpacer(5, 5));
			
			// update installation button
			updatesInstallationButton = new JButton(common.strings.get("CheckInstallationUpdates", "Check for PhET Offline Website Installer update..."));
			updatesInstallationButton.addEventListener(JButton.ON_RELEASE, Delegate.create(this, updatesInstallationClicked));
			CommonButtons.padButtonAdd(updatesInstallationButton, updatesPanel);
		}
		
		updatesPanel.append(new JSpacer(5, 5));
		
		// holds the privacy options
		var privacyPanel = new JPanel(new SoftBoxLayout(SoftBoxLayout.Y_AXIS));
		privacyPanel.setName(common.strings.get("Privacy", "Privacy"));
		privacyPanel.setBorder(new TitledBorder(new EmptyBorder(null, new Insets(5, 5, 5, 5)), common.strings.get("Privacy", "Privacy")));
		
		// text area that displays the following string.
		// NOTE: Text area required, otherwise HTML text will not work.
		var str : String = "";
		
		var defaultStr : String = "<a href='{0}'>PhET</a> is made freely available through grants which ";
		defaultStr += "require us to collect a minimal amount of anonymous information to help document the amount of use ";
		defaultStr += "of PhET sims and to better serve our users' update needs.";
		str += common.strings.get("PrivacyRequirement", defaultStr, ["asfunction:_level0.common.openExternalLink,http://" + FlashCommon.getMainServer() + ""]);

		var textArea = new JTextArea(str, 0, 20);
		textArea.setHtml(true);
		textArea.setEditable(false);
		textArea.setCSS( FlashCommon.LINK_STYLE_SHEET );
		textArea.setWordWrap(true);
		textArea.setWidth(50);
		textArea.setBackground(common.backgroundColor);
		
		var textFormat : ASTextFormat = ASTextFormat.getDefaultASTextFormat();
		textFormat.setAlign(ASTextFormat.CENTER);
		textArea.setTextFormat(textFormat);
		
		privacyPanel.append(textArea);
		
		privacyPanel.append(new JSpacer(5, 5));
		
		// statistics message check-box
		statisticsCheck = new JCheckBox(common.strings.get("AllowMessages", "Allow sending of information to PhET"));
		statisticsCheck.addEventListener(JCheckBox.ON_CLICKED, Delegate.create(this, statisticsToggle));
		if(statisticsState != _level0.preferences.userAllowsStatistics()) {
			// if statistics messages are	 allowed, fill in the check box
			statisticsCheck.click();
		}
		privacyPanel.append(statisticsCheck);
		
		privacyPanel.append(new JSpacer(5, 5));
		
		// button to show details about the privacy information
		detailsButton = new JButton(common.strings.get("Details", "Details") + "...");
		detailsButton.addEventListener(JButton.ON_RELEASE, Delegate.create(this, detailsClicked));
		CommonButtons.padButtonAdd(detailsButton, privacyPanel);
		
		privacyPanel.append(new JSpacer(5, 5));

        var accessibilityPanel = new JPanel( new SoftBoxLayout( SoftBoxLayout.Y_AXIS ) );
        //accessibilityPanel.setName( common.strings.get( "Accessibility", "Accessibility" ) );
        //accessibilityPanel.setBorder(new TitledBorder(new EmptyBorder(null, new Insets(5, 5, 5, 5)), common.strings.get("Accessibility", "Accessibility")));
        accessibilityPanel.append( new JSpacer( 5, 5 ) );
        highContrastCheck = new JCheckBox( common.strings.get( "HighContrast", "High Contrast Colors" ) );
        highContrastCheck.addEventListener(JCheckBox.ON_CLICKED, Delegate.create(this, highContrastToggle));
        if( _level0.highContrast ) {
            highContrastCheck.click();
        }
        accessibilityPanel.append( highContrastCheck );
        accessibilityPanel.append( new JSpacer( 5, 5 ) );
		
		bigPanel.append(updatesPanel);
		bigPanel.append(privacyPanel);
        bigPanel.append( accessibilityPanel );
		
		window.getContentPane().append(bigPanel);
		
		// holds OK and Cancel buttons
		var buttonPanel : JPanel = new JPanel(new BoxLayout());
		
		okButton = new JButton(common.strings.get("OK", "OK"));
		okButton.addEventListener(JButton.ON_RELEASE, Delegate.create(this, okClicked));
		CommonButtons.padButtonAdd(okButton, buttonPanel);
		
		cancelButton = new JButton(common.strings.get("Cancel", "Cancel"));
		cancelButton.addEventListener(JButton.ON_RELEASE, Delegate.create(this, cancelClicked));
		CommonButtons.padButtonAdd(cancelButton, buttonPanel);
		
		window.getContentPane().append(new JSpacer(5, 5));
		
		//window.getContentPane().append(buttonPanel);
		var centerPanel : JPanel = new JPanel(new CenterLayout()); //SoftBoxLayout.X_AXIS, 0, SoftBoxLayout.CENTER
		centerPanel.append(buttonPanel);
		window.getContentPane().append(centerPanel);
		
		window.getContentPane().append(new JSpacer(5, 5));
		
		displayMe();
		
		// release the preferences shared object
		_level0.preferences.unload();

        // TODO: remove after development:
        
	}

    public function setupTabHandler() {
        tabHandler.addAsWingCheckBox( updatesCheck );
        tabHandler.addAsWingButton( updatesSimButton );
		if( updatesInstallationButton ) {
			tabHandler.addAsWingButton( updatesInstallationButton );
		}
        tabHandler.addAsWingCheckBox( statisticsCheck );
		tabHandler.addAsWingButton( detailsButton );
        tabHandler.addAsWingCheckBox( highContrastCheck );
		tabHandler.addAsWingButton( okButton );
		tabHandler.addAsWingButton( cancelButton );
    }
	
	// called when the window is re-shown. it sets the check boxes to the
	// current values of the preferences information. this is needed to handle
	// the case when the user clicks "cancel" and the check-boxes remain different
	// from the actual preferences
	public function reCheck() : Void {
		_level0.preferences.load();
		if(updateState != _level0.preferences.userAllowsUpdates()) {
			updatesCheck.click();
		}
		if(statisticsState != _level0.preferences.userAllowsStatistics()) {
			statisticsCheck.click();
		}
        if( highContrastState != _level0.highContrast ) {
            highContrastCheck.click();
        }
		common.preferences.unload();
	}
	
	public function closeClicked( src : Object ) {
		onClose();
	}
	
	// toggle potential update state
	public function updateToggle(src : JCheckBox) : Void {
		_level0.preferencesDialog.updateState = !_level0.preferencesDialog.updateState;
        updatesCheck.setSelected( _level0.preferencesDialog.updateState );
		debug("updateState toggled to " + _level0.preferencesDialog.updateState.toString() + "\n");
	}
	
	// toggle potential statistics state
	public function statisticsToggle(src : JCheckBox) : Void {
		_level0.preferencesDialog.statisticsState = !_level0.preferencesDialog.statisticsState;
        statisticsCheck.setSelected( _level0.preferencesDialog.statisticsState );
		debug("statisticsState toggled to " + _level0.preferencesDialog.statisticsState.toString() + "\n");
	}

    // toggle potential high contrast state
	public function highContrastToggle(src : JCheckBox) : Void {
		highContrastState = !highContrastState;
        highContrastCheck.setSelected( highContrastState );
		debug("highContrastState toggled to " + highContrastState.toString() + "\n");
	}
	
	// manually check for sim updates
	public function updatesSimClicked(src : JButton) : Void {
		common.updateHandler.manualCheckSim();
	}
	
	// manually check for installation updates
	public function updatesInstallationClicked(src : JButton) : Void {
		common.updateHandler.manualCheckInstallation();
	}
	
	public function detailsClicked(src : JButton) : Void {
		CommonDialog.openStatisticsDetailsDialog();
	}
	
	public function agreementClicked(src : JButton) {
		CommonDialog.openAgreementDialog();
	}
	
	public function cancelClicked(src : JButton) : Void {
		// hide the window
		manualClose();
	}
	
	public function okClicked(src : JButton) : Void {
		// set the potential state (updates and privacy) to the preferences
		common.preferences.setPrivacy(_level0.preferencesDialog.updateState, _level0.preferencesDialog.statisticsState);

        if( highContrastState != _level0.highContrast ) {
            _level0.highContrastFunction( highContrastState );
        }
		
		// hide the window
		manualClose();
	}

    public function manualOpen() {
        super.manualOpen();
        reCheck();
    }
	
}
