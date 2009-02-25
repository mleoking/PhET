// UpdateInstallationDetailsDialog.as
//
// Show details about updating an installation
//
// Author: Jonathan Olson

import org.aswing.*;
import org.aswing.util.*;
import org.aswing.border.*;

class UpdateInstallationDetailsDialog {
	
	public var common : FlashCommon;
	
	public var textArea : JTextArea;
	
	// shorthand for debugging function
	public function debug(str : String) : Void {
		_level0.debug(str);
	}
	
	public function UpdateInstallationDetailsDialog() {
		//debug("UpdateInstallationDetailsDialog initializing\n");
		
		// shortcut to FlashCommon, but now with type-checking!
		common = _level0.common;
		
		
		// mysterious fix since "this" does not refer to a MovieClip or Component
		ASWingUtils.getRootMovieClip();
		
		// create a window
		var window : JFrame = new JFrame(_level0, common.strings.get("NewVersionAvailable", "New Version Available"));
		
		// the window shouldn't be resizable
		window.setResizable(false);
		
		
		// make sure we can access it from anywhere
		_level0.updateInstallationDetailsWindow = window;
		
		// set the background to default
		window.setBackground(common.backgroundColor);
		
		// layout things vertically
		window.getContentPane().setLayout(new SoftBoxLayout(SoftBoxLayout.Y_AXIS));
		
		// construct the string of text to show
		var str : String = "";
		
		str += common.strings.get("InstallerInfo1", "Keeping your PhET Offline Website Installation up-to-date ensures that you have access to the newest PhET simulations and supplemental materials.") + "\n\n";
		str += common.strings.get("InstallerInfo2", "If you choose to get the newest version, a web browser will open to the PhET website, where you can download the PhET Offline Website Installer.");
		
		// create CSS to make links blue
		var css : TextField.StyleSheet = new TextField.StyleSheet();
		css.parseCSS("a:link{color:#0000FF;font-weight:bold;}" +
			"a:visited{color:#0000FF;font-weight:bold;}" +
			"a:hover{color:#0000FF;text-decoration:underline;font-weight:bold;}" +
			"a:active{color:#0000FF;font-weight:bold;}"); 
		
		var textArea = new JTextArea(str, 0, 35);
		//textArea = new JTextArea(str, 0, 0);
		textArea.setHtml(true);
		textArea.setEditable(false);
		textArea.setCSS(css);
		textArea.setMultiline(true);
		
		textArea.setBackground(common.backgroundColor);
		
		// add padding around the text
		textArea.setBorder(new EmptyBorder(null, new Insets(5, 5, 5, 5)));
		textArea.setWidth(300);
		//textArea.setWidth(100);
		textArea.setWordWrap(true);
		//textArea.updateUI();
		//textArea.setText(str);
		
		window.getContentPane().append(textArea);
		
		window.getContentPane().append(new JSpacer(5, 5));
		
		// panel to lay the buttons in
		var panel : JPanel = new JPanel(new FlowLayout());
		
		var okButton : JButton = new JButton(common.strings.get("Close", "Close"));
		okButton.addEventListener(JButton.ON_PRESS, Delegate.create(this, closeClicked));
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
		
		debug("UID::: " + String(textArea.getVisibleRows()) + "\n");
		
		var tex : TextField = textArea.getTextField();
		
		debug("_height " + String(tex._height) + "\n");
		debug("_width " + String(tex._width) + "\n");
		debug("textHeight " + String(tex.textHeight) + "\n");
		debug("textWidth " + String(tex.textWidth) + "\n");
		
		_level0.debugTextArea = tex;
		
	}
	
	public function closeClicked(src : JButton) {
		// hide this window
		_level0.updateInstallationDetailsWindow.setVisible(false);
	}
	
}
