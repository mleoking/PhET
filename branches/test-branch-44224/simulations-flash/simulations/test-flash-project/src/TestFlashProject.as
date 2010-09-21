import edu.colorado.phet.flashcommon.*;

class TestFlashProject {
	public function TestFlashProject() {
		_level0.debug( "Starting TestFlashProject" );
		
		_level0.resetButton_mc.onRelease = function() {
			_level0.debug( "Clicked" );
		}
		
		// initialize flash-common code
		var common : FlashCommon = new FlashCommon(CommonButtons.LOCATION_UPPER_LEFT);
		
		common.tabHandler.addControl(_level0.resetButton_mc);
		common.tabHandler.registerButton(_level0.resetButton_mc);
	}
}