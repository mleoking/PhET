// LicenseDialog.as

import org.aswing.*;
import org.aswing.util.*;

class LicenseDialog {
	
	// shorthand for debugging function
	public function debug(str : String) : Void {
		_level0.debug(str);
	}
	
	public function LicenseDialog() {
		debug("LicenseDialog initializing\n");
		
		ASWingUtils.getRootMovieClip();
		
		var window : JFrame = new JFrame(_level0, "Licensing");
		
		_level0.licenseWindow = window;
		
		window.setBackground(ASColor(0xFFFFFF));
		
		// don't use boxlayout: seems to keep children the same size
		//window.getContentPane().setLayout(new BoxLayout(BoxLayout.Y_AXIS));
		
		var borderLayout : BorderLayout = new BorderLayout();
		//borderLayout.setVgap(0);
		//borderLayout.setStrategy(BorderLayout.STRATEGY_VERTICAL);
		
		window.getContentPane().setLayout(borderLayout);
		
		var str : String = "";
		
		
		str += "The PhET project provides a suite of interactive educational simulations.\n";
		str += "Copyright \u00A9 2004-2008 University of Colorado. Some rights reserved.\n\n";
		str += "PhET interactive simulations by <a href='http://phet.colorado.edu/'>The PhET Team, University of Colorado</a> ";
		str += "are licensed under a <a href='http://creativecommons.org/licenses/by-nc/3.0/us/'>Creative Commons Attribution-Noncommercial 3.0 United States License</a>.\n\n";
		str += "The PhET source code is licensed under a <a href='http://creativecommons.org/licenses/GPL/2.0/'>Creative Commons GNU General Public License</a>.\n\n";
		str += "For more information about licensing, <a href='http://phet.colorado.edu/about/licensing.php'>click here</a>. If you are interested ";
		str += "in alternative license options, please contact PhET at <a href='mailto:phethelp@colorado.edu'>phethelp@colorado.edu</a>.\n";
		
		
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
		
		//window.getContentPane().append(textArea);
		window.getContentPane().append(textArea, BorderLayout.NORTH);
		//window.getContentPane().add(textArea, BorderLayout.NORTH);
		
		var panel : JPanel = new JPanel(new BoxLayout());
		var okButton : JButton = new JButton("OK");
		okButton.addEventListener(JButton.ON_PRESS, Delegate.create(this, okClicked));
		panel.append(okButton);
		
		//window.getContentPane().append(panel);
		window.getContentPane().append(panel, BorderLayout.SOUTH);
		//window.getContentPane().add(panel, BorderLayout.SOUTH);
		
		
		window.setHeight(window.getContentPane().getPreferredSize().height + 50);
		window.setWidth(window.getContentPane().getPreferredSize().width + 50);
		
		window.setLocation(50, 50);
		window.show();
	}
	
	public function okClicked(src : JButton) {
		_level0.licenseWindow.setVisible(false);
	}
}
