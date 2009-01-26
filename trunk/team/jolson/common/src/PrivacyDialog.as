﻿// PrivacyDialog.as
//
// Handles creating and displaying the privacy dialog
//
// Author: Jonathan Olson

import org.aswing.*;
import org.aswing.util.*;
import org.aswing.border.*;

class PrivacyDialog {
	
	public var backgroundMC : MovieClip;
	
	// shorthand for debugging function
	public function debug(str : String) : Void {
		_level0.debug(str);
	}
	
	public function PrivacyDialog() {
		debug("PrivacyDialog initializing\n");
		
		// make this accessible by the asfunction callback in the text
		_level0.privacyDialog = this;
		
		// mysterious fix since "this" does not refer to a MovieClip or Component
		ASWingUtils.getRootMovieClip();
		
		// create the background
		backgroundMC = _root.createEmptyMovieClip("backgroundMC", _root.getNextHighestDepth());
		backgroundMC.beginFill(_level0.bgColor, 50);
		// larger dimensions in case people resize afterwards
		backgroundMC.moveTo(-5000, -5000);
		backgroundMC.lineTo(5000, -5000);
		backgroundMC.lineTo(5000, 5000);
		backgroundMC.lineTo(-5000, 5000);
		backgroundMC.endFill();
		
		// make it catch all mouse clicks, but not show the hand pointer
		backgroundMC.useHandCursor = false;
		backgroundMC.onRelease = function() { }
		
		// create a window
		var window : JFrame = new JFrame(_level0, _level0.comStrings.get("SoftwarePrivacyAgreements", "Software & Privacy Agreements"));
		
		// we don't want this window closable
		window.setClosable(false);
		
		// make sure we can access it from anywhere
		_level0.privacyWindow = window;
		
		// set the background to default
		window.setBackground(_level0.common.backgroundColor);
		
		// layout things vertically
		window.getContentPane().setLayout(new SoftBoxLayout(SoftBoxLayout.Y_AXIS));
		
		// construct the string of text to show
		var str : String = "";
		var defaultString : String = "";
		defaultString += "In all PhET simulations, we collect a minimal amount of <a href=\"asfunction:_level0.privacyDialog.infoClicked,\">information</a> ";
		defaultString += "when the simulation starts. You can disable the sending of this ";
		defaultString += "information at any time via the Preferences button.";
		str += _level0.comStrings.get("PrivacyMessage1", defaultString);
		str += "\n\n";
		defaultString = "By clicking \"Accept and Continue\", you agree to PhET's licensing ";
		defaultString += "and privacy policies. (For details, <a href=\"asfunction:_level0.privacyDialog.detailsClicked,\">click here</a>).";
		str += _level0.comStrings.get("PrivacyMessage2", defaultString);
		
		// create CSS to make links blue
		var css : TextField.StyleSheet = new TextField.StyleSheet();
		css.parseCSS("a:link{color:#0000FF;font-weight:bold;}" +
			"a:visited{color:#0000FF;font-weight:bold;}" +
			"a:hover{color:#0000FF;text-decoration:underline;font-weight:bold;}" +
			"a:active{color:#0000FF;font-weight:bold;}"); 
		
		var textArea = new JTextArea(str, 0, 30);
		textArea.setHtml(true);
		textArea.setEditable(false);
		textArea.setCSS(css);
		textArea.setWordWrap(true);
		textArea.setWidth(300);
		textArea.setBorder(new EmptyBorder(null, new Insets(5, 5, 5, 5)));
		textArea.setBackground(_level0.common.backgroundColor);
		
		window.getContentPane().append(textArea);
		
		window.getContentPane().append(new JSpacer(5, 5));
		
		// panel to lay the buttons in
		var panel : JPanel = new JPanel(new BoxLayout());
		
		// button that will allow us to continue
		var continueButton : JButton = new JButton(_level0.comStrings.get("AcceptContinue", "Accept and Continue"));
		continueButton.addEventListener(JButton.ON_PRESS, Delegate.create(this, continueClicked));
		CommonButtons.padButtonAdd(continueButton, panel);
		
		// button will cancel acceptance, and do... something
		var cancelButton : JButton = new JButton(_level0.comStrings.get("Cancel", "Cancel"));
		cancelButton.addEventListener(JButton.ON_PRESS, Delegate.create(this, cancelClicked));
		CommonButtons.padButtonAdd(cancelButton, panel);
		
		window.getContentPane().append(panel);
		
		// fit the window to its contents
		window.setHeight(window.getContentPane().getPreferredSize().height + 50);
		window.setWidth(window.getContentPane().getPreferredSize().width + 50);
		
		// center the window
		window.setLocation((Stage.width - window.getWidth()) / 2, (Stage.height - window.getHeight()) / 2);
		window.show();
	}
	
	public function continueClicked(src : JButton) {
		// set policy as accepted
		_level0.preferences.agreeToPrivacy();
		
		// hide this window
		_level0.privacyWindow.setVisible(false);
		
		backgroundMC.clear();
		backgroundMC.removeMovieClip();
		
		// continue with common code initialization
		_level0.common.postAgreement();
	}
	
	public function cancelClicked(src : JButton) {
		// hide this window
		_level0.privacyWindow.setVisible(false);
		
		// attempt to close the window
		getURL("javascript:window.close();");
		getURL("javascript:parent.window.close();");
		
		// if that doesn't work
		getURL("about:blank", "_self");
		
		// and if that really doesn't work
		getURL("http://phet.colorado.edu", "_self");
		getURL("http://phet.colorado.edu");
		
		debug("WARNING: Could not close simulation, user clicked cancel on privacy dialog!\n");
		
		//getURL("javascript:window.opener=self; window.close()");
	}
	
	public function infoClicked() : Void {
		if(_level0.trackingDetailsWindow) {
			debug("Showing dialog again\n");
			_level0.trackingDetailsWindow.show();
		} else {
			debug("Creating Dialog\n");
			_level0.trackingDetailsDialog = new TrackingDetailsDialog();
		}
	}
	
	public function detailsClicked() : Void {
		if(_level0.agreementWindow) {
			debug("Showing dialog again\n");
			_level0.agreementWindow.show();
		} else {
			debug("Creating Dialog\n");
			_level0.agreementDialog = new AgreementDialog();
		}
	}
}

