// AgreementDialog.as
//
// Shows license information in a window
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

class edu.colorado.phet.flashcommon.AgreementDialog {
	
	public var common : FlashCommon;
	
	// shorthand for debugging function
	public function debug(str : String) : Void {
		_level0.debug(str);
	}
	
	public function AgreementDialog() {
		//debug("AgreementDialog initializing\n");
		
		// shortcut to FlashCommon, but now with type-checking!
		common = _level0.common;
		
		// somehow this line allows us to create these windows/buttons from
		// code that isn't part of a MovieClip.
		ASWingUtils.getRootMovieClip();
		
		// create a window
		var window : JFrame = new JFrame(_level0, common.strings.get("PhetSoftwareAgreement", "PhET Software Agreement"));
		
		// make it accessible from anywhere
		_level0.agreementWindow = window;
		
		window.setResizable(false);
		
		// set the background color to default
		window.setBackground(common.backgroundColor);
		
		// layout the window vertically
		window.getContentPane().setLayout(new SoftBoxLayout(SoftBoxLayout.Y_AXIS));
		
		// get the string to display
		var str : String = "";
		var defaultString : String = "";
		//str += "<font size='17'>" + common.strings.get("PhetSoftwareAgreement", "PhET Software Agreement") + "</font>\n";
		str += common.getAgreementText();
		str += "\n\n";
		
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
		
		var agreementScroll = new JScrollPane(textArea, JScrollPane.SCROLLBAR_AS_NEEDED, JScrollPane.SCROLLBAR_AS_NEEDED);
		agreementScroll.setPreferredSize(400, 300);
		agreementScroll.setBorder(new EmptyBorder(new LineBorder(null, ASColor.GRAY, 1, 0), new Insets(5, 5, 5, 5)));
		window.getContentPane().append(agreementScroll);
		
		
		window.getContentPane().append(new JSpacer(5, 5));
		
		// add the OK button
		var panel : JPanel = new JPanel(new BoxLayout());
		var okButton : JButton = new JButton(common.strings.get("Close", "Close"));
		okButton.addEventListener(JButton.ON_RELEASE, Delegate.create(this, okClicked));
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
	
	public function okClicked(src : JButton) {
		// make the window invisible
		_level0.agreementWindow.setVisible(false);
	}
}
