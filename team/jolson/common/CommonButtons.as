// CommonButtons.as
//
// Creates the About and Preferences buttons for simulations
// Handles when they are clicked
//
// Author: Jonathan Olson

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
		
		FocusManager.disableTraversal();
		
		// create the window to hold the buttons
		var window : JWindow = new JWindow(_level0);
		
		// creates the about button
		var aboutButton : JButton = new JButton("About...");
		aboutButton.setSize(aboutButton.getPreferredSize());
		aboutButton.addEventListener(JButton.ON_PRESS, Delegate.create(this, aboutButtonClicked));
		
		// creates the preferences button
		var preferencesButton : JButton = new JButton("Preferences");
		preferencesButton.setSize(preferencesButton.getPreferredSize());
		preferencesButton.addEventListener(JButton.ON_PRESS, Delegate.create(this, preferencesButtonClicked));
		
		// flow layout will be left-to-right. it is possible to either reverse the direction,
		// or if vertical layout is desired use SoftBoxLayout(SoftBoxLayout.Y_AXIS) instead
		window.getContentPane().setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		
		// append the buttons
		window.getContentPane().append(aboutButton);
		window.getContentPane().append(preferencesButton);
		
		window.setBounds(0, 0, window.getContentPane().getPreferredSize().width, window.getContentPane().getPreferredSize().height);
		window.show();
		
		
	}
	
	public function aboutButtonClicked(src : JButton) {
		if(_level0.aboutWindow) {
			// window already exists, we just need to show it
			debug("Showing dialog again\n");
			_level0.aboutWindow.show();
		} else {
			// window doesn't exist, we must create it
			debug("Creating Dialog\n");
			_level0.aboutDialog = new AboutDialog();
		}
	}
	
	public function preferencesButtonClicked(src : JButton) {
		if(_level0.preferencesWindow) {
			// window already exists, we just need to show it
			debug("Showing dialog again\n");
			_level0.preferencesWindow.show();
			
			// make sure the check-boxes are set correctly inside the window
			_level0.preferencesDialog.reCheck();
		} else {
			// window doesn't exist, we must create it
			debug("Creating Dialog\n");
			_level0.preferencesDialog = new PreferencesDialog();
		}
	}
	
	// creates padding around the text inside a button, and allows the button
	// to shrink down to the preferred size in the center of its parent location.
	// this is done by creating an anonymous panel, adding the button to it, and
	// adding the panel to the container
	public static function padButtonAdd(button : JButton, container : Container) : Void {
		
		// create panel which will center the button
		var panel : JPanel = new JPanel(new CenterLayout());
		
		// pad the text by 10 pixels to the left and right
		button.setMargin(new Insets(0, 10, 0, 10));
		
		// connect everything together
		panel.append(button);
		container.append(panel);
	}
}
