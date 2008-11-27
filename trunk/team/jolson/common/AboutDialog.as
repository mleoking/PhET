// AboutDialog.as

import org.aswing.*;
import org.aswing.util.*;

class AboutDialog {
	
	// shorthand for debugging function
	public function debug(str : String) : Void {
		_level0.debug(str);
	}
	
	public function AboutDialog() {
		debug("AboutDialog initializing\n");
		
		ASWingUtils.getRootMovieClip();
		
		var window : JFrame = new JFrame(_level0, "About " + _level0.simName);
		
		_level0.aboutWindow = window;
		
		window.setBackground(ASColor(0xFFFFFF));
		
		// don't use boxlayout: seems to keep children the same size
		//window.getContentPane().setLayout(new BoxLayout(BoxLayout.Y_AXIS));
		
		var borderLayout : BorderLayout = new BorderLayout();
		//borderLayout.setVgap(0);
		//borderLayout.setStrategy(BorderLayout.STRATEGY_VERTICAL);
		
		window.getContentPane().setLayout(borderLayout);
		
		var str : String = "";
		
		str += "<b>Physics Education Technology project</b>\n";
		str += "Copyright \u00A9 2004-2008 University of Colorado\n";
		str += "Some rights reserved.\n";
		str += "Visit <a href='http://phet.colorado.edu'>http://phet.colorado.edu</a>\n\n";
		
		str += "<b><font size='16'>" + _level0.simName + "</font></b>\n";
		str += "Version: " + _level0.versionMajor + "." + _level0.versionMinor;
		if(_level0.dev != "00") {
			str += "." + _level0.dev;
		}
		str += " (" + _level0.revision + ")\n";
		str += "Flash Version: " + System.capabilities.version + "\n";
		str += "OS Version: " + System.capabilities.os + "\n";
		
		
		
		var css : TextField.StyleSheet = new TextField.StyleSheet();
		
		css.parseCSS("a:link{color:#0000FF;font-weight:bold;}" +
			"a:visited{color:#0000FF;font-weight:bold;}" +
			"a:hover{color:#0000FF;text-decoration:underline;font-weight:bold;}" +
			"a:active{color:#0000FF;font-weight:bold;}"); 
		
		var textArea = new JTextArea(str);
		textArea.setHtml(true);
		textArea.setEditable(false);
		textArea.setCSS(css);
		
		window.getContentPane().append(textArea, BorderLayout.NORTH);
		
		var panel : JPanel = new JPanel(new BoxLayout());
		var licenseButton : JButton = new JButton("License...");
		licenseButton.addEventListener(JButton.ON_PRESS, Delegate.create(this, licenseClicked));
		var okButton : JButton = new JButton("OK");
		okButton.addEventListener(JButton.ON_PRESS, Delegate.create(this, okClicked));
		panel.append(licenseButton);
		panel.append(okButton);
		
		window.getContentPane().append(panel, BorderLayout.SOUTH);
		
		
		window.setHeight(window.getContentPane().getPreferredSize().height + 50);
		window.setWidth(window.getContentPane().getPreferredSize().width + 50);
		
		window.setLocation(50, 50);
		window.show();
	}
	
	public function licenseClicked(src : JButton) {
		debug("Clicked\n");
		if(_level0.licenseWindow) {
			debug("Showing dialog again\n");
			_level0.licenseWindow.show();
		} else {
			debug("Creating Dialog\n");
			_level0.licenseDialog = new LicenseDialog();
		}
	}
	
	public function okClicked(src : JButton) {
		_level0.aboutWindow.setVisible(false);
	}
}
