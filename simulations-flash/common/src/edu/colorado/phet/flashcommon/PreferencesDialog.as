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

class edu.colorado.phet.flashcommon.PreferencesDialog {
	
	// keep track of what states would be if the user clicks OK
	public var updateState : Boolean;
	public var statisticsState : Boolean;
	
	// need references to the checkboxes
	public var updatesCheck : JCheckBox;
	public var statisticsCheck : JCheckBox;
	
	var updatesSimButton : JButton;
	var updatesInstallationButton : JButton;
	
	var common : FlashCommon;
	
	// shorthand for debugging function
	public function debug(str : String) : Void {
		_level0.debug(str);
	}
	
	public function PreferencesDialog() {
		//debug("PreferencesDialog initializing\n");
		
		// shortcut to FlashCommon, but now with type-checking!
		common = _level0.common;
		
		// make sure we can access this class from anywhere
		_level0.preferencesDialog = this;
		
		// load the shared object so we can pull data from it
		common.preferences.load();
		
		// initialize to false. this will be changed later if either should be true
		this.updateState = false;
		this.statisticsState = false;
		
		// mysterious fix since "this" does not evaluate to a MovieClip or Component
		ASWingUtils.getRootMovieClip();
		
		// create a window
		var window : JFrame = new JFrame(_level0, common.strings.get("PhETPreferences", "PhET Preferences"));
		
		// the window shouldn't be resizable
		window.setResizable(false);
		
		// make the window accessible to the "Preferences" button.
		// thus we don't have to reconstruct the window again, but just show it
		_level0.preferencesWindow = window;
		
		// set the background to white
		//window.setBackground(ASColor(0xFFFFFF));
		
		// set the background color to the common color
		window.setBackground(_level0.common.backgroundColor);
		
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
		str += common.strings.get("PrivacyRequirement", defaultStr, ["asfunction:_level0.common.openExternalLink,http://phet.colorado.edu"]);

		
		// CSS so that the blue link will display properly
		var css : TextField.StyleSheet = new TextField.StyleSheet();
		css.parseCSS("a:link{color:#0000FF;font-weight:bold;}" +
			"a:visited{color:#0000FF;font-weight:bold;}" +
			"a:hover{color:#0000FF;text-decoration:underline;font-weight:bold;}" +
			"a:active{color:#0000FF;font-weight:bold;}"); 
		var textArea = new JTextArea(str, 0, 20);
		textArea.setHtml(true);
		textArea.setEditable(false);
		textArea.setCSS(css);
		textArea.setWordWrap(true);
		textArea.setWidth(50);
		//textArea.setBackground(null); // give it the background color of its parent instead of the default
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
		var detailsButton = new JButton(common.strings.get("Details", "Details") + "...");
		detailsButton.addEventListener(JButton.ON_RELEASE, Delegate.create(this, detailsClicked));
		CommonButtons.padButtonAdd(detailsButton, privacyPanel);
		
		privacyPanel.append(new JSpacer(5, 5));
		
		bigPanel.append(updatesPanel);
		bigPanel.append(privacyPanel);
		
		window.getContentPane().append(bigPanel);
		
		// holds OK and Cancel buttons
		var buttonPanel : JPanel = new JPanel(new BoxLayout());
		
		var okButton : JButton = new JButton(common.strings.get("OK", "OK"));
		okButton.addEventListener(JButton.ON_RELEASE, Delegate.create(this, okClicked));
		CommonButtons.padButtonAdd(okButton, buttonPanel);
		
		var cancelButton : JButton = new JButton(common.strings.get("Cancel", "Cancel"));
		cancelButton.addEventListener(JButton.ON_RELEASE, Delegate.create(this, cancelClicked));
		CommonButtons.padButtonAdd(cancelButton, buttonPanel);
		
		window.getContentPane().append(new JSpacer(5, 5));
		
		//window.getContentPane().append(buttonPanel);
		var centerPanel : JPanel = new JPanel(new CenterLayout()); //SoftBoxLayout.X_AXIS, 0, SoftBoxLayout.CENTER
		centerPanel.append(buttonPanel);
		window.getContentPane().append(centerPanel);
		
		window.getContentPane().append(new JSpacer(5, 5));
		
		// size the window to its ideal size
		window.setSize(window.getPreferredSize());
		
		// center the window
		window.setLocation((Stage.width - window.getWidth()) / 2, (Stage.height - window.getHeight()) / 2);
		window.show();
		
		// attempt to focus the window for keboard accessibility. not working yet.
		window.toFront();
		
		// release the preferences shared object
		_level0.preferences.unload();
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
		common.preferences.unload();
	}
	
	// toggle potential update state
	public function updateToggle(src : JCheckBox) : Void {
		_level0.preferencesDialog.updateState = !_level0.preferencesDialog.updateState;
		debug("updateState toggled to " + _level0.preferencesDialog.updateState.toString() + "\n");
	}
	
	// toggle potential statistics state
	public function statisticsToggle(src : JCheckBox) : Void {
		_level0.preferencesDialog.statisticsState = !_level0.preferencesDialog.statisticsState;
		debug("statisticsState toggled to " + _level0.preferencesDialog.statisticsState.toString() + "\n");
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
		if(_level0.statisticsDetailsWindow) {
			debug("Showing dialog again\n");
			_level0.statisticsDetailsWindow.show();
		} else {
			debug("Creating Dialog\n");
			_level0.statisticsDetailsDialog = new StatisticsDetailsDialog();
		}
	}
	
	public function agreementClicked(src : JButton) {
		if(_level0.agreementWindow) {
			// agreement window exists, just show it
			debug("Showing dialog again\n");
			_level0.agreementWindow.show();
		} else {
			// agreement window doesn't exist, we must create it
			debug("Creating Dialog\n");
			_level0.agreementDialog = new AgreementDialog();
		}
	}
	
	public function cancelClicked(src : JButton) : Void {
		// hide the window
		_level0.preferencesWindow.setVisible(false);
	}
	
	public function okClicked(src : JButton) : Void {
		// set the potential state (updates and privacy) to the preferences
		common.preferences.setPrivacy(_level0.preferencesDialog.updateState, _level0.preferencesDialog.statisticsState);
		
		// hide the window
		_level0.preferencesWindow.setVisible(false);
	}
	
}
