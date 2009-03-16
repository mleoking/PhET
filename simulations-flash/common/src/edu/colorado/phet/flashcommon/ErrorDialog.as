// ErrorDialog.as
//
// Shows any error information
//
// Author: Jonathan Olson

import org.aswing.ASColor;
import org.aswing.ASWingUtils;
import org.aswing.BoxLayout;
import org.aswing.CenterLayout;
import org.aswing.Insets;
import org.aswing.JButton;
import org.aswing.JFrame;
import org.aswing.JPanel;
import org.aswing.JScrollPane;
import org.aswing.JSpacer;
import org.aswing.JTextArea;
import org.aswing.SoftBoxLayout
import org.aswing.util.Delegate;
import org.aswing.border.EmptyBorder;
import org.aswing.border.LineBorder;

import edu.colorado.phet.flashcommon.*;

class edu.colorado.phet.flashcommon.ErrorDialog {
	
	public var common : FlashCommon;
	
	public var errorWindow : JFrame;
	
	// shorthand for debugging function
	public function debug(str : String) : Void {
		_level0.debug(str);
	}
	
	public function ErrorDialog(errorTitle : String, errorString : String) {
		//debug("AgreementDialog initializing\n");
		
		// shortcut to FlashCommon, but now with type-checking!
		common = _level0.common;
		
		// somehow this line allows us to create these windows/buttons from
		// code that isn't part of a MovieClip.
		ASWingUtils.getRootMovieClip();
		
		// create a window
		var window : JFrame = new JFrame(_level0, errorTitle);
		
		// make it accessible from anywhere
		_level0.errorWindow = window;
		errorWindow = window;
		
		window.setResizable(false);
		
		// set the background color to default
		window.setBackground(common.backgroundColor);
		
		// layout the window vertically
		window.getContentPane().setLayout(new SoftBoxLayout(SoftBoxLayout.Y_AXIS));
		
		// get the string to display
		var str : String = errorString;
		
		// CSS will make links blue
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
		textArea.setWidth(400);
		textArea.setBackground(common.backgroundColor);
		// add padding around the text
		textArea.setBorder(new EmptyBorder(null, new Insets(5, 5, 0, 5)));
		
		//window.getContentPane().append(textArea);
		
		window.getContentPane().append(textArea);
		
		window.getContentPane().append(new JSpacer(5, 5));
		
		var panel : JPanel = new JPanel(new BoxLayout());
		var okButton : JButton = new JButton(common.strings.get("Close", "Close"));
		okButton.addEventListener(JButton.ON_PRESS, Delegate.create(this, closeClicked));
		CommonButtons.padButtonAdd(okButton, panel);
		
		//window.getContentPane().append(panel);
		var centerPanel : JPanel = new JPanel(new CenterLayout()); //SoftBoxLayout.X_AXIS, 0, SoftBoxLayout.CENTER
		centerPanel.append(panel);
		window.getContentPane().append(centerPanel);
		
		window.getContentPane().append(new JSpacer(5, 5));
		
		// scale the window to fit
		window.setSize(window.getPreferredSize());
		
		// center the window
		window.setLocation((Stage.width - window.getWidth()) / 2, (Stage.height - window.getHeight()) / 2);
		window.show();
	}
	
	public function closeClicked(src : JButton) {
		// make the window invisible
		errorWindow.setVisible(false);
	}
}
