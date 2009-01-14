



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
	
	public function get(key : String) : String {  
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
		return value;
	}
}
