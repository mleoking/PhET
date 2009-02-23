
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
		
	}
}
