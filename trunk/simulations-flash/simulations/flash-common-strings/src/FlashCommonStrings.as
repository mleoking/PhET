
import org.aswing.*;
import org.aswing.util.*;
import org.aswing.border.*;

class FlashCommonStrings {
	
	public var common : FlashCommon;
	
	// shorthand for debugging function
	public function debug(str : String) : Void {
		_level0.debug(str);
	}
	
	public function FlashCommonStrings() {
		//debug("FlashCommonStrings initializing\n");
		
		// shortcut to FlashCommon, but now with type-checking!
		common = _level0.common;
		
		// mysterious fix since "this" does not refer to a MovieClip or Component
		ASWingUtils.getRootMovieClip();
		
		
		var privacyButton : JButton = new JButton(common.strings.get("SoftwarePrivacyAgreements", "Software & Privacy Agreements"));
		privacyButton.setSize(privacyButton.getPreferredSize());
		privacyButton.addEventListener(JButton.ON_PRESS, Delegate.create(this, privacyButtonClicked));
		_level0.commonWindow.getContentPane().append(privacyButton);
		
		var updateButton : JButton = new JButton(common.strings.get("NewVersionAvailable", "New Version Available"));
		updateButton.setSize(updateButton.getPreferredSize());
		updateButton.addEventListener(JButton.ON_PRESS, Delegate.create(this, updateButtonClicked));
		_level0.commonWindow.getContentPane().append(updateButton);
		
		_level0.commonWindow.setBounds(0, 0, _level0.commonWindow.getContentPane().getPreferredSize().width, _level0.commonWindow.getContentPane().getPreferredSize().height);
		
	}
	
	function privacyButtonClicked() : Void {
		if(_level0.privacyWindow) {
			debug("Showing dialog again\n");
			_level0.privacyWindow.show();
		} else {
			debug("Creating Dialog\n");
			_level0.privacyDialog = new PrivacyDialog();
		}
	}
	
	function updateButtonClicked() : Void {
		common.updateHandler.updatesAvailable(5, 0, 0);
	}
}
