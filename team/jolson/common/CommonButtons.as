// CommonButtons.as

import org.aswing.*;
import org.aswing.util.*;

class CommonButtons {
	
	// shorthand for debugging function
	public function debug(str : String) : Void {
		_level0.debug(str);
	}
	
	public function CommonButtons() {
		debug("CommonButtons initializing\n");
		
		// somehow this line allows us to create these windows/buttons from
		// code that isn't part of a MovieClip.
		ASWingUtils.getRootMovieClip();
		
		// create the window to hold the button
		var window : JWindow = new JWindow(_level0);
		
		var aboutButton : JButton = new JButton("About...");
		aboutButton.setSize(aboutButton.getPreferredSize());
		window.getContentPane().setLayout(new EmptyLayout());
		window.getContentPane().append(aboutButton);
		window.setBounds(0, 0, aboutButton.getPreferredSize().width, aboutButton.getPreferredSize().height);
		window.show();
		
		aboutButton.addEventListener(JButton.ON_PRESS, Delegate.create(this, aboutButtonClicked));
	}
	
	public function aboutButtonClicked(src : JButton) {
		debug("Clicked\n");
		if(_level0.aboutWindow) {
			debug("Showing dialog again\n");
			_level0.aboutWindow.show();
		} else {
			debug("Creating Dialog\n");
			_level0.aboutDialog = new AboutDialog();
		}
	}
}
