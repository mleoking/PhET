// Preloader.as
//
// Handles showing a progress bar when sim is loading
//
// Author: Jonathan Olson

import edu.colorado.phet.flashcommon.*;

class edu.colorado.phet.flashcommon.Preloader {
	public var totalBytes : Number;
	public var loadedBytes : Number;
	public var loadedPercent : Number;
	
	public function Preloader() {
		var mc : MovieClip = _root.createEmptyMovieClip("Preloader_mc", _root.getNextHighestDepth());
		
		mc.beginFill(_level0.bgColor, 50);
		mc.moveTo(-5000, -5000);
		mc.lineTo(5000, -5000);
		mc.lineTo(5000, 5000);
		mc.lineTo(-5000, 5000);
		mc.endFill();
		
		_root.onEnterFrame = function() {
			totalBytes = _root.getBytesTotal();
			loadedBytes = _root.getBytesLoaded();
			loadedPercent = Math.ceil((loadedBytes/totalBytes) * 100);
			
			_root.Preloader_mc.beginFill(0x000000);
			_root.Preloader_mc.moveTo(0, Stage.height / 2 + 5);
			_root.Preloader_mc.lineTo(Stage.width, Stage.height / 2 + 5);
			_root.Preloader_mc.lineTo(Stage.width, Stage.height / 2 - 5);
			_root.Preloader_mc.lineTo(0, Stage.height / 2 - 5);
			_root.Preloader_mc.endFill();
			
			if(loadedBytes >= totalBytes) {
				_root.Preloader_mc.clear();
				_root.removeMovieClip(_root.Preloader_mc);
				_root.gotoAndPlay("sim");
				delete _root.onEnterFrame;
			}
		}
	}
}

