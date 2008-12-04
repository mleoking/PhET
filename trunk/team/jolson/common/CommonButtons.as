// CommonButtons.as

import org.aswing.*;
import org.aswing.util.*;
import org.aswing.border.*;

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
		aboutButton.addEventListener(JButton.ON_PRESS, Delegate.create(this, aboutButtonClicked));
		
		var preferencesButton : JButton = new JButton("Preferences");
		preferencesButton.setSize(preferencesButton.getPreferredSize());
		preferencesButton.addEventListener(JButton.ON_PRESS, Delegate.create(this, preferencesButtonClicked));
		
		window.getContentPane().setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		window.getContentPane().append(aboutButton);
		window.getContentPane().append(preferencesButton);
		window.setBounds(0, 0, window.getContentPane().getPreferredSize().width, window.getContentPane().getPreferredSize().height);
		window.show();
		
		
	}
	
	public function aboutButtonClicked(src : JButton) {
		if(_level0.aboutWindow) {
			debug("Showing dialog again\n");
			_level0.aboutWindow.show();
		} else {
			debug("Creating Dialog\n");
			_level0.aboutDialog = new AboutDialog();
		}
	}
	
	public function preferencesButtonClicked(src : JButton) {
		if(_level0.preferencesWindow) {
			debug("Showing dialog again\n");
			_level0.preferencesWindow.show();
			_level0.preferencesDialog.reCheck();
		} else {
			debug("Creating Dialog\n");
			_level0.preferencesDialog = new PreferencesDialog();
		}
		//_level0.preferencesWindow.setActive();
	}
	
	public static function pad_button_add(button : JButton, container : Container) : Void {
		var panel : JPanel = new JPanel(new CenterLayout());
		button.setMargin(new Insets(0, 10, 0, 10));
		panel.append(button);
		container.append(panel);
	}
}
