



class CommonStrings {
	public var xml : XML;
	
	// shorthand to call _level0.debug()
	public function debug(str : String) : Void {
		_level0.debug(str);
	}
	
	public function CommonStrings() {
		debug("CommonStrings initializing\n");
		
		// install as _level0.comString
		_level0.comStrings = this;
		
		xml = new XML();
		xml.ignoreWhite = true;
		xml.parseXML(_level0.commonStrings);
		
	}
	
	public function get(key : String, defaultString : String, formatArray : Array) : String {  
		// basically copied from simstrings
		var value : String = "keyNotFound"; //Dubson code
		var nodes : Array = xml.firstChild.childNodes; // array of XMLNode
		var node : XMLNode = null;
		for(var i : Number = 0; i < nodes.length; i++) {
			node = XMLNode(nodes[i]);
			if(node.attributes.key == key) {
				value = node.attributes.value;
				if(value != null) {
					break;
				}
			}
		}
		
		// revert to default if not found
		if(value == "keyNotFound" && defaultString != undefined) {
			value = defaultString;
			debug("WARNING CommonStrings: cannot find common string for '" + key + "', using default instead.\n");
		}
		
		return format(value, formatArray);
	}
	
	public function format(pattern : String, args : Array) : String {
		var ret : String = "";
		var len : Number = pattern.length;
		for(var i : Number = 0; i < len; i++) {
			var char : String = pattern.charAt(i);
			if(char == "\\" && i < len - 1 && (pattern.charAt(i+1) == "{" || pattern.charAt(i+1) == "}")) {
				i++;
				ret += pattern.charAt(i);
			} else if(char == "{") {
				var endIndex : Number = pattern.indexOf("}", i);
				if(endIndex == -1) {
					ret += pattern.charAt(i);
				} else {
					var num : Number = new Number(pattern.substring(i+1, endIndex));
					ret += args[num];
					i = endIndex;
				}
			} else {
				ret += pattern.charAt(i);
			}
		}
		return ret;
	}
}
