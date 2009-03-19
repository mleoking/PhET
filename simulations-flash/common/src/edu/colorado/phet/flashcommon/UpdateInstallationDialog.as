// UpdateInstallationDialog.as
//
// Shown when a more recent version of the installation is
// detected to exist
//
// Author: Jonathan Olson

import org.aswing.ASColor;
import org.aswing.ASFont;
import org.aswing.ASWingUtils;
import org.aswing.CenterLayout;
import org.aswing.FlowLayout;
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

class edu.colorado.phet.flashcommon.UpdateInstallationDialog {
	
	public var newInstallerTimestamp : Number;
	public var newInstallerAskLaterDays : Number;
	
	public var common : FlashCommon;
	
	// shorthand for debugging function
	public function debug(str : String) : Void {
		_level0.debug(str);
	}
	
	public function UpdateInstallationDialog(installerTimestamp : Number, installerAskLaterDays : Number) {
		//debug("UpdateInstallationDialog initializing\n");
		
		// shortcut to FlashCommon, but now with type-checking!
		common = _level0.common;
		
		// store new version information so they can be stored if the user decides to skip it
		newInstallerTimestamp = installerTimestamp;
		newInstallerAskLaterDays = installerAskLaterDays;
		
		// mysterious fix since "this" does not refer to a MovieClip or Component
		ASWingUtils.getRootMovieClip();
		
		// create a window
		var window : JFrame = new JFrame(_level0, common.strings.get("NewVersionAvailable", "New Version Available"));
		
		// the window shouldn't be resizable
		window.setResizable(false);
		
		// or for this one, closable:
		//window.setClosable(false);
		
		// make sure we can access it from anywhere
		_level0.updateInstallationWindow = window;
		
		// set the background to default
		window.setBackground(common.backgroundColor);
		
		// layout things vertically
		window.getContentPane().setLayout(new SoftBoxLayout(SoftBoxLayout.Y_AXIS));
		
		// construct the string of text to show
		var str : String = "";
		
		str += common.strings.get("InstallationDated", "Your PhET Offline Website Installation is dated {0}.", [FlashCommon.dateString(FlashCommon.dateOfSeconds(common.getInstallerCreationTimestamp()))]) + "\n";
		//str += "Your PhET Offline Website Installation is dated #####.\n";
		
		str += common.strings.get("InstallationNewer", "A newer version is available, dated {0}.", [FlashCommon.dateString(FlashCommon.dateOfSeconds(newInstallerTimestamp))]) + "\n";
		//str += "A newer version is available, dated $$$$$.\n";
		
		str += "\n";
		
		str += common.strings.get("InstallerAsk", "Would you like to get the latest PhET Offline Website Installer?");
		//str += "Would you like to get the latest PhET Offline Website Installer?";
		
		
		// create CSS to make links blue
		var css : TextField.StyleSheet = new TextField.StyleSheet();
		css.parseCSS("a:link{color:#0000FF;font-weight:bold;}" +
			"a:visited{color:#0000FF;font-weight:bold;}" +
			"a:hover{color:#0000FF;text-decoration:underline;font-weight:bold;}" +
			"a:active{color:#0000FF;font-weight:bold;}"); 
		
		var textArea = new JTextArea(str, 0, 0);
		textArea.setHtml(true);
		textArea.setEditable(false);
		textArea.setCSS(css);
		textArea.setWordWrap(true);
		textArea.setMultiline(true);
		
		textArea.setBackground(common.backgroundColor);
		
		// add padding around the text
		textArea.setBorder(new EmptyBorder(null, new Insets(5, 5, 5, 5)));
		textArea.setWidth(350);
		
		window.getContentPane().append(textArea);
		
		window.getContentPane().append(new JSpacer(5, 5));
		
		// panel to lay the buttons in
		var panel : JPanel = new JPanel(new FlowLayout());
		
		var yesButton : JButton = new JButton(common.strings.get("Yes!", "Yes!"));
		yesButton.addEventListener(JButton.ON_RELEASE, Delegate.create(this, yesClicked));
		yesButton.setForeground(ASColor.BLUE);
		yesButton.setFont(new ASFont(ASFont.DEFAULT_NAME, ASFont.DEFAULT_SIZE + 2, true, false, false));
		yesButton.setUseHandCursor(true);
		CommonButtons.padButtonAdd(yesButton, panel);
		
		var askLaterButton : JButton = new JButton(common.strings.get("AskLater", "Ask me later"));
		askLaterButton.addEventListener(JButton.ON_RELEASE, Delegate.create(this, askLaterClicked));
		CommonButtons.padButtonAdd(askLaterButton, panel);
		
		var tellMoreButton : JButton = new JButton(common.strings.get("TellMore", "Tell me more..."));
		tellMoreButton.addEventListener(JButton.ON_RELEASE, Delegate.create(this, tellMoreClicked));
		CommonButtons.padButtonAdd(tellMoreButton, panel);
		
		//window.getContentPane().append(panel);
		var centerPanel : JPanel = new JPanel(new CenterLayout()); //SoftBoxLayout.X_AXIS, 0, SoftBoxLayout.CENTER
		centerPanel.append(panel);
		window.getContentPane().append(centerPanel);
		
		// fit the window to its contents
		window.setSize(window.getPreferredSize());
		
		window.addEventListener(JFrame.ON_WINDOW_CLOSED, Delegate.create(this, onWindowClose));
		
		// center the window
		window.setLocation((Stage.width - window.getWidth()) / 2, (Stage.height - window.getHeight()) / 2);
		window.show();
		
	}
	
	public function onWindowClose() {
		// hide this window
		_level0.updateInstallationWindow.setVisible(false);
		
		common.updateHandler.handleResponse();
	}
	
	public function yesClicked(src : JButton) {
		// hide this window
		//_level0.updateInstallationWindow.setVisible(false);
		
		common.openExternalLink( "http://phet.colorado.edu/get_phet/full_install.php" );
		//getURL("http://phet.colorado.edu/get_phet/full_install.php", "_blank");
	}
	
	public function askLaterClicked(src : JButton) {
		// hide this window
		_level0.updateInstallationWindow.setVisible(false);
		
		common.preferences.setInstallationAskLater(newInstallerAskLaterDays);
		
		common.updateHandler.handleResponse();
	}
	
	public function tellMoreClicked(src : JButton) {
		if(_level0.updateInstallationDetailsWindow) {
			debug("Showing dialog again\n");
			_level0.updateInstallationDetailsWindow.show();
		} else {
			debug("Creating Dialog\n");
			_level0.updateInstallationDetailsDialog = new UpdateInstallationDetailsDialog();
		}
	}
}
