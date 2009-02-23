// CommonStrings.as
//
// Handles translation of strings for flash-common
//
// Author: Jonathan Olson

class CommonStrings {
	
	// where the XML object lives (it is parsed from FlashVars)
	public var xml : XML;
	
	public var common : FlashCommon;
	
	// shorthand to call _level0.debug()
	public function debug(str : String) : Void {
		_level0.debug(str);
	}
	
	// constructor
	public function CommonStrings() {
		//debug("CommonStrings initializing\n");
		
		// shortcut to FlashCommon, but now with type-checking!
		common = _level0.common;
		
		// install as _level0.comString
		common.strings = this;
		
		// parse the XML from flashvars, store in 'xml'
		xml = new XML();
		xml.ignoreWhite = true;
		xml.parseXML(common.getCommonXML());
	}
	
	// attempts to get the internationalized string corresponding to the 'key'.
	//
	// if the key is not found AND the optional argument defaultString is supplied,
	// then the defaultString is returned
	//
	// if the string contains references like {#} where # is a number, the references
	// are replaced with their counterparts in formatArray (formatArray[#])
	//
	// examples:
	// this in XML:
	// <string key="first" value="First One" />
	// <string key="pick up" value="Pick up the {0} from the {1}." />
	//
	// get("first") returns "First One"
	// get("first", "not found") returns "First One"
	// get("second", "not found") returns "not found"
	// get("pick up", "not found", ["simulation", "table"]) returns "Pick up the simulation from the table."
	//
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
		if((value == "keyNotFound" || value == null) && defaultString != undefined) {
			value = defaultString;
			debug("WARNING CommonStrings: cannot find common string for '" + key + "', using default instead.\n");
		}
		
		// if formatArray is passed, attempt to format the current string with it
		if(formatArray != undefined) {
			return format(value, formatArray);
		} else {
			return value;
		}
	}
	
	// format a string, by replacing instances of {#} with the #'th element of the
	// argument array passed. braces not used for these purposes should be
	// escaped with a backslash: \{0\} will not be replaced with anything, and
	// will appear to be "{0}". double-backslash will also be replaced with a single one
	//
	// example:
	// format("This \{is\} a {0} of the {1} system", ["test", "emergency broadcast"])
	//     returns "This {is} a test of the emergency broadcast system"
	public function format(pattern : String, args : Array) : String {
		// the string accumulated so far that will be returned
		var ret : String = "";
		
		// length of the pattern (in case you couldn't tell from the actual statement. Yes YOU!)
		var len : Number = pattern.length;
		
		// copy characters from pattern to ret, unless it is a brace
		for(var i : Number = 0; i < len; i++) {
			// character at this location
			var char : String = pattern.charAt(i);
			
			if(char == "\\" && i < len - 1 && (pattern.charAt(i+1) == "{" || pattern.charAt(i+1) == "}")) {
				// escaped brace
				i++;
				ret += pattern.charAt(i);
			} else if(char == "\\" && i < len - 1 && pattern.charAt(i+1) == "\\") {
				// escaped backslash
				i++;
				ret += "\\";
			} else if(char == "{") {
				// needs to be replaced
				
				// find the next ending brace afterwards
				var endIndex : Number = pattern.indexOf("}", i);
				
				if(endIndex == -1) {
					// if this doesn't exist, act like the backslash is a normal character
					ret += pattern.charAt(i);
				} else {
					// exists, so we parse the number
					var num : Number = new Number(pattern.substring(i+1, endIndex));
					
					// extract the string from the arguments
					var str : String = args[num];
					
					if(str != undefined) {
						// the string exists, so we append it
						ret += args[num];
						
						// set the index to the ending brace (it will be incremented before
						// anything else next iteration)
						i = endIndex;
					} else {
						// the string does not exist in the arguments. pretend like the initial
						// backslash is an ordinary character
						ret += pattern.charAt(i);
					}
				}
			} else {
				// any other character
				ret += pattern.charAt(i);
			}
		}
		
		return ret;
	}
}
