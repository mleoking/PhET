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
		var window : JFrame = new JFrame(_level0, "Update available for " + _level0.simName);
		
		// make sure we can access it from anywhere
		_level0.updateAvailableWindow = window;
		
		// set the background to default
		window.setBackground(_level0.common.backgroundColor);
		
		// layout things vertically
		window.getContentPane().setLayout(new SoftBoxLayout(SoftBoxLayout.Y_AXIS));
		
		// construct the string of text to show
		var str : String = "";
		str += "Your current version of \"" + _level0.simName + "\" is " + _level0.versionMajor + ".";
		str += _level0.versionMinor + "." + _level0.dev + ".\n";
		str += "A newer version (" + versionMajor + "." + versionMinor + "." + dev + ") is available.\n";
		
		str += "<a href='http://phet.colorado.edu/jolson/deploy/sims/" + _level0.simName + "/" + _level0.simName;
		str += "_" + _level0.languageCode;
		if(_level0.countryCode != "none") {
			str += "_" + _level0.countryCode;
		}
		str += ".html'>Try the new version before you update.</a>";
		
		// create CSS to make links blue
		var css : TextField.StyleSheet = new TextField.StyleSheet();
		css.parseCSS("a:link{color:#0000FF;font-weight:bold;}" +
			"a:visited{color:#0000FF;font-weight:bold;}" +
			"a:hover{color:#0000FF;text-decoration:underline;font-weight:bold;}" +
			"a:active{color:#0000FF;font-weight:bold;}"); 
		
		var textArea = new JTextArea(str, 0, 40);
		textArea.setHtml(true);
		textArea.setEditable(false);
		textArea.setCSS(css);
		textArea.setWordWrap(true);
		textArea.setWidth(300);
		textArea.setBackground(_level0.common.backgroundColor);
		// add padding around the text
		textArea.setBorder(new EmptyBorder(null, new Insets(5, 5, 5, 5)));
		
		window.getContentPane().append(textArea);
		
		window.getContentPane().append(new JSpacer(5, 5));
		
		// panel to lay the buttons in
		var panel : JPanel = new JPanel(new BoxLayout());
		
		// button that will open the license dialog
		var updateNowButton : JButton = new JButton("Update Now!");
		updateNowButton.addEventListener(JButton.ON_PRESS, Delegate.create(this, updateNowClicked));
		CommonButtons.padButtonAdd(updateNowButton, panel);
		
		// button will close the about dialog
		var cancelButton : JButton = new JButton("Cancel");
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
	
	public function updateNowClicked(src : JButton) {
		// TODO: direct user to something to download?
	}
	
	public function cancelClicked(src : JButton) {
		// hide this window
		_level0.updateAvailableWindow.setVisible(false);
	}
}
