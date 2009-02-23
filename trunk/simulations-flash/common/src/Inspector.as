// Inspector.as
//
// Allows a developer to inspect movieclips and their parents/children
//
// Author: Jonathan Olson


class Inspector {
	public var mc : MovieClip;
	public var inputText : TextField;
	public var hits : MovieClip;
	
	// shorthand for debugging function
	public function debug(str : String) : Void {
		_level0.debug(str);
	}
	
	public function Inspector() {
		debug("Initializing Inspector\n");
		mc = _level0.createEmptyMovieClip("inspector", 105849);
		
		// catch key events to this object
		Key.addListener(this);
		
		_level0.inspector = this;
		_level0.inspect = inspect;
		_level0.upLevel = upLevel;
		
		mc._x = 150;
		
		mc.beginFill(0xFFFFFF);
		mc._alpha = 50;
		mc.moveTo(0, 0);
		mc.lineTo(200, 0);
		mc.lineTo(200, 50);
		mc.lineTo(0, 50);
		mc.endFill();
		
		mc._visible = false;
		
		
		hits = mc.createEmptyMovieClip("hits", 0);
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
			str += "<font size=\"8\" color=\"#000000\">";
			
			str += "At: <font color=\"#00bb00\">_level0." + _level0.inspector.inputText.text + "</font>\n";
			
			if(_level0.inspector.inputText.text != "") {
				str += "<font color=\"#FF0000\"><a href=\"asfunction:_level0.upLevel,boo\">... (up one level)</a></font>\n";
			}
			
			
			
			str += "\n";
			
			var mc_str : String = "", btn_str : String = "", txt_str : String = "", ob_str : String = "", str_str : String = "", bool_str : String = "", num_str : String = "", def_str : String = "";
			
			for(var i : String in ob) {
				var type : String = typeof(ob[i]);
				if(type == "object") {
					/*
					if(ob[i].condenseWhite != undefined && ob[i].maxscroll != undefined) {
						type = "textfield";
					} else if(ob[i].useHandCursor != undefined && ob[i].scale9Grid != undefined) {
						type = "button";
					}
					*/
					if(TextField.prototype.isPrototypeOf(ob[i])) {
						type = "textfield";
					}
					if(Button.prototype.isPrototypeOf(ob[i])) {
						type = "button";
					}
					if(Array.prototype.isPrototypeOf(ob[i])) {
						type = "array";
					}
				}
				switch(type) {
					case "movieclip":
						mc_str += "<a href=\"asfunction:_level0.inspect," + i + "\">" + i + " : <font color=\"#aa00aa\">" + typeof(ob[i]) + "</font></a> [" + _level0.inspector.countChildren(ob, i) + "] " + String(ob[i]) + "\n";
						break;
					case "button":
						btn_str += "<a href=\"asfunction:_level0.inspect," + i + "\">" + i + " : <font color=\"#777700\">" + "button" + "</font></a> [" + _level0.inspector.countChildren(ob, i) + "]\n";
						break;
					case "textfield":
						txt_str += "<a href=\"asfunction:_level0.inspect," + i + "\">" + i + " : <font color=\"#aa0000\">" + "textfield" + "</font></a> [" + _level0.inspector.countChildren(ob, i) + "]\n";
						break;
					case "array":
						ob_str += "<a href=\"asfunction:_level0.inspect," + i + "\">" + i + " : <font color=\"#00aa66\">" + "array" + "</font></a> [" + _level0.inspector.countChildren(ob, i) + "]\n";
						break;
					case "object":
						ob_str += "<a href=\"asfunction:_level0.inspect," + i + "\">" + i + " : <font color=\"#0000aa\">" + typeof(ob[i]) + "</font></a> [" + _level0.inspector.countChildren(ob, i) + "]\n";
						break;
					case "string":
						str_str += i + " : " + typeof(ob[i]) + " = <font color=\"#006600\">\"" + _level0.inspector.fixString(ob[i]) + "\"</font>\n";
						break;
					case "boolean":
						bool_str += i + " : " + typeof(ob[i]) + " = " + String(ob[i]) + "\n";
						break;
					case "number":
						num_str += i + " : " + typeof(ob[i]) + " = <font color=\"#004477\">" + String(ob[i]) + "</font>\n";
						break;
					default:
						def_str += i + " : " + typeof(ob[i]) + "\n";
				}
			}
			
			str += mc_str + btn_str + txt_str + ob_str + num_str + bool_str + str_str + def_str;
			
			str += "</font>";
			//str += "----------\n\n";
			
			
			_level0.debugs.htmlText = str;
		}
		
		inputText = mc.createTextField("inputText", mc.getNextHighestDepth(), 0, 0, 200, 40);
		
		inputText.type = "input";
		inputText.border = true;
	}
	
	public function onKeyDown() {
		if(Key.getCode() == Key.PGDN || Key.getCode() == 122) {
			// page down or F11
			_level0.inspector.hits.onPress();
		}
	}
	
	public function fixString(str : String) {
		var ret : String = "";
		for(var i : Number = 0; i < str.length; i++) {
			switch(str.charAt(i)) {
				case "<":
					ret += "&lt;";
					break;
				case ">":
					ret += "&gt;";
					break;
				default:
					ret += str.charAt(i);
			}
		}
		return ret;
	}
	
	public function countChildren(ob : Object, str : String) : String {
		var count : Number = 0;
		for(var i : String in ob[str]) {
			count = count + 1;
		}
		return String(count);
	}
	
	public function upLevel(toss : String) {
		var s : String = _level0.inspector.inputText.text
		var ar : Array = s.split(".");
		var str : String = "";
		for(var z = 0; z < ar.length - 1; z++) {
			if(z > 0) {
				str += ".";
			}
			str += ar[z];
		}
		_level0.inspector.inputText.text = str;
		_level0.inspector.hits.onPress();
	}
	
	public function inspect(str : String) {
		if(_level0.inspector.inputText.text == "") {
			_level0.inspector.inputText.text = str;
		} else {
			_level0.inspector.inputText.text += "." + str;
		}
		_level0.inspector.hits.onPress();
	}
}

