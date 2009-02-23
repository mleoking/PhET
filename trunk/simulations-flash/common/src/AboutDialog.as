// AboutDialog.as
//
// Handles creating and displaying the about dialog
//
// Author: Jonathan Olson

import org.aswing.*;
import org.aswing.util.*;
import org.aswing.border.*;

class AboutDialog {
	
	public var common : FlashCommon;
	
	// shorthand for debugging function
	public function debug(str : String) : Void {
		_level0.debug(str);
	}
	
	public function AboutDialog() {
		//debug("AboutDialog initializing\n");
		
		// shortcut to FlashCommon, but now with type-checking!
		common = _level0.common;
		
		// mysterious fix since "this" does not refer to a MovieClip or Component
		ASWingUtils.getRootMovieClip();
		
		// create a window
		var window : JFrame = new JFrame(_level0, common.strings.get("AboutSim", "About {0}", [common.getSimName()]));
		
		// the window shouldn't be resizable
		window.setResizable(false);
		
		// make sure we can access it from anywhere
		_level0.aboutWindow = window;
		
		// set the background to default
		window.setBackground(common.backgroundColor);
		
		// layout things vertically
		window.getContentPane().setLayout(new SoftBoxLayout(SoftBoxLayout.Y_AXIS));
		
		// construct the string of text to show
		var str : String = "";
		str += "<b>" + common.strings.get("PhET", "PhET") + "</b>\n";
		str += common.strings.get("CopyrightColorado", "Copyright {0} University of Colorado.", ["\u00A9 2004-2008"]) + "\n";
		str += common.strings.get("SomeRightsReserved", "Some rights reserved.") + "\n";
		str += common.strings.get("Visit", "Visit {0}.", ["<a href='http://phet.colorado.edu'>http://phet.colorado.edu</a>"]) + "\n\n";
		
		str += "<b><font size='16'>" + common.getSimName() + "</font></b>\n";
		str += common.strings.get("Version", "Version") + ": " + common.getFullVersionString() + "\n";
		str += common.strings.get("BuildDate", "Build Date") + ": " + FlashCommon.dateString(FlashCommon.dateOfSeconds(common.getVersionTimestamp())) + "\n";
		if(common.getDistributionTag() != null || common.getSimName() == "flash-common-strings") {
			str += common.strings.get("Distribution", "Distribution") + ": " + String(common.getDistributionTag()) + "\n";
		}
		str += "\n";
		str += common.strings.get("FlashVersion", "Flash Version") + ": " + System.capabilities.version + "\n";
		str += common.strings.get("OSVersion", "OS Version") + ": " + System.capabilities.os + "\n";
		
		// create CSS to make links blue
		var css : TextField.StyleSheet = new TextField.StyleSheet();
		css.parseCSS("a:link{color:#0000FF;font-weight:bold;}" +
			"a:visited{color:#0000FF;font-weight:bold;}" +
			"a:hover{color:#0000FF;text-decoration:underline;font-weight:bold;}" +
			"a:active{color:#0000FF;font-weight:bold;}"); 
		
		var textArea = new JTextArea(str);
		textArea.setHtml(true);
		textArea.setEditable(false);
		textArea.setCSS(css);
		textArea.setBorder(new EmptyBorder(null, new Insets(5, 5, 5, 5)));
		textArea.setBackground(common.backgroundColor);
		
		window.getContentPane().append(textArea);
		
		window.getContentPane().append(new JSpacer(5, 5));
		
		// panel to lay the buttons in
		var panel : JPanel = new JPanel(new FlowLayout());
		
		// button that will open the agreements dialog
		var agreementButton : JButton = new JButton(common.strings.get("SoftwareAgreement", "Software Agreement") + "...");
		agreementButton.addEventListener(JButton.ON_PRESS, Delegate.create(this, agreementClicked));
		CommonButtons.padButtonAdd(agreementButton, panel);
		
		// button that will open the credits dialog
		var creditsButton : JButton = new JButton(common.strings.get("Credits", "Credits") + "...");
		creditsButton.addEventListener(JButton.ON_PRESS, Delegate.create(this, creditsClicked));
		CommonButtons.padButtonAdd(creditsButton, panel);
		
		// button will close the about dialog
		var okButton : JButton = new JButton(common.strings.get("OK", "OK"));
		okButton.addEventListener(JButton.ON_PRESS, Delegate.create(this, okClicked));
		CommonButtons.padButtonAdd(okButton, panel);
		
		//window.getContentPane().append(panel);
		var centerPanel : JPanel = new JPanel(new CenterLayout()); //SoftBoxLayout.X_AXIS, 0, SoftBoxLayout.CENTER
		centerPanel.append(panel);
		window.getContentPane().append(centerPanel);
		
		// fit the window to its contents
		window.setHeight(window.getContentPane().getPreferredSize().height + 50);
		window.setWidth(window.getContentPane().getPreferredSize().width + 50);
		
		// center the window
		window.setLocation((Stage.width - window.getWidth()) / 2, (Stage.height - window.getHeight()) / 2);
		window.show();
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
	
	public function okClicked(src : JButton) {
		// hide this window
		_level0.aboutWindow.setVisible(false);
	}
	
	public function creditsClicked(src : JButton) {
		if(_level0.creditsWindow) {
			// credits window exists, just show it
			debug("Showing dialog again\n");
			_level0.creditsWindow.show();
		} else {
			// credits window doesn't exist, we must create it
			debug("Creating Dialog\n");
			_level0.creditsDialog = new CreditsDialog();
		}
	}
	
}
