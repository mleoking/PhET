// PrivacyDialog.as
//
// Handles creating and displaying the privacy dialog
//
// Author: Jonathan Olson

import org.aswing.ASWingUtils;
import org.aswing.BoxLayout;
import org.aswing.Insets;
import org.aswing.JButton;
import org.aswing.JFrame;
import org.aswing.JPanel;
import org.aswing.JSpacer;
import org.aswing.JTextArea;
import org.aswing.SoftBoxLayout
import org.aswing.util.Delegate;
import org.aswing.border.EmptyBorder;

import edu.colorado.phet.flashcommon.*;

class edu.colorado.phet.flashcommon.PrivacyDialog {
	
	public var backgroundMC : MovieClip;
	
	public var textArea : JTextArea;
	public var canceled : Boolean;
	
	public var common : FlashCommon;
	
	// shorthand for debugging function
	public function debug(str : String) : Void {
		_level0.debug(str);
	}
	
	public function PrivacyDialog() {
		//debug("PrivacyDialog initializing\n");
		
		// shortcut to FlashCommon, but now with type-checking!
		common = _level0.common;
		
		// make this accessible by the asfunction callback in the text
		_level0.privacyDialog = this;
		
		// mysterious fix since "this" does not refer to a MovieClip or Component
		ASWingUtils.getRootMovieClip();
		
		canceled = false;
		
		// create the background
		backgroundMC = _root.createEmptyMovieClip("backgroundMC", _root.getNextHighestDepth() + 25467);
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
		var window : JFrame = new JFrame(_level0, common.strings.get("SoftwareAgreement", "Software Agreement"));
		
		// we don't want this window closable
		window.setClosable(false);
		
		window.setResizable(false);
		
		// make sure we can access it from anywhere
		_level0.privacyWindow = window;
		
		// set the background to default
		window.setBackground(common.backgroundColor);
		
		// layout things vertically
		window.getContentPane().setLayout(new SoftBoxLayout(SoftBoxLayout.Y_AXIS));
		
		// construct the string of text to show
		var str : String = "";
		var defaultString : String = "";
		defaultString += "In all PhET simulations, we collect a minimal amount of <a href='{0}'>information</a> ";
		defaultString += "when the simulation starts. You can disable the sending of this ";
		defaultString += "information at any time via the Preferences button.";
		str += common.strings.get("PrivacyMessage1", defaultString, ["asfunction:_level0.privacyDialog.infoClicked,"]);
		str += "\n\n";
		defaultString = "By clicking \"Accept and Continue\", you agree to PhET's licensing ";
		defaultString += "and privacy policies. (For details, <a href='{0}'>click here</a>).";
		str += common.strings.get("PrivacyMessage2", defaultString, ["asfunction:_level0.privacyDialog.detailsClicked,"]);
		//str += "\n";
		
		// create CSS to make links blue
		var css : TextField.StyleSheet = new TextField.StyleSheet();
		css.parseCSS("a:link{color:#0000FF;font-weight:bold;}" +
			"a:visited{color:#0000FF;font-weight:bold;}" +
			"a:hover{color:#0000FF;text-decoration:underline;font-weight:bold;}" +
			"a:active{color:#0000FF;font-weight:bold;}"); 
		
		textArea = new JTextArea(str, 0, 30);
		textArea.setHtml(true);
		textArea.setEditable(false);
		textArea.setCSS(css);
		textArea.setWordWrap(true);
		textArea.setWidth(300);
		textArea.setBorder(new EmptyBorder(null, new Insets(5, 5, 0, 5)));
		textArea.setBackground(common.backgroundColor);
		
		window.getContentPane().append(textArea);
		
		window.getContentPane().append(new JSpacer(5, 5));
		
		// panel to lay the buttons in
		var panel : JPanel = new JPanel(new BoxLayout());
		
		// button that will allow us to continue
		var continueButton : JButton = new JButton(common.strings.get("AcceptContinue", "Accept and Continue"));
		continueButton.addEventListener(JButton.ON_RELEASE, Delegate.create(this, continueClicked));
		CommonButtons.padButtonAdd(continueButton, panel);
		
		window.getContentPane().append(panel);
		
		window.getContentPane().append(new JSpacer(5, 5));
		
		// fit the window to its contents
		window.setSize(window.getPreferredSize());
		
		// center the window
		window.setLocation((Stage.width - window.getWidth()) / 2, (Stage.height - window.getHeight()) / 2);
		window.show();
	}
	
	public function continueClicked(src : JButton) {
		// set policy as accepted
		common.preferences.agreeToPrivacy();
		
		// hide this window
		_level0.privacyWindow.setVisible(false);
		
		backgroundMC.clear();
		backgroundMC.removeMovieClip();
		
		// continue with common code initialization
		common.postAgreement();
	}
	
	public function infoClicked() : Void {
		if(_level0.statisticsDetailsWindow) {
			debug("Showing dialog again\n");
			_level0.statisticsDetailsWindow.show();
		} else {
			debug("Creating Dialog\n");
			_level0.statisticsDetailsDialog = new StatisticsDetailsDialog();
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

