// LicenseDialog.as
//
// Shows license information in a window
//
// Author: Jonathan Olson

import org.aswing.*;
import org.aswing.util.*;
import org.aswing.border.*;

class LicenseDialog {
	
	// shorthand for debugging function
	public function debug(str : String) : Void {
		_level0.debug(str);
	}
	
	public function LicenseDialog() {
		debug("LicenseDialog initializing\n");
		
		// somehow this line allows us to create these windows/buttons from
		// code that isn't part of a MovieClip.
		ASWingUtils.getRootMovieClip();
		
		// create a window
		var window : JFrame = new JFrame(_level0, _level0.comStrings.get("Licensing", "Licensing"));
		
		// make it accessible from anywhere
		_level0.licenseWindow = window;
		
		// set the background color to default
		window.setBackground(_level0.common.backgroundColor);
		
		// layout the window vertically
		window.getContentPane().setLayout(new SoftBoxLayout(SoftBoxLayout.Y_AXIS));
		
		// get the string to display
		var str : String = "";
		var defaultString : String = "";
		str += "<font size='17'>" + _level0.comStrings.get("PhetLicense", "PhET Licensing Information:") + "</font>\n";
		defaultString += "The PhET project provides a suite of interactive educational simulations.";
		str += _level0.comStrings.get("LicensePhetDesc", defaultString);
		str += "\n";
		str += _level0.comStrings.get("CopyrightColorado", "Copyright {0} University of Colorado.", ["\u00A9 2004-2008"]) + " ";
		str += _level0.comStrings.get("SomeRightsReserved", "Some rights reserved.");
		str += "\n\n";
		defaultString = "PhET interactive simulations by <a href='{0}'>The PhET Team, University of Colorado</a> ";
		defaultString += "are licensed under a <a href='{1}'>Creative Commons Attribution-Noncommercial 3.0 United States License</a>.";
		str += _level0.comStrings.get("LicenseSims", defaultString, ["http://phet.colorado.edu/", "http://creativecommons.org/licenses/by-nc/3.0/us/"]);
		str += "\n\n";
		defaultString = "The PhET source code is licensed under a <a href='{0}'>Creative Commons GNU General Public License</a>.";
		str += _level0.comStrings.get("License3", defaultString, ["http://creativecommons.org/licenses/GPL/2.0/"]);
		str += "\n\n";
		defaultString = "For more information about licensing, <a href='{0}'>click here</a>. If you are interested ";
		defaultString += "in alternative license options, please contact PhET at <a href='{1}'>phethelp@colorado.edu</a>.";
		str += _level0.comStrings.get("License4", defaultString, ["http://phet.colorado.edu/about/licensing.php", "mailto:phethelp@colorado.edu"]);
		str += "\n\n";
		str += "<font size='17'>" + _level0.comStrings.get("License5Libraries", "Used Library Licensing Information:") + "</font>\n";
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
		textArea.setBackground(_level0.common.backgroundColor);
		// add padding around the text
		textArea.setBorder(new EmptyBorder(null, new Insets(5, 5, 0, 5)));
		
		window.getContentPane().append(textArea);
		
		/*
		var externalLicenseArea = new JTextArea(aswingText, 0, 40);
		externalLicenseArea.setHtml(true);
		externalLicenseArea.setEditable(false);
		externalLicenseArea.setCSS(css);
		externalLicenseArea.setWordWrap(true);
		externalLicenseArea.setWidth(300);
		externalLicenseArea.setBackground(_level0.common.backgroundColor);
		//externalLicenseArea.setBorder(new EmptyBorder(null, new Insets(0, 5, 5, 5)));
		*/
		var licenseScroll = new JScrollPane(textArea, JScrollPane.SCROLLBAR_AS_NEEDED, JScrollPane.SCROLLBAR_AS_NEEDED);
		licenseScroll.setPreferredSize(400, 400);
		licenseScroll.setBorder(new EmptyBorder(null, new Insets(5, 5, 5, 5)));
		window.getContentPane().append(licenseScroll);
		
		
		window.getContentPane().append(new JSpacer(5, 5));
		
		// add the OK button
		var panel : JPanel = new JPanel(new BoxLayout());
		var okButton : JButton = new JButton(_level0.comStrings.get("OK", "OK"));
		okButton.addEventListener(JButton.ON_PRESS, Delegate.create(this, okClicked));
		CommonButtons.padButtonAdd(okButton, panel);
		window.getContentPane().append(panel);
		
		// scale the window to fit
		window.setHeight(window.getContentPane().getPreferredSize().height + 50);
		window.setWidth(window.getContentPane().getPreferredSize().width + 50);
		
		// center the window
		window.setLocation((Stage.width - window.getWidth()) / 2, (Stage.height - window.getHeight()) / 2);
		window.show();
	}
	
	public function okClicked(src : JButton) {
		// make the window invisible
		_level0.licenseWindow.setVisible(false);
	}
}
