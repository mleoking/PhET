// PreferencesDialog.as
//
// Shows a dialog which allows the user to change their
// preferences that deal with updates and tracking
// they can also manually check updates, and access
// details about the tracking
//
// Author: Jonathan Olson

import org.aswing.*;
import org.aswing.util.*;
import org.aswing.border.*;

class PreferencesDialog {
	
	// keep track of what states would be if the user clicks OK
	public var updateState : Boolean;
	public var trackingState : Boolean;
	
	// need references to the checkboxes
	public var updatesCheck : JCheckBox;
	public var trackingCheck : JCheckBox;
	
	var updatesButton : JButton;
	
	// shorthand for debugging function
	public function debug(str : String) : Void {
		_level0.debug(str);
	}
	
	public function PreferencesDialog() {
		debug("PreferencesDialog initializing\n");
		
		// make sure we can access this class from anywhere
		_level0.preferencesDialog = this;
		
		// load the shared object so we can pull data from it
		_level0.preferences.load();
		
		// initialize to false. this will be changed later if either should be true
		this.updateState = false;
		this.trackingState = false;
		
		// mysterious fix since "this" does not evaluate to a MovieClip or Component
		ASWingUtils.getRootMovieClip();
		
		// create a window
		var window : JFrame = new JFrame(_level0, _level0.comStrings.get("PhETPreferences", "PhET Preferences"));
		
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
		
		// panel that will hold the updates and tracking options (everything except OK/Cancel)
		var bigPanel = new JPanel(new SoftBoxLayout(SoftBoxLayout.Y_AXIS));
		bigPanel.setBorder(new EmptyBorder(null, new Insets(5, 5, 5, 5)));
		
		// holds the update options
		var updatesPanel = new JPanel(new SoftBoxLayout(SoftBoxLayout.Y_AXIS));
		updatesPanel.setName(_level0.comStrings.get("Updates", "Updates"));
		updatesPanel.setBorder(new TitledBorder(new EmptyBorder(null, new Insets(5, 5, 5, 5)), _level0.comStrings.get("Updates", "Updates")));
		
		// update check box
		updatesCheck = new JCheckBox(_level0.comStrings.get("CheckUpdates", "Automatically check for updates"));
		updatesCheck.addEventListener(JCheckBox.ON_CLICKED, Delegate.create(this, updateToggle));
		
		if(updateState != _level0.preferences.userAllowsUpdates()) {
			// if updates are allowed, fill in the check box
			updatesCheck.click();
		}
		updatesPanel.append(updatesCheck);
		
		updatesPanel.append(new JSpacer(5, 5));
		
		// update now button
		updatesButton = new JButton(_level0.comStrings.get("CheckUpdatesNow", "Check for updates now"));
		updatesButton.addEventListener(JButton.ON_PRESS, Delegate.create(this, updatesClicked));
		CommonButtons.padButtonAdd(updatesButton, updatesPanel);
		
		updatesPanel.append(new JSpacer(5, 5));
		
		// holds the tracking options
		var trackingPanel = new JPanel(new SoftBoxLayout(SoftBoxLayout.Y_AXIS));
		trackingPanel.setName(_level0.comStrings.get("Privacy", "Privacy"));
		trackingPanel.setBorder(new TitledBorder(new EmptyBorder(null, new Insets(5, 5, 5, 5)), _level0.comStrings.get("Privacy", "Privacy")));
		
		// text area that displays the following string.
		// NOTE: Text area required, otherwise HTML text will not work.
		var str : String = "";
		
		var defaultStr : String = "<a href='{0}'>PhET</a> is made freely available through grants which ";
		defaultStr += "require us to collect a minimal amount of anonymous information to help document the amount of use ";
		defaultStr += "of PhET sims and to better serve our users' update needs.";
		str += _level0.comStrings.get("PrivacyRequirement", defaultStr, ["http://phet.colorado.edu"]);

		
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
		textArea.setBackground(_level0.common.backgroundColor);
		
		var textFormat : ASTextFormat = ASTextFormat.getDefaultASTextFormat();
		textFormat.setAlign(ASTextFormat.CENTER);
		textArea.setTextFormat(textFormat);
		
		trackingPanel.append(textArea);
		
		trackingPanel.append(new JSpacer(5, 5));
		
		// tracking check-box
		trackingCheck = new JCheckBox(_level0.comStrings.get("AllowMessages", "Allow sending of information to PhET"));
		trackingCheck.addEventListener(JCheckBox.ON_CLICKED, Delegate.create(this, trackingToggle));
		if(trackingState != _level0.preferences.userAllowsTracking()) {
			// if tracking is allowed, fill in the check box
			trackingCheck.click();
		}
		trackingPanel.append(trackingCheck);
		
		trackingPanel.append(new JSpacer(5, 5));
		
		// button to show details about the tracking information
		var detailsButton = new JButton(_level0.comStrings.get("Details", "Details") + "...");
		detailsButton.addEventListener(JButton.ON_PRESS, Delegate.create(this, detailsClicked));
		CommonButtons.padButtonAdd(detailsButton, trackingPanel);
		
		trackingPanel.append(new JSpacer(5, 5));
		
		bigPanel.append(updatesPanel);
		bigPanel.append(trackingPanel);
		
		window.getContentPane().append(bigPanel);
		
		// holds OK and Cancel buttons
		var buttonPanel : JPanel = new JPanel(new BoxLayout());
		
		var okButton : JButton = new JButton(_level0.comStrings.get("OK", "OK"));
		okButton.addEventListener(JButton.ON_PRESS, Delegate.create(this, okClicked));
		CommonButtons.padButtonAdd(okButton, buttonPanel);
		
		var cancelButton : JButton = new JButton(_level0.comStrings.get("Cancel", "Cancel"));
		cancelButton.addEventListener(JButton.ON_PRESS, Delegate.create(this, cancelClicked));
		CommonButtons.padButtonAdd(cancelButton, buttonPanel);
		
		window.getContentPane().append(new JSpacer(5, 5));
		
		window.getContentPane().append(buttonPanel);
		
		// size the window to its ideal size
		window.setHeight(window.getContentPane().getPreferredSize().height + 50);
		window.setWidth(window.getContentPane().getPreferredSize().width + 50);
		
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
		if(trackingState != _level0.preferences.userAllowsTracking()) {
			trackingCheck.click();
		}
		_level0.preferences.unload();
	}
	
	// toggle potential update state
	public function updateToggle(src : JCheckBox) : Void {
		_level0.preferencesDialog.updateState = !_level0.preferencesDialog.updateState;
		debug("updateState toggled to " + _level0.preferencesDialog.updateState.toString() + "\n");
	}
	
	// toggle potential tracking state
	public function trackingToggle(src : JCheckBox) : Void {
		_level0.preferencesDialog.trackingState = !_level0.preferencesDialog.trackingState;
		debug("trackingState toggled to " + _level0.preferencesDialog.trackingState.toString() + "\n");
	}
	
	// manually check for updates
	public function updatesClicked(src : JButton) : Void {
		_level0.updateHandler.manualCheck();
	}
	
	public function detailsClicked(src : JButton) : Void {
		if(_level0.trackingDetailsWindow) {
			debug("Showing dialog again\n");
			_level0.trackingDetailsWindow.show();
		} else {
			debug("Creating Dialog\n");
			_level0.trackingDetailsDialog = new TrackingDetailsDialog();
		}
	}
	
	public function cancelClicked(src : JButton) : Void {
		// hide the window
		_level0.preferencesWindow.setVisible(false);
	}
	
	public function okClicked(src : JButton) : Void {
		// set the potential state (updates and tracking) to the preferences
		_level0.preferences.setTracking(_level0.preferencesDialog.updateState, _level0.preferencesDialog.trackingState);
		
		// hide the window
		_level0.preferencesWindow.setVisible(false);
	}
	
}
