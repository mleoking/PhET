// Inspector.as
//
// Allows a developer to inspect movieclips and their parents/children
//
// Author: Jonathan Olson


class Inspector {
	public var mc : MovieClip;
	public var inputText : TextField;
	
	// shorthand for debugging function
	public function debug(str : String) : Void {
		_level0.debug(str);
	}
	
	public function Inspector() {
		debug("Initializing Inspector\n");
		mc = _level0.createEmptyMovieClip("inspector", _level0.getNextHighestDepth());
		
		_level0.inspector = this;
		
		mc._x = 150;
		
		mc.beginFill(0xFFFFFF);
		mc._alpha = 50;
		mc.moveTo(0, 0);
		mc.lineTo(200, 0);
		mc.lineTo(200, 50);
		mc.lineTo(0, 50);
		mc.endFill();
		
		
		
		var hits : MovieClip = mc.createEmptyMovieClip("hits", 0);
		hits.beginFill(0xFFFFFF);
		hits.moveTo(0, 40);
		hits.lineTo(200, 40);
		hits.lineTo(200, 50);
		hits.lineTo(0, 50);
		hits.endFill();
		//mc.hitArea = hits;
		
		hits.onPress = function() {
			// this = inspector
			// inputText and displayText
			var ob : Object;
			if(_level0.inspector.inputText.text == "") {
				ob = _level0;
			} else {
				var s : String = _level0.inspector.inputText.text
				//_level0.debug("s " + s + "\n");
				var ar : Array = s.split(".");
				ob = _level0;
				for(var z = 0; z < ar.length; z++) {
					//_level0.debug("before " + String(ob) + "\n");
					ob = ob[ar[z]];
					//_level0.debug("after ob[" + ar[z] + "] " + String(ob) + "\n");
				}
				//ob = _level0[_level0.inspector.inputText.text];
			}
			var str : String = ""
			//str += "----------\n";
			str += "<font size=\"6\">";
			for(var i : String in ob) {
				str += i + " : " + typeof(ob[i]) + "\n";
			}
			str += "</font>";
			//str += "----------\n\n";
			
			
			_level0.debugs.htmlText = str;
		}
		
		inputText = mc.createTextField("inputText", mc.getNextHighestDepth(), 0, 0, 200, 40);
		
		inputText.type = "input";
		inputText.border = true;
	}
}

