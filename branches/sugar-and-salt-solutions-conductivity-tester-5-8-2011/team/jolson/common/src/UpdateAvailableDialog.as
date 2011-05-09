// UpdateAvailableDialog.as
//
// Shown when a more recent version of the simulation is
// detected to exist
//
// Author: Jonathan Olson

import org.aswing.*;
import org.aswing.util.*;
import org.aswing.border.*;

class UpdateAvailableDialog {
	
	// shorthand for debugging function
	public function debug(str : String) : Void {
		_level0.debug(str);
	}
	
	public function UpdateAvailableDialog(versionMajor : String, versionMinor : String, dev : String) {
		debug("UpdateAvailableDialog initializing\n");
		
		// mysterious fix since "this" does not refer to a MovieClip or Component
		ASWingUtils.getRootMovieClip();
		
		// create a window
		var window : JFrame = new JFrame(_level0, "Update Available");
		
		// the window shouldn't be resizable
		window.setResizable(false);
		
		// make sure we can access it from anywhere
		_level0.updateAvailableWindow = window;
		
		// set the background to default
		window.setBackground(_level0.common.backgroundColor);
		
		// layout things vertically
		window.getContentPane().setLayout(new SoftBoxLayout(SoftBoxLayout.Y_AXIS));
		
		// construct the string of text to show
		var str : String = "";
		str += "Your current version of <b>" + _level0.simName + "</b> is " + _level0.versionMajor + ".";
		str += _level0.versionMinor + "." + _level0.dev + ".\n";
		str += "A newer version (" + versionMajor + "." + versionMinor + "." + dev + ") is available.\n";
		
		str += "\n<p align='center'>";
		
		str += "<a href='" + _level0.updateHandler.simWebsiteURL() + "'>";
		str += "Go to the new version.</a>";
		
		str += "</p>";
		
		if(_level0.common.fromFullInstallation()) {
			str += "\n";
			str += "To update your installation, please visit the ";
			str += "<a href='http://phet.colorado.edu/get_phet/full_install.php'>full installation page</a>";
			str += " for more information.";
			str += "\n";
		}
		
		str += "\n";
		str += "Update options are available under <i>Preferences</i>.";
		
		// TODO: visit the PhET site for more information ?
		
		
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
		textArea.setWidth(200);
		textArea.setBackground(_level0.common.backgroundColor);
		// add padding around the text
		textArea.setBorder(new EmptyBorder(null, new Insets(5, 5, 5, 5)));
		
		window.getContentPane().append(textArea);
		
		window.getContentPane().append(new JSpacer(5, 5));
		
		// panel to lay the buttons in
		var panel : JPanel = new JPanel(new BoxLayout());
		
		// button that will open the license dialog
		var askLaterButton : JButton = new JButton("Ask me later");
		askLaterButton.addEventListener(JButton.ON_PRESS, Delegate.create(this, askLaterClicked));
		CommonButtons.padButtonAdd(askLaterButton, panel);
		
		// button will close the about dialog
		var skipButton : JButton = new JButton("Skip this update");
		skipButton.addEventListener(JButton.ON_PRESS, Delegate.create(this, skipClicked));
		CommonButtons.padButtonAdd(skipButton, panel);
		
		// store new version information so they can be stored if the user decides to skip it
		_level0.newMajor = parseInt(versionMajor);
		_level0.newMinor = parseInt(versionMinor);
		
		window.getContentPane().append(panel);
		
		// fit the window to its contents
		window.setHeight(window.getContentPane().getPreferredSize().height + 50);
		window.setWidth(window.getContentPane().getPreferredSize().width + 50);
		
		// center the window
		window.setLocation((Stage.width - window.getWidth()) / 2, (Stage.height - window.getHeight()) / 2);
		window.show();
	}
	
	public function askLaterClicked(src : JButton) {
		// always ask later on if time has elapsed
		_level0.preferences.setSkippedUpdate(0, 0);
		
		// record the time the user clicked this
		_level0.preferences.setAskLater();
		
		// hide this window
		_level0.updateAvailableWindow.setVisible(false);
	}
	
	public function skipClicked(src : JButton) {
		// skip this update in the future
		_level0.preferences.setSkippedUpdate(_level0.newMajor, _level0.newMinor);
		
		// hide this window
		_level0.updateAvailableWindow.setVisible(false);
	}
}
