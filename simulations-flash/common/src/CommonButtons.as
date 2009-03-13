// CommonButtons.as
//
// Creates the About and Preferences buttons for simulations
// Handles when they are clicked
//
// Author: Jonathan Olson

import org.aswing.ASWingUtils;
import org.aswing.CenterLayout;
import org.aswing.Container;
import org.aswing.FlowLayout;
import org.aswing.FocusManager;
import org.aswing.Insets;
import org.aswing.JButton;
import org.aswing.JPanel;
import org.aswing.JWindow;
import org.aswing.util.Delegate;

class CommonButtons {
	
	// locations for the common buttons
	public static var LOCATION_UPPER_LEFT : String = "upper-left";
	public static var LOCATION_UPPER_RIGHT : String = "upper-right";
	public static var LOCATION_LOWER_LEFT : String = "lower-left";
	public static var LOCATION_LOWER_RIGHT : String = "lower-right";
	
	public var common : FlashCommon;
	
	// shorthand for debugging function
	public function debug(str : String) : Void {
		_level0.debug(str);
	}
	
	public function CommonButtons(position : String) {
		//debug("CommonButtons initializing\n");
		
		// shortcut to FlashCommon, but now with type-checking!
		common = _level0.common;
		
		// somehow this line allows us to create these windows/buttons from
		// code that isn't part of a MovieClip.
		ASWingUtils.getRootMovieClip();
		
		FocusManager.disableTraversal();
		
		// create the window to hold the buttons
		var window : JWindow = new JWindow(_level0);
		
		_level0.commonWindow = window;
		
		// flow layout will be left-to-right. it is possible to either reverse the direction,
		// or if vertical layout is desired use SoftBoxLayout(SoftBoxLayout.Y_AXIS) instead
		window.getContentPane().setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		
		// creates the about button
		var aboutButton : JButton = new JButton(common.strings.get("About...", "About..."));
		_level0.aboutButton = aboutButton;
		aboutButton.setSize(aboutButton.getPreferredSize());
		aboutButton.addEventListener(JButton.ON_PRESS, Delegate.create(this, aboutButtonClicked));
		window.getContentPane().append(aboutButton);
		
		if(!common.fromPhetWebsite()) {
			// creates the preferences button
			var preferencesButton : JButton = new JButton(common.strings.get("Preferences", "Preferences"));
			_level0.preferencesButton = preferencesButton;
			preferencesButton.setSize(preferencesButton.getPreferredSize());
			preferencesButton.addEventListener(JButton.ON_PRESS, Delegate.create(this, preferencesButtonClicked));
			window.getContentPane().append(preferencesButton);
		}
		
		// determine the window location
		var windowX : Number = 0;
		var windowY : Number = 0;
		switch(position) {
			case LOCATION_UPPER_LEFT:
				windowX = 0;
				windowY = 0;
				break;
			case LOCATION_UPPER_RIGHT:
				windowX = Stage.width - window.getContentPane().getPreferredSize().width;
				windowY = 0;
				break;
			case LOCATION_LOWER_LEFT:
				windowX = 0;
				windowY = Stage.height - window.getContentPane().getPreferredSize().height;
				break;
			case LOCATION_LOWER_RIGHT:
				windowX = Stage.width - window.getContentPane().getPreferredSize().width;
				windowY = Stage.height - window.getContentPane().getPreferredSize().height;
				break;
		}
		
		window.setBounds(windowX, windowY, window.getContentPane().getPreferredSize().width, window.getContentPane().getPreferredSize().height);
		window.show();
		
		common.tabHandler.insertControl(_level0.aboutButton.trigger_mc, 0);
		common.tabHandler.registerButton(_level0.aboutButton.trigger_mc);
		common.tabHandler.insertControl(_level0.preferencesButton.trigger_mc, 1);
		common.tabHandler.registerButton(_level0.preferencesButton.trigger_mc);
		
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
