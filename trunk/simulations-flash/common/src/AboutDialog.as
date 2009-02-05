﻿// AboutDialog.as
//
// Handles creating and displaying the about dialog
//
// Author: Jonathan Olson

import org.aswing.*;
import org.aswing.util.*;
import org.aswing.border.*;

class AboutDialog {
	
	// shorthand for debugging function
	public function debug(str : String) : Void {
		_level0.debug(str);
	}
	
	public function AboutDialog() {
		debug("AboutDialog initializing\n");
		
		// mysterious fix since "this" does not refer to a MovieClip or Component
		ASWingUtils.getRootMovieClip();
		
		// create a window
		var window : JFrame = new JFrame(_level0, _level0.comStrings.get("AboutSim", "About {0}", [_level0.simName]));
		
		// the window shouldn't be resizable
		window.setResizable(false);
		
		// make sure we can access it from anywhere
		_level0.aboutWindow = window;
		
		// set the background to default
		window.setBackground(_level0.common.backgroundColor);
		
		// layout things vertically
		window.getContentPane().setLayout(new SoftBoxLayout(SoftBoxLayout.Y_AXIS));
		
		// construct the string of text to show
		var str : String = "";
		str += "<b>" + _level0.comStrings.get("PhET", "PhET") + "</b>\n";
		str += _level0.comStrings.get("CopyrightColorado", "Copyright {0} University of Colorado.", ["\u00A9 2004-2008"]) + "\n";
		str += _level0.comStrings.get("SomeRightsReserved", "Some rights reserved.") + "\n";
		str += _level0.comStrings.get("Visit", "Visit {0}.", ["<a href='http://phet.colorado.edu'>http://phet.colorado.edu</a>"]) + "\n\n";
		
		str += "<b><font size='16'>" + _level0.simName + "</font></b>\n";
		str += _level0.comStrings.get("Version", "Version") + ": " + _level0.versionMajor + "." + _level0.versionMinor;
		str += "." + _level0.versionDev;
		str += " (" + _level0.versionRevision + ")\n";
		str += _level0.comStrings.get("BuildDate", "Build Date") + ": " + dateString(new Date(int(_level0.versionTimestamp) * 1000)) + "\n";
		if(_level0.simDistributionTag && _level0.simDistributionTag != FlashCommon.NULLVAL && !_level0.common.isPlaceholder(_level0.simDistributionTag)) {
			str += _level0.comStrings.get("Distribution", "Distribution") + ": " + _level0.simDistributionTag + "\n";
		}
		str += "\n";
		str += _level0.comStrings.get("FlashVersion", "Flash Version") + ": " + System.capabilities.version + "\n";
		str += _level0.comStrings.get("OSVersion", "OS Version") + ": " + System.capabilities.os + "\n";
		
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
		textArea.setBackground(_level0.common.backgroundColor);
		
		window.getContentPane().append(textArea);
		
		window.getContentPane().append(new JSpacer(5, 5));
		
		// panel to lay the buttons in
		var panel : JPanel = new JPanel(new BoxLayout());
		
		// button that will open the license dialog
		var licenseButton : JButton = new JButton(_level0.comStrings.get("License", "License") + "...");
		licenseButton.addEventListener(JButton.ON_PRESS, Delegate.create(this, licenseClicked));
		CommonButtons.padButtonAdd(licenseButton, panel);
		
		// button will close the about dialog
		var okButton : JButton = new JButton(_level0.comStrings.get("OK", "OK"));
		okButton.addEventListener(JButton.ON_PRESS, Delegate.create(this, okClicked));
		CommonButtons.padButtonAdd(okButton, panel);
		
		window.getContentPane().append(panel);
		
		// fit the window to its contents
		window.setHeight(window.getContentPane().getPreferredSize().height + 50);
		window.setWidth(window.getContentPane().getPreferredSize().width + 50);
		
		// center the window
		window.setLocation((Stage.width - window.getWidth()) / 2, (Stage.height - window.getHeight()) / 2);
		window.show();
	}
	
	public function licenseClicked(src : JButton) {
		if(_level0.licenseWindow) {
			// license window exists, just show it
			debug("Showing dialog again\n");
			_level0.licenseWindow.show();
		} else {
			// license window doesn't exist, we must create it
			debug("Creating Dialog\n");
			_level0.licenseDialog = new LicenseDialog();
		}
	}
	
	public function okClicked(src : JButton) {
		// hide this window
		_level0.aboutWindow.setVisible(false);
	}
	
	public function dateString(date : Date) : String {
		var year : String = new String(date.getYear() + 1900);
		var month : String = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"][date.getMonth()];
		return month + " " + String(date.getDate()) + ", " + year;
	}
}
