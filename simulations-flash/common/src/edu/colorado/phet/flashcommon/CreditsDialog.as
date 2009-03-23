// CreditsDialog.as
//
// Shows credits information in a window
//
// Author: Jonathan Olson

import org.aswing.ASColor;
import org.aswing.ASWingUtils;
import org.aswing.BoxLayout;
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

class edu.colorado.phet.flashcommon.CreditsDialog {
	
	public var common : FlashCommon;
	
	// shorthand for debugging function
	public function debug(str : String) : Void {
		_level0.debug(str);
	}
	
	public function CreditsDialog() {
		//debug("CreditsDialog initializing\n");
		
		// shortcut to FlashCommon, but now with type-checking!
		common = _level0.common;
		
		// somehow this line allows us to create these windows/buttons from
		// code that isn't part of a MovieClip.
		ASWingUtils.getRootMovieClip();
		
		// create a window
		var window : JFrame = new JFrame(_level0, common.strings.get("Credits", "Credits"));
		
		// make it accessible from anywhere
		_level0.creditsWindow = window;
		
		window.setResizable(false);
		
		// set the background color to default
		window.setBackground(common.backgroundColor);
		
		// layout the window vertically
		window.getContentPane().setLayout(new SoftBoxLayout(SoftBoxLayout.Y_AXIS));
		
		// get the string to display
		var str : String = "";
		var defaultString : String = "";
		str += "<font size='17'>" + common.strings.get("PhetDevelopmentTeam", "PhET Development Team ") + "</font>\n";
		str += common.getCreditsText();
		str += "\n\n";
		
		str += "<font size='17'>" + common.strings.get("LicenseLibraries", "Used Library Licensing Information:") + "</font>\n";
		var aswingText = "For AsWing A2:\n2005-2006(c) AsWing.org.\nAll rights reserved.\n\nRedistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:\n\n1) Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.\n\n2) Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.\n\n3) Neither the name AsWing.org nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.\n\n\nTHIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS \"AS IS\" AND ANY\nEXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF\nMERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL\nTHE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,\nSPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT\nOF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)\nHOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR\nTORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS\nSOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.";
		str += aswingText;
		
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
		
		var textScroll = new JScrollPane(textArea, JScrollPane.SCROLLBAR_AS_NEEDED, JScrollPane.SCROLLBAR_AS_NEEDED);
		textScroll.setPreferredSize(400, 300);
		textScroll.setBorder(new EmptyBorder(new LineBorder(null, ASColor.GRAY, 1, 0), new Insets(5, 5, 5, 5)));
		window.getContentPane().append(textScroll);
		
		
		window.getContentPane().append(new JSpacer(5, 5));
		
		// add the OK button
		var panel : JPanel = new JPanel(new BoxLayout());
		var okButton : JButton = new JButton(common.strings.get("Close", "Close"));
		okButton.addEventListener(JButton.ON_RELEASE, Delegate.create(this, okClicked));
		CommonButtons.padButtonAdd(okButton, panel);
		window.getContentPane().append(panel);
		
		window.getContentPane().append(new JSpacer(5, 5));
		
		// scale the window to fit
		window.setSize(window.getPreferredSize());
		
		// center the window
		window.setLocation((Stage.width - window.getWidth()) / 2, (Stage.height - window.getHeight()) / 2);
		window.show();
	}
	
	public function okClicked(src : JButton) {
		// make the window invisible
		_level0.creditsWindow.setVisible(false);
	}
}
